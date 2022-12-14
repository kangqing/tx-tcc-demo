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
package com.kangqing.tx.tcc.bank01.service;


import com.kangqing.tx.tcc.common.dto.UserAccountDto;

/**
 * @author kangqing
 * @version 1.0.0
 * @description 银行1Service接口
 */
public interface UserAccountBank01Service {

    /**
     * 转账
     */
    void transferAmount(UserAccountDto userAccountDto);
}
