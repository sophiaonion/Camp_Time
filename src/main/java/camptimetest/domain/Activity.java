package camptimetest.domain;

import org.joda.time.DateTime;
import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric on 1/29/2015.
 */
public class Activity {

    private ArrayList<Employee> employees = new ArrayList<>();
    private String title;
    private DateTime time;
    private String session;
    private String activityArea;
    private Boolean fixed;//whether time is fixed by admin
    private Boolean isSet;//whether time has beeisSetet by algorithm


    @Id
    @ObjectId
    private String key;


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
//        if (employees != null){
            employees.add(employee);
//        } else {
//            employees = new ArrayList<>();
//            employees.add(employee);
//        }
        return this;
    }

    public Activity removeEmployee(Employee employee){
        employees.remove(employee);
        return this;
    }

    public Activity setEmployees(ArrayList<Employee> employees){
        this.employees = employees;
        return this;
    }

    public Boolean getIsSet() {
        return this.isSet;
    }

    public void setIsSet(Boolean isSet) {
        this.isSet = isSet;
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
        return employees;
    }

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    public String getActivityArea() {
        return this.activityArea;
    }

    public void setActivityArea(String activityArea) {
        this.activityArea = activityArea;
    }
}
