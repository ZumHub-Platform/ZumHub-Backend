package com.server.mapping.annotation;

import com.server.request.RequestType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface Mapping {

    String value() default "/";

    RequestType method() default RequestType.GET;
}
