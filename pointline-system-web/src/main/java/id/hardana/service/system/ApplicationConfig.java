/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.system;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author Trisna
 */
@javax.ws.rs.ApplicationPath("emo")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(id.hardana.service.system.ChangePasswordGroupService.class);
        resources.add(id.hardana.service.system.ChangePasswordService.class);
        resources.add(id.hardana.service.system.ForgetPasswordService.class);
        resources.add(id.hardana.service.system.LoginGroupService.class);
        resources.add(LoginService.class);
        resources.add(id.hardana.service.system.LogoutGroupService.class);
        resources.add(LogoutService.class);
        resources.add(id.hardana.service.system.NewCrossOriginResourceSharingFilter.class);
        resources.add(id.hardana.service.system.RegisterGroupService.class);
        resources.add(RegisterService.class);
        resources.add(TestService.class);
        resources.add(TokenService.class);
    }
    
}
