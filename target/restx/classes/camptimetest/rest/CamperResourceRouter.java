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
        new StdEntityRoute<Void, Empty>("default#CamperResource#registerCamper",
                readerRegistry.<Void>build(Void.class, Optional.<String>absent()),
                writerRegistry.<Empty>build(void.class, Optional.<String>absent()),
                new StdRestxRequestMatcher("PUT", "/campers/{camperID}/{sessionID}"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<Empty> doRoute(RestxRequest request, RestxRequestMatch match, Void body) throws IOException {
                securityManager.check(request, open());
                resource.registerCamper(
                        /* [PATH] camperID */ match.getPathParam("camperID"),
                        /* [PATH] sessionID */ match.getPathParam("sessionID")
                );
                return Optional.of(Empty.EMPTY);
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


                operation.responseClass = "void";
                operation.inEntitySchemaKey = "";
                operation.outEntitySchemaKey = "";
                operation.sourceLocation = "camptimetest.rest.CamperResource#registerCamper(java.lang.String,java.lang.String)";
            }
        },
        });
    }

}
