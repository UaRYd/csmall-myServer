package cn.tedu.csmall.passport.config;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@MapperScan("cn.tedu.csmall.passport.mapper")
public class MyBatisConfiguration {

    public MyBatisConfiguration() {
        log.debug("创建配置类对象：MyBatisConfiguration");
    }
}
