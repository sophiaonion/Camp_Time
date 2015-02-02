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

public class CamperResourceRouter extends RestxRouter {

    public CamperResourceRouter(
                    final CamperResource resource,
                    final EntityRequestBodyReaderRegistry readerRegistry,
                    final EntityResponseWriterRegistry writerRegistry,
                    final MainStringConverter converter,
                    final Validator validator,
                    final RestxSecurityManager securityManager) {
        super(
            "default", "CamperResourceRouter", new RestxRoute[] {
        new StdEntityRoute<Void, camptimetest.domain.SessionRegistration>("default#CamperResource#registerCamper",
                readerRegistry.<Void>build(Void.class, Optional.<String>absent()),
                writerRegistry.<camptimetest.domain.SessionRegistration>build(camptimetest.domain.SessionRegistration.class, Optional.<String>absent()),
                new StdRestxRequestMatcher("PUT", "/campers/{camperID}/{sessionID}"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<camptimetest.domain.SessionRegistration> doRoute(RestxRequest request, RestxRequestMatch match, Void body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.registerCamper(
                        /* [PATH] camperID */ match.getPathParam("camperID"),
                        /* [PATH] sessionID */ match.getPathParam("sessionID")
                ));
            }

            @Override
            protected void describeOperation(OperationDescription operation) {
                super.describeOperation(operation);
                                OperationParameterDescription camperID = new OperationParameterDescription();
                camperID.name = "camperID";
                camperID.paramType = OperationParameterDescription.ParamType.path;
                camperID.dataType = "string";
                camperID.schemaKey = "";
                camperID.required = true;
                operation.parameters.add(camperID);

                OperationParameterDescription sessionID = new OperationParameterDescription();
                sessionID.name = "sessionID";
                sessionID.paramType = OperationParameterDescription.ParamType.path;
                sessionID.dataType = "string";
                sessionID.schemaKey = "";
                sessionID.required = true;
                operation.parameters.add(sessionID);


                operation.responseClass = "SessionRegistration";
                operation.inEntitySchemaKey = "";
                operation.outEntitySchemaKey = "camptimetest.domain.SessionRegistration";
                operation.sourceLocation = "camptimetest.rest.CamperResource#registerCamper(java.lang.String,java.lang.String)";
            }
        },
        new StdEntityRoute<Void, java.lang.Iterable<camptimetest.domain.Camper>>("default#CamperResource#getCampers",
                readerRegistry.<Void>build(Void.class, Optional.<String>absent()),
                writerRegistry.<java.lang.Iterable<camptimetest.domain.Camper>>build(Types.newParameterizedType(java.lang.Iterable.class, camptimetest.domain.Camper.class), Optional.<String>absent()),
                new StdRestxRequestMatcher("GET", "/campers"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<java.lang.Iterable<camptimetest.domain.Camper>> doRoute(RestxRequest request, RestxRequestMatch match, Void body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.getCampers(
                        
                ));
            }

            @Override
            protected void describeOperation(OperationDescription operation) {
                super.describeOperation(operation);
                

                operation.responseClass = "LIST[Camper]";
                operation.inEntitySchemaKey = "";
                operation.outEntitySchemaKey = "camptimetest.domain.Camper";
                operation.sourceLocation = "camptimetest.rest.CamperResource#getCampers()";
            }
        },
        });
    }

}
