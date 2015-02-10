package camptimetest.domain;

import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

/**
 * Created by sophiawang on 2/4/15.
 */
public class Certification {


    //THIS CLASS IS MAYBE NOT NECESSARY

    @Id
    @ObjectId
    private String name; //name of the certification
    private String certificationID;

    public String getName(){return name;}

    public Certification setName(String name){
        this.name=name;
        return this;
    }

    public String getCertificationID(){return certificationID;}

    public Certification setCertificationID(String id){
        certificationID=id;
        return this;
    }

    @Override
    public String toString(){
        return "Certification{" +
                "certificationID='" + certificationID + '\'' +
                ", name='" + name + '\''+
                '}';
    }

}
