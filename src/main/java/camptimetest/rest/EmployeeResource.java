package camptimetest.rest;

import camptimetest.domain.Employee;
import restx.annotations.DELETE;
import restx.annotations.GET;
import restx.annotations.POST;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.jongo.JongoCollection;
import restx.security.PermitAll;

import javax.inject.Named;

/**
 * @Component designates class as injectable and for factory to be set up for it at compile time
 * @RestxResource designates class to receive http routing requests
 */

@Component @RestxResource
@PermitAll //will eventually want to change to allow only certain roles
public class EmployeeResource {
    private final JongoCollection employees; //Database access object, will inject when resource is created/called

    //@Named designates which collection to connect to in database whose name is specified in AppModule
    public EmployeeResource(@Named("employees") JongoCollection employees) {this.employees = employees;}


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

    //@DELETE("/employees")
    //public Employee deleteEmployee()



}
