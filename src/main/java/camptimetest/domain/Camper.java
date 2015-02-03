package camptimetest.domain;

import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

/**
 * Created by Eric on 2/1/2015.
 */
public class Camper {

    @Id
    @ObjectId
    private String camperID;//still referred to as _id on client side!!!
    //still have to have some designated id field to use though
    private String name;
    private String age;
    private String extraInfo;

    public String getcamperID(){
        return camperID;
    }

    public Camper setcamperID(final String camperID){
        this.camperID = camperID;
        return this;
    }

    public String getName(){
        return name;
    }

    public Camper setName(String name){
        this.name = name;
        return this;
    }

    public String getAge(){
        return age;
    }

    public Camper setAge(String age){
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
                "camperID='" + camperID + '\'' +
                ", name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", extraInfo=" + extraInfo + '\'' +
                '}';
    }

}
