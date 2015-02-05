package camptimetest.rest;

import camptimetest.domain.Activity;
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

    //think we can query for null fields to get generic activites
    //http://docs.mongodb.org/manual/faq/developers/#faq-developers-query-for-nulls
    @GET("/activities/generic")
    public Iterable<Activity> getActivities(){
        return activities.get().find("{startTime: null}").as(Activity.class);
    }

    @POST("/activities")//will work for creating activities specific to sessions or generic ones
    public Activity createActivity(Activity activity){
        activities.get().save(activity);
        return activity;
    }



}
