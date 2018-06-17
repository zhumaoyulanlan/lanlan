package com.lanlan.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lanlan.annotation.ResponseType;

/**
 * 封装了action方法和执行action方法的一个对象
 * @author 朱矛宇
 * @date 2018年6月13日
 */
public class Action {
	private Method atcionMethod;
	private Object controller;
	private ResponseType responseType;
	
	public Action() {

	}

	/**
	 * Action 执行
	 * @param request
	 * @param response
	 * @return
	 */
	public String execute(HttpServletRequest request,HttpServletResponse response) {
		try {
			return (String)atcionMethod.invoke(controller,request, response);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	public Action(Method atcionMethod,Object controller,ResponseType responseType) {
		this.atcionMethod=atcionMethod;
		this.controller=controller;
		this.responseType=responseType;
	}

	public Method getAtcion() {
		return atcionMethod;
	}

	public void setAtcion(Method atcionMethod) {
		this.atcionMethod = atcionMethod;
	}

	public Object getController() {
		return controller;
	}

	public void setController(Object controller) {
		this.controller = controller;
	}

	public ResponseType getResponseType() {
		return responseType;
	}

	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}

	
}
