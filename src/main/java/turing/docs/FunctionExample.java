package turing.docs;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FunctionExample {
    String[] returnValues() default {};

    String[] value();

    String[] comments() default {};
}
