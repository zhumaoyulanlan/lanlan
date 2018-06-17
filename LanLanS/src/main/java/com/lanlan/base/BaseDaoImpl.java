package com.lanlan.base;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.lanlan.mapper.ModelMapper;
import com.lanlan.mapper.ReflectModelMapper;
import com.lanlan.model.SqlParameter;
import com.lanlan.util.DBUtil;

public class BaseDaoImpl<T> implements BaseDao<T> {

	protected ModelMapper<T> mapper;
	protected DBUtil dbUtil;
	
	/**
	 * 构造函数,自动调用ReflectMapper类
	 * @param tClass
	 */
	public BaseDaoImpl(Class<T> tClass) {
		this(tClass,new ReflectModelMapper<T>(tClass));
	}

	/**
	 * 构造函数,同时设置表名与字段,若设置了tableName注解,则表名为其值,若未设置tableName注解,表名为类名全小写
	 * @param tClass
	 * @param mapper 此参数不是必须的,当不存在mapper对象时,将使用反射方式将rs转化为mode类型T
	 */
	public BaseDaoImpl(Class<T> tclass,ModelMapper<T> mapper) {
		this.mapper =mapper;
	}
	
	//chengzh@tedu.cn
	
	/**
	 * 插入一条记录到数据,数据内容按model获取
	 * @param model
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws SQLException
	 */
	public int insert(T model){

		try {
			return dbUtil.executeUpdate(mapper.getInsertSql(), mapper.getAllSqlParameterArray(model));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * 通过id删除
	 * @param id 
	 * @return -1:失败  0:影响行  >0 删除行数
	 */
	public int deleteById(Object... ids)
	{
		String sql =mapper.getDeleteByIdSql();
		SqlParameter[] parameterArray= new SqlParameter[ids.length];
		for(int i =0 ;i<ids.length;i++)
		{
			parameterArray[i]=new SqlParameter(i+1,ids[i]);
		}
		
		try {
			return dbUtil.executeUpdate(sql, parameterArray);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * 修改数据
	 * @param model
	 * @return
	 */
	public int update(T model)
	{
		String sql = mapper.getUpdateByIdSql();
		try {
			return dbUtil.executeUpdate(sql, mapper.getNoIdAndIdSqlParameterArray(model));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * 按id查询一条记录转化为model
	 * @param model
	 * @return
	 */
	public T selectById(T model) {
		String sql = mapper.getSelectByIdSql();
		try(ResultSet rs =dbUtil.executeQuery(sql, mapper.getIdSqlParameterArray(model))){
			return mapper.resultSetToModel(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	

	/**
	 * 有mapper类时可以通过mapper类将rs转化为model类型
	 * 如果未提供Mapper类,此方法自动创建使用ReflectMapper类
	 * @return
	 * @throws SQLException 
	 */
	public List<T> selectAll(){
		String sql= mapper.getSelectAllSql();
		try(ResultSet rs = dbUtil.executeQuery(sql); ){
				return  mapper.resultSetToModelList(rs);
		}catch (SQLException e) {
			e.printStackTrace();
		} 
		return new ArrayList<T>();
	}
	
	/**
	 * 分页查询
	 * @param pageindex
	 * @param pagesize
	 * @return
	 */
	public List<T> selectByPage(int pageindex,int pagesize){
		return selectByPage( pageindex, pagesize,null);
//		String sql = mapper.getSelectByPageSql();
//		int min = (pageindex-1)*pagesize;
//		min=min>0?min:0;
//		try(ResultSet rs =DBUtil.executeQuery(sql, new SqlParameter<Integer>(1, min) ,
//				new SqlParameter<Integer>(2,pagesize)))
//		{
//			return mapper.resultSetToModelList(rs);
//		} catch (SQLException e) {
//		
//			e.printStackTrace();
//		}
//		return new ArrayList<T>();
	}
	/**
	 * 按order字段 分页查询
	 * @param pageindex
	 * @param pagesize
	 * @param order 为字段名字,将直接插入到sql中  order未在此处做防止注入攻击措施.  不可由view层传入
	 * @return
	 */
	public List<T> selectByPage(int pageindex,int pagesize,String order){
		String sql = mapper.getSelectByPageSql(order);
		try(ResultSet rs =dbUtil.executeQuery(sql, new SqlParameter(1, (pagesize-1)*pageindex) ,
				new SqlParameter(2,pagesize)))
		{
			return mapper.resultSetToModelList(rs);
		} catch (SQLException e) {
		
			e.printStackTrace();
		}
		return new ArrayList<T>();
	}
	
	public String getTableName()
	{
		return mapper.getTableName();
		
	}
	
	public T resultSetToModel(ResultSet rs) {
		return mapper.resultSetToModel(rs);
	}
	@Override
	public List<T> resultSetToModelList(ResultSet rs) {
		return mapper.resultSetToModelList(rs);
	}
	
	
	
	@Override
	public int getCount() {
		String sql = mapper.getCountSql();
		try(ResultSet rs = dbUtil.executeQuery(sql)){
			if(rs.next()) {
				return rs.getInt(0);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	@Override
	public int getCount(T model ,String...fields) {
		String sql = mapper.getCoungSql(fields);
		SqlParameter[] sqlParameters = mapper.getSqlParameterArrayByField(model, 1, fields);
		try(ResultSet rs = dbUtil.executeQuery(sql,sqlParameters)){
			if(rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public T requestToModel(HttpServletRequest request) {
		return mapper.requestToModel(request);
	}
	
}
