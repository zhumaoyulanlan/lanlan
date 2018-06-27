package com.lanlan.base;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.lanlan.mapper.ModelMapper;
import com.lanlan.mapper.ReflectModelMapper;
import com.lanlan.model.SqlParameter;
import com.lanlan.util.DBUtil;

public abstract class  BaseDaoImpl<T> implements BaseDao<T> {

	protected Class<T> tClass;
	
	protected ModelMapper<T> mapper;
	
	/**
	 * 构造函数(建议使用)
	 * 自动调用ReflectMapper类
	 * @param tClass
	 */
	public BaseDaoImpl(Class<T> tClass) {
		this(tClass,new ReflectModelMapper<T>(tClass));
	}

	/**
	 * 有参构造函数(不推荐使用)
	 * 同时设置表名与字段,若设置了tableName注解,则表名为其值,若未设置tableName注解,表名为类名全小写
	 * @param tClass
	 * @param mapper 此参数不是必须的,当不存在mapper对象时,将使用反射方式将rs转化为mode类型T
	 */
	public BaseDaoImpl(Class<T> tclass,ModelMapper<T> mapper) {
		this.mapper =mapper;
		this.tClass=tclass;
	}

	/**
	 * 获得一个selectAllSql语句(非常重要的方法)
	 * selectAll, selectByPage ,count 都通过此方法获取基础sql语句
	 *  多表查询时只需重写此方法
	 * @return
	 */
	protected String getSelectAllSql() {
		return mapper.getSelectAllSql();
	}
	
	
	/**
	 * 插入一条记录到数据,数据内容按model获取
	 * @param model
	 * @return
	 */
	@Override
	public int insert(@SuppressWarnings("unchecked") T... model) {
		if(model==null||model[0]==null) {
			return 0;
		}
		int len = model.length;
		SqlParameter[][] parameterArrays = new SqlParameter[len][];
		for(int i=1;i<len;i++) {
			parameterArrays[i]=mapper.getAllSqlParameterArray(model[i]);
		}
		try {
			int[] rs = DBUtil.executeUpdateBatch(mapper.getInsertSql(),parameterArrays);
			return CountResult(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
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
	public int deleteById(Serializable... id)	{
		try {
			if(id==null||id.length==0) {
				return 0;
			}
			int len = id.length;
			
			//对原框架做兼容
			if(id[0].getClass().equals(tClass)) {
				return deleteById((T)id);
			}
			
			//正常执行	
			if(id.getClass().equals(mapper.getIdType())) {
				String sql = mapper.getDeleteByIdSql();
				SqlParameter[][] parameterArrays =new SqlParameter[len][];
				for(int i=0;i<len;i++) {
					parameterArrays[i] =new SqlParameter[] {new SqlParameter(1, id[i])};
				}
				int[]rs=DBUtil.executeUpdateBatch(sql, parameterArrays);
				return CountResult(rs);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Integer.MIN_VALUE;
	}

	/**
	 * 通过model
	 * @param model
	 * @return -1:失败  0:影响行  >0 删除行数
	 */
	@Override
	public int deleteById(@SuppressWarnings("unchecked") T... model) {
		try {
			int[] rs=null;
			if(model==null||model.length==0) {
				return -1;
			}
			int len = model.length;
			String sql = mapper.getDeleteByIdSql();//获取sql语句
			SqlParameter[][] parameterArrays =new SqlParameter[len][];
			for(int i=0;i<len;i++) {//获取每个模型参数
				parameterArrays[i] =mapper.getIdSqlParameterArray((T)model[i]);
			}
			rs=DBUtil.executeUpdateBatch(sql, parameterArrays);
			return CountResult(rs);
	} catch (SQLException e) {
		e.printStackTrace();
	}
		return 0;
	}
	
	
	/**
	 * 修改数据
	 */
	public int update(@SuppressWarnings("unchecked") T... model) {
		if(model==null||model[0]==null) {
			return 0;
		}
		int len = model.length;
		SqlParameter[][] parameterArrays = new SqlParameter[len][];
		for(int i=1;i<len;i++) {
			parameterArrays[i]=mapper.getNoIdAndIdSqlParameterArray(model[i]);
		}
		try {
			int[] rs = DBUtil.executeUpdateBatch(mapper.getUpdateByIdSql(),parameterArrays);
			return CountResult(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 查询所有
	 */
	@Override
	public List<T> selectAll(){
		String sql= getSelectAllSql();
		try(ResultSet rs = DBUtil.executeQuery(sql); ){
				return  mapper.resultSetToModelList(rs);
		}catch (SQLException e) {
			e.printStackTrace();
		} 
		return new ArrayList<T>();
	}
	
	/**
	 * 分页查询
	 */
	@Override
	public List<T> selectByPage(int pageindex,int pagesize){
		String sql = getSelectAllSql()+" limit ?,? ";
		int min = (pageindex-1)*pagesize;
		min=min>0?min:0;
		try(ResultSet rs =DBUtil.executeQuery(sql, new SqlParameter(1, min) ,
				new SqlParameter(2,pagesize)))
		{
			return mapper.resultSetToModelList(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<T>();
	}
	
	/**
	 * 按id查询一条记录转化为model
	 * @param model
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T selectById(Serializable id) {
		if(id==null) {
			return null;
		}
		
		String sql = mapper.getSelectByIdSql();
		if(id.getClass().equals(tClass)) {
			try(ResultSet rs =DBUtil.executeQuery(sql, mapper.getIdSqlParameterArray((T)id))){
				return mapper.resultSetToModel(rs);
			} catch (SQLException e) {
				e.printStackTrace();
			} 
		}
		
		if(int.class.equals(tClass)
				||Integer.class.equals(tClass)
				||String.class.equals(tClass)) {
			
			try(ResultSet rs =DBUtil.executeQuery(sql,new SqlParameter(1,id))){
				return mapper.resultSetToModel(rs);
			} catch (SQLException e) {
				e.printStackTrace();
			} 
		}

		return null;
	}

	 /**
	  * 查询
	 * @param model model中存有要查找的id
	 * @return
	 */
	public T selectById(T model) {
		String sql = mapper.getSelectByIdSql();
		try(ResultSet rs =DBUtil.executeQuery(sql, mapper.getIdSqlParameterArray(model))){
			return mapper.resultSetToModel(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	@Override
	public T resultSetToModel(ResultSet rs) {
		return mapper.resultSetToModel(rs);
	}
	
	@Override
	public List<T> resultSetToModelList(ResultSet rs) {
		return mapper.resultSetToModelList(rs);
	}
	
	@Override
	public T requestToModel(HttpServletRequest request) {
		return mapper.requestToModel(request);
	}

	@Override
	public int getCount() {
		String sql= "select count(*) from ("+getSelectAllSql()+")";
		try(ResultSet rs = DBUtil.executeQuery(sql)){
			if(rs.next()) {
				return rs.getInt(0);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * 获取表名
	 */
	public String getTableName()
	{
		return mapper.getTableName();
	}
	
	
	/**
	 * 对批量处理的结果进行计数,有失败则返回负数
	 * @param rs
	 * @return
	 */
	protected static int CountResult(int[] rs) {
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
		return 0;
	}


}
