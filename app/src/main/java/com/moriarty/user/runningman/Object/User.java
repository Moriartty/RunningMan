package com.moriarty.user.runningman.Object;

import java.io.Serializable;

/**
 * Created by user on 17-9-27.
 */
public class User implements Serializable {
    private static final long serialVersionUID=7626903512312348839L;
    public String uuid;
    public String userName;
    public String passWord;
    public String phone;
    public String email;

    public User(Builder builder){
        this.userName=builder.userName;
        this.passWord=builder.passWord;
        this.phone=builder.phone;
        this.email=builder.email;
        this.uuid=builder.uuid;
    }
    public String getGsonString(){
        return null;
    }
    public String getUserName(){
        return this.userName;
    }
    public String getPassWord(){
        return this.passWord;
    }
    public String getTel(){
        return this.phone;
    }
    public String getEmail(){
        return this.email;
    }
    public String getUuid(){return this.uuid;}

    public static class Builder{
        public String userName;
        public String passWord;
        public String phone;
        public String email;
        public String uuid;
        public Builder setUserName(String userName){
            this.userName=userName;
            return this;
        }
        public Builder setPassword(String passWord){
            this.passWord=passWord;
            return this;
        }
        public Builder setEmail(String email){
            this.email=email;
            return this;
        }
        public Builder setTel(String phone){
            this.phone=phone;
            return this;
        }
        public Builder setUuid(String uuid){
            this.uuid=uuid;
            return this;
        }
        public User build(){
            return new User(this);
        }
    }
}
