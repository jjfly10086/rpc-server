package com.jwh.demo.annotation;

import com.jwh.demo.ProviderFactory;
import com.jwh.demo.ReferenceFactory;
import com.jwh.demo.RpcContext;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({RpcContext.class, ProviderFactory.class, ReferenceFactory.class})
public @interface EnableRpcService {

}
