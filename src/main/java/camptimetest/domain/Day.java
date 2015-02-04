package camptimetest.domain;

import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.ObjectId;

import java.util.ArrayList;

/**
 * Created by sophiawang on 2/4/15.
 */
public class Day {
    @Id
    @ObjectId
    private Hour hour; //name of the certification
    private String day;

    public Hour getHour(){return hour;}

    public Day setHour(Hour hour){
        this.hour=hour;
        return this;
    }

    public String getDay(){return day;}
    public Day setDay(String day){
        this.day=day;
        return this;
    }

}
