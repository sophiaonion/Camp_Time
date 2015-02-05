package camptimetest.domain;

import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

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
    private String ageGroup;
    private int enrollmentCap;
    private Date startDate;
    private Date endDate;
    // private List<Activity> fixedActivities; ----no longer necessary, just have sessionresource do it
    private List<String> requiredActivities; //list of ids of required activities - schedule update will incorporate ~somehow
    private List<Employee> counselors;


    public CampSession() {
        //add skeleton schedule into database?? will need to create and enter activities
        //create new activity for each item in skeleton schedule: meals, checkin/out, all-camp activities, etc.
        //calculate dates based on startdate/enddate
        //need to already have activityAreaIDs (just hard code in?)
        //no employees linked to activity for now - schedule update /should/ take care of that
        //then put each activity into database

    }

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

    public Date getStartDate() {
        return startDate;
    }

    public CampSession setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public Date getEndDate() {
        return endDate;
    }

    public CampSession setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public List<String> getRequiredActivities() {
        return requiredActivities;
    }

    public CampSession setRequiredActivities(List<String> requiredActivities) {
        this.requiredActivities = requiredActivities;
        return this;
    }

    public List<Employee> getCounselors() {
        return counselors;
    }

    public CampSession setCounselors(List<Employee> counselors) {
        this.counselors = counselors;
        return this;
    }

    @Override
    public String toString() {
        return "CampSession{" +
                "sessionID='" + sessionID + '\'' +
                ", name='" + name + '\'' +
                ", ageGroup='" + ageGroup + '\'' +
                ", enrollmentCap=" + enrollmentCap +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
            //    ", fixedActivities=" + fixedActivities +
                ", requiredActivities=" + requiredActivities +
                ", counselors=" + counselors +
                '}';
    }

}
