package camptimetest.rest;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

/**
 * Created by Eric on 2/26/2015.
 */
public class CollectionHelper {

    public CollectionHelper(){

    }

    public static ArrayList<ObjectId> stringsToObjectIds(ArrayList<String> toConvert){
        ArrayList<ObjectId> objectIds = new ArrayList<>();
        for(String Oid: toConvert){
            objectIds.add(new ObjectId(Oid));
        }

        return objectIds;
    }
    //returns string query in correct format YYYY-mm-ddTHH:mm:ssZ
    public static String getDateQuery(DateTime date){
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String dateString = date.toString(fmt);
        return String.format("{time: {$date: \"%s\"}}", dateString);
    }

}
