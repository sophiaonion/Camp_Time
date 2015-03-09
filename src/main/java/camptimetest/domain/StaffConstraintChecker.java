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

public class StaffConstraintChecker {

    private JongoCollection activities;
    private JongoCollection employees;
    private JongoCollection registrations;
    private JongoCollection campsessions;
    private Activity errorCauser;

    public StaffConstraintChecker(JongoCollection activities, JongoCollection employees, JongoCollection registrations, JongoCollection campsessions) {
        this.activities = activities;
        this.employees = employees;
        this.registrations = registrations;
        this.campsessions = campsessions;
        errorCauser = new Activity();
    }




    //runs fixConflicts() as long as there are conflicts
    public Activity update() {
        System.out.println("scheduling employees...");

        //staffing activities
        int i = this.checkConflicts();
        if(i > 0) { //todo change to while
            System.out.println("type "+i+" conflict");
            this.fixConflicts(i); //just here while testing
            i=this.checkConflicts();
        }
        if(i>0) System.out.println("still conflicts");
        System.out.println("employees scheduled");
        return errorCauser;
    }//end update()




    //assigns employees to activities that are insufficiently staffed and then heuristically repairs resulting conflicts
    private void fixConflicts(int type) {

        DBCursor cursor = activities.get().getDBCollection().find();
        List<DBObject> arrry = cursor.toArray();
        ArrayList<DBObject> actList = new ArrayList<DBObject>(arrry);

        if (type==1 || type==2) {//have un(der)employed activities todo CHANGE BACK TO WHILE

            //add staff as required
            for (int i = 0; i < actList.size(); i++) {//for each activity
                String aStringID = String.valueOf(actList.get(i).get("_id"));
                Activity a = activities.get().findOne("{_id: #}", new ObjectId(aStringID)).as(Activity.class);//current activity
                ObjectId campSessionID = new ObjectId(campsessions.get().findOne("{name: # }", String.valueOf(a.getSession())).as(CampSession.class).getSessionID());//campsession id
                int numCampers = (int) registrations.get().count("{sessionID: #}", campSessionID); //get number of campers
                String activityArea = String.valueOf(a.getActivityArea());//get location of activity
                ArrayList<String> e = new ArrayList<String>(a.getEmployees());//get number of employees working session
                int numStaff = e.size();

                if(!a.getTitle().equals("n/a")) {
                //add staff with required certifications
                if (a.getActivityArea() != null && !a.getActivityArea().equals("")) {
                    int numLifeGuards = 0;
                    boolean hasArt = false, hasNature = false, hasArchery = false, hasStore = false, canDrive;

                    //find what certifications activity has already
                    for (int j = 0; j < e.size(); j++) {//for each employee working the activity
                        if (employees.get().findOne("{_id: #}", new ObjectId(e.get(j))).as(Employee.class).getCertifications() != null) {//if that employee has certifications
                            ArrayList<String> certs = new ArrayList<String>(employees.get().findOne("{_id: #}", new ObjectId(e.get(j))).as(Employee.class).getCertifications());//
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
                    ArrayList<String> emps = new ArrayList<String>(activities.get().findOne("{_id: #}", new ObjectId(a.getKey())).as(Activity.class).getEmployees());

                    //adds employees with required certifications to session until acceptable
                    switch (activityArea) {//todo make compact and get rid of repitition
                        case "pool": //todo check for infinite looping in case of not enough staff for now
                            int count = 0;
                            if(numLifeGuards == 0) {
                                //save employee to activity and activity to employee
                                String eID = findEmployeeToWork(a, "lifeguard", "", false, false);
                                if(!eID.equals("none")) {
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
                            }
                            while (count < 1000 && (((numCampers + numStaff) / numLifeGuards) > 25)) {
                                //save employee to activity and activity to employee
                                String eID = findEmployeeToWork(a, "lifeguard", "", false, false);
                                if(!eID.equals("none")) {
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
                            }
                            break;
                        case "canoeing":
                            int count2 = 0;
                            if(numLifeGuards == 0) {
                                //save employee to activity and activity to employee
                                String eID = findEmployeeToWork(a, "lifeguard", "", false, false);
                                if(!eID.equals("none")) {
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
                            }
                            while (count2 < 1000 && (((numCampers + numStaff) / numLifeGuards) > 25)) {
                                //save employee to activity and activity to employee
                                String eID = findEmployeeToWork(a, "lifeguard", "", false, false);
                                if(!eID.equals("none")) {
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
                            }
                            break;
                        case "art":
                            if (!hasArt) {
                                //save employee to activity and activity to employee
                                String eID = findEmployeeToWork(a, "art", "", false, false);
                                if(!eID.equals("none")) {
                                    emps.add(eID);
                                    Employee empl = employees.get().findOne("{_id: #}", new ObjectId(eID)).as(Employee.class);
                                    empl.addActivity(a.getKey());
                                    employees.get().save(empl);
                                    a.setEmployees(new ArrayList<String>(emps));
                                    activities.get().save(a);
                                    numStaff++;
                                }
                            }
                            break;
                        case "nature":
                            if (!hasNature) {
                                //save employee to activity and activity to employee
                                String eID = findEmployeeToWork(a, "nature", "", false, false);
                                if(!eID.equals("none")) {
                                    emps.add(eID);
                                    Employee empl = employees.get().findOne("{_id: #}", new ObjectId(eID)).as(Employee.class);
                                    empl.addActivity(a.getKey());
                                    employees.get().save(empl);
                                    a.setEmployees(new ArrayList<String>(emps));
                                    activities.get().save(a);
                                    numStaff++;
                                }
                            }
                            break;
                        case "archery":
                            if (!hasArchery) {
                                //save employee to activity and activity to employee
                                String eID = findEmployeeToWork(a, "archery", "", false, false);
                                if(!eID.equals("none")) {
                                    emps.add(eID);
                                    Employee empl = employees.get().findOne("{_id: #}", new ObjectId(eID)).as(Employee.class);
                                    empl.addActivity(a.getKey());
                                    employees.get().save(empl);
                                    a.setEmployees(new ArrayList<String>(emps));
                                    activities.get().save(a);
                                    numStaff++;
                                }
                            }
                            break;
                        case "store":
                            if (!hasStore) {
                                //save employee to activity and activity to employee
                                String eID = findEmployeeToWork(a, "store", "", false, false);
                                if(!eID.equals("none")) {
                                    emps.add(eID);
                                    Employee empl = employees.get().findOne("{_id: #}", new ObjectId(eID)).as(Employee.class);
                                    empl.addActivity(a.getKey());
                                    employees.get().save(empl);
                                    a.setEmployees(new ArrayList<String>(emps));
                                    activities.get().save(a);
                                    numStaff++;
                                }
                            }
                            break;
                        default:
                            //location is sports or creek, no extra certifications required
                            break;
                    }
                }//end add staff with required certifications

                //add assigned and available counselors to session
                if (!checkSufficientlyStaffed(a)) {//if that wasn't enough
                    ArrayList<String> emps = new ArrayList<String>(activities.get().findOne("{_id: #}", new ObjectId(a.getKey())).as(Activity.class).getEmployees());
                    String counID = findEmployeeToWork(a, null, a.getSession(), false, false);
                    while (!counID.equals("none") && !checkSufficientlyStaffed(a)) {
                        emps.add(counID);
                        Employee empl = employees.get().findOne("{_id: #}", new ObjectId(counID)).as(Employee.class);
                        empl.addActivity(a.getKey());
                        employees.get().save(empl);
                        a.setEmployees(new ArrayList<String>(emps));
                        activities.get().save(a);
                        numStaff++;
                        counID = findEmployeeToWork(a, null, a.getSession(), false, false);
                    }
                }

                //add admin & spec staff as required for coverage
                if (!checkSufficientlyStaffed(a)) {
                    //based on activity area, add staff with necessary certifications
                    ArrayList<String> emps = new ArrayList<String>(activities.get().findOne("{_id: #}", new ObjectId(a.getKey())).as(Activity.class).getEmployees());
                    //add counselors assigned to session
                    String adID = findEmployeeToWork(a, null, "", false, false);
                    while (!adID.equals("none") && !checkSufficientlyStaffed(a)) {
                        emps.add(adID);
                        Employee empl = employees.get().findOne("{_id: #}", new ObjectId(adID)).as(Employee.class);
                        empl.addActivity(a.getKey());
                        employees.get().save(empl);
                        a.setEmployees(new ArrayList<String>(emps));
                        activities.get().save(a);
                        numStaff++;
                        adID = findEmployeeToWork(a, null, a.getSession(), false, false);
                    }
                }//end add admin & spec staff for coverage
            }//end for each activity
        }
            type = checkConflicts();
        }//end assign employees to unemployed activities

        //if original round of assigning was not sufficient: do heuristic repair
        if(checkConflicts()>0) {
            System.out.println("heree");

            //arraylist of number of conflicts where index corresponds to index of activity in actArray (include conflicts with 2 hour breaks)
            ArrayList< Integer > numConflicts = new ArrayList<>(countConflicts(false));

            //get activity with most conflicts
            int mostConf = getMostConflicts(numConflicts);

            //fix conflicts one by one
            if(numConflicts.get(mostConf) > 0) {
                System.out.println(mostConf);
                System.out.println(numConflicts.get(mostConf));
                //pick most available employee able to work that activity and put them there
                actList.get(mostConf);

                //remove current staff from activity
                Activity editA = activities.get().findOne("{_id: #}", new ObjectId(String.valueOf(actList.get(mostConf).get("_id")))).as(Activity.class);
                editA.getEmployees().clear();
                activities.get().save(editA);

                System.out.println(editA.getTitle()+"/"+editA.getSession()+"/"+editA.getTime());


                //add best choice staff to activity - if adding won't change break
                ArrayList<String> actDomain = new ArrayList<String>(findEmployeesToWork(editA, "", "", false, false));
                //if no such options, include ones changing 2 and fix laterz
                if(actDomain.size()==0) {
                    System.out.println("changing 2");
                    actDomain = new ArrayList<String>(findEmployeesToWork(editA, "", "", false, true));
                }

                //add given employees to list
                for(String eID: actDomain) {
                    //but only as long as not sufficiently staffed
                    if(!checkSufficientlyStaffed(editA)) {
                        editA.getEmployees().add(eID);
                        activities.get().save(editA);
                    }
                }

                //update numConflicts array and index of mostConf
                numConflicts = new ArrayList<>(countConflicts(false));
                mostConf = getMostConflicts(numConflicts);
            }
        }
    }//end fixConflicts()





    //returns >0 if there are conflicts, 0 if not
    private int checkConflicts() {

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
                System.out.println(act.getTitle()+"/"+act.getSession()+"/"+act.getTime());
                System.out.println("insufficiently staffed");
                return 2;
            }
        }

        //check that employees aren't scheduled multiple places at same time
        Iterable<Employee> empCursor = employees.get().find().as(Employee.class);
        for(Employee e: empCursor) {//for each employee
            ArrayList<String> a = e.getActivities();
            for(int j=0; j<a.size(); j++)//for each activity employee is working
            {
                DateTime dtA = activities.get().findOne("{_id: #}", new ObjectId(a.get(j))).as(Activity.class).getTime();
                for (int k = 0; k < a.size(); k++) { //for each other activity employee is working
                    DateTime dtB = activities.get().findOne("{_id: #}", new ObjectId(a.get(k))).as(Activity.class).getTime();
                    if (j != k && dtA.equals(dtB)) {//not sure if works, compare each activity
                        System.out.println(a.get(j)+" is at same time as "+a.get(k));
                      System.out.println("type 3 conflict: employee working two activities at same time");
                        return 3;
                    }
                }
            }
        }

        //check that employees have appropriate number of breaks todo could change from map to set?
        DateTimeFormatter dtf =  DateTimeFormat.forPattern("MM/dd/YYY");
        Iterable<Employee> empCursor2 = employees.get().find().as(Employee.class);
        for(Employee e: empCursor2) {//for each employee
            ArrayList<String> aST = e.getActivities();

            Map<String, boolean[]> working = new HashMap<String, boolean[]>();
            //puts date of each activity into map (no duplicates) of day to array of booleans indicating hour business

            //calculate hours worked for employee
            for(int j=0; j<aST.size(); j++) {//for each activity
                Activity a = activities.get().findOne("{_id: #}", new ObjectId(aST.get(j))).as(Activity.class);
                String day = dtf.print(a.getTime());
                int hour = a.getTime().getHourOfDay();

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
                    System.out.println("type 4: employee does not have 2 hour break");
                    return 4;
                }
            }//end check 2 hour break

            //check that employee has 24 hour break every designated interval
            //find gets activities specific employee is working~ ~sort makes earliest be at top of list~ ~projection makes return only time~ ~.as tells what sort of object is returned as~ ~iterator allows to look through list~ ~next gets pointer to actual object?
            DateTime earliest = new DateTime( activities.get().find("{employees: #}", new ObjectId(e.getKey())).sort("{time: 1}").projection("{time: 1}").as(DateTime.class) );
            DateTime latest = new DateTime(activities.get().find("{employees: #}", new ObjectId(e.getKey())).sort("{time: -1}").projection("{time: 1}").as(DateTime.class));
            int interval = (int) e.getIntervalBreak();
            DateTime start = (DateTime) e.getStartBreak();
            int numToCheck = (latest.getDayOfYear() - earliest.getDayOfYear()) / interval + 1;
            int numIntervalsBeforeEarliest = ((int) (earliest.getDayOfYear() - start.getDayOfYear())) /interval;
            DateTime requiredDayOff = new DateTime(start.plusDays(interval*numIntervalsBeforeEarliest));
            for(int j=0; j<numToCheck; j++) {
                String requiredDayOffString = dtf.print(requiredDayOff);
                if(working.containsKey(requiredDayOffString)) {
                  System.out.println("type 5: employee working on their 24 hour break");
                    return 5;
                }
                requiredDayOff = requiredDayOff.plusDays(interval);
            }
        }
        System.out.println("no conflicts");
        return 0;
    }//end checkConflicts()




    //finds available employee (if there is one, if not?? wat do)
    private String findEmployeeToWork(Activity a, String cert, String session, boolean matureLady, boolean conflictOKTwos) {
        ArrayList<String> options = new ArrayList<String>(findEmployeesToWork(a, cert, session, matureLady, conflictOKTwos));
        //return result
        if(options.size()>0)
            return options.get(0);
        else
            return "none";
    }//end findEmployeeToWork()




    //list of available employees
    private ArrayList<String> findEmployeesToWork(Activity a, String cert, String session, boolean matureLady, boolean conflictOKTwos) {
        ArrayList<String> options = new ArrayList<>();
        //looking for staff with specific certification
        if((session==null || session.equals("")) && !(cert==null || cert.equals("")) ) {
            Iterable<Employee> cursor = employees.get().find("{certifications: #}", cert).as(Employee.class);

            //adds available employees
            for(Employee e: cursor) {
                if(!checkHas24HourBreakOnDate(e, a.getTime()) && (conflictOKTwos || !checkIfLackingTwoHourBreakOnDate(e, a.getTime(), a.getTime().getHourOfDay()))) {
                    //go through each activity emp is working and make sure it isn't same as day being added
                    boolean alreadyWorking = false;
                    for(String wID: e.getActivities()) {//for each activity that employee is working
                        if (activities.get().findOne("{_id: #}", new ObjectId(wID)).as(Activity.class).getTime() == a.getTime()) {//if is at same time as activity being considered
                            alreadyWorking = true;
                        }
                    }
                    if(!alreadyWorking) {
                        options.add(e.getKey());
                    }
                }
            }//end add available staff with certifications
        }


        //looking for counselors assigned to session
        else if(session != null && !session.equals("")){
            String sessionID = campsessions.get().findOne("{name: #}", session).as(CampSession.class).getSessionID();
            ArrayList<String> counselorIDs = campsessions.get().findOne("{_id: #}", new ObjectId(sessionID)).as(CampSession.class).getCounselorIDs();

            //for each counselor
            for(String cID: counselorIDs) {
                Employee e = employees.get().findOne("{_id: #}", new ObjectId(cID)).as(Employee.class);
                if(!checkHas24HourBreakOnDate(e, a.getTime()) && (conflictOKTwos || !checkIfLackingTwoHourBreakOnDate(e, a.getTime(), a.getTime().getHourOfDay()))) {
                    if(!matureLady || (e.getGender().equals("woman") && e.getAge() >= 18)) {
                        boolean alreadyWorking = false;
                        for (String wID : e.getActivities()) {
                            if (activities.get().findOne("{_id: #}", new ObjectId(wID)).as(Activity.class).getTime() == a.getTime()) {
                                alreadyWorking = true;
                            }
                        }
                        if (!alreadyWorking) {
                            options.add(e.getKey());
                        }
                    }
                }//end employee doesn't have break then
            }//end for each counselor assigned to session
        }//end add counselors assigned to session
        //looking for admin or specialty staff to fill in coverage
        if((session==null || session.equals("")) && (cert==null || cert.equals("")) ) {
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
                if(!checkHas24HourBreakOnDate(e, a.getTime()) && (conflictOKTwos || !checkIfLackingTwoHourBreakOnDate(e, a.getTime(), a.getTime().getHourOfDay()))) {
                    //go through each activity emp is working and make sure it isn't same as day being added
                    for(String wID: e.getActivities()) {
                        if (activities.get().findOne("{_id: #}", new ObjectId(wID)).as(Activity.class).getTime() != a.getTime()) {
                            options.add(e.getKey());
                        }
                    }
                }
            }

            //add available spec staff
            for(Employee e: specCursor) {
                if(!checkHas24HourBreakOnDate(e, a.getTime()) && (conflictOKTwos || !checkIfLackingTwoHourBreakOnDate(e, a.getTime(), a.getTime().getHourOfDay()))) {
                    //go through each activity emp is working and make sure it isn't same as day being added
                    for(String wID: e.getActivities()) {
                        if (activities.get().findOne("{_id: #}", new ObjectId(wID)).as(Activity.class).getTime() != a.getTime()) {
                            options.add(e.getKey());
                        }
                    }
                }
            }
        }

        //double check that they aren't already working that same activity
        for(int i=0; i<options.size(); i++) {
            if(a.getEmployees().contains(options.get(i))) {
                options.remove(i);
                i--;//subtract from i because change the thing?????? todo
            }
        }

        //return result
        return options;
    }//end findEmployeeToWork()




    //returns true if given activity is sufficiently staffed according to rules & regulations
    private boolean checkSufficientlyStaffed(Activity a) {
        if(a.getTitle().equals("n/a"))
            return true;
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
        if ((numStaff < 2)) { return false;}

        //woman over 18 is with campers
        boolean hasMatureLady = false;
        for (int j = 0; j < e.size(); j++) {
            if (employees.get().findOne("{_id: #}", new ObjectId(e.get(j))).as(Employee.class).getGender().equals("woman")) //was false
                if (employees.get().findOne("{_id: #}", new ObjectId(e.get(j))).as(Employee.class).getAge() >= 18) {
                    hasMatureLady = true;
                }
        }
        if (hasMatureLady == false) {
//            System.out.println("does not have staff over 18 and woman");
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


            //find what certifications activity has already
            for (int j = 0; j < e.size(); j++) {//for each employee working the activity
                if(employees.get().findOne("{_id: #}", new ObjectId(e.get(j))).as(Employee.class).getCertifications() != null) {//if that employee has certifications
                    ArrayList<String> certs = new ArrayList<String>(employees.get().findOne("{_id: #}", new ObjectId(e.get(j))).as(Employee.class).getCertifications() );//
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


            //based on activity area see if necessary certifications are present
            boolean certsOK = false;
            switch(activityArea) {
                case "pool"://pretty sure this equation works: need
                    if(numLifeGuards != 0)
                        if ((((numCampers + numStaff) / numLifeGuards) < 25) && (((numCampers + numStaff) / (numStaff - numLifeGuards +1)) < 12 )) {certsOK = true;}
                    break;
                case "canoeing":
                    if(numLifeGuards != 0)
                        if ((((numCampers + numStaff) / numLifeGuards) < 25) && (((numCampers + numStaff) / (numStaff - numLifeGuards+1 )) < 12 )) {certsOK = true;}
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
                    certsOK = true;
                    break;
            }
            if (!certsOK) {
                return false;
            }
        }//end check for required certifications
        return true;
    }//end checkSufficientlyStaffed()




    //returns true if given activity is sufficiently staffed according to rules & regulations
    private boolean checkSufficientlyEmployed(Employee e, boolean twoConflictOK) {

        //create list of activities employee is working
        ArrayList<Activity> empActs = new ArrayList<Activity>();
        for(String actID: e.getActivities()) {
            Activity a = activities.get().findOne("{_id: #}", new ObjectId(actID)).as(Activity.class);
            empActs.add(a);
        }

        //for each activity employee is working
        for(Activity a: empActs) {

            //check that activity is not on date of 24 hour break
            if(checkHas24HourBreakOnDate(e, a.getTime()))
                return false;

            //check that working
            if(!twoConflictOK && checkIfLackingTwoHourBreakOnDate(e, a.getTime(), -1))
                return false;

            //if working two activities at the same time
            for(int b=0; b<empActs.size(); b++) {
                if(a.getTime() == empActs.get(b).getTime())
                    return false;
            }
        }
        return true;
    }//end checkSufficientlyStaffed()




    //for given date, returns true if employee has their 24 hour break on that day and thus cannot work
    private boolean checkHas24HourBreakOnDate(Employee e, DateTime day) {
        int interval = e.getIntervalBreak();
        DateTime start = e.getStartBreak();
        if( (day.getDayOfYear() - start.getDayOfYear())%interval == 0 ) return true;
        else return false;
    }//end checkHas24Hour()




    //for given date, returns true if employee needs 2 hours off then for break,
    //can also get result for what would happen if added a specific hour (ifAdd)
    private boolean checkIfLackingTwoHourBreakOnDate(Employee e, DateTime day, int ifAdd) {
        ArrayList<String> a = e.getActivities();
        boolean[] hours = new boolean[25];//index in array corresponds to hour of day
        for(int i=0; i<a.size(); i++) {
            //fill in all the array such that on given day, hours during which employee is already working are marked true
           Activity test =  activities.get().findOne("{_id: #}", new ObjectId(a.get(i))).as(Activity.class);

            if (day.getDayOfYear() == activities.get().findOne("{_id: #}", new ObjectId(a.get(i))).as(Activity.class).getTime().getDayOfYear())
                hours[activities.get().findOne("{_id: #}", new ObjectId(a.get(i))).as(Activity.class).getTime().getHourOfDay()] = true;
        }

        //if adding hypothetical hour
        if(ifAdd>0) hours[ifAdd] = true;

        //check if, while working hours they are, employee has 2 hour break
        if (((hours[9] || hours[10])) && ((hours[10] || hours[11])) && ((hours[13] || hours[14])) && //if working any of the possible 2 hour break slots
                ((hours[14] || hours[15])) && ((hours[15] || hours[16])) && ((hours[19] || hours[20]))) {
            return true;
        }
        return false;
    }//end checkNeedsTwoHour()




    //returns array of number of staffing conflicts per activity with index corresponding to array of activities
    private ArrayList<Integer> countConflicts(boolean twoConflictOK) {

        ArrayList<Integer> numConflicts = new ArrayList<>();
        int i=0;
        Iterable<Activity> actCursor2 = activities.get().find().as(Activity.class);
        for(Activity a: actCursor2) {
            //initialize to 0
            numConflicts.add(i, 0);

            //check for conflicts
            if(!a.getTitle().equals("n/a")) {
                //if no staff, add 1
                if (a.getEmployees() == null || a.getEmployees().size() == 0) {
                    int c = numConflicts.get(i);
                    c++;
                    numConflicts.set(i, c);
                }

                //if innappropriately staffed, add 1 (not enough/not enough with required certifications)
                if (!checkSufficientlyStaffed(a)) {
                    int c = numConflicts.get(i);
                    c++;
                    numConflicts.set(i, c);
                }

                //create list of employees working then
                ArrayList<Employee> actEmps = new ArrayList<Employee>();
                for(String empID: a.getEmployees()) {
                    Employee e = employees.get().findOne("{_id: #}", new ObjectId(empID)).as(Employee.class);
                    actEmps.add(e);
                }

                //if employees working should not be working then
                for (Employee e : actEmps) {
                    if (!checkSufficientlyEmployed(e, twoConflictOK)) {
                        int c = numConflicts.get(i);
                        c++;
                        numConflicts.set(i, c);
                    }
                }
            }
            i++;
        }

        return numConflicts;
    }//end checkConflicts()





    //returns index of most constrained activity
    private int getMostConflicts(ArrayList<Integer> conflicts) {
        int most = -1;
        int mostInd = -1;
        for(int i=0; i<conflicts.size(); i++) {
            if(conflicts.get(i) >= most) {
                most = conflicts.get(i);
                mostInd = i;
            }
        }
//        System.out.println("index "+mostInd+" has "+most+" conflicts, the most");
        return mostInd;
    }//end getMostConflicts

}
