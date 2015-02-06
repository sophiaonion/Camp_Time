package camptimetest.domain;

import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Eric on 1/29/2015.
 */
public class Activity {

    private ArrayList<Employee> workers;
    private String title;
    private Date day; //idk how this stuff will be formatted yet <- & v
    private Date hour;
    private String activityAreaID;


    @Id
    @ObjectId
    private String key;

    public Activity(String title, Date day, Date hour, String activityAreaID) {
        this.title = title;
        this.day = day;
        this.hour = hour;
        this.activityAreaID = activityAreaID;
    }

    //key will be generated automagically if not specified in passed in JSON object
    public Activity setKey(String key){
        this.key = key;
        return this;
    }

    public String getKey(){
        return key;
    }

    public Activity setTitle(String title){
        this.title = title;
        return this;
    }

    public String getTitle(){
        return title;
    }

    public Activity addEmployee(Employee employee){
        workers.add(employee);
        return this;
    }

    public Activity removeEmployee(Employee employee){
        workers.remove(employee);
        return this;
    }

    public Activity setEmployees(ArrayList<Employee> workers){
        this.workers = workers;
        return this;
    }

    public ArrayList<Employee> getEmployees(){
        return workers;
    }

    public Activity setDay(Date day){
        this.day = day;
        return this;
    }

    public Date getDay(){ return day;}

    public Activity setHour(Date hour){
        this.hour = hour;
        return this;
    }

    public Date getHour(){
        return hour;
    }

    public String getActivityAreaID() {
        return activityAreaID;
    }

    public void setActivityAreaID(String activityAreaID) {
        this.activityAreaID = activityAreaID;
    }
}
