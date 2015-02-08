package camptimetest.rest;

import camptimetest.domain.CampSession;
import camptimetest.domain.Employee;
import camptimetest.domain.SessionRegistration;
import camptimetest.domain.User;
import org.bson.types.ObjectId;
import restx.Status;
import restx.annotations.DELETE;
import restx.annotations.GET;
import restx.annotations.POST;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import restx.security.PermitAll;

import javax.inject.Named;

/**
 * Created by sophiawang on 2/7/15.
 */

@Component
@RestxResource
@PermitAll
public class UserResource {

    private JongoCollection users; //Database access object, will inject when resource is created/called

    //@Named designates which collection to connect to in database whose name is specified in AppModule
    public UserResource(@Named("users") JongoCollection employees) {this.users = employees;}

    @GET("/users")
    public Iterable<User> findUser() {
        return users.get().find().as(User.class);
    }

    @GET("/users/{username}/{password}")
    public String authentication(String username, String password) { //& return usertype
        String usertype="";
        String query = "{username:\"" + username + "\", password:\"" + password + "\"}";
        Iterable<User> result = users.get().find(query).as(User.class);
        for(User itr : result){
            usertype= itr.getUsertype();
        }
        return usertype;
    }

    @POST("/users")
    public User createUser(User user){
        users.get().save(user);
        return user;
    }

//    @DELETE("/users/{userID}") //!!!!!!!!!!!!!!!!!!!!!!!!
//    public Status deleteEmployee(String employeeID){
//        ObjectId employeeId= new ObjectId(employeeID);
//        employees.get().remove("{_id:#}", employeeId);
//        return Status.of("deleted");
//    }

}
