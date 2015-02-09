package camptimetest;

import com.google.common.collect.ImmutableSet;
import restx.factory.*;
import camptimetest.AppModule;


@Machine
public class AppModuleFactoryMachine extends DefaultFactoryMachine {
    private static final AppModule module = new AppModule();

    public AppModuleFactoryMachine() {
        super(0, new MachineEngine[] {
            new StdMachineEngine<restx.security.SignatureKey>(Name.of(restx.security.SignatureKey.class, "signatureKey"), BoundlessComponentBox.FACTORY) {
        
                @Override
                public BillOfMaterials getBillOfMaterial() {
                    return new BillOfMaterials(ImmutableSet.<Factory.Query<?>>of(
        
                    ));
                }

                @Override
                public restx.security.SignatureKey doNewComponent(SatisfiedBOM satisfiedBOM) {
	                
	                    return module.signatureKey(
	        
	                    );
	                
                }
            },
            new StdMachineEngine<java.lang.String>(Name.of(java.lang.String.class, "mongo.db"), BoundlessComponentBox.FACTORY) {
        
                @Override
                public BillOfMaterials getBillOfMaterial() {
                    return new BillOfMaterials(ImmutableSet.<Factory.Query<?>>of(
        
                    ));
                }

                @Override
                public java.lang.String doNewComponent(SatisfiedBOM satisfiedBOM) {
	                
	                    return module.dbName(
	        
	                    );
	                
                }
            },
            new StdMachineEngine<restx.security.CredentialsStrategy>(Name.of(restx.security.CredentialsStrategy.class, "credentialsStrategy"), BoundlessComponentBox.FACTORY) {
        
                @Override
                public BillOfMaterials getBillOfMaterial() {
                    return new BillOfMaterials(ImmutableSet.<Factory.Query<?>>of(
        
                    ));
                }

                @Override
                public restx.security.CredentialsStrategy doNewComponent(SatisfiedBOM satisfiedBOM) {
	                
	                    return module.credentialsStrategy(
	        
	                    );
	                
                }
            },
            new StdMachineEngine<restx.security.BasicPrincipalAuthenticator>(Name.of(restx.security.BasicPrincipalAuthenticator.class, "basicPrincipalAuthenticator"), BoundlessComponentBox.FACTORY) {
        private final Factory.Query<camptimetest.rest.MyUserRepository> userRepository = Factory.Query.byClass(camptimetest.rest.MyUserRepository.class).mandatory();
private final Factory.Query<restx.security.SecuritySettings> securitySettings = Factory.Query.byClass(restx.security.SecuritySettings.class).mandatory();
private final Factory.Query<restx.security.CredentialsStrategy> credentialsStrategy = Factory.Query.byClass(restx.security.CredentialsStrategy.class).mandatory();
private final Factory.Query<java.lang.String> adminPasswordHash = Factory.Query.byName(Name.of(java.lang.String.class, "restx.admin.passwordHash")).mandatory();
                @Override
                public BillOfMaterials getBillOfMaterial() {
                    return new BillOfMaterials(ImmutableSet.<Factory.Query<?>>of(
        userRepository,
securitySettings,
credentialsStrategy,
adminPasswordHash
                    ));
                }

                @Override
                public restx.security.BasicPrincipalAuthenticator doNewComponent(SatisfiedBOM satisfiedBOM) {
	                
	                    return module.basicPrincipalAuthenticator(
	        satisfiedBOM.getOne(userRepository).get().getComponent(),
satisfiedBOM.getOne(securitySettings).get().getComponent(),
satisfiedBOM.getOne(credentialsStrategy).get().getComponent(),
satisfiedBOM.getOne(adminPasswordHash).get().getComponent()
	                    );
	                
                }
            },
        });
}
}
