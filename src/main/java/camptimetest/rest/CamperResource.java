package camptimetest.rest;

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

    public CamperResource(@Named ("registrations") JongoCollection campers){
        this.registrations = registrations;
    }

//    @GET("/campers")
//    public Iterable<camp>

    @PUT("/campers/{camperID}/{sessionID}")
    public void registerCamper(String camperID, String sessionID){
        SessionRegistration reg = new SessionRegistration(camperID, sessionID);
        registrations.get().save(reg);

    }
}
