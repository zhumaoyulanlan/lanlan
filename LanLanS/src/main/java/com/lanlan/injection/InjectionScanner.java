package com.lanlan.injection;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import com.lanlan.util.CommonUtil;
import com.lanlan.util.PackageUtil;
@Resource(type=InputStream.class,name="ss",shareable=true)
public class InjectionScanner {
	

	public InjectionScanner() {
		
	}
	
	private Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
	private Map<String, Object> objectMap = new HashMap<String, Object>();
	public  Map<String, Object> scanner() {
		try {
			InputStream ins= CommonUtil.getResourceAsStream("config/lanlan.properties");
			if(ins==null) {
				ins= CommonUtil.getResourceAsStream("lanlan.properties");
			}
			Properties properties =new Properties();
			properties.load(ins);
			String baseLanLanPackage = properties.getProperty("ScanBaseLanLanPackage");
			List<String> classNames = PackageUtil.getClassName(baseLanLanPackage, true);
			for(String calssName:classNames) {
				Class<?> clazz= Class.forName(calssName);
				classMap.put(clazz.getSimpleName(),clazz);
			}
			for(String calssName:classNames) {
				Class<?> clazz= Class.forName(calssName);
				
				Field[] fields= clazz.getDeclaredFields();
			//	for(fields)
				
				classMap.put(clazz.getSimpleName(),clazz);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			RuntimeException et = new RuntimeException("找不到配置文件[config/lanlan.properties]");
			et.printStackTrace();
			throw(et);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 通过注入构造并初始化类
	 * @param clazz
	 * @return
	 */
	private Object injectionNewInstance(Class<?> clazz) {
		Constructor<?>[] constructors= clazz.getConstructors();
		List<Object> parameterObjects =new ArrayList<Object>();
		try {
			for(Constructor<?> constructor:  constructors) {
				Resource resource=constructor.getDeclaredAnnotation(Resource.class);
		
				if(resource!=null|| constructors.length==1) {
					for(Parameter parameter:constructor.getParameters()) {
						String parameterType = parameter.getType().getSimpleName();
						Class<?> parameterClass = classMap.get(CommonUtil.lowerCaseFirst(parameterType));
						
						if(parameterClass==null) {
							//throw new RuntimeException("类["+clazz+"]初始化失败,找不到参数["+parameterType+" "+parameter.getName()+"]对应的对象[id="+CommonUtil.lowerCaseFirst(parameterType)+"]或者["+id="+CommonUtil.lowerCaseFirst(parameterType)+);
						}
						parameterObjects.add(injectionNewInstance(parameterClass));
					}
				}
				return constructor.newInstance(parameterObjects);
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
}
