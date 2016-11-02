/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.viewer;

import id.hardana.ejb.merchantweb.log.LoggingInterceptor;
import id.hardana.entity.profile.merchant.Items;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class ViewerItemsBean {

    private final String STATUS_KEY = "status";
    private final String ITEMS_KEY = "items";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject viewAllItems(String merchantCode) {
        JSONObject response = new JSONObject();

        List<Merchant> merchantSearch = em.createNamedQuery("Merchant.findByCode", Merchant.class)
                .setParameter("code", merchantCode)
                .getResultList();
        if (merchantSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_MERCHANT_CODE.getResponseStatus());
            return response;
        }
        Merchant merchant = merchantSearch.get(0);
        Long merchantId = Long.parseLong(merchant.getId());

        List<Items> itemSearch = em.createQuery("SELECT i FROM Items i WHERE i.merchantId = :merchantId "
                + "AND i.isDeleted = :isDeleted", Items.class)
                .setParameter("merchantId", merchantId)
                .setParameter("isDeleted", Boolean.FALSE)
                .getResultList();

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(ITEMS_KEY, itemSearch);
        return response;
    }

}
