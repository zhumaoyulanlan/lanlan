package com.lanlan.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lanlan.annotation.ResponseType;
import com.lanlan.mapper.ActionMapper;
import com.lanlan.model.Action;

/**
 * Servlet implementation class DispatcherServlet 
 * @author 朱矛宇
 * @date 2018年6月13日
 */
public abstract class DispatcherServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DispatcherServlet() {
		super();
	}
    
	/**
	 * 创建一个handlerMapper,根据不同的请求地址,从handlerMapper取得不同的Handler,并执行
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			//解析请求路径,调用相应的controller.
			String url =request.getRequestURI();
			String path =url.substring(request.getContextPath().length());
			ActionMapper actionMapper = ActionMapper.newInstance();
			Action action= actionMapper.getAction(path);
			if(action!=null) {
				String result =action.execute(request, response);
				if(action.getResponseType()==ResponseType.Dispatcher) {
					//转发
					request.getRequestDispatcher(result).forward(request, response);
					return;
				}else if(action.getResponseType()==ResponseType.Redirect) {
					//重定向
					response.sendRedirect(result);
					return;
				}
			}else {
				redirectTo404Page(request,response);
				return;
			}
		}catch(Exception e){
			e.printStackTrace();
			redirectToErrorPage(request,response);
		}
	}
	
	/**
	 * 子类需要重写此方法,当发生错误时如何跳转到错误页
	 * @author 朱矛宇
	 * @date 2018年6月14日
	 * @param request
	 * @param response
	 */
	protected abstract void redirectToErrorPage(HttpServletRequest request, HttpServletResponse response);
	
	protected abstract void redirectTo404Page(HttpServletRequest request, HttpServletResponse response);

	
	
}
