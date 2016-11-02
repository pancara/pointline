/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb;

import id.hardana.ejb.merchantweb.log.LoggingInterceptor;
import id.hardana.entity.profile.enums.PricingTypeEnum;
import id.hardana.entity.profile.enums.ProductStatusEnum;
import id.hardana.entity.profile.merchant.Category;
import id.hardana.entity.profile.merchant.Items;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Pricing;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.PersistenceContext;
import org.json.JSONObject;

/**
 *
 * @author hanafi
 */
@Stateless
@Interceptors(LoggingInterceptor.class)
public class MerchantProductFacade {

    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public MerchantProductFacade() {
    }

    public String addProductCategory(String merchantCode, String categoryName, String categoryDesc, String colorCode) {
        // get merchantId from merchantCode
        Merchant entityMerchantId = (Merchant) em.createNamedQuery("Merchant.findByCode")
                .setParameter("code", merchantCode)
                .getSingleResult();

        Integer merchantId = Integer.valueOf(entityMerchantId.getId());

        Category entity = new Category();

        // sent
        entity.setMerchantId(merchantId.longValue());
        entity.setName(categoryName);
        entity.setDescription(categoryDesc);
        entity.setColorCode(colorCode);
        // not sent
        entity.setIsDeleted(Boolean.FALSE);
        entity.setLastUpdated(new Date());
        entity.setStatus(ProductStatusEnum.ACTIVE);

        em.persist(entity);

        return "SUCCESS";
    }

    public HashMap<String, String> editProductCategory(String categoryId, String categoryName, String categoryDesc, String colorCode, String status) {
        HashMap<String, String> result = new HashMap<>();
        // convert integers
        // categoryId
        Integer categoryIdInt = Integer.valueOf(categoryId);
        Integer statusInt = Integer.valueOf(status);

        List<Category> resultList = em.createNamedQuery("Category.findById", Category.class)
                .setParameter("id", Long.valueOf(categoryId))
                .getResultList();

        if (resultList.isEmpty()) {
            result.put("status", "NOTFOUND");
            return result;
        }

        Category entity = resultList.get(0);

        // sent
        entity.setName(categoryName);
        entity.setDescription(categoryDesc);
        entity.setColorCode(colorCode);
        entity.setStatus(ProductStatusEnum.getProductStatus(statusInt));
        // not sent
        entity.setLastUpdated(new Date());

        em.merge(entity);

        result.put("status", "SUCCESS");
        return result;
    }

    public String addProductItem(String merchantCode, String categoryId, String itemName, String itemCode, String itemSupplyPrice, String itemMarkUpPrice, String itemSalesPrice, String itemPic, String itemStatus, String itemDesc) {
        JSONObject result = new JSONObject();

        // get merchantId
        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);
        // convert integers
        // categoryId
        // itemStatus
        Integer categoryIdInt = Integer.valueOf(categoryId);
        Integer itemStatusInt = Integer.valueOf(itemStatus);

        // convert strings into bigdecimal
        BigDecimal itemSUPConverted = new BigDecimal(itemSupplyPrice);
        BigDecimal itemMUPConverted = new BigDecimal(itemMarkUpPrice);
        BigDecimal itemSLPConverted = new BigDecimal(itemSalesPrice);

        //check for existing itemcode (unique per merchant)
        List<Items> itemCodeCheck = em.createNamedQuery("Items.findByCodeAndMerchantIdNotDeleted", Items.class)
                .setParameter("code", itemCode)
                .setParameter("merchantId", merchantId)
                .getResultList();

        if (!(itemCodeCheck.isEmpty())) {
            Items item = itemCodeCheck.get(0);
            String itemNameSearch = item.getName();

            result.put("status", "CODEINUSE");
            result.put("detail", itemNameSearch);
            return result.toString();
        }

        Items entity = new Items();

        entity.setMerchantId(merchantId);
        entity.setCategoryId(categoryIdInt.longValue());
        entity.setName(itemName);
        entity.setCode(itemCode);
        entity.setDescription(itemDesc);
        entity.setSupplyPrice(itemSUPConverted);
        entity.setMarkupPrice(itemMUPConverted);
        entity.setSalesPrice(itemSLPConverted);
        entity.setPicture(itemPic);
        entity.setStatus(ProductStatusEnum.getProductStatus(itemStatusInt));
        // not sent
        entity.setLastUpdated(new Date());
        entity.setIsDeleted(Boolean.FALSE);

        em.persist(entity);

        result.put("status", "SUCCESS");
        return result.toString();
    }

    public HashMap<String, String> editProductItem(String merchantCode, String categoryId, String itemId, String itemName, String itemCode, String itemSupplyPrice, String itemMarkUpPrice, String itemSalesPrice, String itemPic, String itemStatus, String itemDesc) {
        HashMap<String, String> result = new HashMap<>();
        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);

        // convert integers
        // categoryId
        // itemId
        // itemStatus
        Integer categoryIdInt = Integer.valueOf(categoryId);
        Integer itemIdInt = Integer.valueOf(itemId);
        Integer itemStatusInt = Integer.valueOf(itemStatus);

        // convert strings into bigdecimal
        BigDecimal itemSUPConverted = new BigDecimal(itemSupplyPrice);
        BigDecimal itemMUPConverted = new BigDecimal(itemMarkUpPrice);
        BigDecimal itemSLPConverted = new BigDecimal(itemSalesPrice);

        List<Items> resultList = em.createNamedQuery("Items.findById", Items.class)
                .setParameter("id", Long.valueOf(itemIdInt))
                .getResultList();

        if (resultList.isEmpty()) {
            result.put("status", "NOTFOUND");
            return result;
        }

        Items entity = resultList.get(0);

        // if changing, check for code
        if (!(itemCode.equalsIgnoreCase(entity.getCode()))) {
            //check for existing itemcode (unique per merchant)
            List<Items> itemCodeCheck = em.createNamedQuery("Items.findByCodeAndMerchantIdNotDeleted", Items.class)
                    .setParameter("code", itemCode)
                    .setParameter("merchantId", merchantId)
                    .getResultList();

            if (!(itemCodeCheck.isEmpty())) {
                if (!(entity.getId().equals(itemCodeCheck.get(0).getId()))) {
                    result.put("status", "CODEINUSE");
                    return result;
                }
            }
        }

        // sent
        entity.setCategoryId(categoryIdInt.longValue());
        entity.setName(itemName);
        entity.setCode(itemCode);
        entity.setSupplyPrice(itemSUPConverted);
        entity.setMarkupPrice(itemMUPConverted);
        entity.setSalesPrice(itemSLPConverted);
        entity.setPicture(itemPic);
        entity.setStatus(ProductStatusEnum.getProductStatus(itemStatusInt));
        entity.setDescription(itemDesc);
        // not sent
        entity.setLastUpdated(new Date());

        em.merge(entity);

        result.put("status", "SUCCESS");
        return result;
    }

    public String addPricing(String merchantCode, String pricingName, String pricingType, String pricingValue, String pricingStatus, String pricingLevel) {

        // convert integers
        // pricingType
        // pricingStatus
        Integer pricingTypeInt = Integer.valueOf(pricingType);
        Integer pricingStatusInt = Integer.valueOf(pricingStatus);
        Integer pricingLevelInt = Integer.valueOf(pricingLevel);

        // get merchantId from merchantCode
        Merchant entityMerchantId = (Merchant) em.createNamedQuery("Merchant.findByCode")
                .setParameter("code", merchantCode)
                .getSingleResult();

        Integer merchantId = Integer.valueOf(entityMerchantId.getId());

        // convert value
        BigDecimal pricingValueConverted = new BigDecimal(pricingValue);

        Pricing entity = new Pricing();

        // sent
        entity.setMerchantId(merchantId.longValue());
        entity.setName(pricingName);
        entity.setType(PricingTypeEnum.getPricingType(pricingTypeInt));
        entity.setPricingValue(pricingValueConverted);
        entity.setStatus(ProductStatusEnum.getProductStatus(pricingStatusInt));
        entity.setLevel(pricingLevelInt);
        // not sent
        entity.setLastUpdated(new Date());
        entity.setIsDeleted(Boolean.FALSE);

        em.persist(entity);

        return "SUCCESS";
    }

    public HashMap<String, String> editPricing(String merchantCode, String pricingId, String pricingName, String pricingType, String pricingValue, String pricingStatus, String pricingLevel) {
        HashMap<String, String> result = new HashMap<>();
        // merchantCode received but not used

        // convert integers
        // pricingId
        // pricingType
        // pricingStatus
        Integer pricingIdInt = Integer.valueOf(pricingId);
        Integer pricingTypeInt = Integer.valueOf(pricingType);
        Integer pricingStatusInt = Integer.valueOf(pricingStatus);
        Integer pricingLevelInt = Integer.valueOf(pricingLevel);

        // convert strings into bigdecimal
        BigDecimal pricingValueConverted = new BigDecimal(pricingValue);

        List<Pricing> resultList = em.createNamedQuery("Pricing.findById", Pricing.class)
                .setParameter("id", Long.valueOf(pricingId))
                .getResultList();

        if (resultList.isEmpty()) {
            result.put("status", "NOTFOUND");
            return result;
        }

        Pricing entity = resultList.get(0);

        // sent
        entity.setName(pricingName);
        entity.setType(PricingTypeEnum.getPricingType(pricingTypeInt));
        entity.setPricingValue(pricingValueConverted);
        entity.setStatus(ProductStatusEnum.getProductStatus(pricingStatusInt));
        entity.setLevel(pricingLevelInt);
        // notsent
        entity.setLastUpdated(new Date());

        em.merge(entity);

        result.put("status", "SUCCESS");
        return result;
    }

    public String getOrderProduct(String merchantCode, Date lastUpdatedDate) {

        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);

        List<Category> resultC = new Vector<>();
        List<Items> resultI = new Vector<>();
        List<Pricing> resultP = new Vector<>();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        //List<Object> results = new Vector<Object>();

        HashMap<String, Object> results = new HashMap<>();
        // get the categories
        //resultMC = em.createNamedQuery("Merchantcategory.findByMonetamerchantCode", Merchantcategory.class)
        //        .setParameter("monetamerchantCode", monetaMerchantCode)
        //        .getResultList();

        Calendar calZero = Calendar.getInstance();
        calZero.clear();
        calZero.set(2000, 0, 1, 0, 0, 0);
        //calZero.set(Calendar.YEAR, 2000);
        //calZero.set(Calendar.MONTH, 0);
        //calZero.set(Calendar.DATE, 1);
        //calZero.set(Calendar.HOUR, 0);
        //calZero.set(Calendar.MINUTE, 0);
        //calZero.set(Calendar.SECOND, 0);

        Calendar calUpdate = Calendar.getInstance();
        calUpdate.setTime(lastUpdatedDate);

        //logger.info("calzero: " + calZero.toString());
        //logger.info("calupdate: " + calUpdate.toString());
        if (calZero.compareTo(calUpdate) == 0) {
            results.put("categories", em.createNamedQuery("Category.findByMerchantId", Category.class)
                    .setParameter("merchantId", merchantId)
                    .getResultList());

            //resultMI = em.createNamedQuery("Merchantitem.findAllItemsMerchantInstall", Merchantitem.class)
            //        .setParameter("monetamerchantCode", monetaMerchantCode)
            //        .getResultList();
            results.put("items", em.createNamedQuery("Items.findAllItemsMerchantInstall", Items.class)
                    .setParameter("monetamerchantCode", merchantCode)
                    .getResultList());

            //resultMP = em.createNamedQuery("Merchantpricing.findByMonetamerchantCode", Merchantpricing.class)
            //        .setParameter("monetamerchantCode", monetaMerchantCode)
            //        .getResultList();
            results.put("pricings", em.createNamedQuery("Pricing.findByMerchantId", Pricing.class)
                    .setParameter("merchantId", merchantId)
                    .getResultList());

            Vector c = (Vector) results.get("categories");
            Vector i = (Vector) results.get("items");
            Vector p = (Vector) results.get("pricings");

            if (c.isEmpty() && i.isEmpty() && p.isEmpty()) {
                results.put("status", "ALLEMPTY");
            } else {
                results.put("status", "SUCCESS");
            }

        } else {

            results.put("categories", em.createNamedQuery("Merchantcategory.findUpdateByMonetaMerchantCode", Category.class)
                    .setParameter("monetamerchantCode", merchantCode)
                    .setParameter("merchantcategoryLastupdated", lastUpdatedDate)
                    .getResultList());

            results.put("items", em.createNamedQuery("Merchantitem.findAllItemsMerchantUpdate", Items.class)
                    .setParameter("monetamerchantCode", merchantCode)
                    .setParameter("merchantitemLastupdated", lastUpdatedDate)
                    .getResultList());

            results.put("pricings", em.createNamedQuery("Merchantpricing.findUpdateByMerchantCode", Pricing.class)
                    .setParameter("monetamerchantCode", merchantCode)
                    .setParameter("merchantpricingLastupdated", lastUpdatedDate)
                    .getResultList());

            Vector c = (Vector) results.get("categories");
            Vector i = (Vector) results.get("items");
            Vector p = (Vector) results.get("pricings");

            if (c.isEmpty() && i.isEmpty() && p.isEmpty()) {
                results.put("status", "NONEWUPDATE");
            } else {
                results.put("status", "SUCCESS");
            }

        }

        // check if empty
        //if(results.get("categories"){}
        results.put("lastUpdate", sdf.format(new Date()));

        //logger.info("getOrderProduct - status: " + results.get("status"));
        return new JSONObject(results).toString();

    }

    public HashMap<String, Object> getProductCategories(String merchantCode) {
        Category entity = new Category();
        HashMap<String, Object> result = new HashMap<>();

        // get merchantId from merchantCode
        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);

        List<Category> resultList = em.createNamedQuery("Category.findByMerchantIdNotDeleted", Category.class)
                .setParameter("merchantId", merchantId)
                .getResultList();

        if (resultList.isEmpty()) {
            result.put("status", "NOTFOUND");
            return result;
        }

        result.put("categories", resultList);
        result.put("status", "SUCCESS");

        return result;
    }

    public HashMap<String, Object> getPricings(String merchantCode) {
        HashMap<String, Object> result = new HashMap<>();

        // get merchantId from merchantCode
        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);

        List<Pricing> resultList = em.createNamedQuery("Pricing.findByMerchantIdNotDeleted", Pricing.class)
                .setParameter("merchantId", merchantId)
                .getResultList();

        if (resultList.isEmpty()) {
            result.put("status", "NOTFOUND");
            return result;
        }

        for (Pricing pricing : resultList) {
            em.refresh(pricing);
        }

        result.put("pricings", resultList);
        result.put("status", "SUCCESS");

        return result;
    }

    public HashMap<String, Object> getItems(String merchantCode, String categoryId) {
        Items entity = new Items();
        HashMap<String, Object> result = new HashMap<>();

        // get merchantId from merchantCode
        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);

        List<Items> resultList = em.createNamedQuery("Items.findByCategoryIdNotDeleted", Items.class)
                .setParameter("categoryId", Long.valueOf(categoryId))
                .getResultList();

        if (resultList.isEmpty()) {
            result.put("status", "NOTFOUND");
            return result;
        }

        result.put("items", resultList);
        result.put("status", "SUCCESS");

        return result;
    }

    public HashMap<String, String> deleteProductCategory(String merchantCode, String categoryIdString) {
        HashMap<String, String> result = new HashMap<>();

        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);
        Long categoryId = Long.valueOf(categoryIdString);
        Date dateNow = new Date();

        Category entity = em.find(Category.class, categoryId);

        if (Objects.equals(merchantId, Long.valueOf(entity.getMerchantId()))) {
            entity.setIsDeleted(Boolean.TRUE);
            entity.setLastUpdated(dateNow);
            em.merge(entity);

            List<Items> itemSearch = em.createNamedQuery("Items.findByCategoryId", Items.class)
                    .setParameter("categoryId", categoryId)
                    .getResultList();
            for (Items item : itemSearch) {
                item.setIsDeleted(Boolean.TRUE);
                item.setLastUpdated(dateNow);
                em.merge(item);
            }
        } else {
            result.put("status", "NOTYOURS");
            return result;
        }

        result.put("status", "SUCCESS");
        return result;
    }

    public HashMap<String, String> deleteProductItem(String merchantCode, String itemId) {
        HashMap<String, String> result = new HashMap<>();

        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);

        Items entity = em.find(Items.class, Long.valueOf(itemId));

        if (Objects.equals(merchantId, Long.valueOf(entity.getMerchantId()))) {
            entity.setIsDeleted(Boolean.TRUE);
            entity.setLastUpdated(new Date());
        } else {
            result.put("status", "NOTYOURS");
            return result;
        }

        result.put("status", "SUCCESS");
        return result;
    }

    public HashMap<String, String> deletePricing(String merchantCode, String pricingId) {
        HashMap<String, String> result = new HashMap<>();

        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);

        Pricing entity = em.find(Pricing.class, Long.valueOf(pricingId));

        if (Objects.equals(merchantId, Long.valueOf(entity.getMerchantId()))) {
            entity.setIsDeleted(Boolean.TRUE);
            entity.setLastUpdated(new Date());
        } else {
            result.put("status", "NOTYOURS");
            return result;
        }

        result.put("status", "SUCCESS");
        return result;
    }

    ///////// helper
    private Long getMerchantIdFromMerchantCode(String merchantCode) {
        Merchant entityMerchantId = (Merchant) em.createNamedQuery("Merchant.findByCode")
                .setParameter("code", merchantCode)
                .getSingleResult();

        return Long.valueOf(entityMerchantId.getId());

    }

}
