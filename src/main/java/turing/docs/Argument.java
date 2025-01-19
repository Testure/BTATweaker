package turing.docs;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Argument {
    String name() default "";

    String value() default "nil";

    String description() default "";
}
