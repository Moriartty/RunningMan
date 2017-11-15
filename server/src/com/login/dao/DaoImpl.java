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
	 * ��ѯ���ݿ��е�һ������
	 * ����������ѯ���ݿ���һ�����ݣ���Ҫ���ڼ�����ݿ����Ƿ���ڴ��û�
	 * @param name �û���
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
	 * �����ݿ��в���һ������
	 * �����û�ע��
	 * @param user
	 * @return 1��ʾ����ɹ���0��ʾ����ʧ��
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
		//��������user��Ϣ
		o.addProperty("content", JsonUtils.userToJson(user));
		return o;
		
	}
}
