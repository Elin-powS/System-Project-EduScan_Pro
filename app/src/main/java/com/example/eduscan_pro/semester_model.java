package com.example.eduscan_pro;

public class semester_model {
    String Degree ,Year,Semester,key,UserID;

    public String getdegree(){ return Degree;}
    public String getyear(){ return Year;}
    public String getsemester(){ return Semester;}
    public String getBatch(){

        return getyear() + " & "+ getsemester();
    }


    public String getUserID() {
        return UserID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
