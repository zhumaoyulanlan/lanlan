package com.lanlan.base;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface BaseService<T> {

	/**
	 * 
	 * @param model
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws SQLException
	 */
	public boolean insert(T model);
	
	/**
	 * 通过id删除
	 * @param id 
	 * @return -1:失败  0:影响行  >0 删除行数
	 */
	public boolean deleteById(Object... ids);

	/**
	 * 修改数据
	 * @param model
	 * @return
	 */
	public boolean update(T model);
	
	public T selectById(T model) ;

	/**
	 * 有Modelmapper类时可以通过mapper类将rs转化为model类型
	 * 如果未提供Mapper类,此方法自动创建使用ReflectModleMapper类
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
}
