package camptimetest.domain;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
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
                || (activities.get().count( "{time: null }" ) != 0) ) {
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
    private ArrayList< ArrayList<DateTime> > findDomain(List<DBObject> list) {
        ArrayList< ArrayList<DateTime> > domains = new ArrayList< ArrayList<DateTime> >(); //list of possible domains for each i
        ArrayList<DateTime> domain = new ArrayList<DateTime>();//domain specific to i
        Multimap<DateTime, String> takenArea = ArrayListMultimap.create();//maps time to activity area
        ArrayList<DateTime> takenSession = new ArrayList<DateTime>();//dates where session is already doing something

        //set up domain for all fixed time activities: should be only time it already is
        for(int i=0; i<list.size(); i++) {//for each activity

            if (list.get(i).get("fixed") == true) {
                domain.add((DateTime) list.get(i).get("time"));//list of available times contains only the time it is set at
                takenArea.put(((DateTime) list.get(i).get("time")), String.valueOf(list.get(i).get("activityarea")));//add to list of unavailable actibity slots
                domains.set(i, domain);//add to domains arraylist
            }
            domain.clear();
        }

        //set up domain for all non-fixed time activities: domain will be all times that are currently not filled (& also times where not already doing something???)
        for(int i=0; i<list.size(); i++) {
            if (list.get(i).get("fixed") == false) {
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


                domains.set(i, domain);//add possible open times to domain for index i (which corresponds to activity i in list)
            }
            domain.clear();
        }//end set up corresponding domain i for each activity i in list
        return domains;
    }//end finddomain()


    //uses heuristic repair to fix conflicts betweeen activities
    private void fixConflicts(int type) {

        //get domains of all activities
        DBCursor cursor = activities.get().getDBCollection().find();
        List<DBObject> acts = cursor.toArray();
        ArrayList< ArrayList<DateTime> > domains = findDomain(acts);//possible domains of times for activity with corresponding index in acts

        //if there are activities without a time set
        if(type==1) {
            //find least constrained activity -- well all the non fixed activities should be almost equally constrained, so not super important
        }
        else {//type=2/3 -- some other type of conflict

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
