package com.login.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.login.bean.Activity;

public class ActivityImpl {
	Dao dao = new Dao();
	private final String SPLITSTR="$$##" ;
	public Boolean insert(Activity ac) throws ClassNotFoundException, SQLException{
		//StringBuffer people = new StringBuffer();
		/*int size=ac.getPeople().split(SPLITSTR).length;
		for(int i=0;i<size;i++){
			if(i<size-1){
				people.append(ac.getPeople()[i]+SPLITSTR);
			}
			else people.append(ac.getPeople()[i]);
		}*/
		String sql = "insert into [activity] values ('"+ ac.getUUID()+"','"+ ac.getLeader()+"'," +
				"'"+ ac.getTopic()+"','"+ ac.getTime()+"','"+ ac.getAddress()+"','"+ ac.getTel()+"'," +
						"'"+ ac.getPeople()+"','"+ ac.getContent()+"')";
		System.out.println("inser Sql is     "+ sql);
		Connection con = dao.getCon();
		Statement st = con.createStatement();
		return st.executeUpdate(sql)==1?true:false;
		/*if(st.executeUpdate(sql)==1)
			return true;
		else
			return false;*/
	}
	//根据acUid查找该活动的leader
	public String selectOne(String acUid) throws ClassNotFoundException, SQLException{
		String leader=null;
		String sql="select leader from [activity] where uuid = '"+ acUid+"'";
		Connection con = dao.getCon();
		Statement statement = con.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		while (rs.next()) {
			leader=rs.getString("leader");
		}
		dao.closeAll(rs, statement, con);
		return leader;
	}
	public Boolean delete(String userUid,String acUid) throws ClassNotFoundException, SQLException{
		if(userUid.equals(selectOne(acUid))){
			String sql="delete from [activity] where uuid = '"+ acUid+"'";
			Connection con = dao.getCon();
			Statement statement = con.createStatement();
			boolean b=statement.executeUpdate(sql)==1?true:false;
			dao.closeAll(null,statement, con);
			return b;
		}
		return false;
	}
	//查询leader是本人和参与者有本人的活动
	public ArrayList<Activity> queryMyActivity(String uuid) throws ClassNotFoundException, SQLException{
		ArrayList<Activity> list = new ArrayList();
		String sql = "select * from [activity] where leader = '"+ uuid +"' or people like '%"+ uuid+"%'";
		Connection con = dao.getCon();
		Statement statement = con.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		while (rs.next()) {
			Activity ac = new Activity();
			ac.setUUID(rs.getString("uuid"));
			ac.setTopic(rs.getString("topic"));
			ac.setTime(rs.getString("time"));
			ac.setAddress(rs.getString("address"));
			ac.setTel(rs.getString("tel"));
			ac.setLeader(rs.getString("leader"));
			ac.setPeople(rs.getString("people"));
			ac.setContent(rs.getString("content"));
			list.add(ac);
		}
		dao.closeAll(rs, statement, con);
		return list;
	}
}
