package id.hardana.ejb.merchantweb;

import id.hardana.ejb.merchantweb.log.LoggingInterceptor;
import id.hardana.entity.profile.enums.DiscountApplyTypeEnum;
import id.hardana.entity.profile.enums.DiscountCalculationTypeEnum;
import id.hardana.entity.profile.enums.DiscountValueTypeEnum;
import id.hardana.entity.profile.merchant.Discount;
import id.hardana.entity.profile.merchant.Merchant;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author pancara
 */

@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})
public class MerchantDiscountBean {
    
    public static final String STATUS_PROPERTY = "status";
    public static final String DISCOUNTS_PROPERTY = "discounts";
    public static final String COUNT_PROPERTY = "count";
    
    
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject addDiscount(String merchantCode, 
            String discountName, 
            String discountDesc, 
            String discountColor,
            String discountValue, 
            String discountValueType, // int enum id
            String discountCalculationType, // int enum id
            String discountApplyType, // int enum id
            String discountEnabled,
            String discountAuto) {
      
        JSONObject json = new JSONObject();
        
        Merchant merchant;
        try {
         merchant = (Merchant) em.createNamedQuery("Merchant.findByCode")
                .setParameter("code", merchantCode)
                .getSingleResult();
        } catch(NoResultException nre) {
//            json.put(STATUS_PROPERTY, "MERCHANT NOT FOUND");
            return json;
        } catch(NonUniqueResultException nure) {
            json.put(STATUS_PROPERTY, "NO UNIQUE MERCHANT CODE");
            return json;
        }
        
        Discount discount = new Discount();
        discount.setName(discountName);
        discount.setDescription(discountDesc);
        discount.setColor(discountColor);
        discount.setMerchantId(Long.valueOf(merchant.getId()));
        
        Boolean enabled = Boolean.valueOf(discountEnabled);
        discount.setEnabled(enabled);
        
        Boolean auto = Boolean.valueOf(discountAuto);
        discount.setAuto(auto);
        
        if (discountValue == null) {
            json.put(STATUS_PROPERTY, "DISCOUNT VALUE IS NULL");
            return json;
        }
        
        BigDecimal value = new BigDecimal(discountValue);
        discount.setValue(value);
        
        DiscountValueTypeEnum valueType = DiscountValueTypeEnum.getValue(Integer.valueOf(discountValueType));
        if (valueType == null) {
            json.put(STATUS_PROPERTY, "DISCOUNT VALUE TYPE UNKNOWN");
            return json;
        }
        discount.setValueType(valueType);
        
        DiscountApplyTypeEnum applyType = DiscountApplyTypeEnum.getValue(Integer.valueOf(discountApplyType));
        if (applyType == null) {
            json.put(STATUS_PROPERTY, "DISCOUNT APPLY TYPE UNKNOWN");
            return json;
        }
        discount.setApplyType(applyType);
        
        DiscountCalculationTypeEnum calculationType = DiscountCalculationTypeEnum.getValue(Integer.valueOf(discountCalculationType));
        if (calculationType == null) {
            json.put(STATUS_PROPERTY, "DISCOUNT CALCULATION TYPE UNKNOWN");
            return json;
        }
        discount.setCalculationType(calculationType);
        
        discount.setIsDeleted(Boolean.FALSE);
        discount.setLastUpdated(new Date());
        
        em.persist(discount);
        
        json.put(STATUS_PROPERTY, "SUCCESS");
        return json;
    }
    
    public JSONObject editDiscount(String merchantCode, 
            String discountId,
            String discountName, 
            String discountDesc, 
            String discountColor,
            String discountValue, 
            String discountValueType, // int enum id
            String discountCalculationType, // int enum id
            String discountApplyType, // int enum id
            String discountEnabled,
            String discountAuto) {
      
        JSONObject json = new JSONObject();
        
        Merchant merchant;
        try {
         merchant = (Merchant) em.createNamedQuery("Merchant.findByCode")
                .setParameter("code", merchantCode)
                .getSingleResult();
        } catch(NoResultException nre) {
            json.put(STATUS_PROPERTY, "MERCHANT NOT FOUND");
            return json;
        } catch(NonUniqueResultException nure) {
            json.put(STATUS_PROPERTY, "NO UNIQUE MERCHANT CODE");
            return json;
        }
        
        Long id = Long.valueOf(discountId);
        Discount discount = em.find(Discount.class, id);
        if (discount == null) {
            json.put(STATUS_PROPERTY, "DISCOUNT DATA NOT FOUND");
            return json;
        }
        Long merchantId = Long.valueOf(merchant.getId());
        if (!merchantId.equals(discount.getMerchantId())) {
            json.put(STATUS_PROPERTY, "MERCHANT AND DISCOUNT MERCHANT NOT MATCH");
            return json;
        }
        discount.setName(discountName);
        discount.setDescription(discountDesc);
        discount.setColor(discountColor);
        
        Boolean enabled = Boolean.valueOf(discountEnabled);
        discount.setEnabled(enabled);
        
        Boolean auto = Boolean.valueOf(discountAuto);
        discount.setAuto(auto);
        
        BigDecimal value = new BigDecimal(discountValue);
        discount.setValue(value);
        
        DiscountValueTypeEnum valueType = DiscountValueTypeEnum.getValue(Integer.valueOf(discountValueType));
        if (valueType == null) {
            json.put(STATUS_PROPERTY, "DISCOUNT VALUE TYPE UNKNOWN");
            return json;
        }
        discount.setValueType(valueType);
        
        DiscountApplyTypeEnum applyType = DiscountApplyTypeEnum.getValue(Integer.valueOf(discountApplyType));
        if (applyType == null) {
            json.put(STATUS_PROPERTY, "DISCOUNT APPLY TYPE UNKNOWN");
            return json;
        }
        discount.setApplyType(applyType);
        
        DiscountCalculationTypeEnum calculationType = DiscountCalculationTypeEnum.getValue(Integer.valueOf(discountCalculationType));
        if (calculationType == null) {
            json.put(STATUS_PROPERTY, "DISCOUNT CALCULATION TYPE UNKNOWN");
            return json;
        }
        discount.setCalculationType(calculationType);
        
        discount.setLastUpdated(new Date());
        
        em.merge(discount);
        
        json.put(STATUS_PROPERTY, "SUCCESS");
        return json;
    }
    
    public JSONObject deleteDiscount(String discountId, String merchantCode ) {
      
        JSONObject json = new JSONObject();
        
        Merchant merchant;
        try {
         merchant = (Merchant) em.createNamedQuery("Merchant.findByCode")
                .setParameter("code", merchantCode)
                .getSingleResult();
        } catch(NoResultException nre) {
            json.put(STATUS_PROPERTY, "MERCHANT NOT FOUND");
            return json;
        } catch(NonUniqueResultException nure) {
            json.put(STATUS_PROPERTY, "NO UNIQUE MERCHANT CODE");
            return json;
        }
        
        Long id = Long.valueOf(discountId);
        Discount discount = em.find(Discount.class, id);
        if (discount == null) {
            json.put(STATUS_PROPERTY, "DISCOUNT DATA NOT FOUND");
            return json;
        }

        Long merchantId = Long.valueOf(merchant.getId());
        if (!merchantId.equals(discount.getMerchantId())) {
            json.put(STATUS_PROPERTY, "MERCHANT AND DISCOUNT MERCHANT NOT MATCH");
            return json;
        }
         
        discount.setIsDeleted(Boolean.TRUE);
        discount.setLastUpdated(new Date());
        
        em.merge(discount);
        
        json.put(STATUS_PROPERTY, "SUCCESS");
        return json;
    }
    
    public JSONObject getDiscounts(String merchantCode, String keyword, Integer page, Integer count) {
        JSONObject json = new JSONObject();
        
        Merchant merchant;
        try {
         merchant = (Merchant) em.createNamedQuery("Merchant.findByCode")
                .setParameter("code", merchantCode)
                .getSingleResult();
        } catch(NoResultException nre) {
            json.put(STATUS_PROPERTY, "MERCHANT NOT FOUND");
            return json;
        } catch(NonUniqueResultException nure) {
            json.put(STATUS_PROPERTY, "NO UNIQUE MERCHANT CODE");
            return json;
        }
        
        
        int start = (page - 1) * count;
        
        Long merchantId = Long.valueOf(merchant.getId());
         
        List<Discount> discounts;
        Long dataCount;
        
        if (keyword == null || keyword.isEmpty()) {
            String jql = "SELECT d FROM Discount d WHERE (d.merchantId = :merchantId) AND (d.isDeleted <> TRUE) "
                    + "ORDER BY d.id ASC";
            discounts = em.createQuery(jql, Discount.class)
                    .setParameter("merchantId", merchantId)
                    .setFirstResult(start)
                    .setMaxResults(count)
                    .getResultList();
            
            jql = "SELECT COUNT(d) FROM Discount d WHERE (d.merchantId = :merchantId) AND (d.isDeleted <> TRUE)";
            dataCount = em.createQuery(jql, Long.class)
                    .setParameter("merchantId", merchantId)
                    .getSingleResult();
        } else {
            String nameParam = "%"+ keyword.toLowerCase() + "%";
            
            String jql = "SELECT d FROM Discount d WHERE (d.merchantId = :merchantId) AND (d.isDeleted <> TRUE) "
                    + "AND (LOWER(d.name) LIKE :name) "
                    + "ORDER BY d.id ASC";
            discounts = em.createQuery(jql, Discount.class)
                    .setParameter("merchantId", merchantId)
                    .setParameter("name", nameParam)
                    .setFirstResult(start)
                    .setMaxResults(count)
                    .getResultList();
            
            jql = "SELECT COUNT(d) FROM Discount d WHERE (d.merchantId = :merchantId) AND (d.isDeleted <> TRUE) "
                    + "AND (LOWER(d.name) LIKE :name) ";
        
            dataCount = em.createQuery(jql, Long.class)
                    .setParameter("merchantId", merchantId)
                    .setParameter("name", nameParam)
                    .getSingleResult();
            
        }
        
        JSONArray jsonDiscounts = new JSONArray();
        for(Discount discount :discounts) {
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
            
            jsonDiscounts.put(item);
        }
        
         
        json.put(STATUS_PROPERTY, "SUCCESS");
        json.put(DISCOUNTS_PROPERTY, jsonDiscounts);
        json.put(COUNT_PROPERTY, dataCount);
        return json;
    }
}
