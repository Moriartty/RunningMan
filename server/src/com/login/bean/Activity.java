package com.login.bean;

import java.util.ArrayList;

public class Activity {
	private String uuid;
	private String topic;
	private String time;
	private String address;
	private String tel;
	private String leader;//活动发起人的uuid
	private String people;
	private String content;
	
	public Activity(){}
	public String getUUID(){
		return this.uuid;
	}
	public void setUUID(String uuid){
		this.uuid=uuid;
	}
	public String getTopic(){
		return topic;
	}
	public void setTopic(String topic){
		this.topic=topic;
	}
	public String getTime(){
		return time;
	}
	public void setTime(String time){
		this.time=time;
	}
	public String getAddress(){
		return address;
	}
	public void setAddress(String address){
		this.address=address;
	}
	public String getTel(){
		return tel;
	}
	public void setTel(String tel){
		this.tel=tel;
	}
	public String getLeader(){
		return this.leader;
	}
	public void setLeader(String leader){
		this.leader=leader;
	}
	public String getPeople(){
		return people;
	}
	public void setPeople(String people){
		this.people=people;
	}
	public String getContent(){
		return content;
	}
	public void setContent(String content){
		this.content=content;
	}
	
}
