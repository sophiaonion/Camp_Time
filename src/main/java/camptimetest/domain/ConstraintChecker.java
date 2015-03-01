package camptimetest.domain;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import restx.jongo.JongoCollection;
import org.bson.types.ObjectId;

import java.util.*;

/**
* Created by sophiawang on 2/4/15.
*/

public class ConstraintChecker {

    private JongoCollection activities;
    private JongoCollection campSessions;



    public ConstraintChecker(JongoCollection activities, JongoCollection campSessions) {
        this.activities = activities;
        this.campSessions = campSessions;
    }



    public JongoCollection update() {
        //scheduling activities
        int i = this.checkConflicts();
        while(i>0) {
            this.fixConflicts(i);
            System.out.println("after fix: ");
            i=this.checkConflicts();
        }
        return this.activities;
    }//end update()


    //sees if any activities are in conflict with each other
    private int checkConflicts() {
        //get domains of all activities
        DBCursor cursor = activities.get().getDBCollection().find();
        List<DBObject> arrry = cursor.toArray();
        ArrayList<DBObject> list = new ArrayList<DBObject>(arrry);

        //if there is an activity without a set time
        if( (activities.get().count( "{time: {$exists: false} }" ) != 0)
                || (activities.get().count( "{time: null }" ) != 0) ) {//if time is set or not
            System.out.println("type 1 conflict found");
            return 1;

        }

        for(int i=0; i<list.size(); i++) {//for each activity
            for(int j=0; j<list.size(); j++) {//for each other activity
                if(i!=j) {//make sure not comparing same activity
                    //if same session in two different places at same time
                    if( list.get(i).get("session").equals(list.get(j).get("session"))
                            && list.get(i).get("time").equals(list.get(j).get("time"))
                            && (list.get(i).get("activityArea") != null) && (list.get(j).get("activityArea")!=null)
                            && !(list.get(i).get("activityArea").equals(list.get(j).get("activityArea"))) ) {
                        System.out.println("type 2 conflict found");
                        return 2;
                    }//end two places at once conflict

                    //if two different sessions in same place at same time
                    if( list.get(i).get("activityArea") != null && list.get(j).get("activityArea") != null)
                    if( list.get(i).get("activityArea").equals(list.get(j).get("activityArea"))
                            && list.get(i).get("time").equals(list.get(j).get("time"))
                            && !(list.get(i).get("session").equals(list.get(j).get("session"))) ) {
                        System.out.println("type 3 conflict found");
                        return 3;
                    }//end two sessions at once conflict

                }//end compare two different activities
            }
        }//end compare all activities for conflicts
        System.out.println("no conflicts, nice");
        return 0;//no conflicts
    }//end checkconflicts()



    //associates an array of possible time assignments (called domain) to each activity in list can include or exclude times that have a set but not fixed activity
    private ArrayList< ArrayList<DateTime> > findDomain(ArrayList<DBObject> list, Boolean excludeSetActivities) {

        ArrayList<ArrayList<DateTime>> domains = new ArrayList<ArrayList<DateTime>>(); //list of possible domains for each i
        ArrayList<DateTime> newDomain = new ArrayList<DateTime>();//domain specific to i
        for(int d=0; d<list.size(); d++) {domains.add(new ArrayList<DateTime>());} //initialize domains to empty
        ArrayList<String> takenAreas = new ArrayList<>();//maps time to activity area ~~represents time that activity area is taken

        String setOrfixed = "";
        if(excludeSetActivities) setOrfixed = "fixed";
        else setOrfixed = "isSet";

        //set up domain for all setOrfixed time activities: should be only time it already is
        for(int i=0; i<list.size(); i++) {//for each activity
            if ((boolean)list.get(i).get(setOrfixed)) {

                DateTime time = new DateTime( list.get(i).get("time") );
                newDomain.add(time);//list of available times contains only the time it is set at
                String area = String.valueOf(list.get(i).get("activityArea"));
                takenAreas.add(time.toString() + area);
                domains.set(i, new ArrayList<DateTime>(newDomain));//add to domains arraylist
            }
            newDomain.clear();
        }

        //set up domain for all non-set time activities: domain will be all times that are currently not filled (& also times where not already doing something???)
        for(int i=0; i<list.size(); i++) {
            if (!((boolean)list.get(i).get(setOrfixed))) {

                //need to get date at which current object's session starts & ends so can calculate possible time slots
                DateTime start = new DateTime((campSessions.get().findOne("{name: \"" + list.get(i).get("session") + "\" }").as(CampSession.class)).getStartDate());
                DateTime end = new DateTime((campSessions.get().findOne("{name: \"" + list.get(i).get("session") + "\" }").as(CampSession.class)).getEndDate());
                DateTime nextTime = new DateTime(start);


                //calculate all possible time slots and put into domain for now
                while (nextTime.getDayOfMonth() != end.plusDays(1).getDayOfMonth()) {//for each day of session
                    nextTime = nextTime.withHourOfDay(0);
                    for (int h = 9; h <= 21; h++) {//for each hour in day
                        newDomain.add(new DateTime(nextTime.withHourOfDay(h)));
                    }//add all possible hours to possible times list
                    nextTime = nextTime.plusDays(1);
                }

                //take out taken times aka times when another session is already doing this activity
                for (int j = 0; j < newDomain.size(); j++) {
                    String checkArea = newDomain.get(j).toString() + list.get(i).get("activityArea");
                    if(takenAreas.contains(checkArea)) {
                        newDomain.remove(j);
                        j--;//decrement because removed one from thingy idk if necessary i can't do math
                    }
                }//remove times from domain that would place activity in area at same time as another session

                //take out unavailable times aka times when this session is already doing a thing
                DBCursor cursor2 = activities.get().getDBCollection().find(new BasicDBObject("session", list.get(i).get("session")));
                ArrayList<DBObject> takenTimesArray = new ArrayList<DBObject>(cursor2.toArray());


                for ( int a=0; a<takenTimesArray.size(); a++) {
                    DateTime takenTimeBad = new DateTime(takenTimesArray.get(a).get("time"));
                    DateTime takenTime = new DateTime( takenTimeBad.withZoneRetainFields(DateTimeZone.UTC) );
                    for(int j=0; j<newDomain.size(); j++) {
                        if (takenTime.withZoneRetainFields(DateTimeZone.UTC).equals(newDomain.get(j)/*.withZoneRetainFields(DateTimeZone.UTC)*/)) {
                            newDomain.remove(j);
                        }
                    }
                }//removes all time slots where activity is already scheduled for this session

               // System.out.println();
               // System.out.println("Resulting domain for:"+ list.get(i).get("title") +"/"+list.get(i).get("session"));
//                for(int p=0; p<newDomain.size(); p++) {
//                    System.out.println(newDomain.get(p));
//                }
//                System.out.println(); System.out.println();
                domains.add(i, new ArrayList<DateTime> (newDomain));//add possible open times to domain for index i (which corresponds to activity i in list)
            }//end set up domain for non-set time activities
            newDomain.clear();
        }//end set up corresponding domain i for each activity i in list
        return domains;
    }//end finddomain()




    //returns index of most constrained activity in arraylist
    private int getMostConstrained(ArrayList< ArrayList<DateTime> > domains) {
        //assuming domains with 1 or no items have already been assigned or whatevs
        int numConst = 100000000;//number of options for most constrained activity
        int mostConst=-1;//index of most constrained activity

        for(int i=0; i<domains.size(); i++) {
            if ((domains.get(i).size() < numConst) && (domains.get(i).size() > 1)) {//smallest domain that still has a choice
                mostConst = i;
                numConst = domains.get(i).size();
            }
        }
        return mostConst;
    }//end getMostConstrained




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
        System.out.println("index "+mostInd+" has "+most+" conflicts, the most");
        return mostInd;
    }//end getMostConflicts




    //returns array of conflict count with indices corresponding to index of activity list (fixed time activities don't count bd can't change them anywayss)
    private ArrayList<Integer> countConflicts(ArrayList<DBObject> list) {
        ArrayList<Integer> numConflicts = new ArrayList<>();

        for(int i=0; i<list.size(); i++) {//initialize all so have no conflicts
            numConflicts.add(i, 0);
        }

        for(int i=0; i<list.size(); i++) {//for each activity
            for(int j=0; j<list.size(); j++) {//for each other activity
                if(i!=j) {//make sure not comparing same activity
                    //if same session in two different places at same time
                    if( list.get(i).get("session").equals(list.get(j).get("session"))
                            && list.get(i).get("time").equals(list.get(j).get("time"))
                            && !(list.get(i).get("activityArea").equals(list.get(j).get("activityArea"))) ) {
                        //add conflict to each thing thing only if not fixed time cuz we can't change those anyway
                        if( !((boolean) list.get(i).get("fixed")) ) {
                            int c = numConflicts.get(i);
                            c++;
                            numConflicts.set(i, c);
                        }
                        if( !((boolean) list.get(j).get("fixed")) ) {
                            int c = numConflicts.get(j);
                            c++;
                            numConflicts.set(j, c);
                        }

                    }//end two places at once conflict

                    //if two different sessions in same place at same time
                    if( list.get(i).get("activityArea") != null && list.get(j).get("activityArea") != null)
                    if( list.get(i).get("activityArea").equals(list.get(j).get("activityArea"))
                            && list.get(i).get("time").equals(list.get(j).get("time"))
                            && !(list.get(i).get("session").equals(list.get(j).get("session"))) ) {
                        //add conflict to each thing thing only if not fixed time cuz we can't change those anyway
                        if( !((boolean) list.get(i).get("fixed")) ) {
                            int c = numConflicts.get(i);
                            c++;
                            numConflicts.set(i, c);
                        }
                        if( !((boolean) list.get(j).get("fixed")) ) {
                            int c = numConflicts.get(j);
                            c++;
                            numConflicts.set(j, c);
                        }

                    }//end two sessions at once conflict

                }//end compare two different activities
            }
        }//end compare all activities for conflicts

        for (int m=0; m<numConflicts.size(); m++) {
            System.out.println("index: "+m+" numconflicts: "+numConflicts.get(m));
        }
        return numConflicts;
    }//end countConflicts




    //uses heuristic repair to fix conflicts between activities
    private Boolean fixConflicts(int type) {
        // assign values to everything without them
        if(type==1) {//type 1: there are variables without assigned value

            //get domains of all activities
            DBCursor cursor = activities.get().getDBCollection().find();
            List<DBObject> arrry = cursor.toArray();
            ArrayList<DBObject> actArray = new ArrayList<DBObject>(arrry);
            ArrayList<ArrayList<DateTime>> domains = findDomain(actArray, false);//possible domains of times for activity with corresponding index in acts
            ArrayList<DateTime> newDomain = new ArrayList<DateTime>();


            for(int m = 0; m<actArray.size(); m++) {
                //assign all activities with only one option in domain
                for(int i=0; i<actArray.size(); i++) {
                    //if no available times in domain, just add a random time to domain and fix it later during heuristic repair
                    if (domains.get(i).size() == 0) {
                        DateTime start = new DateTime((campSessions.get().findOne("{name: \"" + actArray.get(i).get("session") + "\" }").as(CampSession.class)).getStartDate());
                        DateTime end = new DateTime((campSessions.get().findOne("{name: \"" + actArray.get(i).get("session") + "\" }").as(CampSession.class)).getEndDate());

                        //TODO just doing first day possible for now, that's no good - randomize day as well as time

                        //set to random time and fix later
                        Random rand = new Random();
                        int randomNum = rand.nextInt((22 - 8)) + 8;
                        start = start.withHourOfDay(randomNum);
                        newDomain.add(new DateTime(start));
                        domains.set(i, new ArrayList<DateTime>(newDomain));
                        newDomain.clear();
                    }//end random time assignment for no options in domain

                    //if only one option in domain (will catch stuff from previous if statement)
                    if(domains.get(i).size() == 1) {
                        if(!((boolean) actArray.get(i).get("isSet"))) {//if activity's time is not yet set
                            DateTime time = new DateTime(domains.get(i).get(0));//time is only available time in domain

                            //update activity in collection
                            String ID = String.valueOf(actArray.get(i).get("_id"));
                            ObjectId ID2 = new ObjectId(ID);
                            activities.get().update("{_id: #}",ID2).with("{ $set: { isSet:" + true + ", time:\"" + time+ "\" } }");


                            //update domains of other activities to remove taken time
                            //TODO should only remove if they are the same session?? or same activity??
                            for(int j=0; j<actArray.size(); j++) {
                                if(domains.get(j).size() > 1) {
                                    if(domains.get(j).contains(time)) {
                                        newDomain = domains.get(j);
                                        newDomain.remove(time);
                                        domains.set(j, new ArrayList<DateTime>(newDomain));
                                        newDomain.clear();
                                    }
                                }
                            }//end update domains of other activities
                        }//end setting time for unset activity
                    }
                }//end assign all activities with only one option in domain

                //now find item with fewest options (most constrained) and remove all but one item from its possible domain - next run through of for loop will take care of business
                int mostConst = getMostConstrained(domains);
                if(mostConst > 0) {//if there is a most constrained
                    //randomly choose from domain
                    Random rand = new Random();
                    int randomNum = rand.nextInt((domains.get(mostConst)).size());
                    newDomain.add(new DateTime(domains.get(mostConst).get(randomNum)));
                    domains.set(mostConst, newDomain);
                    newDomain.clear();
                }
            }//end assign times to all previously non-fixed activities
        }//end assign values to all variables without time assigned in case of type 1

        //heuristic repair time: there's conflicting stuff so fix it
        if(checkConflicts() > 0) {///might just swap back and forth 5ever? todo
            //get domains of all activities
            DBCursor cursor = activities.get().getDBCollection().find();
            List<DBObject> arrry = cursor.toArray();
            ArrayList<DBObject> actArray = new ArrayList<DBObject>(arrry);
            ArrayList< ArrayList<DateTime> > domainsWithSet = new ArrayList<> (this.findDomain(actArray, false));//possible domains of times for activity with corresponding index in acts
            ArrayList< ArrayList<DateTime> > domainsExcludeSet = new ArrayList<> (this.findDomain(actArray, true));

            //conflict count for each activity at same index
            ArrayList<Integer> numConflicts = new ArrayList<Integer> (this.countConflicts(actArray));
            //change mostConflicted ~~ doesn't include fixed time ones bc can't change them anywho
            int mostConf = getMostConflicts(numConflicts);
            if(mostConf > 0)

            //todo loops infinitely WHY never update numConclicts duh
            while(numConflicts.get(mostConf) > 0) {//there are conflicts
                System.out.println("got the most conflicted, it is # "+mostConf+" ya");

                //remove current time from domains
                DateTime current = new DateTime(actArray.get(mostConf).get("time"));
                domainsWithSet.remove(current);
                domainsExcludeSet.remove(current);

                //new time
                DateTime newTime;

                //check if any times that don't conflict other set times
                if(domainsWithSet.get(mostConf).size() > 0) {
                    System.out.println("there's a free spot so putting it there");
                    newTime = new DateTime(domainsWithSet.get(mostConf).get(0).withZoneRetainFields(DateTimeZone.UTC));
                    System.out.println(newTime+ " is the time");
                }
                //otherwise assign to another time (will cause conflicts, but won't conflict with fixed time thingy)
                else if (domainsExcludeSet.get(mostConf).size() > 0) {
                    Random rand = new Random();
                    int randomNum = rand.nextInt(domainsExcludeSet.get(mostConf).size());
                    newTime = new DateTime(domainsExcludeSet.get(mostConf).get(randomNum));
                }
                else {
                    System.out.println("error line 378");
                    return false;//can't assign to anything - some conflict with amongst fixed activities
                }

                //update activity in collection
                String ID = String.valueOf(actArray.get(mostConf).get("_id"));
                ObjectId ID2 = new ObjectId(ID);
                if( !(activities.get().findOne("{_id: # }", ID2).as(Activity.class).getFixed()) )
                    activities.get().update("{_id: #}",ID2).with("{ $set: { isSet:" + true + ", time:\"" + newTime + "\" } }");

                //reset domainsc and stuff
                DBCursor cursor2 = activities.get().getDBCollection().find();
                List<DBObject> arrry2 = cursor.toArray();
                ArrayList<DBObject> actArray2 = new ArrayList<DBObject>(arrry2);
                domainsWithSet = new ArrayList<> (this.findDomain(actArray2, false));//possible domains of times for activity with corresponding index in acts
                domainsExcludeSet = new ArrayList<> (this.findDomain(actArray2, true));
                numConflicts = new ArrayList<Integer> (this.countConflicts(actArray2));
                mostConf = getMostConflicts(numConflicts);
            }//end change most conflicted
            else {
                System.out.println("error 389");
                return false;
            } //if checkconflicts says there are conflicts but numconflicts are all 0--means fixed activities are conflicting each other
        }//end heuristic repair
        return true;
    }//end fixconflicts()
}
