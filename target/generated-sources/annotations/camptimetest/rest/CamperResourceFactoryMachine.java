package camptimetest.rest;

import com.google.common.collect.ImmutableSet;
import restx.factory.*;
import camptimetest.rest.CamperResource;

@Machine
public class CamperResourceFactoryMachine extends SingleNameFactoryMachine<CamperResource> {
    public static final Name<CamperResource> NAME = Name.of(CamperResource.class, "CamperResource");

    public CamperResourceFactoryMachine() {
        super(0, new StdMachineEngine<CamperResource>(NAME, BoundlessComponentBox.FACTORY) {
private final Factory.Query<restx.jongo.JongoCollection> campers = Factory.Query.byName(Name.of(restx.jongo.JongoCollection.class, "registrations")).mandatory();

            @Override
            public BillOfMaterials getBillOfMaterial() {
                return new BillOfMaterials(ImmutableSet.<Factory.Query<?>>of(
campers
                ));
            }

            @Override
            protected CamperResource doNewComponent(SatisfiedBOM satisfiedBOM) {
                return new CamperResource(
satisfiedBOM.getOne(campers).get().getComponent()
                );
            }
        });
    }

}
