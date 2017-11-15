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
 * ��MVC�еĿ��Ʋ㣬����û����Ƿ��Ѿ�ע�ᣬע���û�����½ʱ����û����������Ƿ�ƥ��
 */

public class Service {	
	private User user = null;
	private DaoImpl dao= new DaoImpl();
	/**
	 * ����û����Ƿ��ѱ�ע���
	 * @param username �û�������û���
	 * @return trueΪ��ע�����falseΪû��ע���
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public JsonObject checkUsername(String username) throws ClassNotFoundException, SQLException{
		boolean flag = false;
		user = dao.selectOne(username);
		if (user != null) {
			//���û�����ע��
			flag = true;
		}
		JsonObject o=new JsonObject();
		o.addProperty("returnState", flag);
		return o;
	} 
	/**
	 * �û�ע��
	 * ���û���Ϣ�������ݿ���
	 * @param user �û���Ϣ�ķ�װ��
	 * @return true �ɹ����룬falseû�гɹ�����
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
	 * �û���¼
	 * �����û��������ݿ��в����û�,���û����������������͸��û������ݿ��д洢������һ�£�����Ե�¼
	 * @param username �û�������û���
	 * @param password �û����������
	 * @return true ���Ե�¼��false�����Ե�¼
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
