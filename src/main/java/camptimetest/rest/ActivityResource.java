package camptimetest.rest;

import camptimetest.domain.Activity;
import camptimetest.domain.CampSession;
import camptimetest.domain.ConstraintChecker;
import camptimetest.domain.StaffConstraintChecker;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import restx.annotations.GET;
import restx.annotations.POST;
import restx.annotations.PUT;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import restx.security.PermitAll;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
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
    private ArrayList<String> possibleActivites;
    private ArrayList<String> activitiesAtAreas;

    public ActivityResource(@Named("registrations") JongoCollection registrations, @Named("employees") JongoCollection employees, @Named("activities") JongoCollection activities, @Named("campsessions") JongoCollection campsessions){
        this.activities = activities;
        this.campsessions = campsessions;
        this.employees = employees;
        this.registrations = registrations;
        possibleActivites = new ArrayList<String>(Arrays.asList(new String[]{"pool", "art", "meal", "sports",
                "counselor", "canoeing", "archery", "creek", "check in/out", "unit"
                , "other"}));

        activitiesAtAreas = new ArrayList<>(
                Arrays.asList(new String[]{"pool", "art", "sports", "canoeing", "archery", "creak"}));

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

    @POST("/activities/multi") //receive arraylist of new activities to add to campsession in campsession field
    public CampSession addMultipleActivities(ArrayList<Activity> activitiesToAdd){
        //must set activityArea field -- check if necessary
        if(activitiesToAdd.size() == 0) return new CampSession();
        String sessionName = activitiesToAdd.get(0).getSession();
        CampSession session = campsessions.get().findOne("{name: #}", sessionName).as(CampSession.class);
        for(Activity act: activitiesToAdd){
            if(activitiesAtAreas.contains(act.getTitle())){
                act.setActivityArea(act.getTitle());
            }
            activities.get().save(act);
            session.addActivity(act.getKey());
        }
        campsessions.get().save(session);

        return session;
    }

    @POST("/activities/open")
    public ArrayList<String> getPossibleActivities(Map<String, DateTime> dateInfo){
        String query = CollectionHelper.getDateQuery(new DateTime(dateInfo.get("dateTime")));
        //remove closing bracket
        query = query.substring(0, query.length() - 1);
        String titleQuery = ", title: {$in: #}}";
        String fullQuery = query + titleQuery;
        Iterable<Activity> areaActsAtTime = activities.get().find(fullQuery, activitiesAtAreas).
                projection("{title: 1}").as(Activity.class);
        ArrayList<String> possibleActsAtTime = new ArrayList<>(possibleActivites);
        for(Activity act: areaActsAtTime){
            possibleActsAtTime.remove(act.getTitle());
        }
        System.out.println(possibleActsAtTime.toString());
        return possibleActsAtTime;

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
