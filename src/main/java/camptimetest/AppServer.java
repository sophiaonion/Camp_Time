package camptimetest;

import com.google.common.base.Optional;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import restx.server.WebServer;
import restx.server.JettyWebServer;
/**
 * Use this class to run app
 * Using system.getEnv(PORT) makes it deployable from heroku?? might be useful for later
 */


public class AppServer {
    //web.xml sets up servelet for routing paths to resources
    //web app location is for static client files to be served
    public static final String WEB_INF_LOCATION = "src/main/webapp/WEB-INF/web.xml";
    public static final String WEB_APP_LOCATION = "src/main/webapp/";

    public static void main(String[] args) throws Exception {

        DateTimeZone.setDefault(DateTimeZone.UTC);
        System.out.println(DateTimeZone.getDefault());
        //google's guava allows for optional parameters with Optional.fromNullable
        //get port from hosting env or default to 8080
        int port = Integer.valueOf(Optional.fromNullable(System.getenv("PORT")).or("8080"));

        //create webserver 0.0.0.0 IP can also be accessed as localhost when run on own comp
        WebServer server = new JettyWebServer(WEB_INF_LOCATION, WEB_APP_LOCATION, port, "0.0.0.0");


        //dev mode produces admin console UI and enforces statelessness by throwing away resources between calls
        System.setProperty("restx.mode", System.getProperty("restx.mode", "dev"));

        //sets root package directory?...needed for finding static client files html, css, etc
        System.setProperty("restx.app.package", "camptimetest");

        server.startAndAwait();
    }

}
