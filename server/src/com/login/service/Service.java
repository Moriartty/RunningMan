package com.login.service;

import java.sql.SQLException;
import java.util.ArrayList;

import com.google.gson.JsonObject;
import com.login.bean.Activity;
import com.login.bean.User;
import com.login.dao.DaoImpl;
import com.login.utils.JsonUtils;
import com.login.utils.Utils;

/**
 * Service
 * @author bing.chen
 * 是MVC中的控制层，检查用户名是否已经注册，注册用户，登陆时检查用户名与密码是否匹配
 */

public class Service {	
	private User user = null;
	private DaoImpl dao= new DaoImpl();
	/**
	 * 检查用户名是否已被注册过
	 * @param username 用户输入的用户名
	 * @return true为已注册过，false为没有注册过
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public JsonObject checkUsername(String username) throws ClassNotFoundException, SQLException{
		boolean flag = false;
		user = dao.selectOne(username);
		if (user != null) {
			//该用户名已注册
			flag = true;
		}
		JsonObject o=new JsonObject();
		o.addProperty("returnState", flag);
		return o;
	} 
	/**
	 * 用户注册
	 * 将用户信息插入数据库中
	 * @param user 用户信息的封装类
	 * @return true 成功插入，false没有成功插入
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public JsonObject register (User user) throws ClassNotFoundException, SQLException{
		/*Activity ac = new Activity();
		ac.setTopic("aaa");
		ArrayList<String> people=new ArrayList();
		people.add("a");
		people.add("b");
		people.add("c");
		ac.setPeople(people);
		ArrayList<String> list=JsonUtils.json2Activity(JsonUtils.activity2Json(ac)).getPeople();
		System.out.println("list-> "+list.get(0));*/
		user.setUUID(Utils.getUUID());
		return dao.insert(user);
	}
	
	/**
	 * 用户登录
	 * 根据用户名在数据库中查找用户,该用户存在且输入的密码和该用户在数据库中存储的密码一致，则可以登录
	 * @param username 用户输入的用户名
	 * @param password 用户输入的密码
	 * @return true 可以登录，false不可以登录
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public JsonObject canLogin (String username, String password) throws ClassNotFoundException, SQLException{
		Boolean flag = true;
		user = dao.selectOne(username);
		if (user == null) {
			flag = false;
		}
		else if (!password.equals(user.getPassword())) {
			flag = false;
		}
		JsonObject o=new JsonObject();
		o.addProperty("returnState", flag);
		return o;
	}
}
