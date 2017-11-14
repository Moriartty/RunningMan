package com.moriarty.user.runningman.Object;

import java.io.Serializable;

/**
 * Created by user on 17-8-11.
 */
public class PersonInfo implements Serializable {
    private static final long serialVersionUID=7626903512355148839L;
    public String name;
    public String tel;
    public String email;
    public String group;
    public String headPortrait;
    public String sexy;
    public String birthday;

    public int isCollected;
    public int age;


    public PersonInfo(){
        this.name="";
        this.tel="";
        this.email="";
        this.group="";
        this.headPortrait="";
        this.isCollected=0;
        this.sexy="male";
        this.age=0;
        this.birthday="";

    }

    public PersonInfo(Builder builder){
        this.name=(builder.name!=null&&!builder.name.equals("")?builder.name:builder.tel);
        this.tel=builder.tel;
        this.email=(builder.email!=null?builder.email:"");
        this.group=builder.group;
        //头像这里要判别空
        this.headPortrait=builder.headPortrait;
        this.isCollected=0;
        this.sexy=builder.sexy;
        this.age=builder.age;
        this.birthday=builder.birthday;
    }


    public void setName(String name){
        this.name=name;
    }
    public void setTel(String tel){
        this.tel=tel;
    }
    public void setEmail(String email){
        this.email=email;
    }
    public void setGroup(String group){
        this.group=group;
    }
    public void setHeadPortrait(String headPortrait){
        this.headPortrait=headPortrait;
    }
    public void setIsCollected(int isCollected){
        this.isCollected=isCollected;
    }
    public void setSexy(String sexy){
        this.sexy=sexy;
    }
    public void setAge(int age){
        this.age=age;
    }
    public void setBirthday(String birthday){
        this.birthday=birthday;
    }


    public String getName(){
        return this.name;
    }
    public String getTel(){
        return this.tel;
    }
    public String getEmail(){
        return this.email;
    }
    public String getGroup(){
        return this.group;
    }
    public String getHeadPortrait(){
        return this.headPortrait;
    }
    public int getIsCollected(){
        return this.isCollected;
    }
    public String getSexy(){
        return this.sexy;
    }
    public int getAge(){
        return this.age;
    }
    public String getBirthday(){
        return this.birthday;
    }


    public static class Builder{
        public String name;
        public String tel;
        public String email;
        public String group;
        public String headPortrait;
        public String sexy;
        public int isCollected;
        public int age;
        public String birthday;

        public Builder name(String name){
            this.name=name;
            return this;
        }
        public Builder tel(String tel){
            this.tel=tel;
            return this;
        }
        public Builder email(String email){
            this.email=email;
            return this;
        }
        public Builder group(String group){
            this.group=group;
            return this;
        }
        public Builder headPortrait(String headPortrait){
            this.headPortrait=headPortrait;
            return this;
        }
        public Builder sexy(String sexy){
            this.sexy=sexy;
            return this;
        }
        public Builder isCollected(int isCollected){
            this.isCollected=isCollected;
            return this;
        }
        public Builder age(int age){
            this.age=age;
            return this;
        }
        public Builder birthday(String birthday){
            this.birthday=birthday;
            return this;
        }

        public PersonInfo build(){
            return new PersonInfo(this);
        }
    }
}
