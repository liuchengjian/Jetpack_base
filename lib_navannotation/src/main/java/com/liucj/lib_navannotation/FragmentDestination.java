package com.liucj.lib_navannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
/**
 * 标记在Fragment的朱注解
 */
@Target(ElementType.TYPE)
public @interface FragmentDestination {
    String pageUrl();

    boolean needLogin() default false;
    //当前页面是不是开始页面
    boolean asStarter() default false;
}
