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
        new StdEntityRoute<Void, java.lang.Iterable<camptimetest.domain.Employee>>("default#EmployeeResource#findEmployee",
                readerRegistry.<Void>build(Void.class, Optional.<String>absent()),
                writerRegistry.<java.lang.Iterable<camptimetest.domain.Employee>>build(Types.newParameterizedType(java.lang.Iterable.class, camptimetest.domain.Employee.class), Optional.<String>absent()),
                new StdRestxRequestMatcher("GET", "/employees"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<java.lang.Iterable<camptimetest.domain.Employee>> doRoute(RestxRequest request, RestxRequestMatch match, Void body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.findEmployee(
                        
                ));
            }

            @Override
            protected void describeOperation(OperationDescription operation) {
                super.describeOperation(operation);
                

                operation.responseClass = "LIST[Employee]";
                operation.inEntitySchemaKey = "";
                operation.outEntitySchemaKey = "camptimetest.domain.Employee";
                operation.sourceLocation = "camptimetest.rest.EmployeeResource#findEmployee()";
            }
        },
        new StdEntityRoute<camptimetest.domain.Employee, camptimetest.domain.Employee>("default#EmployeeResource#createEmployee",
                readerRegistry.<camptimetest.domain.Employee>build(camptimetest.domain.Employee.class, Optional.<String>absent()),
                writerRegistry.<camptimetest.domain.Employee>build(camptimetest.domain.Employee.class, Optional.<String>absent()),
                new StdRestxRequestMatcher("POST", "/employees"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<camptimetest.domain.Employee> doRoute(RestxRequest request, RestxRequestMatch match, camptimetest.domain.Employee body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.createEmployee(
                        /* [BODY] employee */ checkValid(validator, body)
                ));
            }

            @Override
            protected void describeOperation(OperationDescription operation) {
                super.describeOperation(operation);
                                OperationParameterDescription employee = new OperationParameterDescription();
                employee.name = "employee";
                employee.paramType = OperationParameterDescription.ParamType.body;
                employee.dataType = "Employee";
                employee.schemaKey = "camptimetest.domain.Employee";
                employee.required = true;
                operation.parameters.add(employee);


                operation.responseClass = "Employee";
                operation.inEntitySchemaKey = "camptimetest.domain.Employee";
                operation.outEntitySchemaKey = "camptimetest.domain.Employee";
                operation.sourceLocation = "camptimetest.rest.EmployeeResource#createEmployee(camptimetest.domain.Employee)";
            }
        },
        });
    }

}
