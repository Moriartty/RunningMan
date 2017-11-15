package com.login.utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.login.bean.Activity;
import com.login.bean.User;

public class JsonUtils {
	
	public static String userToJson(User user){
		Gson gson = new Gson();
		Type type = new TypeToken<User>() {}.getType(); // ָ��type
        String beanToJson = gson.toJson(user, type); // listת����json�ַ���
        System.out.println("GSON-->" + beanToJson);
        return beanToJson;
	}
	public static String activity2Json(Activity ac){
		Gson gson = new Gson();
		Type type = new TypeToken<Activity>() {}.getType(); // ָ��type
        String beanToJson = gson.toJson(ac, type); // listת����json�ַ���
        System.out.println("GSON-->" + beanToJson);
        return beanToJson;
	}
	public static String activitiesToJson(ArrayList<Activity> list){
		Gson gson = new Gson();
		Type type = new TypeToken<ArrayList<Activity>>(){}.getType();
		String beanListToJson = gson.toJson(list, type);
		return beanListToJson;
	}
	public static User json2User(String json){
		 Gson gson = new Gson();
	     User user = gson.fromJson(json, User.class);//����javabeanֱ�Ӹ���classʵ��
	    // System.out.println(object.toString());
	     return user;
	}
	public static Activity json2Activity(String json){
		Gson gson = new Gson();
	    Activity ac = gson.fromJson(json, Activity.class);//����javabeanֱ�Ӹ���classʵ��
	    // System.out.println(object.toString());
	    return ac;
	}
	
	/*=============================================================*/
	
	public static String javabeanToJson(Object object) {
        Gson gson = new Gson();
        String json = gson.toJson(object);
        return json;
    }

    /**
     * list to json
     * 
     * @param list
     * @return
     */
    public static String listToJson(List<Object> list) {

        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    /**
     * map to json
     * 
     * @param map
     * @return
     */
    public static String mapToJson(Map<String, Object> map) {

        Gson gson = new Gson();
        String json = gson.toJson(map);
        return json;
    }
    
    /* ===========================================================*/
    /**
     * json to javabean
     * 
     * @param json
     */
    public static void jsonToJavaBean(String json) {
        Gson gson = new Gson();
        Object object = gson.fromJson(json, Object.class);//����javabeanֱ�Ӹ���classʵ��
        System.out.println(object.toString());
    }

    /**
     * json�ַ���תList����
     */

    public static void jsonToList(String json) {

        Gson gson = new Gson();
        List<Object> persons = gson.fromJson(json, new TypeToken<List<Object>>() {
        }.getType());//���ڲ������������������������
        for (Object object : persons) {
            System.out.println(object);
        }
    }

    public static void jsonToMap(String json) {
        // TODO Auto-generated method stub
        Gson gson = new Gson();
        Map<String, Object> maps = gson.fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
        for (Map.Entry<String, Object> entry : maps.entrySet()) {
            System.out.println("key: " + entry.getKey() + "  " + "value: " + entry.getValue());

        }
    }
	
}
