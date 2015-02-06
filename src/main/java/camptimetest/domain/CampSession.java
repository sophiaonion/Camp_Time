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
    private String ageGroup; //will use 1, 2, 3, 4, 5
    private int enrollmentCap;
    private Date startDate;
    private Date endDate;
    private List<Activity> activities; //list of all activities, required or not
    private List<Employee> counselors;

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

    public List<Employee> getCounselors() {
        return counselors;
    }

    public CampSession setCounselors(List<Employee> counselors) {
        this.counselors = counselors;
        return this;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        activities = activities;
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
                ", activities=" + activities +
                ", counselors=" + counselors +
                '}';
    }

}
