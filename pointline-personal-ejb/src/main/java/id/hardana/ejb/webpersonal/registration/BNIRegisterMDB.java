/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.webpersonal.registration;

import id.hardana.bnibilling.ejb.remote.ManageBniBillingBeanRemote;
import id.hardana.ejb.webpersonal.util.Util;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 *
 * @author Trisna
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "BNIRegisterQueue"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class BNIRegisterMDB implements MessageListener {

    private static final String bniPaymentBeanJNDI = "java:global/bnibilling/bnibilling-ejb/id.hardana.bnibiller.ejb.ManageBniBillingBean";

    public BNIRegisterMDB() {
    }

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = null;
        try {
            objectMessage = (ObjectMessage) message;
            BNIRegisterObject bniRegObject = (BNIRegisterObject) objectMessage.getObject();
            ManageBniBillingBeanRemote bniReg = (ManageBniBillingBeanRemote) Util.lookup(bniPaymentBeanJNDI);
            bniReg.createAndRegisterBilling(bniRegObject.getPersonalInfoId());
        } catch (Exception ex) {
            Logger.getLogger(BNIRegisterMDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
