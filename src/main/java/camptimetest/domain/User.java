package camptimetest.domain;

import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

/**
 * Created by sophiawang on 2/7/15.
 */
public class User {
    @Id
    @ObjectId
    private String username;//still referred to as _id on client side!!!
    //still have to have some designated id field to use though
    private String password;
    private String usertype;


    public String getUsername(){
        return username;
    }

    public User setUsername(String name){
        this.username = name;
        return this;
    }

    public String getPassword(){
        return password;
    }

    public User setPassword(String password){
        this.password = password;
        return this;
    }

    public String getUsertype(){
        return usertype;
    }

    public User setUsertype(String usertype){
        this.usertype = usertype;
        return this;
    }

}
