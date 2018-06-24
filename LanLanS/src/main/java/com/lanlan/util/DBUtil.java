package com.lanlan.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Test;
import org.junit.internal.runners.model.EachTestNotifier;

import com.lanlan.model.SqlParameter;
import com.lanlan.util.DBUtil;


/**
 * 由spring控制为单例类,此处不再设计单例类模式
 * @author 朱矛宇
 * @date 2018年6月14日
 */
public class DBUtil {
	
	/**
	 * 连接池
	 */
	private static BasicDataSource dataSource =new BasicDataSource();
	
	/**
	 * 初始化连接池
	 */
	static {
		try {
			/* 如果要使用file加载,可以使用以下代码 
			 * Properties properties= new Properties();
			 * properties.load(new FileInputStream("config/jdbc.properties"));*/
			Properties properties= new Properties();
			InputStream inStream= DBUtil.class.getClassLoader().getResourceAsStream("config/jdbc.properties");//使用类加载器,从class根目录加载
			properties.load(inStream);
			dataSource.setDriverClassName(properties.getProperty("driver"));
			dataSource.setUrl(properties.getProperty("url"));
			dataSource.setUsername(properties.getProperty("username"));
			dataSource.setPassword(properties.getProperty("passWord"));
			dataSource.setInitialSize(Integer.parseInt(properties.getProperty("initSize")));
			dataSource.setMaxActive(Integer.parseInt(properties.getProperty("maxActive")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 从连接池获取一个连接对象
	 * 使用ThreadLocal保证每个线程只有一个conn,从而允许开启事务
	 * @return 一个Connection连接对象
	 */
	public static Connection getConnection() {
		//Thread.currentThread();
		try {
			ThreadLocal<Connection> threadLocal=new ThreadLocal<Connection>();
			Connection connection =threadLocal.get();
			if(connection!=null) {
				return connection;
			}else {
				connection=dataSource.getConnection();
				threadLocal.set(connection);
				return connection;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static void setAutoCommit(Connection connection,boolean autoCommit) {
		try {
			connection.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static void setAutoCommit(boolean autoCommit) {
		try {
			getConnection().setAutoCommit(autoCommit);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	

	
	/**
	 * 关闭Connection,PreparedStatement,ResultSet对象
	 * @param conn
	 * @param statement
	 * @param resultSet
	 */
	public static void close(Connection conn, PreparedStatement statement,ResultSet resultSet) {
		try {
			if(resultSet!=null) {
				resultSet.close();
				
			}
			if(statement!=null) {
				statement.close();
			}
			if(conn!=null) {
				conn.rollback();
				conn.setAutoCommit(true);
				conn.close();
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 关闭Connection,PreparedStatement对象
	 * @param conn
	 * @param statement
	 */
	public static void close(Connection conn, PreparedStatement statement) {
		close( conn,  statement, null);
	}
	
	/**
	 * 关闭Connection,
	 * @param conn
	 * @param statement
	 */
	public static void close(Connection conn) {
		close( conn,  null, null);
	}
	

	/**
	 * 提交
	 */
	public static void commit() {
		try {
			Connection conn=getConnection();
			if(conn!=null) {
				conn.commit();
				close(conn);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 要不要空指针检查,为空是否应该直接抛出异常,
	 * 空指针检查后,不抛出异常,将可能存在的严重错误隐藏.  
	 * 应该提交事务的地方没有正常提交,而且不报错,可能错在严重后果
	 * @param conn
	 */
	public static void commit(Connection conn) {
		try {
			if(conn!=null) {
				conn.commit();
				close(conn);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void rollback() {
		try {
			Connection conn=getConnection();
			if(conn!=null) {
				conn.rollback();
				close(conn);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void rollback(Connection conn) {
		try {
			if(conn!=null) {
				conn.rollback();
				close(conn);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	
	/**
	 * 封裝的executeQuery方法
	 * 返回值ResultSet 在外部使用完后必须要close释放,  Connection和PreparedStatement对象随之自动释放;
	 * @param sql 要执行查询的sql语句,
	 * @param parameters sql语句中要填充的参数
	 * @return ResultSet
	 */
	public static ResultSet executeQuery(String sql , SqlParameter... parameters ) 
	{
		try {
			final Connection conn = getConnection();
			final PreparedStatement stat= conn.prepareStatement(sql);
			final ResultSet resultSet=setSqlParameter(stat,parameters).executeQuery();
			System.out.println("执行sql:"+sql);
			if(parameters!=null&&parameters.length>0) {
				System.out.print("参数为:");
				for(SqlParameter p: parameters) {
					System.out.print(" ["+p.getIndex()+"]"+p.getValue().toString());
				}
				System.out.println();
			}
		
			return (ResultSet)Proxy.newProxyInstance(resultSet.getClass().getClassLoader(),resultSet.getClass().getInterfaces(),new InvocationHandler() {	
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					if(method.getName().equals("close")) {
						if(conn.getAutoCommit()) {
							close(conn,stat,resultSet);
						}else {
							close(null,stat,resultSet);
						}
						return null;
					}else
					{
						return method.invoke(resultSet,args);
					}
				}
			});
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 执行非查询的sql语句
	 * @param sql
	 * @param parameters
	 * @return 受影响的行数
	 * @throws SQLException
	 */
	public static int executeUpdate(String sql , SqlParameter... parameters ) throws SQLException 
	{
		try(Connection conn = getConnection()){
			try(PreparedStatement stat= conn.prepareStatement(sql))
			{
				System.out.println("执行sql"+sql);
				System.out.print("参数是");
				for(SqlParameter parameter : parameters) {
					System.out.println("["+parameter.getIndex()+"]"
				+parameter.getValue()+"  ");
				}
				System.out.println();
				return setSqlParameter(stat,parameters).executeUpdate();
			}
		}
	}
	
	/**
	 * 批量执行非查询的sql语句
	 * @param sql
	 * @param parameters
	 * @return 受影响的行数
	 * @throws SQLException
	 */
	public static int[] executeUpdateBatch(String sql , SqlParameter[]... parameterArrays ) throws SQLException 
	{
		try(Connection conn = getConnection()){
			try(PreparedStatement stat= conn.prepareStatement(sql))
			{
				for (SqlParameter[] sqlParameters : parameterArrays) {
					setSqlParameter(stat,sqlParameters);
					stat.addBatch();
				}
				
				System.out.println("执行sql"+sql);
				System.out.print("参数是");
				for (SqlParameter[] sqlParameters : parameterArrays) {
					for(SqlParameter parameter : sqlParameters) {
						System.out.println("["+parameter.getIndex()+"]"
					+parameter.getValue()+"  ");
					}
					System.out.println();
				}
				return stat.executeBatch();
			}
		}
	}
	
	
	
	
	/**
	 * 为PreparedStatement设置参数
	 * @param PreparedStatement对象
	 * @param parameters
	 * @return 设置参数后的PreparedStatement对象
	 * @throws SQLException
	 */
	private static PreparedStatement setSqlParameter(PreparedStatement stat,SqlParameter... parameters ) throws SQLException {
		if(parameters!=null) {
			for (int i=0;i<parameters.length;i++) {
				SqlParameter par = parameters[i];
				Object value=par.getValue();
				if (value instanceof String) {
					stat.setString(par.getIndex(), (String)value);
				}else if (value instanceof Integer) {
					stat.setInt(par.getIndex(), (int)value);
				}else if (value instanceof Float) {
					stat.setFloat(par.getIndex(), (float)value);
				}else if (value instanceof Double) {
					stat.setDouble(par.getIndex(), (double)value);
				}else if (value instanceof Boolean) {
					stat.setBoolean(par.getIndex(), (boolean)value);
				}else if (value instanceof Long) {
					stat.setLong(par.getIndex(), (long)value);
				}else if (value instanceof Byte) {
					stat.setByte(par.getIndex(), (byte)value);
				}else if (value instanceof Date) {
					stat.setDate(par.getIndex(),(Date)value );
				}else if (value instanceof Time) {
					stat.setTime(par.getIndex(),(Time)value );
				}else if (value instanceof BigDecimal) {
					stat.setBigDecimal(par.getIndex(), (BigDecimal)value);
				}else if( value ==null) {
					stat.setNull(par.getIndex(), par.getType());
				}
			}
		}
		return stat;
	}

}
