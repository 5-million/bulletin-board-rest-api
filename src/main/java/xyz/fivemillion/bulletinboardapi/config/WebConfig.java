package xyz.fivemillion.bulletinboardapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import xyz.fivemillion.bulletinboardapi.config.web.PageRequestHandlerMethodArgumentResolver;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public PageRequestHandlerMethodArgumentResolver pageRequestHandlerMethodArgumentResolver() {
        return new PageRequestHandlerMethodArgumentResolver();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(pageRequestHandlerMethodArgumentResolver());
    }
}
