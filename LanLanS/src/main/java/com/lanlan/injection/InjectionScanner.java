package com.lanlan.injection;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
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

import com.lanlan.annotation.Autowired;
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
	 * 处理逻辑:
	 * 1.获取类中的全部带@Autowired注解的构造方法
	 * 2.遍历带@Autowired注解的构造方法
	 * 3.查询带@Component的注解类表(未实例化表)
	 * 4.若存在需要的对象,试着从objectMap获取.若获取失败则创建
	 * @param clazz
	 * @return
	 */
	private Object injectionNewInstance(Class<?> clazz) {
		Constructor<?>[] constructors= clazz.getConstructors();
		List<Object> parameterObjects =new ArrayList<Object>();
		List<Constructor> autowiredConstructors=null;
		try {
			if(constructors.length==1){
				
			}
			
			for(Constructor<?> constructor:  constructors) {
				Autowired autowired=constructor.getDeclaredAnnotation(Autowired.class);
				if(autowired!=null) {
					autowiredConstructors.add(constructor);
				}
			}
			
			
			for(Constructor<?> constructor:  autowiredConstructors) {
				//分析构造函数
				Autowired autowired=constructor.getDeclaredAnnotation(Autowired.class);
				boolean required = autowired.required();//当为true时,无法调用有参构造函数,抛出异常.设置为false时,允许调用无参构造函数0
				if(required) {
					for(Parameter parameter:constructor.getParameters()) {
						String parameterType = parameter.getType().getSimpleName();
						String parameterName = parameter.getName();
						Class<?> parameterClass = classMap.get(CommonUtil.lowerCaseFirst(parameterType));
						if(parameterClass==null){
							parameterClass = classMap.get(parameterName);
						}
						if(parameterClass==null) {
							if(!required)
							{
								
							}else {
								throw new RuntimeException("类["+clazz+"]初始化失败,"
										+ "找不到参数["+parameterType+" "+parameter.getName()+"]]"
										+ "对应的对象id=["+CommonUtil.lowerCaseFirst(parameterType)+"]"
										+ "或者id=["+parameterName+"]的对象");
								
							}
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
	
	private boolean executeConstructor( Constructor constructor) {
		Parameter[] parameters= constructor.getParameters();
		for (Parameter parameter : parameters) {
			String parameterType = CommonUtil.lowerCaseFirst(parameter.getClass().getSimpleName());
			String parameterName =parameter.getName();
			//Annotation[] annotations =parameter.getDeclaredAnnotation();
			//for (Annotation annotation : annotations) {
				
			//} 
			Class parameterClass=null;
			
			parameterClass=classMap.get(parameterType);
			if(parameterClass==null) {
				parameterClass=classMap.get(parameterName);
			}
			if(parameterClass==null) {
				return false;
			}
			
			
		}
		return false;
		
	}
	
}
