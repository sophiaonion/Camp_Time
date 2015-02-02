package camptimetest.rest;

import camptimetest.domain.CampSession;
import restx.annotations.GET;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import restx.security.PermitAll;
import javax.inject.Named;


/**
 * Created by sophiawang on 2/1/15.
 */


@Component
@RestxResource
@PermitAll
public class CampSessionResource {

        private JongoCollection campSession;

        public CampSessionResource(@Named("campsessions") JongoCollection campSession){
            this.campSession = campSession;
        }

        @GET("/campsessions")
        public Iterable<CampSession> getSessions(){
            return campSession.get().find().as(CampSession.class);
        }


    //todo add list of fixed activities, activity collection??
    //todo create session page
}
