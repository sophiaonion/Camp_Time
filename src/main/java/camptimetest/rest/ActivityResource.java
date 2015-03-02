package camptimetest.rest;

import camptimetest.domain.Activity;
import camptimetest.domain.ConstraintChecker;
import camptimetest.domain.StaffConstraintChecker;
import org.bson.types.ObjectId;
import restx.annotations.GET;
import restx.annotations.POST;
import restx.annotations.PUT;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import restx.security.PermitAll;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Eric on 1/30/2015.
 */
@Component
@RestxResource
@PermitAll
public class ActivityResource {

    private JongoCollection activities;
    private JongoCollection campsessions;
    private JongoCollection employees;
    private JongoCollection registrations;

    public ActivityResource(@Named("registrations") JongoCollection registrations, @Named("employees") JongoCollection employees, @Named("activities") JongoCollection activities, @Named("campsessions") JongoCollection campsessions){
        this.activities = activities;
        this.campsessions = campsessions;
        this.employees = employees;
        this.registrations = registrations;
    }

    //this is to get schedule to work with for stuff
    @GET("/activities")
    public Iterable<Activity> getActivities(){

        //constraint checker assigns updated schedule to activitiesCopy, campSessionCopy
        ConstraintChecker cc = new ConstraintChecker(activities, campsessions);
        cc.update();

        StaffConstraintChecker scc = new StaffConstraintChecker(activities, employees, registrations, campsessions);
        scc.update();

        return activities.get().find().as(Activity.class);//returns copy of activities
    }

    //get list of activities with certain activity area
    @GET("/activities/{areaName}")
    public Iterable<Activity> getAreaActivities(String areaName){
        return activities.get().find("{activityArea:#}",areaName).as(Activity.class);
    }


    @POST("/activities")//will work for creating activities specific to sessions or generic ones
    public Activity createActivity(Activity activity){
        activities.get().save(activity);
        return activity;
    }

    @PUT("/activities/campsession")
    public Iterable<Activity> getSessionActivities(Map<String, ArrayList<String>> data){
        System.out.println(data.get("activityIds").get(1));
        ArrayList<ObjectId> actIds = new ArrayList<>();
        for(String id: data.get("activityIds")){
            actIds.add(new ObjectId(id));
        }
        Iterable<Activity> sessionActivities =  activities.get().find("{_id: {$in: #}}", actIds).as(Activity.class);
        return sessionActivities;
    }




}
