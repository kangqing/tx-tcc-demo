/**
 * Copyright 2020-9999 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kangqing.tx.tcc.bank01.service.impl;

import com.kangqing.tx.tcc.bank01.service.UserAccountBank01Service;
import com.kangqing.tx.tcc.common.api.UserAccountBank02Service;
import com.kangqing.tx.tcc.common.dto.UserAccountDto;
import com.kangqing.tx.tcc.common.entity.UserAccount;
import com.kangqing.tx.tcc.common.mapper.UserAccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.hmily.annotation.HmilyTCC;
import org.springframework.stereotype.Service;

/**
 * @author kangqing
 * @version 1.0.0
 * @description 转出银行微服务Service实现类
 */
@Slf4j
@RequiredArgsConstructor
@Service("userAccountBank01Service")
public class UserAccountBank01ServiceImpl implements UserAccountBank01Service {

    private final UserAccountMapper userAccountMapper;

    @DubboReference(timeout = 1000000)
    private UserAccountBank02Service userAccountBank02Service;

    @Override
    @HmilyTCC(confirmMethod = "confirmMethod", cancelMethod = "cancelMethod")
    public void transferAmount(UserAccountDto userAccountDto) {
        String txNo = userAccountDto.getTxNo();
        log.info("执行bank01的Try方法，事务id为:{}", txNo);
        if(userAccountMapper.existsTryLog(txNo)!= null){
            log.info("bank01已经执行过Try方法, txNo:{}", txNo);
            return;
        }
        //悬挂处理
        if(userAccountMapper.existsConfirmLog(txNo) != null || userAccountMapper.existsCancelLog(txNo) != null){
            log.info("bank01的Confirm方法或者Cancel方法已经执行过，txNo:{}", txNo);
            return;
        }
        UserAccount sourceAccount = userAccountMapper.getUserAccountByAccountNo(userAccountDto.getSourceAccountNo());
        if(sourceAccount == null){
            throw new RuntimeException("不存在转出账户");
        }
        if(sourceAccount.getAccountBalance().compareTo(userAccountDto.getAmount()) < 0){
            throw new RuntimeException("账户余额不足");
        }
        UserAccount targetAccount = userAccountBank02Service.getUserAccountByAccountNo(userAccountDto.getTargetAccountNo());
        if(targetAccount == null){
            throw new RuntimeException("不存在转入账户");
        }
        userAccountMapper.saveTryLog(txNo);
        // 减少转出账户金额，添加到转账字段中
        userAccountMapper.updateUserAccountBalanceBank01(userAccountDto.getAmount(), userAccountDto.getSourceAccountNo());
        // 执行转账，走转入账户的 TCC
        userAccountBank02Service.transferAmountToBank2(userAccountDto);
    }


    public void confirmMethod(UserAccountDto userAccountDto){
        String txNo = userAccountDto.getTxNo();
        log.info("执行bank01的Confirm方法，事务id为:{}", txNo);
        if(userAccountMapper.existsConfirmLog(txNo) != null){
            log.info("bank01已经执行过Confirm方法, txNo:{}", txNo);
            return;
        }
        userAccountMapper.saveConfirmLog(txNo);
        // 把转账字段（冻结）的金额减去
        userAccountMapper.confirmUserAccountBalanceBank01(userAccountDto.getAmount(), userAccountDto.getSourceAccountNo());
    }

    public void cancelMethod(UserAccountDto userAccountDto){
        String txNo = userAccountDto.getTxNo();
        log.info("执行bank01的Cancel方法，事务id为:{}", txNo);
        if(userAccountMapper.existsCancelLog(txNo) != null){
            log.info("bank01已经执行过Cancel方法, txNo:{}", txNo);
            return;
        }
        if (userAccountMapper.existsTryLog(txNo) == null) {
            log.info("bank01还没有执行扣款方法, txNo:{}", txNo);
            return;
        }
        userAccountMapper.saveCancelLog(txNo);
        // 出现异常，退回扣款，减去冻结字段金额
        userAccountMapper.cancelUserAccountBalanceBank01(userAccountDto.getAmount(), userAccountDto.getSourceAccountNo());

    }
}
