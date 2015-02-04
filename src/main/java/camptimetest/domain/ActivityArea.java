package camptimetest.domain;

import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

import java.util.ArrayList;

/**
 * Created by sophiawang on 2/4/15.
 */
public class ActivityArea {

    @Id
    @ObjectId
    private String name; //name of the certification
    private String activityAreaID;
    private ArrayList<Certification> certifications;

    public String getName(){return name;}

    public void setName(String name){
        this.name=name;
    }

    public String getActivityAreaID(){return activityAreaID;}

    public ActivityArea setActivityAreaID(String id){
        activityAreaID=id;
        return this;
    }

    public ActivityArea setCertifications(ArrayList<Certification> certifications){
        this.certifications = certifications;
        return this;
    }

    public ArrayList<Certification> getCertifications(){
        return certifications;
    }

    public ActivityArea addCertification(Certification item){
        certifications.add(item);
        return this;
    }

    public ActivityArea removeCertification(Certification item){
        certifications.remove(item);
        return this;
    }

/*    @Override
    public String toString(){
        return "ActivityArea{" +
                "activityAreaID='" + activityAreaID + '\'' +
                ", name='" + name + '\''+
                '}';
    }*/

}
