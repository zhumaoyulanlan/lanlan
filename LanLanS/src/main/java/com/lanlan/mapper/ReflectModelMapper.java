package com.lanlan.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletRequest;

import com.lanlan.annotation.ID;
import com.lanlan.annotation.OrderBy;
import com.lanlan.annotation.OrderByType;
import com.lanlan.annotation.TableName;
import com.lanlan.model.SqlParameter;
import com.lanlan.util.CommonUtil;


/**
 *  通过反射实现的Mapper类,public方法功能参照Mapper接口
 * 允许多字段id
 * 传入参数orderby做了防注入,orderby字段与model字段一致时(大小写不敏感),才会被采用,否则将不使用排序规则
 * 当mysql开启大小写敏感时,要求T model 字段与数据库保持一致
 * @author 朱矛宇
 * @date 2018年6月15日
 * @param <T>
 */
public class ReflectModelMapper<T> implements ModelMapper<T> {
	
	/**
	 * 是否使用用户自定义的tableName 转ModelName方法. 
	 * 当使用传入TableNameToModelNameHandle参数的构造方法时,该值自动设为true
	 */
	private boolean isUserDefinedTableNameToModelName=false;
	
	/**
	 * T类的class
	 */
	protected Class<T> tClass;
	/**
	 * 表名,如有注解 按着注解,如无注解,将调用modelToTableName方法生成表名,如有必要可在子类中重写此方法
	 */
	protected String tableName=null;
	
	/**
	 * id字段
	 */
	protected Field[] idFieldArray=null;  
	
	/**
	 * 非id字段
	 */
	protected Field[] noIdFieldArray=null;
	
	/**
	 * 非id字段+id字段  ,保留顺序
	 */
	protected Field[] noIdFieldAndIdFieldArry=null;
	
	/**
	 * 全部字段,默认顺序
	 */
	protected Field[] allFieldArray=null;
	
	/**
	 * 分页默认排序字段,若model 中没有 orderBy 注解,自动设为第一个id值(id顺序参照T mode 代码顺序)
	 */
	protected Field orderByField=null;
	
	
	/**
	 * 默认排序字段的排序方式,正序与倒序
	 * @see OrderByType
	 */
	protected OrderByType orderByType=null;
	
	/**
	 * delete   sql语句
	 */
	protected String deletByidSql=null;
	
	
	/**
	 * selectById sql语句
	 */
	protected String selectByIdSql=null;
	
	/**
	 *  selectAll sql语句
	 */
	protected String selectAllSql =null;
	
	/**
	 *  selectAllByPage sql语句
	 */
	protected String selectByPageSql =null;
	
	/**
	 * updateById  sql语句
	 */
	protected String updateByIdSql=null;

	/**
	 * insert sql  类似 "insert tableName(PartitionName,PartitionName...) values(?,?...)" 的字符串
	 * 	通过getTableNameAndPartitionName方法获取,根据tClass 的name 和get方法 生成
	 */
	private String insertSql=null;
	
	
	/**
	 * 构造函数必须传入一个T类型的class
	 * 因要求子类对 tClass赋值,不提供无参构造函数
	 * @param tClass
	 */
	public ReflectModelMapper(Class<T> tClass) {
		this.tClass=tClass;
		allFieldArray=tClass.getDeclaredFields();
		
		List<Field> idFieldList = new ArrayList<>();
		List<Field> noIdfieldList = new ArrayList<>();
		List<Field> noIdFieldAndIdFieldList = new ArrayList<>();	
		//获取表名
		if(tClass.isAnnotationPresent(TableName.class)) {
			TableName name= tClass.getAnnotation(TableName.class);
			tableName=name.value();
		}else
		{
			//tableName=modelToTableName(tClass.getSimpleName());//不在使用此转化函数
			String exceptionStr ="Can't find annotation @id in ["+tClass.getName()+"],please check it"
					+"在["+tClass.getName()+"]类中找不到@id注解,请检查该类." ;
			RuntimeException e = new RuntimeException(exceptionStr);
			e.printStackTrace();
			throw(e);
		}
		//获取id和非id字段
		for(Field field : allFieldArray)
		{
			if(field.isAnnotationPresent(ID.class)){
				idFieldList.add(field);
			}else
			{
				noIdfieldList.add(field);
			}
			
			if(field.isAnnotationPresent(OrderBy.class)) {
				if(orderByField==null) {
					orderByField = field;
					orderByType = field.getAnnotation(OrderBy.class).value();
				}
			}
		}
		//当没有id注解时,查找名为id的字段
		if(idFieldList.size()==0) {
			for(Field field : allFieldArray)
			{
				if(field.getName().toLowerCase().equals("id")) {
					idFieldList.add(field);
					noIdfieldList.remove(field);
				}
			}
		}
		//如果仍找不到id,抛出异常
		if(idFieldList.size()==0) {
			String exceptionStr="can't found id field or id annotation,please check model class["+tClass.getName()
			+"]\n找不到名为id的字段,也找不到id注解,请检查模型类["+tClass.getName()+"]";
			Exception e = new Exception(exceptionStr);
			e.printStackTrace();
			throw new RuntimeException();
		}
		//找不到orderBy注解时,使用id代替分页排序
		if(orderByField==null) {
			orderByField = idFieldList.get(0);
			orderByType = OrderByType.ASC;
		}
		//转成array,并赋值
		idFieldArray=idFieldList.toArray(new Field[idFieldList.size()]);
		noIdFieldArray=noIdfieldList.toArray(new Field[noIdfieldList.size()]);
		noIdFieldAndIdFieldList.addAll(noIdfieldList);
		noIdFieldAndIdFieldList.addAll(idFieldList);
		noIdFieldAndIdFieldArry=noIdFieldAndIdFieldList.toArray(new Field[noIdFieldAndIdFieldList.size()]);
	}
	

	
	/**
	 * sql表名转mode类名规则
	 * 默认为Model名首字母改小写,如果没有标注tableName注解需要重写此方法
	 * @param simpleName
	 * @return
	 */
	protected String modelToTableName(String simpleName) {
		return tClass.getSimpleName().toLowerCase();
	}



	/**
	 * 按需要字段获取sqlparameter,此方法,参数从1开始设置
	 * @param fields 需要生成参数的字段
	 * @param model sqlparameter的值将从model获取
	 * @return
	 */
	private SqlParameter[] getSqlParameterArrayByField(Field[] fields, T model )
	{
		return getSqlParameterArrayByField(fields,model,1);
	}
	
	/**
	 * 通过model 和传入的 字段(field)  创建出连续的参数, 并可以指定参数的开始索引
	 * @param model
	 * @param beginIndex
	 * @param fields
	 * @return
	 */
	@Override
	public SqlParameter[] getSqlParameterArrayByField(T model,int beginIndex,String... fields ){
		if(fields==null||fields.length==0) {
			return null;
		}
		List<Field> targetFields=new ArrayList<>();
		for(Field field :allFieldArray) {
			for(String fieldstr :fields) {
				if(field.getName().toLowerCase().equals(fieldstr.toLowerCase())) {
					targetFields.add(field);
				}
			}
		}
		Field[] fieldArray= new Field[targetFields.size()];
		fieldArray=targetFields.toArray(fieldArray);
		if(targetFields.size()!=0) {
			return getSqlParameterArrayByField(fieldArray,model,beginIndex);
		}
		return null;
	}
	
	
	/**
	 * 按需要字段获取sqlparameter
	 * @param fields  需要生成参数的字段
	 * @param model sqlparameter的值将从model获取
	 * @param beginNum 设置参数的开始
	 * @return
	 */
	private SqlParameter[] getSqlParameterArrayByField(Field[] fields, T model,int beginNum ) {
		List<SqlParameter> list =new ArrayList<SqlParameter>(); 
		Method method =null;
		Field field=null;
		Class<?> type=null;
		try {
			for (int i =0 ;i<fields.length;i++ ) {
				field=fields[i];
				method= tClass.getMethod("get"+CommonUtil.upperCaseFirst(field.getName()));
				type= field.getType();
				if(type == String.class) {
					list.add(new SqlParameter(i+beginNum,(String)method.invoke(model)));
				}else if(type == int.class ||type == Integer.class){
					list.add(new SqlParameter(i+beginNum,(Integer)method.invoke(model)));
				}else if(type == float.class ||type == Float.class){
					list.add(new SqlParameter(i+beginNum,(Float)method.invoke(model)));
				}else if(type == double.class ||type == Double.class){
					list.add(new SqlParameter(i+beginNum,(Double)method.invoke(model)));
				}else if(type == boolean.class ||type == Boolean.class){
					list.add(new SqlParameter(i+beginNum,(Boolean)method.invoke(model)));
				}else if(type == long.class ||type == Long.class){
					list.add(new SqlParameter(i+beginNum,(Long)method.invoke(model)));
				}else if(type == byte.class ||type == Byte.class){
					list.add(new SqlParameter(i+beginNum,(Byte)method.invoke(model)));
				}else if(type == Date.class){
					list.add(new SqlParameter(i+beginNum,(Date)method.invoke(model)));
				}else if(type == Time.class){
					list.add(new SqlParameter(i+beginNum,(Time)method.invoke(model)));
				}else if(type == BigDecimal.class){
					list.add(new SqlParameter(i+beginNum,(BigDecimal)method.invoke(model)));
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return list.toArray(new SqlParameter[list.size()]);
	}
	
	/**
	 * 将ResultSet对象转化程model
	 */
	@Override
	public List<T> resultSetToModelList(ResultSet rs) {	
		List<T> list= new ArrayList<T>();
		try {
				while(rs.next()) {
					T model=tClass.newInstance();
					//给model复制
					for (Field field : allFieldArray) {
						Class<?> type = field.getType();
						String fieldName = field.getName();
						Object value=null; 
						if(type == String.class) {
							value=rs.getString(fieldName);
						}else if(type == int.class ||type == Integer.class){
							value=rs.getInt(fieldName);
						}else if(type == float.class ||type == Float.class){
							value=rs.getFloat(fieldName);
						}else if(type == double.class ||type == Double.class){
							value=rs.getDouble(fieldName);
						}else if(type == boolean.class ||type == Boolean.class){
							value=rs.getBoolean(fieldName);
						}else if(type == long.class ||type == Long.class){
							value=rs.getLong(fieldName);
						}else if(type == byte.class ||type == Byte.class){
							value=rs.getByte(fieldName);
						}else if(type == Date.class){
							value=rs.getDate(fieldName);
						}else if(type == Time.class){
							value=rs.getTime(fieldName);
						}else if(type == BigDecimal.class){
							value=rs.getBigDecimal(fieldName);
						}
						tClass.getMethod("set"+CommonUtil.upperCaseFirst(fieldName),type).invoke(model, value);
					}
					list.add(model);
				}
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		return list;
	}
	
	/**
	 * isnert 使用的设置参数  根据mode对对象生成sqlParameter
	 */
	@Override
	public SqlParameter[] getAllSqlParameterArray(T model)  {
		return getSqlParameterArrayByField(allFieldArray,model);
	}
	
	
	/**
	 *  update 使用的设置参数 根据mode对对象生成非id部分的sqlParameter
	 */
	@Override
	public SqlParameter[] getNoIdAndIdSqlParameterArray(T model) {
		return getSqlParameterArrayByField(noIdFieldAndIdFieldArry,model);
	}
	
	/**
	 * dele 和findById 使用的设置参数  根据mode对对象生成id部分的sqlParameter
	 */
	@Override
	public SqlParameter[] getIdSqlParameterArray(T model) {
		return getSqlParameterArrayByField(idFieldArray,model);
	}

	
	/**
	 * ResultSet对象第一条记录转化为model
	 */
	@Override
	public T resultSetToModel(ResultSet rs) {
		List<T> list=resultSetToModelList(rs);
		if(list.size()>0) {
			return list.get(0);
		}
		return null;
	}

	
	/**
	 * 获取 "insert tableName(PartitionName,PartitionName...) values(?,?...)" 的insert sql字符串
	 * 设置参数使用同一对象的modelToSqlParameter方法可以保持顺序一致;
	 * @return
	 */@Override
	public String getInsertSql() {
		if(insertSql==null) {
			StringBuffer sb =new StringBuffer();
			sb.append("insert into ").append(tableName)
			.append("(").append(joinFieldName("",",",allFieldArray)).append(")").append(" ")
			.append("values(")
			.append(joinPlaceholder(allFieldArray.length)).append(")");
			insertSql=sb.toString();
		}
		return insertSql;
	}
	
	
	/**
	 * 获取 "delete tableName where id1=? and id2=?" 的delete sql字符串
	 */
	@Override
	public String getDeleteByIdSql() {
		if(deletByidSql==null) {
			StringBuffer sb =new StringBuffer();
			sb.append("delete from ").append(tableName)
			.append(" where ").append(joinFieldName("=?","and",idFieldArray));
			deletByidSql=sb.toString();
		}
		return deletByidSql;
	}
	
	/**
	 *  获取 "Select * from  tableName where id1=? and id2=?" 的delete sql字符串
	 */
	@Override
	public String getSelectByIdSql() {
		if(selectByIdSql==null) {
			StringBuffer sb =new StringBuffer();
			sb.append("Select * from ").append(tableName)
			.append(" where ").append(joinFieldName("=?","and",idFieldArray));
			selectByIdSql=sb.toString();
		}
		return selectByIdSql;
	}
	/**
	 * 获取 "select * from  tableName where id1=? and id2=?"
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */

	@Override
	public String getSelectAllSql(){
		if(selectAllSql==null) {
			StringBuffer sb =new StringBuffer();
			sb.append("Select * from ").append(tableName)
			.append(" order by ").append(orderByField.getName()).append(" ").append(orderByType);
			selectAllSql=sb.toString();
		}
		return selectAllSql;
	}
	/**
	 * select * from  tableName order by [field] limit ?,?
	 * 使用注解的orderby字段或默认id字段
	 */
	@Override
	public String getSelectByPageSql() {
		if(selectByPageSql==null) {
			StringBuffer sb =new StringBuffer();
			sb.append("Select * from ").append(tableName)
			.append(" order by ").append(orderByField.getName()).append(" ").append(orderByType).append(" limit ?,?");
			selectByPageSql=sb.toString();
		}
		return selectByPageSql;
	}
	/**
	 * select * from  tableName order by [field] limit ?,?
	 * 使用传入的 orderBy  字段 如果传入参数不正确,  使用默认order参数
	 */
	@Override
	public String getSelectByPageSql(String orderBy) {
		for(Field field : allFieldArray) {
			if(field.getName().equalsIgnoreCase(orderBy)) {
				StringBuffer sb =new StringBuffer();
				sb.append("Select * from ").append(tableName)
				.append(" order by ").append(field.getName()).append(" limit ?,?");
				return sb.toString();
			}
		}
		return getSelectByPageSql();
	}

	/**
	 * 返回update sql语句
	 * update tableName set field=? , field=?... where id1=? and id2=?
	 */
	@Override
	public String getUpdateByIdSql() {
		if(updateByIdSql==null) {
			StringBuffer sb =new StringBuffer();
			sb.append("update ").append(tableName)
			.append(" set ").append(joinFieldName("=?",",",noIdFieldArray))
			.append(" where ").append(joinFieldName("=?","and",idFieldArray));
			updateByIdSql=sb.toString();
		}
		return updateByIdSql;
	}

	/**
	 * 生成 PartitionName [addStr] [spilt] PartitionName[addStr]...
	 * 例如 PartitionName = ? and PartitionName = ? ...
	 * 例如 PartitionName = ? , PartitionName = ? ...
	 * 
	 * @example 以"PartitionName = ? and PartitionName = ? ..."为例
	 * fields为  {"PartitionName","PartitionName"}
	 * addStr为  " = ? "
	 * spilt为    "and" 
	 * ps:spilt 参数中的字段不会添加在最后字段尾
	 * @param addStr 连接在字段名后的固定文本
	 * @param spilt 多个字段的分割符号,这个符号在最后一个字段将不会被添加
	 * @param fields
	 * @return
	 */
	private static String joinFieldName(String addStr,String spilt, Field[] fields ) {
		StringBuffer sb=new StringBuffer();
		if(fields!=null)
		{
			for(Field field :fields) {
				sb.append(field.getName()).append(addStr).append(" ").append(spilt).append(" ");
			}
		}
		return sb.substring(0, sb.lastIndexOf(spilt));
	};

	/**
	 * 生成 ?,?...  预编译中的占位符号
	 * @param length
	 * @return
	 */
	private static String joinPlaceholder(int length) {
		StringBuffer sb=new StringBuffer();
		if(length>0) {
			for(int i=0;i<length;i++) {
				sb.append("?").append(",");
			}
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}


	/**
	 * 获得表名
	 */
	@Override
	public String getTableName() {
		return tableName;
	}



	/**
	 * 分页时用到的统计条数的sql语句
	 */
	@Override
	public String getCountSql() {
		String sql = "select count(*) from "+tableName;
		return sql;
	}


	/**
	 * 返回带条件的sql统计条数语句
	 */
	@Override
	public String getCoungSql(String... fields) {
		StringBuffer sb = new StringBuffer("select count(*) from ").append(tableName);
		if(fields==null||fields.length==0) {
			return sb.toString();
		}
		List<Field> targetFields=new ArrayList<Field>();
		for(Field field :allFieldArray) {
			for(String fieldstr :fields) {
				if(field.getName().toLowerCase().equals(fieldstr.toLowerCase())) {
					targetFields.add(field);
				}
			}
		}
		Field[] fieldArray= new Field[targetFields.size()];
		fieldArray =targetFields.toArray(fieldArray);
		if(fieldArray.length>0) {
			 sb.append(" where ").append(joinFieldName("=?",",",fieldArray)).toString();
		}
			return sb.toString();
	}

	/**
	 * 从request获取并转换成T类型
	 * @param request
	 * @return
	 */
	public T requestToModel(ServletRequest request) {
		try {
			T model = tClass.newInstance();
			for(Field field: allFieldArray) {
				String name = field.getName();
				Class<?> type=field.getType();
				String value =request.getParameter(name);
				Method method = tClass.getMethod("set"+CommonUtil.upperCaseFirst(name), type);
				if (value!=null&& !value.equals("")) {
					if(type == String.class) {
						method.invoke(model, value);
					}else if(type == int.class ||type == Integer.class){
						method.invoke(model, Integer.parseInt(value));
					}else if(type == float.class ||type == Float.class){
						method.invoke(model, Float.parseFloat(value));
					}else if(type == double.class ||type == Double.class){
						method.invoke(model, Double.parseDouble(value));
					}else if(type == boolean.class ||type == Boolean.class){
						method.invoke(model, Boolean.parseBoolean(value));
					}else if(type == long.class ||type == Long.class){
						method.invoke(model, Long.parseLong(value));
					}else if(type == byte.class ||type == Byte.class){
						method.invoke(model, Byte.parseByte(value));
					}else if(type == Date.class){
						method.invoke(model, Date.valueOf(value));
					}else if(type == Time.class){
						method.invoke(model,Time.valueOf(value));
					}else if(type == BigDecimal.class){
							method.invoke(model,new BigDecimal(value));
					}
					
					
				}
			}
			return model;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Class<?> getIdType() {
		if(idFieldArray.length==1) {
			return idFieldArray[0].getType();
		}
		return null;
		
	}

}
