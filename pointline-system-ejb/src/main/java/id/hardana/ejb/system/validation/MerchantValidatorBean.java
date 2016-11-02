/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.system.validation;

import id.hardana.entity.profile.enums.OutletStatusEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Operator;
import id.hardana.entity.profile.merchant.Outlet;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.util.HashMap;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class MerchantValidatorBean {

    private final String STATUS_KEY = "status";
    private final String MERCHANT_KEY = "merchant";
    private final String OUTLET_KEY = "outlet";
    private final String OPERATOR_KEY = "operator";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public HashMap<String, Object> validateMerchantCode(String merchantCode) {
        HashMap response = new HashMap();
        List<Merchant> merchantSearch = em.createNamedQuery("Merchant.findByCode", Merchant.class)
                .setParameter("code", merchantCode)
                .getResultList();
        if (merchantSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_MERCHANT_CODE.getResponseStatus());
            return response;
        }
        Merchant merchant = merchantSearch.get(0);
        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(MERCHANT_KEY, merchant);
        return response;
    }

    public HashMap<String, Object> validateOutlet(String merchantCode, String outletId) {
        HashMap response = new HashMap();

        Long outletIdLong;
        try {
            outletIdLong = Long.parseLong(outletId);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        List<Outlet> listOutlet = em.createQuery("SELECT o FROM Outlet o JOIN Merchant m ON o.merchantId = m.id "
                + "WHERE m.code = :merchantCode AND o.status = :status AND o.isDeleted = :isDeleted "
                + "AND o.id = :id", Outlet.class)
                .setParameter("merchantCode", merchantCode)
                .setParameter("status", OutletStatusEnum.ACTIVE)
                .setParameter("isDeleted", false)
                .setParameter("id", outletIdLong)
                .getResultList();
        if (listOutlet.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_OUTLET_ID.getResponseStatus());
            return response;
        }

        Outlet outlet = listOutlet.get(0);
        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(OUTLET_KEY, outlet);
        return response;
    }

    public HashMap<String, Object> validateOperator(String merchantCode, String userName) {
        HashMap response = new HashMap();

        List<Operator> operatorsSearch = em.createQuery("SELECT o FROM Operator o JOIN "
                + "Merchant m ON o.merchantId = m.id WHERE o.userName = :userName AND "
                + "m.code = :merchantCode", Operator.class)
                .setParameter("userName", userName)
                .setParameter("merchantCode", merchantCode)
                .getResultList();

        if (operatorsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_USERNAME.getResponseStatus());
            return response;
        }

        Operator operatorSearch = operatorsSearch.get(0);
        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(OPERATOR_KEY, operatorSearch);
        return response;
    }

}
