package com.login.bean;

public class User {
	private String uuid = null;
	private String userName = null;
	private String passWord = null;
	private String phone = null;
	private String email = null;
	
	public User(){}
	public User(String uuid,String username, String password,String phone,String email){
		this.uuid=uuid;
		this.userName = userName;
		this.passWord = passWord;
		this.phone = phone;
		this.email = email;
	}
	public String getUUID(){
		return uuid;
	}
	public void setUUID(String uuid){
		this.uuid=uuid;
	}
	public String getUsername() {
		return userName;
	}
	public void setUsername(String username) {
		this.userName = userName;
	}
	public String getPassword() {
		return passWord;
	}
	public void setPassword(String password) {
		this.passWord = passWord;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email=email;
	}
	
}
