package com.lanlan.base;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;



public interface BaseDao<T> {
	
	
	/** 插入数据
	 * @param model
	 */
	public int insert(@SuppressWarnings("unchecked") T... model);
	
	/**
	 * 删除(兼容model)
	 *通过id或者model中的id删除
	 * 当idsOrModels为id时,仅仅支持Model中只有一个id的情况,
	 * 当model中有多个值共同作为id,请传入model
	 * @param id 
	 * @return 负数:失败次数  0:执行异常或无影响行  >0 影响行数,且全部成功
	 */
	public int deleteById(Serializable... id);

	/**
	 * 删除
	 * @param model
	 * @return
	 */
	public int deleteById(@SuppressWarnings("unchecked") T... model);
	/**
	 * 修改数据
	 * @param model
	 * @return
	 */
	public int update(@SuppressWarnings("unchecked") T... model);
	
	/**
	 * 查找全部
	 * 有mapper类时可以通过mapper类将rs转化为model类型
	 * 如果未提供Mapper类,此方法自动创建使用ReflectMapper类
	 * @return
	 * @throws SQLException 
	 */
	public List<T> selectAll();
	
	/**
	 * 分页查询
	 * @param pageindex
	 * @param pagesize
	 * @return
	 */
	public List<T> selectByPage(int pageindex,int pagesize);
	
	/**
	 * 按id查询(兼容model)
	 * @param id
	 * @return
	 */
	public T selectById(Serializable id);
	
	/**
	 * 查询
	 * @param model model中存有要查找的id
	 * @return
	 */
	public T selectById(T model);
	
	/**
	 * 获取表名
	 * @return
	 */
	public  String getTableName();
	
	/**
	 * 将resultSet转为单个model
	 * @param rs
	 * @return
	 */
	public T resultSetToModel(ResultSet rs) ;
	
	/**
	 * 将resultSet转为一个List<T>
	 * @param rs
	 * @return
	 */
	List<T> resultSetToModelList(ResultSet rs);
	
	/**
	 * 从request的请求参数中封装一个model
	 * @param request
	 * @return
	 */
	public T requestToModel(HttpServletRequest request);

	/**
	 * 计数
	 * 一般用于分页计算,sql语句条件同selectAll()
	 * @return
	 */
	public int getCount() ;


}
