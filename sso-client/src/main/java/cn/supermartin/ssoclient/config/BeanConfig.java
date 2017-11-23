package cn.supermartin.ssoclient.config;

import cn.supermartin.export.filter.SsoFilter;
import cn.supermartin.export.service.AuthenticationRpcService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.Resource;
import javax.servlet.Filter;

/**
 * @author maxiaoding
 * @date 2017/11/16 下午3:28
 * @description:
 */
@Configuration
@ImportResource(locations = {"classpath:dubbo-consumer.xml"})
public class BeanConfig {
    @Resource
    private AuthenticationRpcService authenticationRpcService;
    @Value("${sso.server.url}")
    private String serverUrl;
    @Value("${sso.logoutUri}")
    private String logoutUri;
    @Value("${sso.timeout}")
    private int tokenTimeout;


    @Bean
    public FilterRegistrationBean ssoFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(ssoFilter());
        registration.addUrlPatterns("/*");
        registration.setName("ssoFilter");
        return registration;
    }

    @Bean(name = "ssoFilter")
    public Filter ssoFilter() {
        return new SsoFilter(serverUrl, logoutUri, tokenTimeout, authenticationRpcService);
    }

}
