package camptimetest.rest;

import camptimetest.domain.Activity;
import camptimetest.domain.Employee;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import restx.Status;
import restx.admin.AdminPagesResource;
import restx.annotations.*;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import restx.security.PermitAll;
import restx.security.RolesAllowed;
import camptimetest.rest.CollectionHelper;


import javax.inject.Named;
import java.util.*;

import static camptimetest.AppModule.Roles.*;
import static org.jongo.Oid.withOid;

/**
 * @Component designates class as injectable and for factory to be set up for it at compile time
 * @RestxResource designates class to receive http routing requests
 */

@Component
@RestxResource
@PermitAll
public class EmployeeResource {
    private final JongoCollection employees; //Database access object, will inject when resource is created/called
    private final JongoCollection activities;

    //@Named designates which collection to connect to in database whose name is specified in AppModule
    public EmployeeResource(@Named("employees") JongoCollection employees,
                            @Named("activities") JongoCollection activities)
    {
        this.employees = employees;
        this.activities = activities;
    }

    @RolesAllowed(ADMIN)
    @GET("/employees") //has to return iterable since .find() returns iterable of objects
    //even if only one entry is returned, .findOne will return object not wrapped in iterable
    public Iterable<Employee> getEmployees() {
        return employees.get().find().as(Employee.class);
    }

    //get list of activities with certain Employee
    @GET("/employees/{employeeID}")
    public Iterable<Activity> getEmployeeActivities(String employeeID){
        return employees.get().find("{employee:#}",employeeID).as(Activity.class);
    }


    //get employees based on array of employee ids
    //sent over with 'employee_ids' key
    @PUT("/employees/ids")
    public Iterable<Employee> getEmployeesFromIds(Map<String, ArrayList<String>> empIds){
        ArrayList<ObjectId> empObjectIds = CollectionHelper.stringsToObjectIds(empIds.get("employee_ids"));
        return employees.get().find("{_id: {$in : #}}", empObjectIds).as(Employee.class);
    }

    @POST("/employees")
    public Employee createEmployee(Map<String, Object> info){

        //convert date into date
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yy-MM-dd");
        DateTime startBreak = new DateTime( fmt.parseDateTime(info.get("startBreak").toString()));

        //convert certifications to arraylist
        ArrayList<String> certs = (ArrayList<String>) info.get("certifications");

        Employee newEmp = new Employee();
        newEmp.setName(String.valueOf(info.get("name")));
        newEmp.setAge(Integer.valueOf(String.valueOf(info.get("age"))));
        newEmp.setGender(String.valueOf(info.get("gender")));
        newEmp.setJob(String.valueOf(info.get("job")));
        newEmp.setIntervalBreak(Integer.valueOf(String.valueOf(info.get("intervalBreak"))));
        newEmp.setStartBreak(startBreak);
        newEmp.setCertifications(certs);


        employees.get().save(newEmp);
        return newEmp; //can return anything doesn't have to be sent object
    }

    @DELETE("/employees/{employeeID}")
    public Status deleteEmployee(String employeeID){
        ObjectId employeeId= new ObjectId(employeeID);
        employees.get().remove("{_id:#}", employeeId);
        return Status.of("deleted");
    }

    @PUT("/employees/time") //returns object map object with scheduled, available Employee fields
    public Iterable<Employee> employeesToActivity(DateTime time){
        //first get all activities happening at that time
       String query = CollectionHelper.getDateQuery(time);
       Iterable<Activity> activitiesAtTime = activities.get().find(query).as(Activity.class);

        //get ObjectIds of all working employees
        ArrayList<String> workingEmps = new ArrayList<>();
        for(Activity act : activitiesAtTime){
            for(String key : act.getEmployees()){
                workingEmps.add(key);
            }
        }

        //query get all employee ids not in workingEmps array
        Iterable<Employee> available = employees.get().find("{_id: {$nin: #}}", CollectionHelper.stringsToObjectIds(workingEmps)).as(Employee.class);
        return available;
    }

    //getEmployeeObjects

    //get sent Map of key value pairs
    //employee_id and activity_id
    @PUT("/employees/activities/add")
    public String addEmployeeToActivity(Map<String, Object> values){
        ArrayList<String> stringIds = (ArrayList<String>)values.get("employees");
        ArrayList<ObjectId> employeeIds = CollectionHelper.stringsToObjectIds(stringIds);
        String activityId = (String)values.get("activity_id");

        Iterable<Employee> emps = employees.get().find("{_id: {$in: #}}", employeeIds).as(Employee.class);
        Activity act = activities.get().findOne("{_id: #}", new ObjectId(activityId)).as(Activity.class);

        for(Employee emp: emps){
            emp.addActivity(activityId);
            employees.get().save(emp);
        }

        act.addEmployees(stringIds);
        activities.get().save(act);

        return "200";
    }

    //employee_id and activity_id
    @PUT("/employees/activities/remove")
    public Activity removeEmployeeFromActivity(Map<String, Object> values){
        ArrayList<String> employeeStringIds = (ArrayList<String>)values.get("employees");
        ArrayList<ObjectId> employeeIds = CollectionHelper.stringsToObjectIds(employeeStringIds);
        String activityId = (String)values.get("activity_id");

        Iterable<Employee> emps = employees.get().find("{_id: {$in: #}}", employeeIds).as(Employee.class);
        Activity act = activities.get().findOne("{_id: #}", new ObjectId(activityId)).as(Activity.class);

        for(Employee emp: emps){
            emp.removeActivity(activityId);
            employees.get().save(emp);
        }

        act.removeEmployees(employeeStringIds);
        activities.get().save(act);
    return act;
    }




}
