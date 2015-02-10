package camptimetest.rest;

import camptimetest.domain.CampSession;
import camptimetest.domain.Camper;
import com.google.common.base.Optional;
import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;
import restx.Status;
import restx.annotations.*;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import restx.security.PermitAll;
import camptimetest.domain.SessionRegistration;
import restx.security.RolesAllowed;
import static camptimetest.AppModule.Roles.*;

import javax.inject.Named;
import java.io.Console;
import java.util.ArrayList;

/**
 * Created by Eric on 2/1/2015.
 */
@Component @RestxResource
@PermitAll
public class CamperResource {
    private JongoCollection registrations;
    private JongoCollection campers;
    private JongoCollection campSessions;

    public CamperResource(@Named ("registrations") JongoCollection registrations,
                          @Named ("campers") JongoCollection campers,
                          @Named("campsessions") JongoCollection campSessions){
        this.registrations = registrations;
        this.campers = campers;
        this.campSessions=campSessions;
    }

    @PUT("/campers/{camperID}/{sessionID}")
    public SessionRegistration registerCamper(String camperID, String sessionID){
        SessionRegistration reg = new SessionRegistration(camperID, sessionID);
        registrations.get().save(reg);
        return reg;

    }

    @GET("/campers")
    public Iterable<Camper> getCampers(){
        return campers.get().find().as(Camper.class);
    }
//    @GET("/campers/{camperID}")
//    public Iterable<Camper> getCamper(Optional<String> camperID) {
//        if(camperID.isPresent()) {
//            registrations.get().find(new ObjectId(camperID)).as(Camper);
//        }
//        else {
//
//        }
//    }


    @GET("/campers/registrations/{camperID}")
    public Iterable<CampSession> getCampers(String camperID){
        Iterable<SessionRegistration> regsOfSession= registrations.get().find("{camperID: #}", camperID).as(SessionRegistration.class);
        ArrayList<ObjectId> sessionIDs = new ArrayList<>();
        for(SessionRegistration reg : regsOfSession){
            sessionIDs.add(new ObjectId(reg.getSessionID()));
            System.out.println("sessionID"+ reg.getSessionID());
        }

        return campSessions.get().find("{_id: {$in:#}}", sessionIDs).as(CampSession.class);
    }


    @POST("/campers")
    public Camper createCamper(Camper camper){
        campers.get().save(camper);
        return camper;

    }

    @DELETE("/campers/{camperID}/{sessionID}")
    public Status deleteRegistration(String camperID, String sessionID){
        registrations.get().remove("{camperID:\""+camperID+"\", sessionID:\""+sessionID+"\"}");
        return Status.of("deleted");
    }
}
