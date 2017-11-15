package com.login.service;

import java.sql.SQLException;
import java.util.ArrayList;

import com.google.gson.JsonObject;
import com.login.bean.Activity;
import com.login.dao.ActivityImpl;
import com.login.utils.JsonUtils;
import com.login.utils.Utils;

public class ActivityService {
	private Activity ac;
	ActivityImpl impl=new ActivityImpl();
	
	public String insertActivity(Activity ac) throws ClassNotFoundException, SQLException{
		ac.setUUID(Utils.getUUID());
		boolean b=impl.insert(ac);
		JsonObject o=new JsonObject();
		o.addProperty("returnState", b);
		return o.toString();
	}
	
	public String deleteActivity(String userUid,String acUid) throws ClassNotFoundException, SQLException{
		JsonObject o=new JsonObject();
		o.addProperty("returnState", impl.delete(userUid, acUid));
		return o.toString();
	}
	
	public String queryMyActivities(String uuid) throws ClassNotFoundException, SQLException{
		ArrayList<Activity> list = impl.queryMyActivity(uuid);
		boolean b=true;
		JsonObject o=new JsonObject();
		if(list==null||list.size()==0){
			b=false;
			o.addProperty("returnState", b);
		}
		else{
			b=true;
			String content = JsonUtils.activitiesToJson(list);
			System.out.println(content);
			o.addProperty("returnState", b);
			o.addProperty("content", content);
		}
		return o.toString();
	}

}
