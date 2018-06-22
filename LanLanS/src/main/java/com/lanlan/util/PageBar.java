package com.lanlan.util;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;


import com.lanlan.base.BaseService;

public class PageBar {
	
	public static <T> String getPageBar(HttpServletRequest request,BaseService<T> service,int pageSize, T model ,String...fields) { 
		String baseSrc=request.getRequestURI();
		int last= baseSrc.lastIndexOf("?");
		if(last>0) {
			baseSrc = baseSrc.substring(0,last);
		}

		int pageIndex=getPageIndex(request);
			
		int allcount =-1;
		if(fields!=null) {
			allcount=service.getCount(model,fields);
		}else{
			allcount=service.getCount();
		}
		StringBuffer sb= new StringBuffer();
		
		int minPage=1;
		int maxPage=allcount>1?(allcount-1)/pageSize+1:1;
		int beginPage=pageIndex-5;
		int endPage=pageIndex+5;
		if(beginPage<0) {
			beginPage=1;
			endPage=Math.min(beginPage+11,maxPage);
		}else if(endPage>maxPage) {
			endPage=maxPage;
			beginPage=Math.max(minPage, maxPage-11);
		}
		
		sb.append("<a class='pageBar' href='"+baseSrc+"?pageIndex="+minPage+"'>首页<a/>");
		
		for(int i=beginPage; i<=endPage;i++) {
			if(i==pageIndex) {
				sb.append("<span class='pageBar'>["+pageIndex+"]</span>");
			}
			else {
				sb.append("<a class='pageBar' href='"+baseSrc+"?pageIndex="+i+"'>"+i+"<a/>");			
			}
		}
		sb.append("<a class='pageBar' href='"+baseSrc+"?pageIndex="+maxPage+"'>尾页<a/>");
		return sb.toString();
	}
	
	public static  String getPageBar(HttpServletRequest request ,BaseService<?> service ,int pageSize) { 
		return getPageBar(request,service,pageSize,null);
	}
	
	
	public static int getPageIndex(HttpServletRequest request) {
		String pageIndexStr= request.getParameter("pageIndex");
		int pageIndex=1;
		if(pageIndexStr==null||"".equals(pageIndexStr)) {
			pageIndex=1;
		}
		else {
			pageIndex = Integer.parseInt(pageIndexStr);
		}
		return pageIndex;
	}
	
	public static int getPageSize(HttpServletRequest request) {
		ServletContext  context = request.getSession().getServletContext();

		Integer pageSize = (Integer)context.getAttribute("globalPageSize");
		try {
			if(pageSize==null) {
				Properties properties =new Properties();//globe
				properties.load(CommonUtil.getResourceAsStream("config/pageSize.properties"));
				pageSize= Integer.parseInt(properties.getProperty("globalPageSize"));
				context.setAttribute("globalPageSize", pageSize);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return 10;
		}
		return pageSize;
	}

	public static int getPageSize(HttpServletRequest request,String key) {
		ServletContext  context = request.getSession().getServletContext();
		Integer pageSize = (Integer)context.getAttribute(key);
		try {
			if(pageSize==null) {
				Properties properties =new Properties();//globe
				properties.load(CommonUtil.getResourceAsStream("pageSize.properties"));
				pageSize= Integer.parseInt(properties.getProperty(key));
				context.setAttribute(key, pageSize);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return getPageSize(request);
		}
		return pageSize;
	}
	
	
}
