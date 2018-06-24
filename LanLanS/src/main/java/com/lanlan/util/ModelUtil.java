package com.lanlan.util;

import java.lang.reflect.Field;

public class ModelUtil {

	/**
	 * 主要用于ViewModel给Model赋值,然后进行insert update 等操作
	 * @param modelFrom
	 * @param modelTo
	 */
	public static <T> T CloneValueModelToModel(Object modelFrom,T modelTo){
		try {
			Field[] fieldsFrom =  modelFrom.getClass().getFields();
			Class<?> classTo =  modelTo.getClass();
			for (Field fieldFrom : fieldsFrom) {
				Field fieldTo = classTo.getField(fieldFrom.getName());
				if(fieldTo!=null&& fieldTo.getType().equals(fieldFrom.getType())) {
					fieldTo.set(modelTo, fieldFrom.get(modelFrom));
				}
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return modelTo; 
	}
}
