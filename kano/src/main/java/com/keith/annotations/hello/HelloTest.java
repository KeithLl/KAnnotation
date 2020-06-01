package com.keith.annotations.hello;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by KeithLee on 2019-07-30.
 * Introduction:
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface HelloTest {

}
