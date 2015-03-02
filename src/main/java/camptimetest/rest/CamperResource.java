package camptimetest.rest;

import camptimetest.domain.CampSession;
import camptimetest.domain.Camper;
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

    @RolesAllowed({ADMIN,COUNSELOR})
    @GET("/campers/{sessionId}") //get Campers by campsession
    public Iterable<Camper> getCampersforCounselor(String sessionId){
        Iterable<SessionRegistration> regs= registrations.get().find("{sessionID: #}", sessionId).as(SessionRegistration.class);
        //ArrayList<Camper> camperList= new ArrayList<>();
        ArrayList<ObjectId> camperIDs = new ArrayList<>();
        for (SessionRegistration reg:regs){
            //String camperId= reg.getCamperID();
            camperIDs.add(new ObjectId(reg.getCamperID()));
            //Camper found = campers.get().find("{_id: {$in:#}}", camperId).as(Camper.class).iterator().next();
            //camperList.add(found);
        }

        return campers.get().find("{_id: {$in:#}}", camperIDs).as(Camper.class);
    }

    @RolesAllowed({ADMIN,CUSTOMER}) //TODO this should be working, need to test
    @GET("/campers/customer/{customerID}") //get Campers by user
    public Iterable<Camper> getCampersforCustomer(String customerID){
        return campers.get().find("{user_id: #}", customerID).as(Camper.class);
    }


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


    @DELETE("/campers/{camperID}")
    public Status deleteCamper(String camperID){
        registrations.get().remove("{camperID: #}", camperID);
        campers.get().remove("{camperID: #}", camperID);

        return Status.of("deleted");

    }
}
