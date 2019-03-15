package com.jwh.demo.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * Created by Administrator on 2017/10/11 0011.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface RpcProvider {

}
