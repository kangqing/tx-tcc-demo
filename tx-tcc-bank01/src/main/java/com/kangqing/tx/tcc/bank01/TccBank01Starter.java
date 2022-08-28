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
package com.kangqing.tx.tcc.bank01;

import com.kangqing.tx.tcc.bank01.service.UserAccountBank01Service;
import com.kangqing.tx.tcc.common.dto.UserAccountDto;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author kangqing
 * @version 1.0.0
 * @description 银行1服务
 */

@SpringBootApplication(exclude = MongoAutoConfiguration.class)
@ComponentScan(basePackages = {"com.kangqing.tx.tcc"})
@MapperScan(value = { "com.kangqing.tx.tcc.common.mapper" })
@ImportResource({"classpath:applicationContext.xml"})
@EnableTransactionManagement(proxyTargetClass = true)
@EnableDubbo
@EnableDiscoveryClient
public class TccBank01Starter {

    public static void main(String[] args){
        ConfigurableApplicationContext context = SpringApplication.run(TccBank01Starter.class, args);
        //UserAccountBank01Service userAccountBank01Service = context.getBean(UserAccountBank01Service.class);
        //userAccountBank01Service.transferAmount(new UserAccountDto(UUID.randomUUID().toString(), "1001", "1002", BigDecimal.valueOf(1)));
    }
}
