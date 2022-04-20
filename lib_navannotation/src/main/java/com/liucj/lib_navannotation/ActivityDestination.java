package com.liucj.lib_navannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 标记在Activity的朱注解
 */
@Target(ElementType.TYPE)
public @interface ActivityDestination {
    String pageUrl();

    boolean needLogin() default false;

    boolean asStarter() default false;
}
