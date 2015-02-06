package camptimetest.rest;

import camptimetest.domain.Activity;
import camptimetest.domain.CampSession;
import camptimetest.domain.Camper;
import camptimetest.domain.SessionRegistration;
import org.bson.types.ObjectId;
import restx.Status;
import restx.annotations.*;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import restx.security.PermitAll;
import javax.inject.Named;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

        public CampSessionResource(@Named("campsessions") JongoCollection campSession,
                                   @Named("campers") JongoCollection campers,
                                   @Named("registrations") JongoCollection registrations){
            this.campSession = campSession;
            this.campers = campers;
            this.registrations = registrations;
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
            Date start = new Date((String) info.get("startDate"));

            //take items from map of info from inputted page
            newCS.setName((String) info.get("name"));
            newCS.setAgeGroup((String) info.get("ageGroup"));
            newCS.setStartDate(start);
            newCS.setEndDate(new Date((String) info.get("endDate")));
            List<Activity> activities;
                //ummm what need to add activities but idk how
//            for(int i=0; i < info.get("activities").length(); i++) {
//                Activity a = new Activity();
//               // a.setTime(new Date())
//                a.setTitle(info.get("activities").get(i).)
//
//            }
//            newCS.setActivities(activities);

            campSession.get().save(newCS);
            return newCS;





//            private String sessionID;
//            private String name;
//            private String ageGroup; //will use 1, 2, 3, 4, 5
//            private int enrollmentCap;
//            private Date startDate;
//            private Date endDate;
//            private List<Activity> activities; //list of all activities, required or not
//            private List<Employee> counselors;
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
