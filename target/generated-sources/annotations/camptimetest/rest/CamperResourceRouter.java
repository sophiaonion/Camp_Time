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
                new StdRestxRequestMatcher("PUT", "/campers/{camperID}/{sessionID}/{approved}"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<camptimetest.domain.SessionRegistration> doRoute(RestxRequest request, RestxRequestMatch match, Void body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.registerCamper(
                        /* [PATH] camperID */ match.getPathParam("camperID"),
                        /* [PATH] sessionID */ match.getPathParam("sessionID"),
                        /* [PATH] approved */ match.getPathParam("approved")
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

                OperationParameterDescription approved = new OperationParameterDescription();
                approved.name = "approved";
                approved.paramType = OperationParameterDescription.ParamType.path;
                approved.dataType = "string";
                approved.schemaKey = "";
                approved.required = true;
                operation.parameters.add(approved);


                operation.responseClass = "SessionRegistration";
                operation.inEntitySchemaKey = "";
                operation.outEntitySchemaKey = "camptimetest.domain.SessionRegistration";
                operation.sourceLocation = "camptimetest.rest.CamperResource#registerCamper(java.lang.String,java.lang.String,java.lang.String)";
            }
        },
        new StdEntityRoute<java.util.Map<java.lang.String,java.lang.Object>, Empty>("default#CamperResource#approveCampers",
                readerRegistry.<java.util.Map<java.lang.String,java.lang.Object>>build(Types.newParameterizedType(java.util.Map.class, java.lang.String.class, java.lang.Object.class), Optional.<String>absent()),
                writerRegistry.<Empty>build(void.class, Optional.<String>absent()),
                new StdRestxRequestMatcher("PUT", "/campers/approve"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<Empty> doRoute(RestxRequest request, RestxRequestMatch match, java.util.Map<java.lang.String,java.lang.Object> body) throws IOException {
                securityManager.check(request, open());
                resource.approveCampers(
                        /* [BODY] reg */ checkValid(validator, body)
                );
                return Optional.of(Empty.EMPTY);
            }

            @Override
            protected void describeOperation(OperationDescription operation) {
                super.describeOperation(operation);
                                OperationParameterDescription reg = new OperationParameterDescription();
                reg.name = "reg";
                reg.paramType = OperationParameterDescription.ParamType.body;
                reg.dataType = "Object>";
                reg.schemaKey = "";
                reg.required = true;
                operation.parameters.add(reg);


                operation.responseClass = "void";
                operation.inEntitySchemaKey = "";
                operation.outEntitySchemaKey = "";
                operation.sourceLocation = "camptimetest.rest.CamperResource#approveCampers(java.util.Map<java.lang.String,java.lang.Object>)";
            }
        },
        new StdEntityRoute<Void, java.lang.Iterable<camptimetest.domain.Camper>>("default#CamperResource#getCampersforCounselor",
                readerRegistry.<Void>build(Void.class, Optional.<String>absent()),
                writerRegistry.<java.lang.Iterable<camptimetest.domain.Camper>>build(Types.newParameterizedType(java.lang.Iterable.class, camptimetest.domain.Camper.class), Optional.<String>absent()),
                new StdRestxRequestMatcher("GET", "/campers/{sessionId}"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<java.lang.Iterable<camptimetest.domain.Camper>> doRoute(RestxRequest request, RestxRequestMatch match, Void body) throws IOException {
                securityManager.check(request, anyOf(hasRole("admin"), hasRole("counselor")));
                return Optional.of(resource.getCampersforCounselor(
                        /* [PATH] sessionId */ match.getPathParam("sessionId")
                ));
            }

            @Override
            protected void describeOperation(OperationDescription operation) {
                super.describeOperation(operation);
                                OperationParameterDescription sessionId = new OperationParameterDescription();
                sessionId.name = "sessionId";
                sessionId.paramType = OperationParameterDescription.ParamType.path;
                sessionId.dataType = "string";
                sessionId.schemaKey = "";
                sessionId.required = true;
                operation.parameters.add(sessionId);


                operation.responseClass = "LIST[Camper]";
                operation.inEntitySchemaKey = "";
                operation.outEntitySchemaKey = "camptimetest.domain.Camper";
                operation.sourceLocation = "camptimetest.rest.CamperResource#getCampersforCounselor(java.lang.String)";
            }
        },
        new StdEntityRoute<Void, java.lang.Iterable<camptimetest.domain.Camper>>("default#CamperResource#getCampersforCustomer",
                readerRegistry.<Void>build(Void.class, Optional.<String>absent()),
                writerRegistry.<java.lang.Iterable<camptimetest.domain.Camper>>build(Types.newParameterizedType(java.lang.Iterable.class, camptimetest.domain.Camper.class), Optional.<String>absent()),
                new StdRestxRequestMatcher("GET", "/campers/customer/{customerID}"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<java.lang.Iterable<camptimetest.domain.Camper>> doRoute(RestxRequest request, RestxRequestMatch match, Void body) throws IOException {
                securityManager.check(request, anyOf(hasRole("admin"), hasRole("customer"), hasRole("counselor"), hasRole("specialty")));
                return Optional.of(resource.getCampersforCustomer(
                        /* [PATH] customerID */ match.getPathParam("customerID")
                ));
            }

            @Override
            protected void describeOperation(OperationDescription operation) {
                super.describeOperation(operation);
                                OperationParameterDescription customerID = new OperationParameterDescription();
                customerID.name = "customerID";
                customerID.paramType = OperationParameterDescription.ParamType.path;
                customerID.dataType = "string";
                customerID.schemaKey = "";
                customerID.required = true;
                operation.parameters.add(customerID);


                operation.responseClass = "LIST[Camper]";
                operation.inEntitySchemaKey = "";
                operation.outEntitySchemaKey = "camptimetest.domain.Camper";
                operation.sourceLocation = "camptimetest.rest.CamperResource#getCampersforCustomer(java.lang.String)";
            }
        },
        new StdEntityRoute<Void, java.lang.Iterable<camptimetest.domain.Camper>>("default#CamperResource#getCamper",
                readerRegistry.<Void>build(Void.class, Optional.<String>absent()),
                writerRegistry.<java.lang.Iterable<camptimetest.domain.Camper>>build(Types.newParameterizedType(java.lang.Iterable.class, camptimetest.domain.Camper.class), Optional.<String>absent()),
                new StdRestxRequestMatcher("GET", "/campers/camperInfo/{camperID}"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<java.lang.Iterable<camptimetest.domain.Camper>> doRoute(RestxRequest request, RestxRequestMatch match, Void body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.getCamper(
                        /* [PATH] camperID */ match.getPathParam("camperID")
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


                operation.responseClass = "LIST[Camper]";
                operation.inEntitySchemaKey = "";
                operation.outEntitySchemaKey = "camptimetest.domain.Camper";
                operation.sourceLocation = "camptimetest.rest.CamperResource#getCamper(java.lang.String)";
            }
        },
        new StdEntityRoute<Void, java.lang.Iterable<camptimetest.domain.CampSession>>("default#CamperResource#getCampersApprovedSessions",
                readerRegistry.<Void>build(Void.class, Optional.<String>absent()),
                writerRegistry.<java.lang.Iterable<camptimetest.domain.CampSession>>build(Types.newParameterizedType(java.lang.Iterable.class, camptimetest.domain.CampSession.class), Optional.<String>absent()),
                new StdRestxRequestMatcher("GET", "/campers/registrations/approve/{camperID}"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<java.lang.Iterable<camptimetest.domain.CampSession>> doRoute(RestxRequest request, RestxRequestMatch match, Void body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.getCampersApprovedSessions(
                        /* [PATH] camperID */ match.getPathParam("camperID")
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


                operation.responseClass = "LIST[CampSession]";
                operation.inEntitySchemaKey = "";
                operation.outEntitySchemaKey = "camptimetest.domain.CampSession";
                operation.sourceLocation = "camptimetest.rest.CamperResource#getCampersApprovedSessions(java.lang.String)";
            }
        },
        new StdEntityRoute<Void, java.lang.Iterable<camptimetest.domain.Camper>>("default#CamperResource#getCampers",
                readerRegistry.<Void>build(Void.class, Optional.<String>absent()),
                writerRegistry.<java.lang.Iterable<camptimetest.domain.Camper>>build(Types.newParameterizedType(java.lang.Iterable.class, camptimetest.domain.Camper.class), Optional.<String>absent()),
                new StdRestxRequestMatcher("GET", "/campers/all"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<java.lang.Iterable<camptimetest.domain.Camper>> doRoute(RestxRequest request, RestxRequestMatch match, Void body) throws IOException {
                securityManager.check(request, anyOf(hasRole("admin"), hasRole("counselor")));
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
        new StdEntityRoute<Void, java.lang.Iterable<camptimetest.domain.CampSession>>("default#CamperResource#getCampers",
                readerRegistry.<Void>build(Void.class, Optional.<String>absent()),
                writerRegistry.<java.lang.Iterable<camptimetest.domain.CampSession>>build(Types.newParameterizedType(java.lang.Iterable.class, camptimetest.domain.CampSession.class), Optional.<String>absent()),
                new StdRestxRequestMatcher("GET", "/campers/registrations/{camperID}"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<java.lang.Iterable<camptimetest.domain.CampSession>> doRoute(RestxRequest request, RestxRequestMatch match, Void body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.getCampers(
                        /* [PATH] camperID */ match.getPathParam("camperID")
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


                operation.responseClass = "LIST[CampSession]";
                operation.inEntitySchemaKey = "";
                operation.outEntitySchemaKey = "camptimetest.domain.CampSession";
                operation.sourceLocation = "camptimetest.rest.CamperResource#getCampers(java.lang.String)";
            }
        },
        new StdEntityRoute<Void, java.lang.Iterable<camptimetest.domain.CampSession>>("default#CamperResource#getApprovedRegistrations",
                readerRegistry.<Void>build(Void.class, Optional.<String>absent()),
                writerRegistry.<java.lang.Iterable<camptimetest.domain.CampSession>>build(Types.newParameterizedType(java.lang.Iterable.class, camptimetest.domain.CampSession.class), Optional.<String>absent()),
                new StdRestxRequestMatcher("GET", "/campers/registrations/approved/{camperID}"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<java.lang.Iterable<camptimetest.domain.CampSession>> doRoute(RestxRequest request, RestxRequestMatch match, Void body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.getApprovedRegistrations(
                        /* [PATH] camperID */ match.getPathParam("camperID")
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


                operation.responseClass = "LIST[CampSession]";
                operation.inEntitySchemaKey = "";
                operation.outEntitySchemaKey = "camptimetest.domain.CampSession";
                operation.sourceLocation = "camptimetest.rest.CamperResource#getApprovedRegistrations(java.lang.String)";
            }
        },
        new StdEntityRoute<camptimetest.domain.Camper, camptimetest.domain.Camper>("default#CamperResource#createCamper",
                readerRegistry.<camptimetest.domain.Camper>build(camptimetest.domain.Camper.class, Optional.<String>absent()),
                writerRegistry.<camptimetest.domain.Camper>build(camptimetest.domain.Camper.class, Optional.<String>absent()),
                new StdRestxRequestMatcher("POST", "/campers"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<camptimetest.domain.Camper> doRoute(RestxRequest request, RestxRequestMatch match, camptimetest.domain.Camper body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.createCamper(
                        /* [BODY] camper */ checkValid(validator, body)
                ));
            }

            @Override
            protected void describeOperation(OperationDescription operation) {
                super.describeOperation(operation);
                                OperationParameterDescription camper = new OperationParameterDescription();
                camper.name = "camper";
                camper.paramType = OperationParameterDescription.ParamType.body;
                camper.dataType = "Camper";
                camper.schemaKey = "camptimetest.domain.Camper";
                camper.required = true;
                operation.parameters.add(camper);


                operation.responseClass = "Camper";
                operation.inEntitySchemaKey = "camptimetest.domain.Camper";
                operation.outEntitySchemaKey = "camptimetest.domain.Camper";
                operation.sourceLocation = "camptimetest.rest.CamperResource#createCamper(camptimetest.domain.Camper)";
            }
        },
        new StdEntityRoute<Void, restx.Status>("default#CamperResource#deleteRegistration",
                readerRegistry.<Void>build(Void.class, Optional.<String>absent()),
                writerRegistry.<restx.Status>build(restx.Status.class, Optional.<String>absent()),
                new StdRestxRequestMatcher("DELETE", "/campers/registrations/{camperID}/{sessionID}"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<restx.Status> doRoute(RestxRequest request, RestxRequestMatch match, Void body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.deleteRegistration(
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


                operation.responseClass = "Status";
                operation.inEntitySchemaKey = "";
                operation.outEntitySchemaKey = "restx.Status";
                operation.sourceLocation = "camptimetest.rest.CamperResource#deleteRegistration(java.lang.String,java.lang.String)";
            }
        },
        new StdEntityRoute<Void, restx.Status>("default#CamperResource#deleteCamper",
                readerRegistry.<Void>build(Void.class, Optional.<String>absent()),
                writerRegistry.<restx.Status>build(restx.Status.class, Optional.<String>absent()),
                new StdRestxRequestMatcher("DELETE", "/campers/{camperID}"),
                HttpStatus.OK, RestxLogLevel.DEFAULT) {
            @Override
            protected Optional<restx.Status> doRoute(RestxRequest request, RestxRequestMatch match, Void body) throws IOException {
                securityManager.check(request, open());
                return Optional.of(resource.deleteCamper(
                        /* [PATH] camperID */ match.getPathParam("camperID")
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


                operation.responseClass = "Status";
                operation.inEntitySchemaKey = "";
                operation.outEntitySchemaKey = "restx.Status";
                operation.sourceLocation = "camptimetest.rest.CamperResource#deleteCamper(java.lang.String)";
            }
        },
        });
    }

}
