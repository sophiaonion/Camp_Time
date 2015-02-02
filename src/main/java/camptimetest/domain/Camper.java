package camptimetest.domain;

import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

/**
 * Created by Eric on 2/1/2015.
 */
public class Camper {

    @ObjectId
    @Id
    private String camperId;
    private String name;
    private int age;
    private String extraInfo;

    public String getCamperId(){
        return camperId;
    }

    public Camper setCamperId(String camperId){
        this.camperId = camperId;
        return this;
    }

    public String getName(){
        return name;
    }

    public Camper setName(String name){
        this.name = name;
        return this;
    }

    public int getAge(){
        return age;
    }

    public Camper setAge(int age){
        this.age = age;
        return this;
    }

    public String getExtraInfo(){
        return extraInfo;
    }

    public Camper setExtraInfo(String extraInfo){
        this.extraInfo = extraInfo;
        return this;
    }

    @Override
    public String toString(){
        return "Camper{" +
                "camperId='" + camperId + '\'' +
                ", name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", extraInfo=" + extraInfo + '\'' +
                '}';
    }

}
