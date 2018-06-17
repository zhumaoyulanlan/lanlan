package com.lanlan.base;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;



public interface BaseDao<T> {
	
	/**
	 * 
	 * @param model
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws SQLException
	 */
	public int insert(T model);
	
	/**
	 * 通过id删除
	 * @param id 
	 * @return -1:失败  0:影响行  >0 删除行数
	 */
	public int deleteById(Object... ids);

	/**
	 * 修改数据
	 * @param model
	 * @return
	 */
	public int update(T model);
	
	public T selectById(T model) ;

	/**
	 * 有mapper类时可以通过mapper类将rs转化为model类型
	 * 如果未提供Mapper类,此方法自动创建使用ReflectMapper类
	 * @return
	 * @throws SQLException 
	 */
	public List<T> selectAll();
	
	public List<T> selectByPage(int pageindex,int pagesize);
	
	public List<T> selectByPage(int pageindex,int pagesize,String order);
	
	public String getTableName();
	
	public T resultSetToModel(ResultSet rs) ;
	
	public int getCount() ;
	public int getCount(T model ,String...fields);

	List<T> resultSetToModelList(ResultSet rs);
	public T requestToModel(HttpServletRequest request);
}
