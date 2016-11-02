/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.view;

import id.hardana.ejb.mpos.log.LoggingInterceptor;
import id.hardana.entity.profile.merchant.Category;
import id.hardana.entity.profile.merchant.Discount;
import id.hardana.entity.profile.merchant.Items;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Pricing;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class ItemsBean {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final String FIRST_TIME_VALUE = "0";
    private final String STATUS_KEY = "status";
    private final String CATEGORY_KEY = "category";
    private final String ITEM_KEY = "item";
    private final String PRICING_KEY = "pricing";
    private final String DISCOUNT_KEY = "discount";
    
    private final String LAST_UPDATED_KEY = "lastUpdated";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject getAllItems(String merchantCode, String lastUpdated) {
        JSONObject response = new JSONObject();

        boolean isFirstTime = false;
        Date nowDate = new Date();
        Date lastUpdatedDate = null;

        if (lastUpdated.trim().equals(FIRST_TIME_VALUE)) {
            isFirstTime = true;
        } else {
            isFirstTime = false;
        }

        if (!isFirstTime) {
            try {
                lastUpdatedDate = DATE_FORMAT.parse(lastUpdated);
            } catch (ParseException ex) {
                response.put(STATUS_KEY, ResponseStatusEnum.INVALID_DATE.getResponseStatus());
                return response;
            }
        }

        List<Merchant> merchantSearch = em.createNamedQuery("Merchant.findByCode", Merchant.class)
                .setParameter("code", merchantCode)
                .getResultList();
        if (merchantSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_MERCHANT_CODE.getResponseStatus());
            return response;
        }
        Merchant merchant = merchantSearch.get(0);
        Long merchantId = Long.parseLong(merchant.getId());

        String queryCategory = "SELECT c FROM Category c WHERE c.merchantId = :merchantId ";
        String queryItems = "SELECT i FROM Items i WHERE i.merchantId = :merchantId ";
        String queryPricing = "SELECT p FROM Pricing p WHERE p.merchantId = :merchantId ";
        List<Category> categorySearch;
        List<Items> itemsSearch;
        List<Pricing> pricingSearch;

        if (isFirstTime) {
            categorySearch = em.createQuery(queryCategory, Category.class)
                    .setParameter("merchantId", merchantId)
                    .getResultList();
            itemsSearch = em.createQuery(queryItems, Items.class)
                    .setParameter("merchantId", merchantId)
                    .getResultList();
            pricingSearch = em.createQuery(queryPricing, Pricing.class)
                    .setParameter("merchantId", merchantId)
                    .getResultList();

        } else {
            queryCategory += "AND c.lastUpdated > :lastUpdated";
            queryItems += "AND i.lastUpdated > :lastUpdated";
            queryPricing += "AND p.lastUpdated > :lastUpdated";

            categorySearch = em.createQuery(queryCategory, Category.class)
                    .setParameter("merchantId", merchantId)
                    .setParameter("lastUpdated", lastUpdatedDate)
                    .getResultList();
            itemsSearch = em.createQuery(queryItems, Items.class)
                    .setParameter("merchantId", merchantId)
                    .setParameter("lastUpdated", lastUpdatedDate)
                    .getResultList();
            
            pricingSearch = em.createQuery(queryPricing, Pricing.class)
                    .setParameter("merchantId", merchantId)
                    .setParameter("lastUpdated", lastUpdatedDate)
                    .getResultList();
        }

        for (Category categorySearch1 : categorySearch) {
            em.refresh(categorySearch1);
        }
        for (Pricing pricingSearch1 : pricingSearch) {
            em.refresh(pricingSearch1);
        }
        for (Items itemsSearch1 : itemsSearch) {
            em.refresh(itemsSearch1);
        }

        response.put(CATEGORY_KEY, categorySearch);
        response.put(PRICING_KEY, pricingSearch);
        response.put(ITEM_KEY, itemsSearch);

        JSONArray categoryArray = response.getJSONArray(CATEGORY_KEY);
        JSONArray pricingArray = response.getJSONArray(PRICING_KEY);
        JSONArray itemArray = response.getJSONArray(ITEM_KEY);
         
        response.remove(CATEGORY_KEY);
        response.remove(PRICING_KEY);
        response.remove(ITEM_KEY);

        for (int i = 0; i < categoryArray.length(); i++) {
            JSONObject categoryObject = categoryArray.getJSONObject(i);
            categoryObject.remove("merchantId");
        }
        for (int i = 0; i < pricingArray.length(); i++) {
            JSONObject pricingObject = pricingArray.getJSONObject(i);
            pricingObject.remove("merchantId");
        }
        for (int i = 0; i < itemArray.length(); i++) {
            JSONObject itemObject = itemArray.getJSONObject(i);
            itemObject.remove("merchantId");
            itemObject.remove("picture");
        }       
        
        
        response.put(CATEGORY_KEY, categoryArray);
        response.put(PRICING_KEY, pricingArray);
        response.put(ITEM_KEY, itemArray);
        response.put(DISCOUNT_KEY, getDiscounts(merchantId, isFirstTime, lastUpdatedDate));
        
        response.put(LAST_UPDATED_KEY, DATE_FORMAT.format(nowDate));
        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());

        return response;
    }

    
    public JSONArray getDiscounts(Long merchantId, boolean firstTime, Date lastUpdatedDate) {
        
        List<Discount> discounts;
        if (firstTime) {
            String jql = "SELECT d FROM Discount d WHERE (d.merchantId = :merchantId) ORDER BY d.id ASC";

            discounts = em.createQuery(jql)
                    .setParameter("merchantId", merchantId)
                    .getResultList();
        } else {
            String jql = "SELECT d FROM Discount d WHERE (d.merchantId = :merchantId) AND "
                    + "(d.lastUpdated > :lastUpdated) ORDER BY d.id ASC";
            
            discounts = em.createQuery(jql)
                .setParameter("merchantId", merchantId)
                .setParameter("lastUpdated", lastUpdatedDate)
                .getResultList();
        }
        
        
              
        JSONArray jsonDiscounts = new JSONArray();
        for(Discount discount :discounts) {
            em.refresh(discount);
            
            JSONObject item = new JSONObject();
            
            item.put("id", discount.getId());
            item.put("name", discount.getName());
            item.put("description", discount.getDescription());
            item.put("color", discount.getColor());
            item.put("enabled", discount.getEnabled());
            item.put("auto", discount.getAuto());
            item.put("merchantId", discount.getMerchantId());
            item.put("value", discount.getValue());
            item.put("valueType", discount.getValueType().getDiscountValueTypeId());
            item.put("applyType", discount.getApplyType().getDiscountApplyTypeId());
            item.put("calculationType", discount.getCalculationType().getDiscountCalculationTypeId());
            item.put("deleted", discount.getIsDeleted());
            
            jsonDiscounts.put(item);
        }
        
        return jsonDiscounts;
        
    }
}
