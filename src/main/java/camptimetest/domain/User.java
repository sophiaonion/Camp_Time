package camptimetest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;
import restx.security.BCryptCredentialsStrategy;
import restx.security.RestxPrincipal;

import javax.inject.Named;
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

//    public String getUsertype(){
//        return usertype;
//    }
//
//    public User setUsertype(String usertype){
//        this.usertype = usertype;
//        return this;
//    }

    @Override @JsonIgnore //won't try to convert to property of json object
    public ImmutableSet<String> getPrincipalRoles() {
        return ImmutableSet.copyOf(roles);
    }
}
