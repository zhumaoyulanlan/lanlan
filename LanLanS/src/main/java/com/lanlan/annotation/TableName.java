package com.lanlan.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @see
 *  表名注解 value存放数据库中的表明
 * @author 朱矛宇
 * @date 2018年6月13日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableName {
	public String value() default"";
}

