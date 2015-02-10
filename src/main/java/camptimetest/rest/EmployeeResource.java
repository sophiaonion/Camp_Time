package camptimetest.rest;

import camptimetest.domain.Activity;
import camptimetest.domain.Employee;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import restx.Status;
import restx.annotations.*;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import restx.security.PermitAll;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.Map;

/**
 * @Component designates class as injectable and for factory to be set up for it at compile time
 * @RestxResource designates class to receive http routing requests
 */

@Component @RestxResource
@PermitAll //will eventually want to change to allow only certain roles
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


    @GET("/employees") //has to return iterable since .find() returns iterable of objects
    //even if only one entry is returned, .findOne will return object not wrapped in iterable
    public Iterable<Employee> findEmployee() {
        return employees.get().find().as(Employee.class);
    }


    //if parameter isn't specified in URI path with {}, assumes it is the message body
    //Jackson library automatically tries to map sent JSON to specified class,
    //setting fields that match and ignoring others
    //parameters sent by POST are automatically put into message body
    @POST("/employees")
    public Employee createEmployee(Employee employee){
        employees.get().save(employee);
        return employee; //can return anything doesn't have to be sent object
    }

    @DELETE("/employees/{employeeID}")
    public Status deleteEmployee(String employeeID){
        ObjectId employeeId= new ObjectId(employeeID);
        employees.get().remove("{_id:#}", employeeId);
        return Status.of("deleted");
    }


    @GET("/employees/time/{time}") //get all unscheduled employees at time
    public Iterable<Employee>findAvailableEmployees(String time){
        //first get all activities happening at that time
        Iterable<Activity> concurrentActs = activities.get().find("{time: #}", time).as(Activity.class);
        //get ObjectIds of all working employees
        ArrayList<ObjectId> workingEmps = new ArrayList<>();
        for(Activity act : concurrentActs){
            for(Employee emp : act.getEmployees()){
                workingEmps.add(new ObjectId(emp.getKey()));
            }
        }
        //query based on all employee ids not in workingEmps array
         return employees.get().find("{_id: {$nin: #}}", workingEmps).as(Employee.class);
    }

    //get sent Map of key value pairs
    //employee_id and activity_id
    @PUT("/employees/activities/add")
    public Activity addEmployeeToActivity(Map<String, String> values){
        ObjectId empId = new ObjectId(values.get("employee_id"));
        Employee emp = employees.get().findOne("{_id: #}", new ObjectId(values.get("employee_id"))).as(Employee.class);
        //rather than update -- retrieve activity -- add employee to activity and add activity to employee and save both
        Activity act = activities.get().findOne("{_id: #}", new ObjectId(values.get("activity_id"))).as(Activity.class);


        activities.get().update("{_id:#}", new ObjectId(values.get("employee_id"))).with("{$push: {employees: #}}", emp);
        employees.get().update("{_id:#", new ObjectId(values.get("activity_id"))).with("{$push: {activities: #}}", act);

//brutish way but works as well, duplicating array adding and setting again ... ran into object marshalling problems when using an
//add employee method on activity and then trying to save
//        ArrayList<Employee> updatedEmps = act.getEmployees();
//        updatedEmps.add(emp);
//        act.setEmployees(updatedEmps);
//        activities.get().save(act);
// will just overwrite objects in database with same ObjectId instead of duplicating
        return act;
    }




}
