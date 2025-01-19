package turing.docs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Method {
    String value();

    String returnValue() default "";

    boolean builder() default false;

    Argument[] arguments() default {};

    FunctionExample[] examples() default {};
}
