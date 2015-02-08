package camptimetest.domain;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.joda.time.DateTime;
import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;
import restx.jongo.JongoCollection;

import java.util.*;

/**
* Created by sophiawang on 2/4/15.
*/
public class ConstraintChecker {
    @Id
    @ObjectId
    private String ID; //idk if it needs one, probs not
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
            i=this.checkConflicts();
        }

        //staffing activities
        while(this.checkStaffConflicts()) {
            this.fixStaffConflicts();
        }
        return this.activities;
    }//end update()




    //sees if any activities are in conflict with each other
    private int checkConflicts() {

        //if there is an activity without a set time
        if( (activities.get().count( "{time: {$exists: false} }" ) != 0)
                || (activities.get().count( "{time: null }" ) != 0) ) {//if time is set or not????????????
            return 1;
        }

        DBCursor cursor1 = activities.get().getDBCollection().find(); //can iterate through all activities - points to DBObjects
        while(cursor1.hasNext()) {//for each activity
            DBCursor cursor2 = activities.get().getDBCollection().find();//second iterator through all activities
            while(cursor2.hasNext()) {
                if(cursor1.curr() != cursor2.curr()) {//make sure not comparing same activity

                    //if same session in two different places at same time
                    if(cursor1.curr().get("session") == cursor2.curr().get("session")
                            && (cursor1.curr().get("time") == cursor2.curr().get("time"))
                            && (cursor1.curr().get("activityAreaID") != cursor2.curr().get("activityAreaID"))) {
                         return 2;
                     }

                    //if two different sessions in same place at same time
                    if( (cursor1.curr().get("activityAreaID") == cursor2.curr().get("activityAreaID"))//if activity in same time & place
                            && (cursor1.curr().get("time") == cursor2.curr().get("time"))
                            && (cursor1.curr().get("session") != cursor2.curr().get("session"))) {
                        return 3;
                    }
                }
            }
        }
        return 0;
    }//end checkconflicts()




    //associates an array of possible time assignments (called domain) to each activity in list
    private ArrayList< ArrayList<DateTime> > findDomain(ArrayList<DBObject> list) {
        ArrayList< ArrayList<DateTime> > domains = new ArrayList< ArrayList<DateTime> >(); //list of possible domains for each i
        ArrayList<DateTime> domain = new ArrayList<DateTime>();//domain specific to i
        Multimap<DateTime, String> takenArea = ArrayListMultimap.create();//maps time to activity area ~~represents time that activity area is taken

        //set up domain for all set time activities: should be only time it already is
        for(int i=0; i<list.size(); i++) {//for each activity
            if (list.get(i).get("fixed") == true /* || list.get(i).get("set") == true*/ ) {
                domain.add((DateTime) list.get(i).get("time"));//list of available times contains only the time it is set at
                takenArea.put(((DateTime) list.get(i).get("time")), String.valueOf(list.get(i).get("activityarea")));//add to list of unavailable actibity slots
                domains.set(i, domain);//add to domains arraylist
            }
            domain.clear();
        }

        //set up domain for all non-set time activities: domain will be all times that are currently not filled (& also times where not already doing something???)
        for(int i=0; i<list.size(); i++) {
            if ((list.get(i).get("fixed") == false) /* && (list.get(i).get("set") == false)*/) {
                //need to get date at which current object's session starts & ends so can calculate possible time slots
                DBObject sessionDates = (DBObject) campSessions.get().findOne("{session: " + list.get(i).get("session") + " } { startDate: 1, endDate: 1, _id:0 }");//should return DBObject with just start and end date of current session
                DateTime start = (DateTime) sessionDates.get("startDate");
                DateTime end = (DateTime) sessionDates.get("endDate");

                //calculate all possible time slots and put into domain for now
                while (start.getDayOfMonth() != end.getDayOfMonth()) {//for each day of session
                    start = start.withHourOfDay(0);
                    for (int h = 9; h <= 21; h++) {//for each hour in day
                        start = start.withHourOfDay(h);
                        domain.add(start);
                    }//add all possible hours to possible times list
                }

                //take out taken times aka times when another session is already doing this activity
                for (int j = 0; j < domain.size(); j++) {
                    if (takenArea.containsEntry(domain.get(j), list.get(i).get("activityArea"))) {//if something is in same area at same time
                        domain.remove(j);
                        j--;//decrement because removed one from thingy???? idk if necessary i can't do math
                    }
                }//remove times from domain that would place activity in area at same time as another session

                //take out unavailable times aka times when this session is already doing a thing
                String sessionString = String.valueOf(list.get(i).get("session"));
                DBObject sessionObj = new BasicDBObject("session:", sessionString);
                DBCursor cursor = campSessions.get().getDBCollection().find(sessionObj);//points to list of activities for session
                while (cursor.hasNext()) {//won't matter if it is not fixed bc then won't have date and thus won't interfere ~~probably
                    for(int j=0; j<domain.size(); j++)
                        if( cursor.curr().get("time").equals(domain.get(j))) {
                            domain.remove(j);
                        }
                }//removes all time slots where activity is already scheduled for this session

                domains.set(i, domain);//add possible open times to domain for index i (which corresponds to activity i in list)
            }//end set up domain for non-set time activities
            domain.clear();
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
    }



    //returns array of concflict count with indices corresponding to index of activity list
    private ArrayList<Integer> countConflicts(ArrayList<DBObject> list) {
        ArrayList<Integer> numConflicts = new ArrayList<>();
        for(int i=0; i<list.size(); i++) {//initialize all so have no conflicts
            numConflicts.set(i, 0);
        }

        for(int i=0; i<list.size(); i++) {//for each activity
            for(int j=0; j<list.size(); j++) {//for each other activity
                if(i!=j) {//make sure not comparing same activity

                    //if same session in two different places at same time
                    if(list.get(i).get("session") == list.get(j).get("session")
                            && (list.get(i).get("time") == list.get(j).get("time"))
                            && (list.get(i).get("activityAreaID") != list.get(j).get("activityAreaID"))) {

                        //add conflict to each thing thing !!!!!!!SHOULD CHECK IF FIXED HERE???????!!!!!!!!!IDK TODO
                        numConflicts.set(i, (numConflicts.get(i)+1) );
                        numConflicts.set(j, (numConflicts.get(j)+1) );
                    }//end two places at once conflict

                    //if two different sessions in same place at same time
                    if( (list.get(i).get("activityAreaID") == list.get(j).get("activityAreaID"))//if activity in same time & place
                            && (list.get(i).get("time") == list.get(j).get("time"))
                            && (list.get(i).get("session") != list.get(j).get("session"))) {

                        //add conflict to each thing thing !!!!!!!SHOULD CHECK IF FIXED HERE???????!!!!!!!!!IDK TODO
                        numConflicts.set(i, (numConflicts.get(i)+1) );
                        numConflicts.set(j, (numConflicts.get(j)+1) );
                    }//end two sessions at once conflict

                }//end compare two different activities
            }
        }//end compare all activities for conflicts

        return numConflicts;
    }//end countConflicts




    //uses heuristic repair to fix conflicts betweeen activities
    private void fixConflicts(int type) {

        //assign values to everything without them
        if(type==1) {//type 1: there are variables without assigned value

            //get domains of all activities
            DBCursor cursor = activities.get().getDBCollection().find();
            List<DBObject> arrry = cursor.toArray();
            ArrayList<DBObject> actArray = new ArrayList<DBObject>(arrry);
            ArrayList< ArrayList<DateTime> > domains = findDomain(actArray);//possible domains of times for activity with corresponding index in acts
            ArrayList<DateTime> newDomain = new ArrayList<DateTime>();

            while(!(actArray.isEmpty())) {
                //assign all activities with only one option in domain
                for(int i=0; i<actArray.size(); i++) {
                    //if no available times in domain, just add a random time to domain and fix it later #yolo
                    if(domains.get(i).size() == 0) {
                        DBObject sessionDates = (DBObject) campSessions.get().findOne("{session: " + actArray.get(i).get("session") + " } { startDate: 1, endDate: 1, _id:0 }");//should return DBObject with just start and end date of current session
                        DateTime start = (DateTime) sessionDates.get("startDate");
                        start = start.withHourOfDay(11);//just set to 11 o'clock whatever maybe fix later TODO
                        newDomain.add(start);
                        domains.set(i, newDomain);
                        newDomain.clear();
                    }//end random time assignment for no options in domain

                    //if only one option in domain (will catch stuff from previous if statement)
                    if(domains.get(i).size() == 1) {
                        if(actArray.get(i).get("set") == false) {//if activity's time is not yet set

                            DateTime time = domains.get(i).get(0);//time is only available time in domain

                            //update activity in collection
                            String ID = String.valueOf(actArray.get(i).get("_id"));
                            activities.get().findAndModify("{query: {_id: " + ID + " }, update: { $set: { set: true }, {time: "+ time +" }");//changes activity so set is true and time is added:
                                                                                                                                            //'query' finds activity with appropriate id, '$set' changes set to true
                            //SHOULD ALSO CHANGE ACTIVITY IN CAMPSESSION COLLECTION?? IDK TODO

                            //update domains of other thingys because now getting rid of  time option
                            for(int j=0; j<actArray.size(); j++) {
                                if(domains.get(j).size() > 1) {//
                                    if(domains.get(j).contains(time)) {
                                        newDomain = domains.get(j);
                                        newDomain.remove(time);
                                        domains.set(j, newDomain);
                                        newDomain.clear();
                                    }
                                }
                            }//end update domains of other activities
                        }//end setting time for unset activity

                        //remove activity from actArray because it is set and no longer needs to be looked at
                        actArray.remove(i);
                        domains.remove(i);//remove from domains so indices still match
                        i--;//because size of array is one smaller
                    }
                }//end assign all activities with only one option in domain

                //now find item with least # of options (most constrained) and remove all but one item from its possible domain - next run through of while loop will take care of business (probably)
                int mostConst = getMostConstrained(domains);
                if(mostConst > 0) {//if there is a most constrained
                    newDomain.add(domains.get(mostConst).get(0)); //just take first option for domain
                    domains.set(mostConst, newDomain);
                    newDomain.clear();
                } else {} //probably the thing is empty? idk do nothing for now

            }//end assign times to all previously non-fixed activities


        }//end assign values to all variables without time assigned

        //heuristic repair time: there's conflicting stuff so fix it
        while(checkConflicts() > 0) {

            //get domains of all activities
            DBCursor cursor = activities.get().getDBCollection().find();
            List<DBObject> arrry = cursor.toArray();
            ArrayList<DBObject> actArray = new ArrayList<DBObject>(arrry);
            ArrayList< ArrayList<DateTime> > domains = findDomain(actArray);//possible domains of times for activity with corresponding index in acts
            ArrayList<Integer> numConflicts = countConflicts(actArray);
            ArrayList<DateTime> newDomain = new ArrayList<DateTime>();

            //get count of conflicts for each activities

        }


        return;
    }//end fixconflicts()




    //checks if everything is staffed appropriately
    private boolean checkStaffConflicts() {
        return false;
    }




    //uses heuristic repair to fix any staffing conflicts
    private void fixStaffConflicts() {

    }
}
