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
    private boolean approved;


    public SessionRegistration(String camperID, String sessionID, String approval){
        this.camperID = camperID;
        this.sessionID = sessionID;
        this.approved=Boolean.valueOf(approval);
    }

    public SessionRegistration(){}

    public String getKey(){
        return key;
    }

    public String getCamperID(){
            return camperID;
    }

    public String getSessionID(){
        return sessionID;
    }

    public SessionRegistration setKey(String key){
        this.key = key;
        return this;
    }

    public SessionRegistration setCamperID(String camperID){
        this.camperID = camperID;
        return this;
    }

    public SessionRegistration setSessionID(String sessionID){
        this.sessionID = sessionID;
        return this;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
