package camptimetest.rest;

import com.google.common.collect.ImmutableSet;
import restx.factory.*;
import camptimetest.rest.CamperResource;

@Machine
public class CamperResourceFactoryMachine extends SingleNameFactoryMachine<CamperResource> {
    public static final Name<CamperResource> NAME = Name.of(CamperResource.class, "CamperResource");

    public CamperResourceFactoryMachine() {
        super(0, new StdMachineEngine<CamperResource>(NAME, BoundlessComponentBox.FACTORY) {
private final Factory.Query<restx.jongo.JongoCollection> registrations = Factory.Query.byName(Name.of(restx.jongo.JongoCollection.class, "registrations")).mandatory();
private final Factory.Query<restx.jongo.JongoCollection> campers = Factory.Query.byName(Name.of(restx.jongo.JongoCollection.class, "campers")).mandatory();
private final Factory.Query<restx.jongo.JongoCollection> campSessions = Factory.Query.byName(Name.of(restx.jongo.JongoCollection.class, "campsessions")).mandatory();

            @Override
            public BillOfMaterials getBillOfMaterial() {
                return new BillOfMaterials(ImmutableSet.<Factory.Query<?>>of(
registrations,
campers,
campSessions
                ));
            }

            @Override
            protected CamperResource doNewComponent(SatisfiedBOM satisfiedBOM) {
                return new CamperResource(
satisfiedBOM.getOne(registrations).get().getComponent(),
satisfiedBOM.getOne(campers).get().getComponent(),
satisfiedBOM.getOne(campSessions).get().getComponent()
                );
            }
        });
    }

}
