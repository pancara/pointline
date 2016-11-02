/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.history;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import id.hardana.ejb.mpos.holder.AllItemsHolder;
import id.hardana.ejb.mpos.holder.ItemsHolder;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.invoice.enums.InvoiceStatusEnum;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Arya
 */
@Stateless
@LocalBean
public class PrintItemsBean {

    private final String STATUS_KEY = "status";
    private final String SALE_ITEMS_KEY = "itemsData";
    private final String queryOperatorIdList = "AND i.operatorId IN :operatorIdList ";

    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject printItems(String merchantCode, String outletId,
            String operatorId, String strStartDate, String strEndDate, String startTime, String endTime) {
        
        int startOfHour = Integer.parseInt(startTime.substring(0, 2));
        int startOfMinute = Integer.parseInt(startTime.substring(2, 4));
        int endOfHour = Integer.parseInt(endTime.substring(0, 2));
        int endOfMinute = Integer.parseInt(endTime.substring(2, 4));
        
        Calendar date = new GregorianCalendar();
        int iStartDate = Integer.parseInt(strStartDate.substring(0, 2));
        int iStartMonth = Integer.parseInt(strStartDate.substring(3, 5));
        int iStartYear = Integer.parseInt(strStartDate.substring(6, 10));
        date.set(Calendar.DATE, iStartDate);
        date.set(Calendar.MONTH, iStartMonth - 1);
        date.set(Calendar.YEAR, iStartYear);
        
        date.set(Calendar.HOUR_OF_DAY, startOfHour);
        date.set(Calendar.MINUTE, startOfMinute);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        Date startDate = date.getTime();
        
        int iEndDate = Integer.parseInt(strEndDate.substring(0, 2));
        int iEndMonth = Integer.parseInt(strEndDate.substring(3, 5));
        int iEndYear = Integer.parseInt(strEndDate.substring(6, 10));
        date.set(Calendar.HOUR_OF_DAY, endOfHour);
        date.set(Calendar.MINUTE, endOfMinute);
        date.set(Calendar.SECOND, 59);
        date.set(Calendar.MILLISECOND, 0);
        
        date.set(Calendar.DATE, iEndDate);
        date.set(Calendar.MONTH, iEndMonth - 1);
        date.set(Calendar.YEAR, iEndYear);
        Date endDate = date.getTime();
        return getItems(merchantCode, outletId, operatorId, startDate, endDate);
    }

    private JSONObject getItems(String merchantCode, String outletId, String operatorId, Date startDate, Date endDate) {
        JSONObject response = new JSONObject();
        JSONArray operatorIdArray;
        Long outletIdLong;
        
        try {
            outletIdLong = Long.parseLong(outletId);        
            operatorIdArray = new JSONArray(operatorId);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }
        Type listType = new TypeToken<List<Long>>() {}.getType();
        List<Long> operatorIdList = new Gson().fromJson(operatorIdArray.toString(), listType);
        ItemsHolder allItems = new ItemsHolder();
        
        List<AllItemsHolder> seedItemsList = getItemsSeed(merchantCode, outletIdLong, operatorIdList, startDate, endDate);
        List<AllItemsHolder> lastItemsList = getAllItems(merchantCode, outletIdLong, operatorIdList, startDate, endDate, seedItemsList);
        allItems.setAllItems(lastItemsList);
        
        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(SALE_ITEMS_KEY, new JSONObject(new Gson().toJson(allItems)));
        return response;
    }
    
    private List<AllItemsHolder> getItemsSeed(String merchantCode, Long outletIdLong, List<Long> operatorIdList, Date startDate, Date endDate) {
        List<AllItemsHolder> result;
        List<String> resultItemsName;
        List<AllItemsHolder> lastResult = new ArrayList<>();
        List<AllItemsHolder> allItems = new ArrayList<>();
        
        String qlAllItems = 
                
                "SELECT NEW id.hardana.ejb.mpos.holder.AllItemsHolder"
                + "(cat.name, its.id, its.code, it.itemName, it.itemQuantity) FROM InvoiceItems it "
                + "JOIN Invoice i ON i.id = it.invoiceId "
                + "JOIN Items its ON its.id = it.itemId "
                + "JOIN Category cat ON cat.id = its.categoryId "
                + "JOIN Merchant m ON m.id = i.merchantId "
                + "WHERE m.code = :merchantCode AND i.status = :invoiceStatus "
                + "AND i.dateTime BETWEEN :startDate AND :endDate "
                + "AND i.outletId = :outletId "
                ;
        
        String qlItemName = "SELECT DISTINCT it.itemName FROM InvoiceItems it " 
                 + "JOIN Invoice i ON i.id = it.invoiceId "
                 + "JOIN Merchant m ON m.id = i.merchantId "
                 + "WHERE m.code = :merchantCode AND i.status = :invoiceStatus "
                 + "AND i.dateTime BETWEEN :startDate AND :endDate "
                 + "AND i.outletId = :outletId ";

        if (!operatorIdList.isEmpty()) {
            qlItemName += queryOperatorIdList;
            qlAllItems += queryOperatorIdList;
        }
        
        Query queryAllItems = em.createQuery(qlAllItems, AllItemsHolder.class)
                .setParameter("merchantCode", merchantCode)
                .setParameter("invoiceStatus", InvoiceStatusEnum.PAID)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("outletId", outletIdLong);

        Query queryItemsName = em.createQuery(qlItemName, String.class) 
            .setParameter("merchantCode", merchantCode)
            .setParameter("invoiceStatus", InvoiceStatusEnum.PAID)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .setParameter("outletId", outletIdLong);

        if (!operatorIdList.isEmpty()) {
            queryItemsName.setParameter("operatorIdList", operatorIdList);
            queryAllItems.setParameter("operatorIdList", operatorIdList);
        }
        
        try {
            result = queryAllItems.getResultList();
            resultItemsName = queryItemsName.getResultList();
        } 
            catch(NoResultException e) {
            return null;
        }
        
        Integer totalQuantity = 0;
        int k=0;
        for(String strItemName : resultItemsName){
            k=0;

            for(AllItemsHolder saleItemHolder : result) { 
                AllItemsHolder addItemHolder = new AllItemsHolder();
                k=k+1;
            
                if(saleItemHolder.getItemName().equalsIgnoreCase(strItemName)) {
                   totalQuantity = totalQuantity + saleItemHolder.getItemQuantity();

                   addItemHolder.setCategoryName(saleItemHolder.getCategoryName());
                   addItemHolder.setItemId(saleItemHolder.getItemId());
                   addItemHolder.setItemCode(saleItemHolder.getItemCode());
                   addItemHolder.setItemName(saleItemHolder.getItemName());
                   addItemHolder.setItemQuantity(totalQuantity);
                   lastResult.add(addItemHolder);
                  
                }
            }
            int m=0;
            for(AllItemsHolder saleItemHolder2 : lastResult) {
                m=m+1;    

                AllItemsHolder addItemHolder2 = new AllItemsHolder();
                if(m == lastResult.size()){
                    addItemHolder2.setCategoryName(saleItemHolder2.getCategoryName());
                    addItemHolder2.setItemId(saleItemHolder2.getItemId());
                    addItemHolder2.setItemCode(saleItemHolder2.getItemCode());
                    addItemHolder2.setItemName(saleItemHolder2.getItemName());
                    addItemHolder2.setItemQuantity(saleItemHolder2.getItemQuantity());

                    List<AllItemsHolder> lastResult02 = new ArrayList<>();
                    lastResult02.add(addItemHolder2);
                    allItems.addAll(lastResult02);
                }
            }
            lastResult.clear();
            totalQuantity = 0;
        }
        
        return allItems;
    }
    
    private List<AllItemsHolder> getAllItems(String merchantCode, Long outletIdLong, List<Long> operatorIdList, Date startDate, Date endDate, List<AllItemsHolder> listItemsSeed) {
        List<String> resultCategoryName;
        List<AllItemsHolder> lastResult = new ArrayList<>();
        List<AllItemsHolder> allItems = new ArrayList<>();
        
        String qlCategoryName ="";
        qlCategoryName = "SELECT DISTINCT cat.name FROM InvoiceItems it "
                + "JOIN Invoice i ON i.id = it.invoiceId "
                + "JOIN Items its ON its.id = it.itemId "
                + "JOIN Category cat ON cat.id = its.categoryId "
                + "JOIN Merchant m ON m.id = i.merchantId " 
                + "WHERE m.code = :merchantCode AND i.status = :invoiceStatus "
                + "AND i.dateTime BETWEEN :startDate AND :endDate "
                + "AND i.outletId = :outletId ";
        
        if (!operatorIdList.isEmpty()) {
            qlCategoryName += queryOperatorIdList;
        }
        
        Query queryCategoryName = em.createQuery(qlCategoryName, String.class) 
            .setParameter("merchantCode", merchantCode)
            .setParameter("invoiceStatus", InvoiceStatusEnum.PAID)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .setParameter("outletId", outletIdLong);

        if (!operatorIdList.isEmpty()) {
            queryCategoryName.setParameter("operatorIdList", operatorIdList);
        }
        
        try {
            resultCategoryName = queryCategoryName.getResultList();
        } 
            catch(NoResultException e) {
            return null;
        }
        
        int k=0;
        for(String strCategoryName : resultCategoryName){
            k=0;

            for(AllItemsHolder saleItemHolder : listItemsSeed) { 
                AllItemsHolder addSeedItemHolder = new AllItemsHolder();

                k=k+1;
                if(saleItemHolder.getCategoryName().equalsIgnoreCase(strCategoryName)) {
                    addSeedItemHolder.setCategoryName(saleItemHolder.getCategoryName());
                    addSeedItemHolder.setItemId(saleItemHolder.getItemId());
                    addSeedItemHolder.setItemCode(saleItemHolder.getItemCode());
                    addSeedItemHolder.setItemName(saleItemHolder.getItemName());
                    addSeedItemHolder.setItemQuantity(saleItemHolder.getItemQuantity());
                    
                    lastResult.add(addSeedItemHolder);
                }
            }
            
            for(AllItemsHolder saleItemHolder2 : lastResult) {
                AllItemsHolder addItemHolder2 = new AllItemsHolder();
                
                addItemHolder2.setCategoryName(saleItemHolder2.getCategoryName());
                addItemHolder2.setItemId(saleItemHolder2.getItemId());
                addItemHolder2.setItemCode(saleItemHolder2.getItemCode());
                addItemHolder2.setItemName(saleItemHolder2.getItemName());
                addItemHolder2.setItemQuantity(saleItemHolder2.getItemQuantity());
                
                List<AllItemsHolder> lastResult02 = new ArrayList<>();
                lastResult02.add(addItemHolder2);
                allItems.addAll(lastResult02);
            }
            lastResult.clear();
        }
        
        return allItems;
    }
}
