package camptimetest.rest;

import com.google.common.collect.ImmutableSet;
import restx.factory.*;
import camptimetest.rest.EmployeeResource;

@Machine
public class EmployeeResourceFactoryMachine extends SingleNameFactoryMachine<EmployeeResource> {
    public static final Name<EmployeeResource> NAME = Name.of(EmployeeResource.class, "EmployeeResource");

    public EmployeeResourceFactoryMachine() {
        super(0, new StdMachineEngine<EmployeeResource>(NAME, BoundlessComponentBox.FACTORY) {
private final Factory.Query<restx.jongo.JongoCollection> employees = Factory.Query.byName(Name.of(restx.jongo.JongoCollection.class, "employees")).mandatory();

            @Override
            public BillOfMaterials getBillOfMaterial() {
                return new BillOfMaterials(ImmutableSet.<Factory.Query<?>>of(
employees
                ));
            }

            @Override
            protected EmployeeResource doNewComponent(SatisfiedBOM satisfiedBOM) {
                return new EmployeeResource(
satisfiedBOM.getOne(employees).get().getComponent()
                );
            }
        });
    }

}
