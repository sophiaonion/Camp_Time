package camptimetest.rest;

import camptimetest.domain.Activity;
import camptimetest.domain.Employee;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
    public Iterable<Employee> findEmployee() {
        return employees.get().find().as(Employee.class);
    }

    //get list of activities with certain Employee
    @GET("/employees/{employeeID}")
    public Iterable<Activity> getEmployeeActivities(String employeeID){
        return employees.get().find("{employee:#}",employeeID).as(Activity.class);
    }


    //if parameter isn't specified in URI path with {}, assumes it is the message body
    //Jackson library automatically tries to map sent JSON to specified class,
    //setting fields that match and ignoring others
    //parameters sent by POST are automatically put into message body
//    @POST("/employees")
//    public Employee createEmployee(Employee employee){
//        employees.get().save(employee);
//        return employee; //can return anything doesn't have to be sent object
//    }

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

    @GET("/employees/time/{time}") //returns object map object with scheduled, available Employee fields
    public Map<String, Employee> employeesToActivity(String time){
        //first get all activities happening at that time
        Iterable<Activity> concurrentActs = activities.get().find("{time: #}", time).as(Activity.class);
        Map<String, ArrayList<Employee>> data = new HashMap<>();
        //get ObjectIds of all working employees
        ArrayList<String> workingEmps = new ArrayList<>();
        for(Activity act : concurrentActs){
            for(String key : act.getEmployees()){
                workingEmps.add(key);
            }
        }

        //query based on all employee ids not in workingEmps array
         employees.get().find("{_id: {$nin: #}}", CollectionHelper.stringsToObjectIds(workingEmps)).as(Employee.class);
    }

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
