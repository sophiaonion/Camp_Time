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
import java.util.Map;

/**
 * Created by Eric on 2/1/2015.
 */
@Component
@RestxResource
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

    @PUT("/campers/{camperID}/{sessionID}/{approved}")
    public SessionRegistration registerCamper(String camperID, String sessionID, String approved){

        SessionRegistration reg = new SessionRegistration(camperID, sessionID, approved);
        //SessionRegistration a = registrations.get().find("{_id: #}");
        registrations.get().save(reg);
        return reg;

    }

    @PUT("/campers/approve")
    public void approveCampers(Map<String, Object> reg){
        ArrayList<String> registrationIDs = (ArrayList<String>) reg.get("ids");
        for(int i=0; i<registrationIDs.size(); i++) {
            SessionRegistration approve = registrations.get().findOne("{_id: #}", new ObjectId(registrationIDs.get(i))).as(SessionRegistration.class);
            approve.setApproved(true);
            registrations.get().save(approve);
        }
        return;
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

    @RolesAllowed({ADMIN,CUSTOMER, COUNSELOR, SPECIALTY})
    @GET("/campers/customer/{customerID}") //get Campers by user
    public Iterable<Camper> getCampersforCustomer(String customerID){
        return campers.get().find("{user_id: #}", customerID).as(Camper.class);
    }

    @GET("/campers/camperInfo/{camperID}")
    public Iterable<Camper> getCamper(String camperID){
        System.out.println("In");

        return campers.get().find("{_id: #}", new ObjectId(camperID)).as(Camper.class);
    }

    @GET("/campers/registrations/approve/{camperID}")
    public Iterable<CampSession> getCampersApprovedSessions(String camperID){
        Iterable<SessionRegistration> regsOfSession= registrations.get().find("{camperID: #, approved:true}", camperID).as(SessionRegistration.class);
        ArrayList<ObjectId> sessionIDs = new ArrayList<>();
        for(SessionRegistration reg : regsOfSession){
            sessionIDs.add(new ObjectId(reg.getSessionID()));
            System.out.println("sessionID"+ reg.getSessionID());
        }

        return campSessions.get().find("{_id: {$in:#}}", sessionIDs).as(CampSession.class);
    }

    @RolesAllowed({ADMIN,COUNSELOR})
    @GET("/campers/all")
    public Iterable<Camper> getCampers(){
        return campers.get().find().as(Camper.class);
    }
    
    @PermitAll
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


    @GET("/campers/registrations/approved/{camperID}")
    public Iterable<CampSession> getApprovedRegistrations(String camperID){
        Iterable<SessionRegistration> regsOfSession= registrations.get().find("{camperID: #, approved: true}", camperID).as(SessionRegistration.class);
        ArrayList<ObjectId> sessionIDs = new ArrayList<>();
        for(SessionRegistration reg : regsOfSession){
            sessionIDs.add(new ObjectId(reg.getSessionID()));
            System.out.println("sessionID"+ reg.getSessionID());
        }

        return campSessions.get().find("{_id: {$in:#}}", sessionIDs).as(CampSession.class);
    }


 //   @GET("/campers/unapproved")
 //   public Iterable<SessionRegistration> getUnapprovedRegistrations(){
 //       System.out.println("test");
 //       System.out.println("this many: "+registrations.get().count("{approved: false}"));
 //       Iterable<SessionRegistration> unapp = registrations.get().find("{approved: false}").as(SessionRegistration.class);

 //       return unapp;
 //   }


    @POST("/campers")
    public Camper createCamper(Camper camper){
        campers.get().save(camper);
        return camper;

    }

    @DELETE("/campers/registrations/{camperID}/{sessionID}")
    public Status deleteRegistration(String camperID, String sessionID){
        registrations.get().remove("{camperID:\""+camperID+"\", sessionID:\""+sessionID+"\"}");
        return Status.of("deleted");
    }


    @DELETE("/campers/{camperID}")
    public Status deleteCamper(String camperID){
        registrations.get().remove("{camperID: #}", camperID);
        ObjectId id= new ObjectId(camperID);
        campers.get().remove("{_id: #}", id);

        return Status.of("deleted");

    }
}
