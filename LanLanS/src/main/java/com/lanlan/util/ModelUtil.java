package com.lanlan.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;

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
	
	/**
	 * 批量赋值
	 * @param modelFrom
	 * @param modelTo
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] CloneValueModelToModelList(Object[] modelFroms,Class<T> classTo,T[] modelTos){
		try {
			int len = Math.min(modelFroms.length, modelTos.length) ;
			if(modelTos==null);
			modelTos= (T[]) Array.newInstance(classTo,len);
			for(int i =1; i<len;i++ ) {
				Object modelFrom=modelFroms[i];
				T modelTo= classTo.newInstance();
				modelTos[i]=modelTo;
				Field[] fieldsFrom =  modelFrom.getClass().getFields();
				for (Field fieldFrom : fieldsFrom) {
					Field fieldTo = classTo.getField(fieldFrom.getName());
					if(fieldTo!=null&& fieldTo.getType().equals(fieldFrom.getType())) {
						fieldTo.set(modelTo, fieldFrom.get(modelFrom));
					}
				}
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return modelTos; 
	}
	
	/**
	 *  批量赋值
	 * @param modelFroms
	 * @param classTo
	 * @return
	 */
	public static <T> T[] CloneValueModelToModelList(Object[] modelFroms,Class<T> classTo){
		return CloneValueModelToModelList(modelFroms,classTo);
	}

}
