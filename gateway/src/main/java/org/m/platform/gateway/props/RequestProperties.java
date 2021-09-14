package org.m.platform.gateway.props;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author tai
 * @create 2021-09-14 18:24
 */
@Getter
@Setter
@RefreshScope
@ConfigurationProperties("my.request")
@Component
@Slf4j
@ToString
public class RequestProperties {
    /**
     * 是否开启日志链路追踪
     */
    private Boolean trace = false;

    /**
     * 是否启用获取IP地址
     */
    private Boolean ip = false;

    /**
     * 是否启用黑名单
     */
    private Boolean ifUseBlackList = false;

    @PostConstruct
    void print(){
        log.info(this.toString());
    }
}
