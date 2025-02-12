package util;
/*
 * C3p0工具类
 * C3p0为数据库连接池
 */

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3p0Tool {
	
	private static DataSource dataSource = new ComboPooledDataSource();
	
	/*
	 * 获取数据源
	 */
	public static DataSource getDataSource()
	{
		return dataSource;
	}
	
	/*
	 * 获取连接
	 */
	public static Connection getConnection()
	{
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
}
