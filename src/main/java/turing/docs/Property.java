package turing.docs;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Properties.class)
public @interface Property {
    String value() default "nil";

    String name() default "";

    String description() default "";
}
