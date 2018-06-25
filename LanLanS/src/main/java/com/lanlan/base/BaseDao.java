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
	 *通过id或者model中的id删除
	 * 当idsOrModels为id时,仅仅支持Model中只有一个id的情况,
	 * 当model中有多个值共同作为id,请传入model
	 * @param id 
	 * @return -1:失败  0:影响行  >0 删除行数
	 */
	public int deleteById(Object... ids);

	public int deleteByModelId(@SuppressWarnings("unchecked") T... model);
	/**
	 * 修改数据
	 * @param model
	 * @return
	 */
	public int update(T model);
	
	public T selectById(Object idOrModel) ;

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

	public int deleteById(String... id);

	public int deleteById(Integer... id);
}
