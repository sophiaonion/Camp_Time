package camptimetest.rest;

import camptimetest.domain.*;
import com.mongodb.DBCollection;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import restx.Status;
import restx.annotations.*;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import restx.security.PermitAll;
import javax.inject.Named;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import static restx.common.MorePreconditions.checkEquals;


/**
 * Created by sophiawang on 2/1/15.
 */


@Component
@RestxResource
@PermitAll
public class CampSessionResource {

        private JongoCollection campSession;
        private JongoCollection campers;
        private JongoCollection registrations;
        private JongoCollection activities;

        public CampSessionResource(@Named("campsessions") JongoCollection campSession,
                                   @Named("campers") JongoCollection campers,
                                   @Named("registrations") JongoCollection registrations,
                                   @Named("activities") JongoCollection activities) {
            this.campSession = campSession;
            this.campers = campers;
            this.registrations = registrations;
            this.activities = activities;
        }

        @GET("/campsessions/all") //get the camp session ids
        public Iterable<CampSession> getSessions(){

            Iterable<CampSession> CSessions = campSession.get().find().as(CampSession.class);
            //Iterable<Data> data = campSession.get().find().as(CampSession.class);
            /*for(CampSession cs : CSessions){

                cs.getSessionID();
            }*/
            return CSessions;
            //return campSession.get().find("{},{name:1}").as(CampSession.class);
        }

    @GET("/campsessions/unapproved")
    public Iterable<SessionRegistration> getUnapproved(){
        System.out.println("test");
        System.out.println("this many: "+registrations.get().count("{approved: false}"));

        return registrations.get().find("{approved: false}").as(SessionRegistration.class);
        //System.out.println(unapp);
        // unapp;
    }


//        @DELETE("/campsessions") //map has keys _id, name
//        public int deleteCampSession(Map<String, String> deleteInfo){
//            //remove campsession ids from registrations, and activities with
//            activities.get().remove("{session: #}", deleteInfo.get("name"));
//            registrations.get().remove("")
//            campSession.get().remove
//        }
        @GET("/campsessions/{id}")
        public CampSession updateCampSession(String id){
            CampSession toUpdate = campSession.get().findOne("{_id: #}", id).as(CampSession.class);
            return toUpdate;
        }

        @DELETE("/campsessions") //map has keys _id, name
        public String deleteCampSession(Map<String, String> deleteInfo){
            //remove campsession ids from registrations, and activities with
            activities.get().remove("{session: #}", deleteInfo.get("name"));
            registrations.get().remove("{sessionID: #}", deleteInfo.get("_id"));
            campSession.get().remove("{_id: #}", new ObjectId(deleteInfo.get("_id")));
            return "200";
        }

        //get campers in a session
        @GET("/campsessions/campers/{id}")
        public Iterable<Camper> getCampersInSession(String id){ //
            //mapping straight to strings or ObjectIds DOESN'T work
            //can use find().projection({camperID: 1, _id:0} to return only camperID field and then can map to String.class
            //to pass to ObjectId constructor easier, but it doesn't seem to return anything in the query
            Iterable<SessionRegistration> regsOfSession = registrations.get().find("{sessionID: #}", id).as(SessionRegistration.class);

            //kind of cumbersome turn Iterable<SessionRegistration> into ArrayList<ObjectIds> in order to query for _id of campers
            ArrayList<ObjectId> camperIDs = new ArrayList<>();
            for(SessionRegistration reg : regsOfSession){
                camperIDs.add(new ObjectId(reg.getCamperID()));
                System.out.println(reg.getCamperID());
            }
            //things that didn't work... using .as(ObjectId.class) to return only session IDs as a group of object IDs
            //then using Lists.newArrayList(Iterable) to turn Iterable into list of ObjectIds
            return campers.get().find("{_id: {$in:#}}", camperIDs).as(Camper.class);
        }

        @GET("/campsessions/agegroup/{agegroup}")
        public Iterable<CampSession> getCampSessionsOfAgeGroup(String agegroup){
            return campSession.get().find("{ageGroup: #}", agegroup).as(CampSession.class);
        }

        //get the campsessions that this counselor will be work with
        @GET("/campsessions/counselor/{counselorId}") //TODO need to test this
        public Iterable<CampSession> getCampSessionsOfCounselor(String counselorId){
            return campSession.get().find("{counselors: #}", counselorId).as(CampSession.class);
        }

//        @GET("/campsessions/{sessionName}")
//        public ArrayList<Activity> getActivities(String sessionName){
//            Iterable<CampSession> itr= campSession.get().findOne("{name: #}", sessionName).as(CampSession.class);
//            ArrayList<Activity> activities= new ArrayList<Activity>();
//            for(CampSession session : itr){
//                activities= session.getActivities();
//
//            }
//            return activities;
//        }

        @POST("/campsessions")
        public CampSession createCampSession(Map<String, Object> info){//change camp session to a mpa, pull out each individual thing and save into campsession
            CampSession newCS = new CampSession();

            //convert stuff into dates
            DateTimeFormatter fmt = DateTimeFormat.forPattern("yy-MM-dd");
            DateTime start = new DateTime( fmt.parseDateTime(info.get("startDate").toString()));
            DateTime end = new DateTime( fmt.parseDateTime(info.get("endDate").toString()));

            //take items from map of info from inputted page
            newCS.setStartDate(start);
            newCS.setEndDate(end);
            newCS.setName(String.valueOf(info.get("name")));
            newCS.setAgeGroup(String.valueOf(info.get("ageGroup")));
            newCS.setEnrollmentCap(Integer.valueOf(String.valueOf(info.get("enrollmentCap"))));
            newCS.setCounselorIDs((ArrayList<String>) info.get("counselors"));

            //make activitities
            ArrayList<Activity> activityList = new ArrayList<Activity>();

            @SuppressWarnings("unchecked")
            ArrayList< Map<String, String> > activityInfo = (ArrayList< Map<String, String> >) info.get("activities");
            for(int i=0; i<activityInfo.size(); i++) {
                if ( true) {//activity is filled out
                    Activity a = new Activity();
                    System.out.println("title: " + activityInfo.get(i).get("title"));
                    String title = String.valueOf(activityInfo.get(i).get("title"));
                    a.setTitle(title);
                    a.setSession(String.valueOf(info.get("name")));

                    if (title.equals("pool"))
                        a.setActivityArea("pool");
                    else if (title.equals("art"))
                        a.setActivityArea("art");
                    else if (title.equals("sports"))
                        a.setActivityArea("sports");
                    else if (title.equals("canoeing"))
                        a.setActivityArea("canoeing");
                    else if (title.equals("archery"))
                        a.setActivityArea("archery");
                    else if (title.equals("creek"))
                        a.setActivityArea("creek");

                    if (!(String.valueOf(activityInfo.get(i).get("day")).isEmpty())) {//day # if has a value in it (i.e. is fixed-time)
                        //set time to appropriate time
                        if (activityInfo.get(i).get("time") != null) { //if the activity were required time field would be null
                            String[] timesplit = (activityInfo.get(i).get("time")).toString().split(":");//just get hour number from given time string

                            DateTime day = new DateTime(start.plusDays(Integer.parseInt(activityInfo.get(i).get("day"))).withZone(DateTimeZone.UTC));//make day be startDate plus day number in session
                            //hours, minutes, seconds, milli
                            DateTime time1 = new DateTime(day.withTime(Integer.parseInt(timesplit[0]), 0, 0, 0).withZone(DateTimeZone.UTC));//set time to given time
                           System.out.print("adding fixed time: "+time1);
                            a.setTime(time1);
                            //a.setIsSet(false);
                            a.setFixed(true);//activity is fixed time
                            a.setIsSet(true);//activity time is vacuously set
                        } else {
                            a.setFixed(false);//activity is not fixed time
                            a.setIsSet(false);//activity time is not yet set by algorithm
                        }
                    }
                    activities.get().save(a);
                    System.out.println(a.getKey());
                    newCS.addActivity(a.getKey());
                }
            }
            System.out.println("Number of activities created: " + activityList.size());

//            newCS.setActivities(activityList);
            campSession.get().save(newCS);
            return newCS;
        }

        //sessionid is passed in url
        //and rest of parameters are passed in as query parameters
        //similar to post method
        @PUT("/campsessions/{sessionID}")
        public CampSession updateCampSession(String sessionID, CampSession campsession) {
            //make sure sent session to update matches sent URL so that intended session is the
            //one being updated, strings passed in our for unequal error message
            checkEquals("sessionID", sessionID, "campsession.sessionID", campsession.getSessionID());
            campSession.get().save(campsession);
            return campsession;
        }

    @DELETE("/campers/registrations/byregid/{regId}")
    public Status deleteRegistrationByRegId(String regId){
        System.out.println("Get Here");
        registrations.get().remove("{_id:#}", new ObjectId(regId));
        return Status.of("deleted");
    }
}
