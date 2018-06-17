package com.lanlan.mapper;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


import com.lanlan.annotation.RequestPath;
import com.lanlan.annotation.ResponseType;
import com.lanlan.model.Action;
import com.lanlan.util.PackageUtil;

/**
 * 负责映射路径与Controller的关系 
 * 主要提供了getController()方法
 * @author 朱矛宇
 * @date 2018年6月13日
 */
public class ActionMapper {
	private Map<String, Action> map;
	private static ActionMapper mapper;
//	private String controllerPackage = properties.getProperty("ControllerPackage");

	/**
	 * 构造方法初始化,根据在config/dispatcher.properties中配置的ControllerPackage值
	 * 找到Controller包下的所有类,遍历所有类,生成map映射
	 */
	private ActionMapper() {
		map = new HashMap<String, Action>();
		try (InputStream ins = ActionMapper.class.getClassLoader().getResourceAsStream("config/dispatcher.properties");) {
			Properties properties = new Properties();
			properties.load(ins);
			String controllerPackage = properties.getProperty("ControllerPackage");
			List<String> controllerClassNames = PackageUtil.getClassName(controllerPackage, false);
			for (String className : controllerClassNames) {
				Class<?> clazz = Class.forName(className);
				Object object = clazz.newInstance();
				for (Method method : clazz.getDeclaredMethods()) {
					RequestPath requestPath = method.getDeclaredAnnotation(RequestPath.class);
					ResponseType type=	requestPath.type();
			
					if (requestPath != null) {
						Action handler = new Action(method, object,type);
						map.put(requestPath.value(), handler);
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 使用单例类模式,该类的创建实例方法
	 * @author 朱矛宇
	 * @date 2018年6月13日
	 * @return
	 */
	public static ActionMapper newInstance(){
		if(mapper==null) {
			mapper=new ActionMapper();
		}
		return mapper;
	}
	
	/**
	 * 
	 * @author 朱矛宇
	 * @date 2018年6月13日
	 * @param key 请求路径.也是requestMapping中的value
	 * @return 一个handler,包含了一个action方法和执行这个方法的对象;
	 */
	public Action getAction(String path){
		return map.get(path);
	}
	
}
