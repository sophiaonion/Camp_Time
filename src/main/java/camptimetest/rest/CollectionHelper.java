package camptimetest.rest;

import org.bson.types.ObjectId;

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
}
