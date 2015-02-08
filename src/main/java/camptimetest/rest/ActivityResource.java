package camptimetest.rest;

import camptimetest.domain.Activity;
import camptimetest.domain.ConstraintChecker;
import restx.annotations.GET;
import restx.annotations.POST;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import restx.security.PermitAll;

import javax.inject.Named;

/**
 * Created by Eric on 1/30/2015.
 */
@Component
@RestxResource
@PermitAll
public class ActivityResource {

    private JongoCollection activities;
    private JongoCollection campSessions;

    public ActivityResource(@Named("activities") JongoCollection activities, @Named("campSessions") JongoCollection campSessions){
        this.activities = activities;
        this.campSessions = campSessions;
    }

    //this is to get schedule to work with for stuff
    @GET("/activities")
    public Iterable<Activity> getActivities(){
        JongoCollection activitiesCopy = activities;
        JongoCollection campSessionsCopy = campSessions;
        ConstraintChecker cc = new ConstraintChecker(activitiesCopy, campSessionsCopy);
        //constraint checker assigns updated schedule to activitiesCopy, campSessionCopy
        cc.update();
        return activities.get().find().as(Activity.class);//returns copy of activities
    }

    @POST("/activities")//will work for creating activities specific to sessions or generic ones
    public Activity createActivity(Activity activity){
        activities.get().save(activity);
        return activity;
    }



}
