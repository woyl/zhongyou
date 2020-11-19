package com.zhongyou.meet.mobile.utils;

/**
 * @author golangdorid@gmail.com
 * @date 2020/4/3 8:36 PM.
 * @页面信息标注
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Page {
	/**
	 * @return 页面的名称
	 */
	String name() default "";

	/**
	 * @return 界面传递的参数Key
	 */
	String[] params() default {""};

	/**
	 * @return 页面切换的动画
	 */

	/**
	 *
	 * @return 拓展字段
	 */
	int extra() default -1;
}