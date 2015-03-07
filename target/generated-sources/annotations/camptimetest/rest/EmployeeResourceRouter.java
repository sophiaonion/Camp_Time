package camptimetest.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;

import restx.common.Types;
import restx.*;
import restx.entity.*;
import restx.http.*;
import restx.factory.*;
import restx.security.*;
import static restx.security.Permissions.*;
import restx.description.*;
import restx.converters.MainStringConverter;
import static restx.common.MorePreconditions.checkPresent;

import javax.validation.Validator;
import static restx.validation.Validations.checkValid;

import java.io.IOException;
import java.io.PrintWriter;

@Component(priority = 0)

public class EmployeeResourceRouter extends RestxRouter {

    public EmployeeResourceRouter(
                    final EmployeeResource resource,
                    final EntityRequestBodyReaderRegistry readerRegistry,
                    final EntityResponseWriterRegistry writerRegistry,
                    final MainStringConverter converter,
                    final Validator validator,
                    final RestxSecurityManager securityManager) {
        super(
            "default", "EmployeeResourceRouter", new RestxRoute[] {
        new StdEntityRoute<Void, java.lang.Iterable<camptimetest.domain.Employee>>("default#EmployeeResource#getEmployees",
                readerRegistry.<Void>build(Void.class, Optional.<String>absent()),
                writerRegistry.<java.lang.Iterable<camptimetest.domain.Employee>>build(Types.newParameterizedType(java.lang.Iterable.class, camptimetest.domain.Employee.class), Optional.<String>absent()),
                new StdRestxRequestMatcher("GET", "/employees"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<java.lang.Iterable<camptimetest.domain.Employee>> doRoute(RestxRequest request, RestxRequestMatch match, Void body) throws IOException {
                securityManager.check(request, hasRole("admin"));
                return Optional.of(resource.getEmployees(
                        
                ));
            }

            @Override
            protected void describeOperation(OperationDescription operation) {
                super.describeOperation(operation);
                

                operation.responseClass = "LIST[Employee]";
                operation.inEntitySchemaKey = "";
                operation.outEntitySchemaKey = "camptimetest.domain.Employee";
                operation.sourceLocation = "camptimetest.rest.EmployeeResource#getEmployees()";
            }
        },
        new StdEntityRoute<Void, java.lang.Iterable<camptimetest.domain.Activity>>("default#EmployeeResource#getEmployeeActivities",
                readerRegistry.<Void>build(Void.class, Optional.<String>absent()),
                writerRegistry.<java.lang.Iterable<camptimetest.domain.Activity>>build(Types.newParameterizedType(java.lang.Iterable.class, camptimetest.domain.Activity.class), Optional.<String>absent()),
                new StdRestxRequestMatcher("GET", "/employees/activities/{employeeID}"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<java.lang.Iterable<camptimetest.domain.Activity>> doRoute(RestxRequest request, RestxRequestMatch match, Void body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.getEmployeeActivities(
                        /* [PATH] employeeID */ match.getPathParam("employeeID")
                ));
            }

            @Override
            protected void describeOperation(OperationDescription operation) {
                super.describeOperation(operation);
                                OperationParameterDescription employeeID = new OperationParameterDescription();
                employeeID.name = "employeeID";
                employeeID.paramType = OperationParameterDescription.ParamType.path;
                employeeID.dataType = "string";
                employeeID.schemaKey = "";
                employeeID.required = true;
                operation.parameters.add(employeeID);


                operation.responseClass = "LIST[Activity]";
                operation.inEntitySchemaKey = "";
                operation.outEntitySchemaKey = "camptimetest.domain.Activity";
                operation.sourceLocation = "camptimetest.rest.EmployeeResource#getEmployeeActivities(java.lang.String)";
            }
        },
        new StdEntityRoute<java.util.Map<java.lang.String,java.util.ArrayList<java.lang.String>>, java.lang.Iterable<camptimetest.domain.Employee>>("default#EmployeeResource#getEmployeesFromIds",
                readerRegistry.<java.util.Map<java.lang.String,java.util.ArrayList<java.lang.String>>>build(Types.newParameterizedType(java.util.Map.class, java.lang.String.class, Types.newParameterizedType(java.util.ArrayList.class, java.lang.String.class)), Optional.<String>absent()),
                writerRegistry.<java.lang.Iterable<camptimetest.domain.Employee>>build(Types.newParameterizedType(java.lang.Iterable.class, camptimetest.domain.Employee.class), Optional.<String>absent()),
                new StdRestxRequestMatcher("PUT", "/employees/ids"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<java.lang.Iterable<camptimetest.domain.Employee>> doRoute(RestxRequest request, RestxRequestMatch match, java.util.Map<java.lang.String,java.util.ArrayList<java.lang.String>> body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.getEmployeesFromIds(
                        /* [BODY] empIds */ checkValid(validator, body)
                ));
            }

            @Override
            protected void describeOperation(OperationDescription operation) {
                super.describeOperation(operation);
                                OperationParameterDescription empIds = new OperationParameterDescription();
                empIds.name = "empIds";
                empIds.paramType = OperationParameterDescription.ParamType.body;
                empIds.dataType = "String>>";
                empIds.schemaKey = "";
                empIds.required = true;
                operation.parameters.add(empIds);


                operation.responseClass = "LIST[Employee]";
                operation.inEntitySchemaKey = "";
                operation.outEntitySchemaKey = "camptimetest.domain.Employee";
                operation.sourceLocation = "camptimetest.rest.EmployeeResource#getEmployeesFromIds(java.util.Map<java.lang.String,java.util.ArrayList<java.lang.String>>)";
            }
        },
        new StdEntityRoute<camptimetest.domain.Employee, java.lang.String>("default#EmployeeResource#updateEmployee",
                readerRegistry.<camptimetest.domain.Employee>build(camptimetest.domain.Employee.class, Optional.<String>absent()),
                writerRegistry.<java.lang.String>build(java.lang.String.class, Optional.<String>absent()),
                new StdRestxRequestMatcher("PUT", "/employee/update"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<java.lang.String> doRoute(RestxRequest request, RestxRequestMatch match, camptimetest.domain.Employee body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.updateEmployee(
                        /* [BODY] updateEmployee */ checkValid(validator, body)
                ));
            }

            @Override
            protected void describeOperation(OperationDescription operation) {
                super.describeOperation(operation);
                                OperationParameterDescription updateEmployee = new OperationParameterDescription();
                updateEmployee.name = "updateEmployee";
                updateEmployee.paramType = OperationParameterDescription.ParamType.body;
                updateEmployee.dataType = "Employee";
                updateEmployee.schemaKey = "camptimetest.domain.Employee";
                updateEmployee.required = true;
                operation.parameters.add(updateEmployee);


                operation.responseClass = "string";
                operation.inEntitySchemaKey = "camptimetest.domain.Employee";
                operation.outEntitySchemaKey = "";
                operation.sourceLocation = "camptimetest.rest.EmployeeResource#updateEmployee(camptimetest.domain.Employee)";
            }
        },
        new StdEntityRoute<java.util.Map<java.lang.String,java.lang.Object>, camptimetest.domain.Employee>("default#EmployeeResource#createEmployee",
                readerRegistry.<java.util.Map<java.lang.String,java.lang.Object>>build(Types.newParameterizedType(java.util.Map.class, java.lang.String.class, java.lang.Object.class), Optional.<String>absent()),
                writerRegistry.<camptimetest.domain.Employee>build(camptimetest.domain.Employee.class, Optional.<String>absent()),
                new StdRestxRequestMatcher("POST", "/employees"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<camptimetest.domain.Employee> doRoute(RestxRequest request, RestxRequestMatch match, java.util.Map<java.lang.String,java.lang.Object> body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.createEmployee(
                        /* [BODY] info */ checkValid(validator, body)
                ));
            }

            @Override
            protected void describeOperation(OperationDescription operation) {
                super.describeOperation(operation);
                                OperationParameterDescription info = new OperationParameterDescription();
                info.name = "info";
                info.paramType = OperationParameterDescription.ParamType.body;
                info.dataType = "Object>";
                info.schemaKey = "";
                info.required = true;
                operation.parameters.add(info);


                operation.responseClass = "Employee";
                operation.inEntitySchemaKey = "";
                operation.outEntitySchemaKey = "camptimetest.domain.Employee";
                operation.sourceLocation = "camptimetest.rest.EmployeeResource#createEmployee(java.util.Map<java.lang.String,java.lang.Object>)";
            }
        },
        new StdEntityRoute<Void, restx.Status>("default#EmployeeResource#deleteEmployee",
                readerRegistry.<Void>build(Void.class, Optional.<String>absent()),
                writerRegistry.<restx.Status>build(restx.Status.class, Optional.<String>absent()),
                new StdRestxRequestMatcher("DELETE", "/employees/{employeeID}"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<restx.Status> doRoute(RestxRequest request, RestxRequestMatch match, Void body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.deleteEmployee(
                        /* [PATH] employeeID */ match.getPathParam("employeeID")
                ));
            }

            @Override
            protected void describeOperation(OperationDescription operation) {
                super.describeOperation(operation);
                                OperationParameterDescription employeeID = new OperationParameterDescription();
                employeeID.name = "employeeID";
                employeeID.paramType = OperationParameterDescription.ParamType.path;
                employeeID.dataType = "string";
                employeeID.schemaKey = "";
                employeeID.required = true;
                operation.parameters.add(employeeID);


                operation.responseClass = "Status";
                operation.inEntitySchemaKey = "";
                operation.outEntitySchemaKey = "restx.Status";
                operation.sourceLocation = "camptimetest.rest.EmployeeResource#deleteEmployee(java.lang.String)";
            }
        },
        new StdEntityRoute<org.joda.time.DateTime, java.lang.Iterable<camptimetest.domain.Employee>>("default#EmployeeResource#employeesToActivity",
                readerRegistry.<org.joda.time.DateTime>build(org.joda.time.DateTime.class, Optional.<String>absent()),
                writerRegistry.<java.lang.Iterable<camptimetest.domain.Employee>>build(Types.newParameterizedType(java.lang.Iterable.class, camptimetest.domain.Employee.class), Optional.<String>absent()),
                new StdRestxRequestMatcher("PUT", "/employees/time"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<java.lang.Iterable<camptimetest.domain.Employee>> doRoute(RestxRequest request, RestxRequestMatch match, org.joda.time.DateTime body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.employeesToActivity(
                        /* [BODY] time */ checkValid(validator, body)
                ));
            }

            @Override
            protected void describeOperation(OperationDescription operation) {
                super.describeOperation(operation);
                                OperationParameterDescription time = new OperationParameterDescription();
                time.name = "time";
                time.paramType = OperationParameterDescription.ParamType.body;
                time.dataType = "Date";
                time.schemaKey = "org.joda.time.DateTime";
                time.required = true;
                operation.parameters.add(time);


                operation.responseClass = "LIST[Employee]";
                operation.inEntitySchemaKey = "org.joda.time.DateTime";
                operation.outEntitySchemaKey = "camptimetest.domain.Employee";
                operation.sourceLocation = "camptimetest.rest.EmployeeResource#employeesToActivity(org.joda.time.DateTime)";
            }
        },
        new StdEntityRoute<java.util.Map<java.lang.String,java.lang.Object>, java.lang.String>("default#EmployeeResource#addEmployeeToActivity",
                readerRegistry.<java.util.Map<java.lang.String,java.lang.Object>>build(Types.newParameterizedType(java.util.Map.class, java.lang.String.class, java.lang.Object.class), Optional.<String>absent()),
                writerRegistry.<java.lang.String>build(java.lang.String.class, Optional.<String>absent()),
                new StdRestxRequestMatcher("PUT", "/employees/activities/add"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<java.lang.String> doRoute(RestxRequest request, RestxRequestMatch match, java.util.Map<java.lang.String,java.lang.Object> body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.addEmployeeToActivity(
                        /* [BODY] values */ checkValid(validator, body)
                ));
            }

            @Override
            protected void describeOperation(OperationDescription operation) {
                super.describeOperation(operation);
                                OperationParameterDescription values = new OperationParameterDescription();
                values.name = "values";
                values.paramType = OperationParameterDescription.ParamType.body;
                values.dataType = "Object>";
                values.schemaKey = "";
                values.required = true;
                operation.parameters.add(values);


                operation.responseClass = "string";
                operation.inEntitySchemaKey = "";
                operation.outEntitySchemaKey = "";
                operation.sourceLocation = "camptimetest.rest.EmployeeResource#addEmployeeToActivity(java.util.Map<java.lang.String,java.lang.Object>)";
            }
        },
        new StdEntityRoute<java.util.Map<java.lang.String,java.lang.Object>, camptimetest.domain.CampSession>("default#EmployeeResource#removeEmployeeFromActivity",
                readerRegistry.<java.util.Map<java.lang.String,java.lang.Object>>build(Types.newParameterizedType(java.util.Map.class, java.lang.String.class, java.lang.Object.class), Optional.<String>absent()),
                writerRegistry.<camptimetest.domain.CampSession>build(camptimetest.domain.CampSession.class, Optional.<String>absent()),
                new StdRestxRequestMatcher("PUT", "/employees/activities/remove"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<camptimetest.domain.CampSession> doRoute(RestxRequest request, RestxRequestMatch match, java.util.Map<java.lang.String,java.lang.Object> body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.removeEmployeeFromActivity(
                        /* [BODY] values */ checkValid(validator, body)
                ));
            }

            @Override
            protected void describeOperation(OperationDescription operation) {
                super.describeOperation(operation);
                                OperationParameterDescription values = new OperationParameterDescription();
                values.name = "values";
                values.paramType = OperationParameterDescription.ParamType.body;
                values.dataType = "Object>";
                values.schemaKey = "";
                values.required = true;
                operation.parameters.add(values);


                operation.responseClass = "CampSession";
                operation.inEntitySchemaKey = "";
                operation.outEntitySchemaKey = "camptimetest.domain.CampSession";
                operation.sourceLocation = "camptimetest.rest.EmployeeResource#removeEmployeeFromActivity(java.util.Map<java.lang.String,java.lang.Object>)";
            }
        },
        });
    }

}
