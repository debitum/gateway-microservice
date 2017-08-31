package com.debitum.gateway;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandFactory;
import org.springframework.cloud.netflix.zuul.filters.route.apache.HttpClientRibbonCommandFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@EnableHystrix
@EnableFeignClients
@EnableDiscoveryClient
@EnableZuulProxy
@EnableCircuitBreaker
@SpringBootApplication
public class GatewayApplication {

    private static final String DEV_PROFILE = "dev";
    public static final String LOCAL_PROFILE = "local";
    public static final String TEST_PROFILE = "test";

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GatewayApplication.class);
        SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);
        addDefaultProfile(app, source);
        app.run(args);
    }

    private static void addDefaultProfile(SpringApplication app, SimpleCommandLinePropertySource source) {
        if (LOCAL_PROFILE.equals(System.getProperty("spring.profiles.active"))) {
            System.setProperty("spring.cloud.bootstrap.enabled", "false");
        }
        if (!source.containsProperty("spring.profiles.active")
                && !System.getenv().containsKey("SPRING_PROFILES_ACTIVE")
                && System.getProperty("spring.profiles.active") == null) {

            app.setAdditionalProfiles(DEV_PROFILE);
        }
    }

    @Bean
    public FilterRegistrationBean corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(-1000);
        return bean;
    }

    @Bean
    public RibbonCommandFactory ribbonCommandFactory(SpringClientFactory clientFactory, ZuulProperties zuulProperties) {
        //    https://github.com/spring-cloud/spring-cloud-netflix/issues/412
        return new HttpClientRibbonCommandFactory(clientFactory);
    }
}
