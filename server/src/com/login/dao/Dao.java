package com.login.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Dao {
	/*//����������
	private final static String DRIVER = "com.mysql.jdbc.Driver";
	//URLָ��Ҫ���ʵ����ݿ���
	private final static String URL = "jdbc:mysql://127.0.0.1:3306/project";
	//MySql����ʱ���û�������
	private final static String USER = "root";
	private final static String PWD = "123";
	public Connection getCon() throws ClassNotFoundException, SQLException{
		Class.forName(DRIVER);
		Connection con = DriverManager.getConnection(URL,USER,PWD);
		System.out.println(con);
		return con;
	}*/
	
	public void closeAll(ResultSet rs , Statement st , Connection con) throws SQLException{
		if(rs != null){
			rs.close();
		}
		if(st != null){
			st.close();
		}
		if(con != null){
			con.close();
		}
	}
	
	public static String userName="sa";
	public static String userPwd="313569773b";
	public static Connection getCon() throws ClassNotFoundException, SQLException{
		String driverName="com.microsoft.sqlserver.jdbc.SQLServerDriver";
		String dbURL="jdbc:sqlserver://localhost:1433;DatabaseName=RunningMan";
		Connection dbConn=null;
		Class.forName(driverName);
		dbConn=DriverManager.getConnection(dbURL,userName,userPwd);
		return dbConn;
	}
}
