package camptimetest.rest;

import camptimetest.AppModule;
import camptimetest.domain.Activity;
import camptimetest.domain.Camper;
import camptimetest.domain.Employee;
import camptimetest.domain.User;
import org.bson.types.ObjectId;
import org.eclipse.jetty.http.HttpStatus;
import restx.Status;
import restx.annotations.*;
import restx.exceptions.RestxErrors;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import restx.security.*;
import static camptimetest.AppModule.Roles.*;

import static camptimetest.AppModule.Roles.*;
import com.google.common.base.Optional;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by sophiawang on 2/7/15.
 */

@Component
@RestxResource

public class UserResource {

    private final MyUserRepository myUserRepository; //Database access object, will inject when resource is created/called
    //@Named designates which collection to connect to in database whose name is specified in AppModule
    private final String adminPasswordHash;
    private CredentialsStrategy crypper;
    private final JongoCollection campers;

    public UserResource(MyUserRepository myUserRepository,
                        @Named("credentialsStrategy")CredentialsStrategy credentialsStrategy,
                        @Named("restx.admin.passwordHash") String adminPasswordHash,
                        @Named("campers") JongoCollection campers) {
        this.myUserRepository = myUserRepository;
        this.adminPasswordHash = adminPasswordHash;
        this.crypper = credentialsStrategy;
        this.campers= campers;
    }

    @GET("/users")
    public Iterable<User> findUser() {
        return myUserRepository.findAllUsers();
    }

    @PermitAll
    @POST("/users") //user repository handles hashing
    public User createUser(User user)
    {
        //crypper takes name parameter but doesn't do anything with, just hashes password with underlying
        //BCrypt mechnism...implements CredentialsStrategy interface where other implementations might use
        //second arg is hashed
        user.setPassword(crypper.cryptCredentialsForStorage(user.getName(), user.getPassword()));
        return myUserRepository.createUser(user);
    }

    @PermitAll
    @POST("/login")
    public User logIn(User user){
        Optional<User> dbOptUser = myUserRepository.findUserByName(user.getName());
        if (dbOptUser.isPresent()){
            User dbUser =  dbOptUser.get();//turn into user for convenience
            //checkCredentials parameters username, sent password, stored password
            //again doesnt do anything with username parameter
            Boolean passwordMatch = crypper.checkCredentials(dbUser.getName(), user.getPassword(), dbUser
            .getPassword());
            if(passwordMatch){
                //authenticateAs creates new session with permissions based on roles of user
                RestxSession.current().authenticateAs(dbUser);
                return dbUser; //user from database returned
            } else {
                return (new User()).setName("pw"); //dummy user sent back denoting wrong password
            }
        } else {
            System.out.println("user login not present");
            return (new User()).setName("usr"); //
        }
    }

    //get the Session key of the current session
    @PermitAll
    @GET("/login/current")
    public String currentSession(){
        String sessionKey = RestxSession.current().get(String.class, Session.SESSION_DEF_KEY).get();
        RestxPrincipal principal = RestxSession.current().getPrincipal().get();

        Session test = new Session(sessionKey, principal);
        return test.getKey();
    }

    @PermitAll// delete the current session by session key
    @DELETE("/login/current/{sessionKey}")
    public String logout(String sessionKey){
        RestxSession.current().clearPrincipal();
        RestxSession.current().define(String.class, Session.SESSION_DEF_KEY, null);
        return "200";
    }

    @PermitAll
    @GET("/login/current/user")
    public User getUser(){
        return AppModule.currentUser();
    }

    @PermitAll
    @GET("/login/role")
    public String getUserRole(){
        return AppModule.currentUser().getRoles().iterator().next();
    }

    //get sent Map of key value pairs
    //user_id and camper_id
    @PUT("/users/campers/add")
    public String addCamperToUser(Map<String, String> values){
        String camperId = (String)values.get("camper_id");
        String userId = (String)values.get("user_id");

        Camper camper = campers.get().find("{_id: #}", new ObjectId(camperId)).as(Camper.class).iterator().next();
        User user = myUserRepository.findUserByKey(userId).get();


        camper.setUser(userId);
        campers.get().save(camper);

        user.addCamper(camperId);
        myUserRepository.updateUser(user);

        return "200";
    }

    //get sent Map of key value pairs
    //user_id and camper_id
    @PUT("/users/campers/remove")
    public String removeCamperFromUser(Map<String, String> values){
        String camperId = (String)values.get("camper_id");
        String userId = (String)values.get("user_id");

        Camper camper = campers.get().find("{_id: #}", new ObjectId(camperId)).as(Camper.class).iterator().next();
        User user = myUserRepository.findUserByKey(userId).get();

        camper.removeUser();
        campers.get().save(camper);

        user.removeCamper(camperId);
        myUserRepository.updateUser(user);

        return "200";
    }

}
