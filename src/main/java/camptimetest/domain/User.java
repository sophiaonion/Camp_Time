package camptimetest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;
import restx.security.BCryptCredentialsStrategy;
import restx.security.RestxPrincipal;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by sophiawang on 2/7/15.
 */
public class User implements RestxPrincipal {
    @Id
    @ObjectId
    private String userID;
    private String name;//still referred to as _id on client side!!!
    //still have to have some designated id field to use though
    private String password;
    private Collection<String> roles;
    private ArrayList<String> campers = new ArrayList<>();

    public User setUserID(final String userID){
        this.userID = userID;
        return this;
    }

    public String getUserID(){
        return this.userID;
    }

    public String getName(){
        return name;
    }

    public User setName(String name){
        this.name = name;
        return this;
    }

    //permission levels implement restx.security
    public Collection<String> getRoles() {
        return roles;
    }

    public User setRoles(final Collection<String> roles) {
        this.roles = roles;
        return this;
    }

    public String getPassword(){
        return password;
    }

    public User setPassword(String password){

        this.password = password;
        return this;
    }

    public ArrayList<String> getCampers() {
        return campers;
    }

    public void setCampers(ArrayList<String> campers) { this.campers = campers;
    }

    public User addCamper(String objectId){//must be object id of activity

        this.campers.add(objectId);
        return this;
    }

    public User addCampers(ArrayList<String> campersIds){
        campers.addAll(campersIds);
        return this;
    }

    public User removeCamper(String objectId){//must be object id of activity

        this.campers.remove(objectId);
        return this;
    }

    @Override @JsonIgnore //won't try to convert to property of json object
    public ImmutableSet<String> getPrincipalRoles() {
        return ImmutableSet.copyOf(roles);
    }
}
