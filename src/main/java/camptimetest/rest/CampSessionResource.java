package camptimetest.rest;

import camptimetest.domain.CampSession;
import camptimetest.domain.Camper;
import camptimetest.domain.SessionRegistration;
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
import java.util.List;

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
        public Iterable<Camper> getCampersInSession(String id){
            Iterable<SessionRegistration> regsOfSession = registrations.get().find("{sessionID: #}", id).
                    projection("{camperID: 1, sessionID: 0}").as(SessionRegistration.class);
            ArrayList<String> camperIDs = new ArrayList<>();
            for(SessionRegistration reg : regsOfSession){
                camperIDs.add(reg.getCamperID());
            }
            Iterable<Camper> campersOfSession = campers.get().find("{_id: {$in:#}}", camperIDs).as(Camper.class);

            return campersOfSession;
        }

        @POST("/campsessions")
        public CampSession createCampSession(CampSession newCS){
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
