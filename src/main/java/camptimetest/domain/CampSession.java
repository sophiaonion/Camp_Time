package camptimetest.domain;

import org.joda.time.DateTime;
import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by rachelfeltes on 2/1/15.
 */
public class CampSession {

    @ObjectId
    @Id
    private String sessionID;
    private String name;
    private String ageGroup; //will use 1, 2, 3, 4, 5
    private int enrollmentCap;
    private DateTime startDate;
    private DateTime endDate;
    private ArrayList<String> activities = new ArrayList<>(); //list of all activities, required or not
    private ArrayList<String> counselors;

    public String getSessionID() {
        return sessionID;
    }

    public CampSession setSessionID(String sessionID) {
        this.sessionID = sessionID;
        return this;
    }

    public String getName() {
        return name;
    }

    public CampSession setName(String name) {
        this.name = name;
        return this;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public CampSession setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
        return this;
    }

    public int getEnrollmentCap() {
        return enrollmentCap;
    }

    public CampSession setEnrollmentCap(int enrollmentCap) {
        this.enrollmentCap = enrollmentCap;
        return this;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public CampSession setStartDate(DateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public CampSession setEndDate(DateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    public ArrayList<String> getCounselorIDs() {
        return counselors;
    }

    public void setCounselorIDs(ArrayList<String> counselorIDs) {
        this.counselors = counselorIDs;
    }

    public ArrayList<String> getActivities() {
        return activities;
    }

    public void setActivities(ArrayList<String> activities) { this.activities = activities;
    }

    public CampSession addActivity(String objectId){//must be object id of activity

        this.activities.add(objectId);
        return this;
    }

    @Override
    public String toString() {
        return "CampSession{" +
                "sessionID='" + sessionID + '\'' +
                ", name='" + name + '\'' +
//                ", ageGroup='" + ageGroup + '\'' +
//                ", enrollmentCap=" + enrollmentCap +
//                ", startDate=" + startDate +
//                ", endDate=" + endDate +
//                ", activities=" + activities +
//                ", activities=" + activities +
//                ", counselors=" + counselors +
                '}';
    }

}
