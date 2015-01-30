package camptimetest.rest;

import com.google.common.collect.ImmutableSet;
import restx.factory.*;
import camptimetest.rest.ActivityResource;

@Machine
public class ActivityResourceFactoryMachine extends SingleNameFactoryMachine<ActivityResource> {
    public static final Name<ActivityResource> NAME = Name.of(ActivityResource.class, "ActivityResource");

    public ActivityResourceFactoryMachine() {
        super(0, new StdMachineEngine<ActivityResource>(NAME, BoundlessComponentBox.FACTORY) {


            @Override
            public BillOfMaterials getBillOfMaterial() {
                return new BillOfMaterials(ImmutableSet.<Factory.Query<?>>of(

                ));
            }

            @Override
            protected ActivityResource doNewComponent(SatisfiedBOM satisfiedBOM) {
                return new ActivityResource(

                );
            }
        });
    }

}
