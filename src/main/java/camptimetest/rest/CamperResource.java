package camptimetest.rest;

import camptimetest.domain.Camper;
import com.google.common.base.Optional;
import org.bson.types.ObjectId;
import restx.annotations.GET;
import restx.annotations.PUT;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import restx.security.PermitAll;
import camptimetest.domain.SessionRegistration;
import javax.inject.Named;

/**
 * Created by Eric on 2/1/2015.
 */
@Component @RestxResource
@PermitAll
public class CamperResource {
    private JongoCollection registrations;
    private JongoCollection campers;

    public CamperResource(@Named ("registrations") JongoCollection registrations,
                          @Named ("campers") JongoCollection campers){
        this.registrations = registrations;
    }

//    @GET("/campers")
//    public Iterable<camp>

    @PUT("/campers/{camperID}/{sessionID}")
    public SessionRegistration registerCamper(String camperID, String sessionID){
        SessionRegistration reg = new SessionRegistration(camperID, sessionID);
        registrations.get().save(reg);
        return reg;

    }


//    @GET("/campers/{camperID}")
//    public Iterable<Camper> getCamper(Optional<String> camperID) {
//        if(camperID.isPresent()) {
//            registrations.get().find(new ObjectId(camperID)).as(Camper);
//        }
//        else {
//
//        }
//
//    }
}
