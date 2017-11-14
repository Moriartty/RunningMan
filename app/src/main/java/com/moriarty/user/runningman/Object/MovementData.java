package com.moriarty.user.runningman.Object;

import com.moriarty.user.runningman.Activity.MainActivity;

import java.io.Serializable;

/**
 * Created by user on 17-8-25.
 */
public class MovementData implements Serializable {
    private static final long serialVersionUID=7646903512215148339L;
    public String time;
    public int ride_Time;
    public int walk_Step;
    public int walk_Pace;
    public int run_Step;
    public int number_Of_Pedals;
    public double walk_Distance;
    public double walk_Speed;
    public double run_Distance;
    public double ride_Distance;
    public double calories;
    public MovementData(Builder builder){
        this.time=builder.time;
        this.ride_Time=builder.ride_Time;
        this.calories=builder.calories;
        this.number_Of_Pedals=builder.number_Of_Pedals;
        this.ride_Distance=builder.ride_Distance;
        this.run_Step=builder.run_Step;
        this.walk_Step=builder.walk_Step;
        this.walk_Distance=builder.walk_Distance;
        this.run_Distance=builder.run_Distance;
        this.walk_Pace=builder.walk_Pace;
        this.walk_Speed=builder.walk_Speed;
    }
    public String getTime(){
        return this.time;
    }
    public int getRide_Time(){
        return this.ride_Time;
    }
    public int getWalk_Step(){
        return this.walk_Step;
    }
    public int getRun_Step(){
        return this.run_Step;
    }
    public int getNumber_Of_Pedals(){
        return this.number_Of_Pedals;
    }
    public double getWalk_Distance(){
        return this.walk_Distance;
    }
    public double getRun_Distance(){
        return this.run_Distance;
    }
    public double getRide_Distance(){
        return this.ride_Distance;
    }
    public double getCalories(){
        return this.calories;
    }
    public int getWalk_Pace(){
        return this.walk_Pace;
    }
    public double getWalk_Speed(){
        return this.walk_Speed;
    }

    public static class Builder{
        public String time;
        public int ride_Time;
        public int walk_Step;
        public int walk_Pace;
        public int run_Step;
        public int number_Of_Pedals;
        public double walk_Distance;
        public double walk_Speed;
        public double run_Distance;
        public double ride_Distance;
        public double calories;
        public Builder time(String time){
            this.time=time;
            return this;
        }
        public Builder ride_Time(int ride_Time){
            this.ride_Time=ride_Time;
            return this;
        }
        public Builder walk_Step(int walk_Step){
            this.walk_Step=walk_Step;
            return this;
        }
        public Builder
        run_Step(int run_Step){
            this.run_Step=run_Step;
            return this;
        }
        public Builder number_Of_Pedals(int number_Of_Pedals){
            this.number_Of_Pedals=number_Of_Pedals;
            return this;
        }
        public Builder walk_Distance(double walk_Distance){
            this.walk_Distance=walk_Distance;
            return this;
        }
        public Builder run_Distance(double run_Distance){
            this.run_Distance=run_Distance;
            return this;
        }
        public Builder ride_Distance(double ride_Distance){
            this.ride_Distance=ride_Distance;
            return this;
        }
        public Builder calories(double calories){
            this.calories=calories;
            return this;
        }
        public Builder walk_Pace(int walk_Pace){
            this.walk_Pace=walk_Pace;
            return this;
        }
        public Builder walk_Speed(double walk_Speed){
            this.walk_Speed=walk_Speed;
            return this;
        }
        public MovementData build(){
            return new MovementData(this);
        }
    }

}
