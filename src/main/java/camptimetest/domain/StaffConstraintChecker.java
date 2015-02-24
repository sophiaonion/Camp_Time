package camptimetest.domain;

import com.mongodb.BasicDBList;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import restx.jongo.JongoCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import restx.jongo.JongoCollection;
import org.bson.types.ObjectId;

import java.util.*;
/**
 * Created by rachelfeltes on 2/14/15.
 */


/*
Number codes:
-1: error
0: no conflict
1: activity with no employees
2: only 1 staff when need 2
3:
4: no staff over 18 and woman
5: ratios for camper age not met
6: activity area certification requirements not met
7: employee working 2 activities at same time
8: employee does not have 2 hour break
9: employee does not have 24 hour break
*/

public class StaffConstraintChecker {

    private JongoCollection activities;
    private JongoCollection employees;
    private JongoCollection registrations;
    private JongoCollection campsessions;


    public StaffConstraintChecker(JongoCollection activities, JongoCollection employees, JongoCollection registrations, JongoCollection campsessions) {
        this.activities = activities;
        this.employees = employees;
        this.registrations = registrations;
        this.campsessions = campsessions;
    }

    public JongoCollection update() {
        System.out.println("updating now...");

        //staffing activities
        int i = this.checkConflicts();
        if(i > 0) {
            System.out.println("type "+i+" conflict");
            this.fixConflicts(i); //just here while testing
            i=this.checkConflicts();
        }
        return this.activities;
    }//end update()

    private void fixConflicts(int type) {
        System.out.println("fixing conflicts...");


        DBCursor cursor = activities.get().getDBCollection().find();
        List<DBObject> arrry = cursor.toArray();
        ArrayList<DBObject> actList = new ArrayList<DBObject>(arrry);

        if(type==1) {//have unemployed activities
            //ArrayList<ArrayList<DateTime>> domains = findDomain(actArray, false);
        }//end assign employees to unemployed activities
    }

    private int checkConflicts() {
        System.out.println("checking conflicts...");


        DBCursor cursor = activities.get().getDBCollection().find();
        List<DBObject> arrry = cursor.toArray();
        ArrayList<DBObject> actList = new ArrayList<DBObject>(arrry);

        //if there is an activity without employees
        if( (activities.get().count( "{employees: {$exists: false} }" ) != 0)
                || (activities.get().count( "{employees: null }" ) != 0) ) {//if time is set or not
            System.out.println("type 1 conflict found (no staff)");
            return 1;
        }


        //for each activity make sure has appropriate number of employees
        for(int i=0; i<actList.size(); i++) {
        //get necessary facts to use for the checking of constraints:
            //get number of campers in session
            String session = String.valueOf(actList.get(i).get("session")); //get session name
            String sessionID = campsessions.get().findOne("{name: # }", session).as(CampSession.class).getSessionID();
            ObjectId ID = new ObjectId(sessionID);
            int numCampers = (int) registrations.get().count("{sessionID: #}", ID); //get session id from that

            //get age of campers in session
            String age = String.valueOf(campsessions.get().findOne("{_id: # }", ID).as(CampSession.class).getAgeGroup());

            //get location of activity
            String activityArea = String.valueOf(actList.get(i).get("activityArea"));

            //get number of employees working session
            int numStaff = 0;
            BasicDBList e = null;
            if (actList.get(i).get("employees") != null) {//can maybe get rid of this laterzzz
                e = (BasicDBList) actList.get(i).get("employees");
                numStaff = e.size();
            } else System.out.println("is empty");//return 1;//no employees scheduled yet


        //begin constraint checking
            //at least 2 staff are with campers
            if ((numStaff < 2)) {System.out.println("not enough staff"); return 2;}

            //woman over 18 is with campers
            boolean hasMatureLady = false;
            for (int j = 0; j < e.size(); j++) {
                if (((Employee) e.get(j)).getGender().equals("woman") && ((Employee) e.get(j)).getAge() >= 18) {
                    hasMatureLady = true;
                }
            }
            if (hasMatureLady == false) {
                System.out.println("does not have staff over 18 and woman");
                return 4;
            }

            //check that activities have correct ratios
            switch (age) {
                case "daisy": //need 2 staff for first 6 campers, then 1 staff for every 4 campers after that
                    if(numCampers<= 6) {if(numStaff < 2) {return 5;}}//need 2 staff for first 6 campers
                    else if (numStaff == 2) {return 5;}//separate this instance so won't divide by 0 - more than 6 campers but only 2 staff
                    else if ( (numCampers - 6) / (numStaff - 2) >= 4) {return 5;}//need 1 staff for every 4 campers after that
                    break;
                case "brownie":
                    if (numCampers / numStaff >= 6) {return 5;}
                    break;
                case "junior":
                    if (numCampers / numStaff >= 8) {return 5;}
                    break;
                case "cadette":
                    if (numCampers / numStaff >= 10) {return 5;}
                    break;
                case "senior":
                    if (numCampers / numStaff >= 12) {return 5;}
                    break;
                case "ambassador":
                    if (numCampers / numStaff >= 12) {return 1;}
                    break;
                default:
                    System.out.println("error: no age group assigned");
                    return -1;
            }

            //ensure that areas requiring certifications have those certifications
            if (actList.get(i).get("activityArea") != null) {
                int numLifeGuards = 0;
                boolean hasArt, hasNature, hasArchery, hasStore, canDrive;
                hasArt=false; hasNature=false; hasArchery=false; hasStore=false; canDrive=false;

                //see what certifications activity has
                for(int j=0; j<e.size(); j++) {
                    ArrayList<String> certs = new ArrayList<String> (((Employee) e.get(j)).getCertifications());
                    for(int k=0; k<certs.size(); k++) {
                        if(certs.get(k).equals("lifeguard")) {numLifeGuards++;}
                        if(certs.get(k).equals("art")) {hasArt=true;}
                        if(certs.get(k).equals("nature")) {hasNature=true;}
                        if(certs.get(k).equals("archery")) {hasArchery=true;}
                        if(certs.get(k).equals("store")) {hasStore=true;}
                        if(certs.get(k).equals("drive")) {canDrive=true;}
                    }//end check certifications for employee
                }//end check certifications for all employees in activity


                //based on activity area see if necessary certifications are present
                boolean certsOK = false;
                switch(activityArea) {
                    case "pool"://pretty sure this equation works: need
                        if ((((numCampers + numStaff) / numLifeGuards) < 25) && (((numCampers + numStaff) / (numStaff - ((numCampers + numStaff) / 25))) >= 12)) {certsOK = true;}
                        break;
                    case "canoeing":
                        if ((((numCampers + numStaff) / numLifeGuards) < 25) && (((numCampers + numStaff) / (numStaff - ((numCampers + numStaff) / 25))) >= 12)) {certsOK = true;}
                        break;
                    case "art":
                        if(hasArt) {certsOK = true;}
                        break;
                    case "nature":
                        if(hasNature) {certsOK = true;}
                        break;
                    case "archery":
                        if(hasArchery) {certsOK = true;}
                        break;
                    case "store":
                        if(hasStore) {certsOK = true;}
                        break;
                    default:
                        System.out.println("error: somethin funky going on");
                }
                if (!certsOK) {
                    System.out.println("lacking required certifications");
                    return 6;
                }
            }//end check for required certifications
        }//end check for correct ratios

        //check that employees aren't schedule multiple places at same time
        DBCursor cursor2 = employees.get().getDBCollection().find();
        List<DBObject> arrry2 = cursor2.toArray();
        ArrayList<DBObject> empList = new ArrayList<>(arrry2);
        for(int i=0; i<empList.size(); i++) {//for each employee
            ArrayList<Activity> a = (ArrayList<Activity>) empList.get(i).get("activities");
            for(int j=0; j<a.size(); j++)//for each activity employee is working
                for(int k=0; k<a.size(); k++) { //for each other activity employee is working
                    if (j != k && a.get(j).getTime().equals(a.get(k).getTime())) {//not sure if works, compare each activity
                        System.out.println("employee working two activities at same time");
                        return 7;
                    }
                }
        }

        //check that employees have appropriate number of breaks
        DateTimeFormatter dtf =  DateTimeFormat.forPattern("MM/dd/YYY");
        for(int i=0; i<empList.size(); i++) {
            ArrayList<Activity> a = (ArrayList<Activity>) empList.get(i).get("activities");
            Map<String, boolean[]> working = new HashMap<String, boolean[]>();
            //puts date of each activity into map (no duplicates) of day to array of booleans indicating hour business

            //calculate hours worked for employee
            for(int j=0; j<a.size(); j++) {//for each activity
                String day = dtf.print(a.get(j).getTime());
                int hour = a.get(j).getTime().getHourOfDay();

                //if day hasn't been looked at yet put into map
                working.putIfAbsent(day, new boolean[25]);

                //update taken hour
                boolean[] hours = working.get(day);
                hours[hour] = true;

                //update working schedule
                working.replace(day, hours);
            }//end get hours working for employee

            //check that employee has 2 hour break every day they are working
            for (boolean[] hours : working.values()) {
                if (((hours[9] || hours[10])) && ((hours[10] || hours[11])) && ((hours[13] || hours[14])) && //if working any of the possible 2 hour break slots
                        ((hours[14] || hours[15])) && ((hours[15] || hours[16])) && ((hours[19] || hours[20]))) {
                    System.out.println("employee does not have 2 hour break");
                    return 8;
                }
            }//end check 2 hour break

            //check that employee has 24 hour break every designated interval

        }

        System.out.println("checked conflicts");
        return 0;
    }

}