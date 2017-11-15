package com.login.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import com.google.gson.JsonObject;
import com.login.bean.User;
import com.login.utils.JsonUtils;


public class DaoImpl {
	Dao dao = new Dao();
	/**
	 * 查询数据库中的一条数据
	 * 根据主键查询数据库中一条数据，主要用于检查数据库中是否存在此用户
	 * @param name 用户名
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public User selectOne(String username) throws ClassNotFoundException, SQLException{
		User user = new User();
		String str = "select * from [user] where username = '"+ username+"'";
		Connection con = dao.getCon();
		Statement statement = con.createStatement();
		ResultSet rs = statement.executeQuery(str);
		while (rs.next()) {
			user.setUsername(rs.getString("username"));
			user.setPassword(rs.getString("password"));
			user.setPhone(rs.getString("phone"));
			user.setEmail(rs.getString("email"));
		}
		dao.closeAll(rs, statement, con);
		return user;
	}
	/**
	 * 向数据库中插入一条数据
	 * 用于用户注册
	 * @param user
	 * @return 1表示插入成功，0表示插入失败
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	
	public JsonObject insert(User user) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		String sql = "insert into [user] values ('"+ user.getUUID()+"','"+ user.getUsername()
				+"','"+ user.getPassword()+"','"+ user.getPhone()+"','"+ user.getEmail()+"')";
		System.out.println("inser Sql is     "+ sql);
		Connection con = dao.getCon();
		Statement st = con.createStatement();
		//String result=JsonUtils.toJson(user);
		//int result = st.executeUpdate(sql);
		boolean b = false;
		if(st.executeUpdate(sql)==1)
			b=true;
		else
			b=false;
		JsonObject o=new JsonObject();
		o.addProperty("returnState", b);
		//返回整个user信息
		o.addProperty("content", JsonUtils.userToJson(user));
		return o;
		
	}
}
