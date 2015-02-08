package camptimetest.domain;

import org.joda.time.DateTime;
import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

import java.util.ArrayList;

/**
 * Created by Eric on 1/29/2015.
 */
public class Activity {

    private ArrayList<Employee> workers;
    private String title;
    private DateTime time;
    private String session;
    private String activityArea;
    private Boolean fixed;


    @Id
    @ObjectId
    private String key;

//    public Activity(String title, Date day, Date hour, String acactivityArea{
//        this.title = title;
//        this.day = day;
//        this.hour = hour;
//        this.acactivityArea acactivityArea//    }

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

    public Boolean getFixed() {
        return fixed;
    }

    public void setFixed(Boolean fixed) {
        this.fixed = fixed;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public ArrayList<Employee> getEmployees(){
        return workers;
    }

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    public String getActivityArea() {
        return activityArea;
    }

    public void setActivityArea(String activityArea) {
        this.activityArea = activityArea;
    }
}
