package camptimetest.domain;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import restx.jongo.JongoCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.joda.time.DateTime;
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
2: insufficiently staffed
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

    //runs fixConflicts() as long as there are conflicts
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

    //assigns employees to activities that are insufficiently staffed and then heuristically repairs resulting conflicts
    private void fixConflicts(int type) {
        System.out.println("fixing conflicts...");

        DBCursor cursor = activities.get().getDBCollection().find();
        List<DBObject> arrry = cursor.toArray();
        ArrayList<DBObject> actList = new ArrayList<DBObject>(arrry);

        if (type==1 || type==2) {//have un(der)employed activities todo CHANGE BACK TO WHILE
        System.out.println("fixing type 1/2 errors...");

            //add staff as required
            for(int i=0; i<actList.size(); i++) {//for each activity
                String aStringID = String.valueOf(actList.get(i).get("_id"));
                Activity a = activities.get().findOne("{_id: #}", new ObjectId(aStringID)).as(Activity.class);//current activity
                ObjectId campSessionID = new ObjectId(campsessions.get().findOne("{name: # }", String.valueOf(a.getSession())).as(CampSession.class).getSessionID());//campsession id
                int numCampers = (int) registrations.get().count("{sessionID: #}", campSessionID); //get number of campers
                String activityArea = String.valueOf(a.getActivityArea());//get location of activity
                ArrayList<String> e = new ArrayList<String>(a.getEmployees());//get number of employees working session
                int numStaff = e.size();

                //add staff with required certifications
                if (a.getActivityArea() != null) {
                    int numLifeGuards = 0;
                    boolean hasArt = false, hasNature = false, hasArchery = false, hasStore = false, canDrive;

                    //find what certifications activity has already
                    for (int j = 0; j < e.size(); j++) {
                        if(employees.get().findOne("{_id: #}", e.get(j)).projection("{certifications: 1}").as(ArrayList.class) != null) {
                            ArrayList<String> certs = new ArrayList<String>(employees.get().findOne("{_id: #}", e.get(j)).projection("{certifications: 1}").as(ArrayList.class));
                            for (int k = 0; k < certs.size(); k++) {
                                if (certs.get(k).equals("lifeguard")) numLifeGuards++;
                                if (certs.get(k).equals("art")) hasArt = true;
                                if (certs.get(k).equals("nature")) hasNature = true;
                                if (certs.get(k).equals("archery")) hasArchery = true;
                                if (certs.get(k).equals("store")) hasStore = true;
                                if (certs.get(k).equals("drive")) canDrive = true;
                            }//end check certifications for employee
                        }
                    }//end count certifications for all employees in activity

                    //based on activity area, add staff with necessary certifications
                    ArrayList<String> emps = new ArrayList(activities.get().findOne("{_id: #}", new ObjectId(a.getKey())).as(Activity.class).getEmployees());

                    //adds employees with required certifications to session until acceptable
                    switch (activityArea) {//todo make compact and get rid of repitition
                        case "pool": //todo check for infinite looping in case of not enough staff for now
                            int count=0;
                            while (count<100000000 && (((numCampers + numStaff) / numLifeGuards) < 25)) {
                                //save employee to activity and activity to employee
                                String eID = findEmployeeToWork(a, "lifeguard", "", false, false);
                                emps.add(eID);
                                Employee empl = employees.get().findOne("{_id: #}", new ObjectId(eID)).as(Employee.class);
                                empl.addActivity(a.getKey());
                                employees.get().save(empl);
                                a.setEmployees(new ArrayList<String>(emps));
                                activities.get().save(a);

                                //update counts
                                numStaff++;
                                numLifeGuards++;
                                count++;
                            }
                            break;
                        case "canoeing":
                            int count2=0;
                            while (count2<100000000 && (((numCampers + numStaff) / numLifeGuards) < 25)) {
                                //save employee to activity and activity to employee
                                String eID = findEmployeeToWork(a, "lifeguard", "", false, false);
                                emps.add(eID);
                                Employee empl = employees.get().findOne("{_id: #}", new ObjectId(eID)).as(Employee.class);
                                empl.addActivity(a.getKey());
                                employees.get().save(empl);
                                a.setEmployees(new ArrayList<String>(emps));
                                activities.get().save(a);

                                //update counts
                                numStaff++;
                                numLifeGuards++;
                                count2++;
                            }
                            break;
                        case "art":
                            if (!hasArt) {
                                //save employee to activity and activity to employee
                                String eID = findEmployeeToWork(a, "art", "", false, false);
                                emps.add(eID);
                                Employee empl = employees.get().findOne("{_id: #}", new ObjectId(eID)).as(Employee.class);
                                empl.addActivity(a.getKey());
                                employees.get().save(empl);
                                a.setEmployees(new ArrayList<String>(emps));
                                activities.get().save(a);
                                numStaff++;
                            }
                            break;
                        case "nature":
                            if (!hasNature) {
                                //save employee to activity and activity to employee
                                String eID = findEmployeeToWork(a, "nature", "", false, false);
                                emps.add(eID);
                                Employee empl = employees.get().findOne("{_id: #}", new ObjectId(eID)).as(Employee.class);
                                empl.addActivity(a.getKey());
                                employees.get().save(empl);
                                a.setEmployees(new ArrayList<String>(emps));
                                activities.get().save(a);
                                numStaff++;
                            }
                            break;
                        case "archery":
                            if (!hasArchery) {
                                //save employee to activity and activity to employee
                                String eID = findEmployeeToWork(a, "archery", "", false, false);
                                emps.add(eID);
                                Employee empl = employees.get().findOne("{_id: #}", new ObjectId(eID)).as(Employee.class);
                                empl.addActivity(a.getKey());
                                employees.get().save(empl);
                                a.setEmployees(new ArrayList<String>(emps));
                                activities.get().save(a);
                                numStaff++;
                            }
                            break;
                        case "store":
                            if (!hasStore) {
                                //save employee to activity and activity to employee
                                String eID = findEmployeeToWork(a, "store", "", false, false);
                                emps.add(eID);
                                Employee empl = employees.get().findOne("{_id: #}", new ObjectId(eID)).as(Employee.class);
                                empl.addActivity(a.getKey());
                                employees.get().save(empl);
                                a.setEmployees(new ArrayList<String>(emps));
                                activities.get().save(a);
                                numStaff++;
                            }
                            break;
                        default:
                            System.out.println("error: somethin funky going on");
                    }
                }

                //add assigned and available counselors to session
                if(!checkSufficientlyStaffed(a)) {//if that wasn't enough
                    ArrayList<String> emps = new ArrayList(activities.get().findOne("{_id: #}", new ObjectId(a.getKey())).as(Activity.class).getEmployees());
                    String counID = findEmployeeToWork(a, null, a.getSession(), false, false);
                    while (counID != "none" && !checkSufficientlyStaffed(a)) {
                        String eID = findEmployeeToWork(a, "archery", "", false, false);
                        emps.add(eID);
                        Employee empl = employees.get().findOne("{_id: #}", new ObjectId(eID)).as(Employee.class);
                        empl.addActivity(a.getKey());
                        employees.get().save(empl);
                        a.setEmployees(new ArrayList<String>(emps));
                        activities.get().save(a);
                        numStaff++;
                        counID = findEmployeeToWork(a, null, a.getSession(), false, false);
                    }
                }

                //add admin & spec staff as required for coverage
                if(!checkSufficientlyStaffed(a)) {
                    //based on activity area, add staff with necessary certifications
                    ArrayList<String> emps = new ArrayList(activities.get().findOne("{_id: #}", new ObjectId(a.getKey())).as(Activity.class).getEmployees());
                    //add counselors assigned to session
                    String adID = findEmployeeToWork(a, null, "", false, false);
                    while (adID != "none" && !checkSufficientlyStaffed(a)) {
                        String aID = findEmployeeToWork(a, "", "", false, false);
                        emps.add(aID);
                        Employee empl = employees.get().findOne("{_id: #}", new ObjectId(aID)).as(Employee.class);
                        empl.addActivity(a.getKey());
                        employees.get().save(empl);
                        a.setEmployees(new ArrayList<String>(emps));
                        activities.get().save(a);
                        numStaff++;
                        adID = findEmployeeToWork(a, null, a.getSession(), false, false);
                    }
                }


                //pick some random stuff
                if(!checkSufficientlyStaffed(a)) {
                    System.out.println("not fixed, need assign whatever");
                }
            }

        }//end assign employees to unemployed activities
    }//end fixConflicts()

    //returns true if given activity is sufficiently staffed according to rules & regulations
    private boolean checkSufficientlyStaffed(Activity a) {
        //get necessary facts to use for the checking of constraints:
            //get number of campers in session
            String session = String.valueOf(a.getSession()); //get session name
            String sessionID = campsessions.get().findOne("{name: # }", session).as(CampSession.class).getSessionID();
            ObjectId ID = new ObjectId(sessionID);
            int numCampers = (int) registrations.get().count("{sessionID: #}", ID); //get session id from that

            //get age of campers in session
            String age = String.valueOf(campsessions.get().findOne("{_id: # }", ID).as(CampSession.class).getAgeGroup());

            //get location of activity
            String activityArea = String.valueOf(a.getActivityArea());

            //get number of employees working session
            ArrayList<String> e = new ArrayList<String>(a.getEmployees());
            int numStaff = e.size();

        //begin constraint checking
            //at least 2 staff are with campers
            if ((numStaff < 2)) {System.out.println("not enough staff"); return false;}

            //woman over 18 is with campers
            boolean hasMatureLady = false;
            for (int j = 0; j < e.size(); j++) {
                if (employees.get().findOne("{_id: #}", e.get(j)).projection("{gender: 1}").equals("woman"))
                    if ((employees.get().findOne("{_id: #}", e.get(j)).projection("{age: 1}").as(int.class) >= 18)) {
                        hasMatureLady = true;
                    }
            }
            if (hasMatureLady == false) {
                System.out.println("does not have staff over 18 and woman");
                return false;
            }

            //check that activities have correct ratios
            switch (age) {
                case "daisy": //need 2 staff for first 6 campers, then 1 staff for every 4 campers after that
                    if(numCampers<= 6) {if(numStaff < 2) {return false;}}//need 2 staff for first 6 campers
                    else if (numStaff == 2) {return false;}//separate this instance so won't divide by 0 - more than 6 campers but only 2 staff
                    else if ( (numCampers - 6) / (numStaff - 2) >= 4) {return false;}//need 1 staff for every 4 campers after that
                    break;
                case "brownie":
                    if (numCampers / numStaff >= 6) {return false;}
                    break;
                case "junior":
                    if (numCampers / numStaff >= 8) {return false;}
                    break;
                case "cadette":
                    if (numCampers / numStaff >= 10) {return false;}
                    break;
                case "senior":
                    if (numCampers / numStaff >= 12) {return false;}
                    break;
                case "ambassador":
                    if (numCampers / numStaff >= 12) {return false;}
                    break;
                default:
                    System.out.println("error: no age group assigned");
                    return true;
            }

            //ensure that areas requiring certifications have those certifications
            if (a.getActivityArea() != null) {
                int numLifeGuards = 0;
                boolean hasArt, hasNature, hasArchery, hasStore, canDrive;
                hasArt=false; hasNature=false; hasArchery=false; hasStore=false; canDrive=false;

                //see what certifications activity has
                for(int j=0; j<e.size(); j++) {
                    ArrayList<String> certs = new ArrayList<String> ( employees.get().findOne("{_id: #}", e.get(j)).projection("{certifications: 1}").as(ArrayList.class));
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
                    return false;
                }
            }//end check for required certifications
             return true;
    }//end checkSufficientlyStaffed()

    //finds available employee (if there is one, if not?? wat do)
    private String findEmployeeToWork(Activity a, String cert, String sessionID, boolean matureLady, boolean conflictOK) {
        System.out.println("finding employee to work");
        ArrayList<String> options = new ArrayList<>();

        //looking for admin or specialty staff to fill in coverage
        if((sessionID==null || sessionID.equals("")) && (cert==null || cert.equals("")) ) {
            Iterable<Employee> adminCursor, specCursor;

            //ensures employee is mature lady if necessary
            if (matureLady) {
                adminCursor = employees.get().find("{job: 'admin', woman: true, age: {$gt: 18}}").as(Employee.class);
                specCursor = employees.get().find("{job: 'specialty', woman: true, age: {$gt: 18}}").as(Employee.class);
            } else {
                adminCursor = employees.get().find("{job: 'admin'}").as(Employee.class);
                specCursor = employees.get().find("{job: 'specialty'}").as(Employee.class);
            }

            //add available admin
            for(Employee e: adminCursor) {
                if(!checkHas24HourBreakOnDate(e, a.getTime()) && !checkIfNeedsTwoHourBreakOnDate(e, a.getTime(), a.getTime().getHourOfDay())) {
                    if(!conflictOK) {
                        //go through each activity emp is working and make sure it isn't same as day being added
                        for(String wID: e.getActivities()) {
                            if (activities.get().findOne("{_id: #}", wID).projection("{time: 1}").as(DateTime.class) == a.getTime()) {
                                options.add(e.getKey());
                            }
                        }
                    }
                    else
                        options.add(e.getKey());
                }
            }

            //add available spec staff
            for(Employee e: specCursor) {
                if(!checkHas24HourBreakOnDate(e, a.getTime()) && !checkIfNeedsTwoHourBreakOnDate(e, a.getTime(), a.getTime().getHourOfDay())) {
                    if(!conflictOK) {
                        //go through each activity emp is working and make sure it isn't same as day being added
                        for(String wID: e.getActivities()) {
                            if (activities.get().findOne("{_id: #}", wID).projection("{time: 1}").as(DateTime.class) == a.getTime()) {
                                options.add(e.getKey());
                            }
                        }
                    } else
                        options.add(e.getKey());
                }
            }
        }

        //looking for staff with specific certification
        if((sessionID==null || sessionID.equals("")) && !(cert==null || cert.equals("")) ) {
            Iterable<Employee> cursor;
            //ensures employee is mature lady if necessary
            if (matureLady) {
                cursor = employees.get().find("{certifications: #, woman: true, age: {$gt: 18}}", cert).as(Employee.class);
            } else {
                cursor = employees.get().find("{certifications: #}", cert).as(Employee.class);
            }

            //adds available employees
            for(Employee e: cursor) {
                if(!checkHas24HourBreakOnDate(e, a.getTime()) && !checkIfNeedsTwoHourBreakOnDate(e, a.getTime(), a.getTime().getHourOfDay())) {
                    if(!conflictOK) {
                        //go through each activity emp is working and make sure it isn't same as day being added
                        for(String wID: e.getActivities()) {
                            if (activities.get().findOne("{_id: #}", wID).projection("{time: 1}").as(DateTime.class) == a.getTime()) {
                                options.add(e.getKey());
                            }
                        }
                    } else
                        options.add(e.getKey());
                }
            }
        }

        //looking for counselor assigned to session
        else {
            Iterable<String> counselorIDs;
            counselorIDs = campsessions.get().find("{sessionID: #}", sessionID).projection("{counselorIDs: 1}").as(String.class);
            //for each counselor
            for(String cID: counselorIDs) {
                Employee e = employees.get().findOne("{_id: #}", cID).as(Employee.class);
                //if isn't on break
                if(!checkHas24HourBreakOnDate(e, a.getTime()) && !checkIfNeedsTwoHourBreakOnDate(e, a.getTime(), a.getTime().getHourOfDay())) {
                    //case 1: need mature lady
                    if (matureLady) {
                        if (e.getGender().equals("woman") && e.getAge() >= 18) {
                            //case 1.1: not ok to be working something else
                            if (!conflictOK) {
                                //go through each activity emp is working and make sure it isn't same as day being added
                                boolean alreadyWorking = false;
                                for (String wID : e.getActivities()) {
                                    if (activities.get().findOne("{_id: #}", wID).projection("{time: 1}").as(DateTime.class) == a.getTime()) {
                                        alreadyWorking = true;
                                    }
                                }
                                if (!alreadyWorking) {
                                    options.add(e.getKey());
                                }
                            }
                            //case 1.2: if ok to be working something else (idk why i would need this just in case for now can remove laterzz)
                            else
                                options.add(e.getKey());
                        }
                    } //end need a mature lady

                    //case 2: don't need mature lady
                    else {
                        if (!conflictOK) {
                            //go through each activity emp is working and make sure it isn't same as day being added
                            boolean alreadyWorking = false;
                            for (String wID : e.getActivities()) {
                                if (activities.get().findOne("{_id: #}", wID).projection("{time: 1}").as(DateTime.class) == a.getTime()) {
                                    alreadyWorking = true;
                                }
                            }
                            if (!alreadyWorking) {
                                options.add(e.getKey());
                            }
                        }
                        else
                            options.add(e.getKey());
                    }//end don't need mature lady
                }//end employee doesn't have break then
            }//end for each counselor assigned to session
        }//end add counselors assigned to session

        //double check that they aren't already working that same activity
        for(int i=0; i<options.size(); i++) {
            if(a.getEmployees().contains(options.get(i))) {
                    options.remove(i);
                    i--;//subtract from i because change the thing?????? todo
            }
        }

        //return result
        if(options.size()>0) {
            System.out.println("findEmployeeToWork found: "+ options.get(0));
            return options.get(0);
        }
        else System.out.println("no employees available 497");
            return "none";
    }//end findEmployeeToWork()

    //for given date, returns true if employee needs 2 hours off then for break,
    //can also get result for what would happen if added a specific hour (ifAdd)
    private boolean checkIfNeedsTwoHourBreakOnDate(Employee e, DateTime day, int ifAdd) {
        ArrayList<String> a = e.getActivities();
        boolean[] hours = new boolean[25];//index in array corresponds to hour of day
        for(int i=0; i<a.size(); i++) {
            //fill in all the array such that on given day, hours during which employee is already working are marked true
            if (day.getDayOfYear() == activities.get().findOne("{_id: #}", new ObjectId(a.get(i))).projection("{time: 1}").as(DateTime.class).getDayOfYear())
                hours[activities.get().findOne("{_id: #}", a.get(i)).projection("{time: 1}").as(DateTime.class).getHourOfDay()] = true;
        }

        //if adding hypothetical hour
        if(ifAdd>0) hours[ifAdd] = true;

        //check if, while working hours they are, employee has 2 hour break
        if (((hours[9] || hours[10])) && ((hours[10] || hours[11])) && ((hours[13] || hours[14])) && //if working any of the possible 2 hour break slots
                ((hours[14] || hours[15])) && ((hours[15] || hours[16])) && ((hours[19] || hours[20]))) {
            System.out.println("employee does not have 2 hour break");
            return true;
        }
        System.out.println("employee does not need 2 hour break here");
        return false;
    }//end checkNeedsTwoHour()

    //for given date, returns true if employee has their 24 hour break on that day and thus cannot work
    private boolean checkHas24HourBreakOnDate(Employee e, DateTime day) {
        int interval = e.getIntervalBreak();
        DateTime start = e.getStartBreak();
        System.out.println((day.getDayOfYear() - start.getDayOfYear())%interval);
        if( (day.getDayOfYear() - start.getDayOfYear())%interval == 0 ) return true;
        else
            System.out.println("does not has 24 now");return false;
    }//end checkHas24Hour()

//    private ArrayList<ArrayList<String>> findDomain(ArrayList<DBObject> actList) {
//        ArrayList<ArrayList<String>> domains = new ArrayList<ArrayList<String>>(); //list of possible domains for each i
//        ArrayList<String> newDomain = new ArrayList<String>();//domain specific to i
//
//        DBCursor cursor2 = employees.get().getDBCollection().find();
//        List<DBObject> arrry2 = cursor2.toArray();
//        ArrayList<DBObject> empList = new ArrayList<>(arrry2);
//
//        for(int i=0; i<actList.size(); i++) {
//            String[] counselors = campsessions.get().findOne("{name: #}", actList.get(i).get("session")).projection("{counselorIDs: 1}").as(String[].class);//get array of counselorIDs from camp session whose name is same as the name of the camp session for this activity
//            //if(  campsessions.get().findOne("{name: #}", actList.get(i).get("session"))
//
//
//            for(int j=0; j<empList.size(); j++) {
//                //if j ok add to newDomain
//            }
//            domains.add(i, new ArrayList<String> (newDomain));
//            newDomain.clear();
//        }
//        return domains;
//    }

    //returns >0 if there are conflicts, 0 if not
    private int checkConflicts() {
        System.out.println("checking conflicts...");


        DBCursor cursor = activities.get().getDBCollection().find();
        List<DBObject> arrry = cursor.toArray();
        ArrayList<DBObject> actList = new ArrayList<DBObject>(arrry);

        //if there is an activity without employees
        if( (activities.get().count( "{employees: {$exists: false} }" ) != 0)
                || (activities.get().count( "{employees: null }" ) != 0)) {//if time is set or not
            System.out.println("type 1 conflict found (no staff)");
            return 1;
        }

        //check ratios and certification requirements
        for(int i=0; i<actList.size(); i++) {
            String aString = String.valueOf(actList.get(i).get("_id"));
            Activity act = activities.get().findOne("{_id: #}", new ObjectId(aString)).as(Activity.class);
            if(!checkSufficientlyStaffed(act)) {
                System.out.println("insufficiently staffed");
                return 2;
            }
        }

        //check that employees aren't scheduled multiple places at same time
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

        //check that employees have appropriate number of breaks todo could change from map to set?
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
            //find gets activities specific employee is working~ ~sort makes earliest be at top of list~ ~projection makes return only time~ ~.as tells what sort of object is returned as~ ~iterator allows to look through list~ ~next gets pointer to actual object?
            DateTime earliest = new DateTime(activities.get().find("{employees: #}", empList.get(i).get("_id").toString()).sort("{time: 1}").projection("{time: 1}").as(DateTime.class).iterator().next());
            DateTime latest = new DateTime(activities.get().find("{employees: #}", empList.get(i).get("_id").toString()).sort("{time: -1}").projection("{time: 1}").as(DateTime.class).iterator().next());
            int interval = (int) empList.get(i).get("intervalBreak");
            DateTime start = (DateTime) empList.get(i).get("startBreak");
            int numToCheck = (latest.getDayOfYear() - earliest.getDayOfYear()) / interval + 1;
            int numIntervalsBeforeEarliest = ((int) (earliest.getDayOfYear() - start.getDayOfYear())) /interval;
            DateTime requiredDayOff = new DateTime(start.plusDays(interval*numIntervalsBeforeEarliest));
            for(int j=0; j<numToCheck; j++) {
                String requiredDayOffString = dtf.print(requiredDayOff);
                if(working.containsKey(requiredDayOffString)) {
                    System.out.println("employee working on their 24 hour break");
                    return 9;
                }
                requiredDayOff = requiredDayOff.plusDays(interval);
            }
        }

        System.out.println("checked conflicts: there are none");
        return 0;
    }//end checkConflicts()
}



//OLD CHECK RATIOS BUSINESS
        /*//for each activity
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
            LazyDBList e = null;
            if (actList.get(i).get("employees") != null) {//can maybe get rid of this laterzzz
                 e = (LazyDBList) actList.get(i).get("employees");
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
                    if (numCampers / numStaff >= 12) {return 5;}
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
        }//end check for correct ratios*/