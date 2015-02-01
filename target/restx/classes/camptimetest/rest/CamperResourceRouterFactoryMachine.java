package camptimetest.rest;

import com.google.common.collect.ImmutableSet;
import restx.factory.*;
import camptimetest.rest.CamperResourceRouter;

@Machine
public class CamperResourceRouterFactoryMachine extends SingleNameFactoryMachine<CamperResourceRouter> {
    public static final Name<CamperResourceRouter> NAME = Name.of(CamperResourceRouter.class, "CamperResourceRouter");

    public CamperResourceRouterFactoryMachine() {
        super(0, new StdMachineEngine<CamperResourceRouter>(NAME, BoundlessComponentBox.FACTORY) {
private final Factory.Query<camptimetest.rest.CamperResource> resource = Factory.Query.byClass(camptimetest.rest.CamperResource.class).mandatory();
private final Factory.Query<restx.entity.EntityRequestBodyReaderRegistry> readerRegistry = Factory.Query.byClass(restx.entity.EntityRequestBodyReaderRegistry.class).mandatory();
private final Factory.Query<restx.entity.EntityResponseWriterRegistry> writerRegistry = Factory.Query.byClass(restx.entity.EntityResponseWriterRegistry.class).mandatory();
private final Factory.Query<restx.converters.MainStringConverter> converter = Factory.Query.byClass(restx.converters.MainStringConverter.class).mandatory();
private final Factory.Query<javax.validation.Validator> validator = Factory.Query.byClass(javax.validation.Validator.class).mandatory();
private final Factory.Query<restx.security.RestxSecurityManager> securityManager = Factory.Query.byClass(restx.security.RestxSecurityManager.class).mandatory();

            @Override
            public BillOfMaterials getBillOfMaterial() {
                return new BillOfMaterials(ImmutableSet.<Factory.Query<?>>of(
resource,
readerRegistry,
writerRegistry,
converter,
validator,
securityManager
                ));
            }

            @Override
            protected CamperResourceRouter doNewComponent(SatisfiedBOM satisfiedBOM) {
                return new CamperResourceRouter(
satisfiedBOM.getOne(resource).get().getComponent(),
satisfiedBOM.getOne(readerRegistry).get().getComponent(),
satisfiedBOM.getOne(writerRegistry).get().getComponent(),
satisfiedBOM.getOne(converter).get().getComponent(),
satisfiedBOM.getOne(validator).get().getComponent(),
satisfiedBOM.getOne(securityManager).get().getComponent()
                );
            }
        });
    }

}
