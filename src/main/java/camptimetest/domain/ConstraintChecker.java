package camptimetest.domain;

import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;
import restx.jongo.JongoCollection;

/**
* Created by sophiawang on 2/4/15.
*/
public class ConstraintChecker {
    @Id
    @ObjectId
    private String ID; //idk if it needs one, probs not
    private JongoCollection activities;

    public ConstraintChecker(JongoCollection activities) {
        this.activities = activities;
    }

    public JongoCollection update() {
        while(this.checkConflicts()) {
            fixConflicts();
        }



        return this.activities;
    }


    private boolean checkConflicts() {

        return false;
    }


    private void fixConflicts() {


    }



    //    private HeuristicSearchAlgorithm algorithm;
//    private ProblemDomain constraints;

//    public boolean check(MasterSchedule masterSchedule){return;}
}
