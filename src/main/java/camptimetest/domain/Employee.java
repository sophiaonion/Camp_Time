package camptimetest.domain;

import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

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

    @Id @ObjectId //designates as key in mongoDB, not sure of difference between @Id and @ObjectId
    private String key;

    public String getKey(){
        return key;
    }

    public Employee setKey(final String key){
        this.key = key;
        return this;
    }

    public String getName(){
        return name;
    }

    public Employee setName(final String name){
        this.name = name; // + "more text" can manipulate parameter before setting object value and storing in database
        return this;
    }

    public int getAge(){
        return age;
    }

    public Employee setAge(final int age){
        this.age = age;
        return this;
    }

    public String getJob(){
        return job;
    }

    public Employee setJob(final String job){
        this.job = job;
        return this;
    }

    //not necessary for JSON conversion to send to client or receive
    @Override
    public String toString(){
        return "Employee{" +
                "age='" + age + '\'' +
                ", name='" + name + '\'' +
                ", job='" + job + '\'' +
                '}';
    }

}
