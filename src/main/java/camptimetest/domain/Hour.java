package camptimetest.domain;

import org.joda.time.DateTime;
import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sophiawang on 2/4/15.
 */
public class Hour {
    @Id
    @ObjectId
    private String hour; //name of the certification
    private Date date;
    private ArrayList<Activity> activities;

    public Hour setDate(Date date){
        this.date=date;
        return this;
    }

    public Date getDate(){
        return date;
    }

    public String getHour(){return hour;}

    public Hour setHour(String hour){
        this.hour=hour;
        return this;
    }

    public ArrayList<Activity> getActivities() {
        return activities;
    }

    public Hour setActivities(ArrayList<Activity> activities) {
        this.activities = activities;
        return this;
    }

    public Hour addActivity(Activity activity){
        activities.add(activity);
        return this;
    }

    public Hour removeActivity(Activity activity){
        activities.remove(activity);
        return this;
    }



}
