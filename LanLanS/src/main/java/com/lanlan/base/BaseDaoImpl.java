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

	protected Class<T> tClass;
	
	protected ModelMapper<T> mapper;
	//protected DBUtil dbUtil;
	
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
		this.tClass=tclass;
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
			return DBUtil.executeUpdate(mapper.getInsertSql(), mapper.getAllSqlParameterArray(model));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * 通过id删除
	 * 2018.06.25大幅度修改,参数改为可变参数
	 * 改为通过int或者String类型的的参数删除
	 * 同时增加了一个deleteByModelId,通过使用model类型删除
	 * 但是为了保持兼容,此处仍可以使用model参数
	 * @param id 
	 * @return -1:失败  0:影响行  >0 删除行数
	 */
	@Override
	@SuppressWarnings("unchecked")
	public int deleteById(Object... ids)	{
		try {
			int[] rs=null;
			if(ids==null||ids.length==0) {
				return Integer.MIN_VALUE;
			}

			int len = ids.length;
			//是模型,
			if(ids[0].getClass().equals(tClass)) {
				return deleteByModelId((T)ids);
			}
			
			if(ids[0].getClass().equals(int.class)||
					ids[0].getClass().equals(Integer.class)||
					ids[0].getClass().equals(String.class)) {
				String sql = mapper.getDeleteByIdSql();
				SqlParameter[][] parameterArrays =new SqlParameter[len][];
				for(int i=0;i<len;i++) {
					parameterArrays[i] =new SqlParameter[] {new SqlParameter(1, ids[i])};
				}
				rs=DBUtil.executeUpdateBatch(sql, parameterArrays);
			}
			if(rs!=null) {
				int fail=0;
				int success=0;
				for(int i:rs) {
					if(i>0){
						success=+i;
					}
					if(i<0) {
						fail=+i;
					}
				}
				if(fail>0) {
					return fail;
				}else if(success>0) {
					return success;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Integer.MIN_VALUE;
	}
	@Override
	public int deleteById(String... id) {
		try {
		if(id!=null && String.class.equals(mapper.getIdType())){
			String sql =mapper.getDeleteByIdSql();
			int len=id.length;
			SqlParameter[] parameters =new SqlParameter[len];
			for(int i=0;i<len;i++) {
				parameters[i] =new SqlParameter(i+1,id[i]);
			}
			int[] rs=  DBUtil.executeUpdateBatch(sql, parameters);
			if(rs!=null) {
				int fail=0;
				int success=0;
				for(int i:rs) {
					if(i>0){
						success=+i;
					}
					if(i<0) {
						fail=+i;
					}
				}
				if(fail>0) {
					return fail;
				}else if(success>0) {
					return success;
				}
			}
		}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	@Override
	public int deleteById(Integer... id) {
		try {
		if(id!=null && Integer.class.equals(mapper.getIdType())||int.class.equals(mapper.getIdType()) ){
			String sql =mapper.getDeleteByIdSql();
			int len=id.length;
			SqlParameter[] parameters =new SqlParameter[len];
			for(int i=0;i<len;i++) {
				parameters[i] =new SqlParameter(i+1,id[i]);
			}
			int[] rs=  DBUtil.executeUpdateBatch(sql, parameters);
			if(rs!=null) {
				int fail=0;
				int success=0;
				for(int i:rs) {
					if(i>0){
						success=+i;
					}
					if(i<0) {
						fail=+i;
					}
				}
				if(fail>0) {
					return fail;
				}else if(success>0) {
					return success;
				}
			}
		}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * 通过id删除
	 * @param model
	 * @return -1:失败  0:影响行  >0 删除行数
	 */
	@Override
	public int deleteByModelId(@SuppressWarnings("unchecked") T... model) {
		try {
			int[] rs=null;
			if(model==null||model.length==0) {
				return -1;
		}
		//是模型
		int len = model.length;
		if(model[0].getClass().equals(tClass)) {
			String sql = mapper.getDeleteByIdSql();
			SqlParameter[][] parameterArrays =new SqlParameter[len][];
			for(int i=0;i<len;i++) {
				parameterArrays[i] =mapper.getIdSqlParameterArray((T)model[i]);
			}
			rs=DBUtil.executeUpdateBatch(sql, parameterArrays);
		}if(rs!=null) {
			int fail=0;
			int success=0;
			for(int i:rs) {
				if(i>0){
					success=+i;
				}
				if(i<0) {
					fail=+i;
				}
			}
			if(fail>0) {
				return fail;
			}else if(success>0) {
				return success;
			}
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
		return Integer.MIN_VALUE;
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
			return DBUtil.executeUpdate(sql, mapper.getNoIdAndIdSqlParameterArray(model));
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
	@SuppressWarnings("unchecked")
	public T selectById(Object idOrModel) {
		if(idOrModel==null) {
			return null;
		}
		
		String sql = mapper.getSelectByIdSql();
		if(idOrModel.getClass().equals(tClass)) {
			try(ResultSet rs =DBUtil.executeQuery(sql, mapper.getIdSqlParameterArray((T)idOrModel))){
				return mapper.resultSetToModel(rs);
			} catch (SQLException e) {
				e.printStackTrace();
			} 
		}
		
		if(int.class.equals(tClass)
				||Integer.class.equals(tClass)
				||String.class.equals(tClass)) {
			
			try(ResultSet rs =DBUtil.executeQuery(sql,new SqlParameter(1,idOrModel))){
				return mapper.resultSetToModel(rs);
			} catch (SQLException e) {
				e.printStackTrace();
			} 
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
		try(ResultSet rs = DBUtil.executeQuery(sql); ){
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
		try(ResultSet rs =DBUtil.executeQuery(sql, new SqlParameter(1, (pageindex-1)*pagesize) ,
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
		try(ResultSet rs = DBUtil.executeQuery(sql)){
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
		try(ResultSet rs = DBUtil.executeQuery(sql,sqlParameters)){
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
