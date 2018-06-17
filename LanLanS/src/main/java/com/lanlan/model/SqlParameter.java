package com.lanlan.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

/**
 * sql参数的简单封装,方便DButil下executeUpdate方法和executeQuery方法的调用
 * @author 朱矛宇
 *
 * @param <T>  value的类型,即sql参数的类型,int,long,float,double,string bigdecimal等
 */
public class SqlParameter {

	/**
	 * 参数在prestatement中的索引,从1开始
	 */
	private  int index;
	
	/**
	 * 参数值
	 */
	private  Object value;
	
	
	/**
	 * sql参数类型 给value赋值时自动设置. 具体类型请参照 java.sql.Types
	 */
	private  int type;
	
	
	/**
	 * 为需要指定数据类型长度时预留(未实现)
	 */
	private  int length;
	
	/**
	 * 无参构造函数
	 */
	public SqlParameter(){
		
	}
	/**
	 * 有参构造函数,通过有参构造函数可以创建一个可以使用的参数对象
	 * @param index
	 * @param value
	 * @param length
	 */
	public SqlParameter(int index,Object value,int length){
		this(index,value);
		this.length=length;
	}
	
	/**
	 * 有参构造函数,通过有参构造函数可以创建一个可以使用的参数对象
	 * @param index
	 * @param value
	 */
	public SqlParameter(int index,Object value) {
		this.index =index;
		setValue(value);
	}
	
	public int getIndex() {
		return index;
	}
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	public Object getValue() {
		return value;
	}
	
	public int getType() {
		return type;
	}
	
	/**
	 * 通过setValue给type赋值找到对应的sql类型
	 * @author 朱矛宇
	 * @date 2018年6月14日
	 * @param value
	 */
	public void setValue(Object value) {
		this.value = value;
		if(value instanceof Integer) {
			type=java.sql.Types.INTEGER;
		}else if(value instanceof Double) {
			type=java.sql.Types.DOUBLE;
		}else if(value instanceof Float) {
			type=java.sql.Types.FLOAT;
		}else if(value instanceof Boolean) {
			type=java.sql.Types.BOOLEAN;
		}else if(value instanceof Long) {
			type=java.sql.Types.BIGINT;
		}else if(value instanceof Byte) {
			type=java.sql.Types.BLOB;
		}else if(value instanceof Date) {
			type=java.sql.Types.DATE;
		}else if(value instanceof Time) {
			type=java.sql.Types.TIME;
		}else if(value instanceof BigDecimal) {
			type=java.sql.Types.DECIMAL;
		}else if(value instanceof String) {
			type=java.sql.Types.VARCHAR;
		}
	}
}
