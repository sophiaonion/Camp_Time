package camptimetest.domain;

import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric on 1/23/2015.
 */
//classes in domain are beans -- just getters and setters and a toString that returns javascript object
//there should be a better way then just String concatenation
//also not sure if toString is used by jackson library to convert to JSON object
public class Employee {
    private String name;
    private int age;
    private String job;
    private String gender;
    private ArrayList<String> certifications;
    private ArrayList<String> activities = new ArrayList<>();

    @ObjectId
    @Id  //designates as key in mongoDB, not sure of difference between @Id and @ObjectId
    private String key;

    public String getKey(){
        return key;
    }

    public Employee setKey(final String key){
        this.key = key;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public ArrayList<String> getCertifications() {
        return certifications;
    }

    public void setCertifications(ArrayList<String> certifications) {
        this.certifications = certifications;
    }

    public ArrayList<String> getActivites(){
        return this.activities;
    }

    public Employee setActivities(ArrayList<String> activities){
        this.activities = activities;
        return this;
    }


    public Employee addActivity(Activity activity){
        if (activities.contains(activity.getKey()))
        {}
        else{
            activities.add(activity.getKey());
        }
        return this;
    }

    public Employee removeActivity(Activity activity){
        activities.remove(activity.getKey());
        return this;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", job='" + job + '\'' +
                ", gender='" + gender + '\'' +
                ", certifications=" + certifications +
                ", key='" + key + '\'' +
                ", activities='" + activities + '\'' +
                '}';
    }
}
