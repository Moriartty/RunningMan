package com.moriarty.user.runningman.Object;

import java.io.Serializable;

/**
 * Created by user on 17-9-15.
 */
public class ActivityData implements Serializable {
    private static final long serialVersionUID=7646904444215148339L;
    public String uuid;
    public String leader;
    public String topic;
    public String time;
    public String address;
    public String tel;
    public String people;
    public String content;
    public String img_url;
    public ActivityData(Builder builder){
        this.uuid=builder.uuid;
        this.leader=builder.leader;
        this.topic=builder.topic;
        this.time=builder.time;
        this.address=builder.address;
        this.tel=builder.tel;
        this.people=builder.people;
        this.content=builder.content;
        this.img_url=builder.img_url;
    }
    public String getUuid(){return this.uuid;}
    public String getLeader(){
        return this.leader;
    }
    public String getTitle(){
        return this.topic;
    }
    public String getTime(){
        return this.time;
    }
    public String getAddress(){
        return this.address;
    }
    public String getTel(){
        return this.tel;
    }
    public String getPeople(){
        return this.people;
    }
    public String getContent(){
        return this.content;
    }
    public String getImg_url(){
        return this.img_url;
    }
    public static class Builder{
        public String uuid;
        public String leader;
        public String topic;
        public String time;
        public String address;
        public String tel;
        public String people;
        public String content;
        public String img_url;
        public Builder uuid(String uuid){
            this.uuid=uuid;
            return this;
        }
        public Builder leader(String leader){
            this.leader=leader;
            return this;
        }
        public Builder title(String title){
            this.topic=title;
            return this;
        }
        public Builder time(String time){
            this.time=time;
            return this;
        }
        public Builder address(String address){
            this.address=address;
            return this;
        }
        public Builder tel(String tel){
            this.tel=tel;
            return this;
        }
        public Builder people(String people){
            this.people=people;
            return this;
        }
        public Builder content(String content){
            this.content=content;
            return this;
        }
        public Builder img_url(String img_url){
            this.img_url=img_url;
            return this;
        }
        public ActivityData build(){
            return new ActivityData(this);
        }
    }
}
