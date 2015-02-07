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

    public ActivityResource(@Named("activities") JongoCollection activities){
        this.activities = activities;
    }

    //this is to get schedule to work with for stuff
    @GET("/activities")
    public Iterable<Activity> getActivities(){
        JongoCollection activitiesCopy = activities;
        ConstraintChecker cc = new ConstraintChecker(activitiesCopy);


        return activities.get().find("{startTime: null}").as(Activity.class);
    }

    @POST("/activities")//will work for creating activities specific to sessions or generic ones
    public Activity createActivity(Activity activity){
        activities.get().save(activity);
        return activity;
    }



}
