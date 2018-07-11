package com.lanlan.base;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.lanlan.util.Count;

public interface BaseService<T> extends Count{

	/**
	 *  插入
	 * @param model
	 * @return
	 */
	public boolean insert(@SuppressWarnings("unchecked") T... model);
	

	/***
	 *  删除
	 * @param model 存有要删除的id 的model
	 * @return
	 */
	public int deleteById(@SuppressWarnings("unchecked") T... model);
	
	/**
	 * 	删除
	 * @param id (允许传入model,兼容上个版本)
	 * @return
	 */
	public int deleteById(Serializable... id);


	/**
	 * 修改数据
	 * @param model
	 * @return
	 */
	public boolean update(@SuppressWarnings("unchecked")T... model);

	/**
	 * 查找数据
	 * @return
	 */
	public List<T> selectAll();
	
	/**
	 * 分页查询
	 * @param pageindex 页面索引
	 * @param pagesize 页面大小
	 * @return
	 */
	public List<T> selectByPage(int pageindex,int pagesize);
	
	/**
	 * 查找数据
	 * @param id 兼容上个版本使用model
	 * @return
	 */
	public T selectById(Serializable id);
	
	/**
	 * 查找数据
	 * @param model
	 * @return
	 */
	public T selectById(T model);
	
	/**
	 * 获取表名
	 * @return
	 */
	public String getTableName();
	
	public T resultSetToModel(ResultSet rs) ;
	public T requestToModel(HttpServletRequest request) ;
	public List<T> resultSetToModelList(ResultSet rs) ;
	
	public int getCount() ;




	
	
}
