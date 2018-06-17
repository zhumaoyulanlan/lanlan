package com.lanlan.util;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Types;

public class CommonUtil {

	/**
	 * 使用类加载器(ClassLoad)从class根目录加载资源
	 * 对通过类加载器获取资源的简单封装
	 * 
	 * @author 朱矛宇
	 * @date 2018年6月14日
	 * @param path 以Src为根目录. ps:运行时,实际以class为根目录.
	 * @return InputStream
	 */
	public static InputStream getResourceAsStream(String path) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
	}
	
	/**
	 * 简单封装获得ClassLoader方法;
	 * @return
	 */
	public static ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
	
	
	/**
	 * 首字母转大写
	 * @author 朱矛宇
	 * @date 2018年6月15日
	 * @param str
	 * @return
	 */
	public static String upperCaseFirst(String str) {  
	    char[] ch = str.toCharArray();  
	    if (ch[0] >= 'a' && ch[0] <= 'z') {  
	        ch[0] = (char) (ch[0] - 32);  
	    }  
	    return new String(ch);  
	}
	
	/**
	 * 首字母转小写
	 * @author 朱矛宇
	 * @date 2018年6月15日
	 * @param str
	 * @return
	 */
	public static String lowerCaseFirst(String str) {  
	    char[] ch = str.toCharArray();  
	    if (ch[0] >= 'A' && ch[0] <= 'Z') {  
	        ch[0] = (char) (ch[0] + 32);  
	    }  
	    return new String(ch);  
	}
	
	/**
	 * 将 java.sql.Types 转为java类型(仍然有部分类型未完全实现)
	 * @author 朱矛宇
	 * @date 2018年6月14日
	 * @param types
	 * @return
	 */
	public static Class<?>  sqlTypetoJavaType(int types){
		if(types==Types.ARRAY) {
			
		}
		if(types==Types.BIGINT) {
			return Long.class;
		}
		if(types==Types.BINARY) {
			return Byte[].class;
		}
		if(types==Types.BIT) {
			return Boolean.class;
		}
		if(types==Types.BLOB) {
			
		}
		if(types==Types.BOOLEAN) {
			return Boolean.class;
		}
		if(types==Types.CHAR) {
			return String.class;
		}
		if(types==Types.CLOB) {
			
		}
		if(types==Types.DATALINK) {
			
		}
		if(types==Types.DATE) {
			return java.sql.Date.class;
		}
		if(types==Types.DECIMAL) {
			return BigDecimal.class;
		}
		if(types==Types.DISTINCT) {
			
		}
		if(types==Types.DOUBLE) {
			return Double.class;
		}
		if(types==Types.FLOAT) {
			return Double.class;
		}
		if(types==Types.INTEGER) {
			return Integer.class;
		}
		if(types==Types.JAVA_OBJECT) {
			//javaobject
			return Object.class;
		}
		if(types==Types.LONGNVARCHAR) {
			return String.class;
		}
		if(types==Types.LONGVARBINARY) {
			return Byte[].class;
		}
		if(types==Types.LONGVARCHAR) {
			//longchar
			return String.class;
		}
		if(types==Types.NCHAR) {
			//nchar
			return String.class;
		}
		if(types==Types.NCLOB) {
			
		}
		if(types==Types.NULL) {
			return null;
		}
		if(types==Types.NUMERIC) {
			return BigDecimal.class;
		}
		if(types==Types.NVARCHAR) {
			
		}
		if(types==Types.OTHER) {
			
		}
		if(types==Types.REAL) {
			return Float.class;
		}
		if(types==Types.REF) {
			
		}
		if(types==Types.REF_CURSOR) {
			
		}
		if(types==Types.ROWID) {
			//行号
			return Integer.class;
		}
		if(types==Types.SMALLINT) {
			return Integer.class;
		}
		if(types==Types.SQLXML) {
			
		}
		if(types==Types.STRUCT) {
			
		}
		if(types==Types.TIME) {
			return java.sql.Time.class;
		}
		if(types==Types.TIME_WITH_TIMEZONE) {
			
		}
		if(types==Types.TIMESTAMP) {
			return java.sql.Timestamp.class;
		}
		if(types==Types.TIMESTAMP_WITH_TIMEZONE) {
			
		}
		if(types==Types.TINYINT) {
			return Integer.class;
		}
		if(types==Types.VARBINARY) {
			return Byte[].class;
		}
		if(types==Types.VARCHAR) {
			return String.class;
		}
		return null;
			
	}
	
}
