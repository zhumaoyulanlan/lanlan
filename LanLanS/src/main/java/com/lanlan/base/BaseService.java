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
	 * 通过id或者model中的id删除
	 * 当idsOrModels为id时,仅仅支持Model中只有一个id的情况,
	 * 当model中有多个值共同作为id,请传入model
	 * @param idsOrModels
	 * @return 负数:失败行数  正数:全部成功, 删除行数
	 */
	public int deleteById(Object... idsOrModels);


	
	
	/**
	 * 修改数据
	 * @param model
	 * @return
	 */
	public boolean update(T model);
	


	/**
	 * 自动判断参数若是model,取model中的id删除
	 * 若是String 或是Integer类型,只支持model中只有一个id时才可用
	 * @param idsOrModles
	 * @return
	 */
	public T selectById(Object idOrModle) ;

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
