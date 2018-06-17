package com.lanlan.util;

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

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;

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
	@Resource()
	private BasicDataSource dataSource;
	
	/**
	 * @param dataSource
	 */
	public void setDataSource(BasicDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * 从连接池获取一个连接对象
	 * @return 一个Connection连接对象
	 */
	public Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
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
	 * 封裝的executeQuery方法
	 * 返回值ResultSet 在外部使用完后必须要close释放,  Connection和PreparedStatement对象随之自动释放;
	 * @param sql 要执行查询的sql语句,
	 * @param parameters sql语句中要填充的参数
	 * @return ResultSet
	 */
	public  ResultSet executeQuery(String sql , SqlParameter... parameters ) 
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
						close(conn,stat,resultSet);
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
	public  int executeUpdate(String sql , SqlParameter... parameters ) throws SQLException 
	{
		try(Connection conn = getConnection()){
			try(PreparedStatement stat= conn.prepareStatement(sql))
			{
				return setSqlParameter(stat,parameters).executeUpdate();
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
	private  PreparedStatement setSqlParameter(PreparedStatement stat,SqlParameter... parameters ) throws SQLException {
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
