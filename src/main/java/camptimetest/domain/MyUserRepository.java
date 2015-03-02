package camptimetest.domain;

import camptimetest.domain.User;
import org.bson.types.ObjectId;
import restx.admin.AdminModule;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import restx.jongo.JongoUserRepository;
import restx.security.CredentialsStrategy;


import javax.inject.Named;
import java.util.Arrays;

import static camptimetest.AppModule.Roles.*;

/**
 * Created by Eric on 2/8/2015.
 */
@Component
public class MyUserRepository extends JongoUserRepository<User>{
    //creates default user
    public static final User defaultAdminUser = new User ()
            .setUserID(new ObjectId().toString())
            .setName("admin")
            .setRoles(Arrays.asList(ADMIN, AdminModule.RESTX_ADMIN_ROLE));

    //sets way for Repository to get key to match users in database
    public static final RefUserByKeyStrategy<User> USER_REF_STRATEGY = new RefUserByKeyStrategy<User>() {
        @Override
        protected String getId(User user) {
            return user.getUserID();
        }
    };

    public MyUserRepository(@Named("users") JongoCollection users,
                             @Named("usersCredentials") JongoCollection usersCredentials,
                             @Named("credentialsStrategy") CredentialsStrategy credentialsStrategy) {
        super(
                users, usersCredentials,
                USER_REF_STRATEGY, credentialsStrategy,
                User.class, defaultAdminUser
        );
    }
}
