package camptimetest.rest;

import com.google.common.collect.ImmutableSet;
import restx.factory.*;
import camptimetest.rest.ActivityResource;

@Machine
public class ActivityResourceFactoryMachine extends SingleNameFactoryMachine<ActivityResource> {
    public static final Name<ActivityResource> NAME = Name.of(ActivityResource.class, "ActivityResource");

    public ActivityResourceFactoryMachine() {
        super(0, new StdMachineEngine<ActivityResource>(NAME, BoundlessComponentBox.FACTORY) {
private final Factory.Query<restx.jongo.JongoCollection> activities = Factory.Query.byName(Name.of(restx.jongo.JongoCollection.class, "activities")).mandatory();
private final Factory.Query<restx.jongo.JongoCollection> campsessions = Factory.Query.byName(Name.of(restx.jongo.JongoCollection.class, "campsessions")).mandatory();

            @Override
            public BillOfMaterials getBillOfMaterial() {
                return new BillOfMaterials(ImmutableSet.<Factory.Query<?>>of(
activities,
campsessions
                ));
            }

            @Override
            protected ActivityResource doNewComponent(SatisfiedBOM satisfiedBOM) {
                return new ActivityResource(
satisfiedBOM.getOne(activities).get().getComponent(),
satisfiedBOM.getOne(campsessions).get().getComponent()
                );
            }
        });
    }

}
