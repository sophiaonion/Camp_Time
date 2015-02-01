package camptimetest.domain;

import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

/**
 * Created by Eric on 2/1/2015.
 */
public class SessionRegistration {

    @ObjectId
    @Id
    private String key;
    private String camperID;
    private String sessionID;


    public SessionRegistration(String camperID, String sessionID){
        this.camperID = camperID;
        this.sessionID = sessionID;
    }

}
