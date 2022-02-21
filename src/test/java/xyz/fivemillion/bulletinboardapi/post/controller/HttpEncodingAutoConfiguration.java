package xyz.fivemillion.bulletinboardapi.post.controller;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration.class)
@Inherited
@Documented
public @interface HttpEncodingAutoConfiguration {
}
