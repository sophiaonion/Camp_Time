package camptimetest.rest;

import camptimetest.domain.Activity;
import camptimetest.domain.CampSession;
import camptimetest.domain.Camper;
import camptimetest.domain.SessionRegistration;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import restx.annotations.*;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import restx.security.PermitAll;
import javax.inject.Named;

import java.util.ArrayList;
import java.util.Map;

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

        @GET("/campsessions")
        public Iterable<CampSession> getSessions(){
            return campSession.get().find().as(CampSession.class);
        }

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


        @POST("/campsessions")
        public CampSession createCampSession(Map<String, Object> info){//change camp session to a mpa, pull out each individual thing and save into campsession
            CampSession newCS = new CampSession();

            //convert stuff into dates
            DateTimeFormatter fmt = DateTimeFormat.forPattern("yy-MM-dd");
            System.out.println(info.get("startDate").toString());
            System.out.println(info.get("endDate").toString());
            DateTime start = fmt.parseDateTime(info.get("startDate").toString());
            DateTime end = fmt.parseDateTime(info.get("endDate").toString());

            //take items from map of info from inputted page
            newCS.setStartDate(start);
            newCS.setEndDate(end);
            newCS.setName(String.valueOf(info.get("name")));
            newCS.setAgeGroup(String.valueOf(info.get("ageGroup")));
            newCS.setEnrollmentCap(Integer.valueOf(String.valueOf(info.get("enrollmentCap"))));

            //make activitites
            ArrayList<Activity> activityList = new ArrayList<Activity>();

            @SuppressWarnings("unchecked")
            ArrayList< Map<String, String> > activityInfo = (ArrayList< Map<String, String> >) info.get("activities");
            for(int i=0; i<activityInfo.size(); i++) {
                Activity a = new Activity();
                System.out.println("title: " + activityInfo.get(i).get("title"));
                String title = String.valueOf(activityInfo.get(i).get("title"));
                a.setTitle(title);
                a.setSession(String.valueOf(info.get("name")));

                if(title == "unit")
                    a.setActivityArea("unit");
                else if(title == "pool")
                    a.setActivityArea("pool");
                else if (title == "art")
                    a.setActivityArea("art");
                else if (title == "sports")
                    a.setActivityArea("sports");
                else if (title == "canoeing")
                    a.setActivityArea("canoeing");
                else if (title == "archery")
                    a.setActivityArea("archery");
                else if (title == "creek")
                    a.setActivityArea("creek");
                System.out.print("HEHREHRHEH");

//                switch (name) {
//                    case "unit": a.setActivityArea("unit");
//                    case "pool": a.setActivityArea("pool");
//                    case "art":a.setActivityArea("art");
//                    case "sports":a.setActivityArea("sports");
//                    case "canoeing": a.setActivityArea("canoeing");
//                    case "archery": a.setActivityArea("archery");
//                    case "creek": a.setActivityArea("creek");
//                }
                if( !( String.valueOf(activityInfo.get(i).get("day")).isEmpty() ) ) {//day # if has a value in it (i.e. is fixed-time)
                    //set time to appropriate time
                    if (activityInfo.get(i).get("time") != null) { //if the activity is required time field will be null
                        String[] timesplit = (activityInfo.get(i).get("time")).toString().split(":");//just get hour number from given time string

                        DateTime day = new DateTime(start.plusDays(Integer.parseInt(activityInfo.get(i).get("day"))));//make day be startDate plus day number in session
                        //hours, minutes, seconds, milli
                        DateTime time = day.withTime(Integer.parseInt(timesplit[0]), 0, 0, 0);//set time to given time
                        a.setTime(time);
                        //a.setIsSet(false);
                        a.setFixed(true);//activity is fixed time
                        a.setIsSet(true);//activity time is vacuously set
                    }
                    else {
                        a.setFixed(false);//activity is not fixed time
                        a.setIsSet(false);//activity time is not yet set by algorithm
                    }
                }
                activityList.add(a);
                activities.get().save(a);
            }
            System.out.println("Number of activities created: " + activityList.size());
            newCS.setActivities(activityList);
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


}
