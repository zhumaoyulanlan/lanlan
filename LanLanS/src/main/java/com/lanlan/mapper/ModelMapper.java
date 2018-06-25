package com.lanlan.mapper;

import java.sql.ResultSet;
import java.util.List;

import javax.servlet.ServletRequest;

import com.lanlan.model.SqlParameter;


/**
 	* 数据库结果与模型转化类
 	*  提供与数据库相对应的 T model 的从ResultSet及简单sql语句生成 
 * @author 朱矛宇
 * @date 2018年6月15日
 * @param <T> Model javaBean类型
 */
public interface ModelMapper<T> {
	
	/**
	 * 获取表名
	 * @return
	 */
	public String getTableName();
	
	/**
	 * 将ResultSet对象转化成List<T>集合,实现时要注意rs关闭,以及statam 和Connection的关闭
	 * @param rs
	 * @return
	 */
	public List<T> resultSetToModelList(ResultSet rs) ;
	
	/**
	 * 将ResultSet对象转化成T类型的单个Model,
	 * ps:实现时要注意rs关闭,以及PreparedStatement 和Connection的关闭
	 * @param rs 可以转化成T类型 的Result
	 * @return T类型的Mole
	 */
	public T resultSetToModel(ResultSet rs);
	
	/**
	 * 获取一个SqlParameter数组,其中包含了全部字段的参数,参数值根据model生成
	 * @param model
	 * @return
	 */
	public SqlParameter[] getAllSqlParameterArray(T model);
	
	/**
	 * 获取一个SqlParameter数组,其中包含了全部字段的参数, 其顺序为 非id字段,id字段. 参数值根据model生成
	 * 此数组主要用于update  sql语句的参数赋值
	 * @param model
	 * @return
	 */
	public SqlParameter[] getNoIdAndIdSqlParameterArray(T model);
	
	/**
	 * 获取一个SqlParameter数组,其中包含了全部id字段的参数. 参数值根据model生成
	 * 此数组主要用于selectById  sql语句的参数赋值
	 * @param model
	 * @return
	 */
	public SqlParameter[] getIdSqlParameterArray(T model);
	
	/**
	 * 获取自动生成的插入sql语句
	 * @return
	 */
	public String getInsertSql();
	
	/**
	 * 获取自动生成的按主键删除的sql语句
	 * @return
	 */
	public String getDeleteByIdSql();
	
	/**
	 * 获取自动生成的按id查找的sql语句
	 * @return
	 */
	public String getSelectByIdSql();
	
	/**
	 * 获取查找全部数据的sql语句
	 * @return
	 */
	public String getSelectAllSql();
	
	/**
	 * 获取分页查找的sql语句
	 * @return
	 */
	public String getSelectByPageSql();
	
	/**
	 * 获取 update by id 的语句
	 * @return
	 */
	public String getUpdateByIdSql();
	
	/**
	 * 获取了分页查找的sql语句 并指定了按照排序的字段名称
	 * @param orderBy
	 * @return
	 */
	public String getSelectByPageSql(String orderBy);

	public String getCountSql();
	
	public String getCoungSql(String... fields);

	public SqlParameter[] getSqlParameterArrayByField(T model, int beginIndex, String... fields);
	
	/**
	 *  从request请求参数封装成一个model类型
	 * @param request
	 * @return
	 */
	public T requestToModel(ServletRequest request) ;
	
	/**
	 * 当仅有一个id值时返回id类型,否则返回null
	 * @return
	 */
	public Class<?> getIdType() ;
}
