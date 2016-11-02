/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.invoice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import id.hardana.ejb.mpos.holder.InvoiceItemDiscountHolder;
import id.hardana.ejb.mpos.holder.InvoiceItemHolder;
import id.hardana.ejb.mpos.holder.InvoicePricingHolder;
import id.hardana.ejb.mpos.holder.InvoiceTransactionDiscountHolder;
import id.hardana.ejb.mpos.holder.ItemRequestHolder;
import id.hardana.ejb.mpos.holder.deserializer.DiscountApplyTypeJsonDeserializer;
import id.hardana.ejb.mpos.holder.deserializer.DiscountCalculationTypeJsonDeserializer;
import id.hardana.ejb.mpos.holder.deserializer.DiscountValueTypeJsonDeserializer;
import id.hardana.ejb.mpos.inventory.InventoryBean;
import id.hardana.ejb.mpos.log.LoggingInterceptor;
import id.hardana.entity.inventory.enums.InventoryLogTypeEnum;
import id.hardana.entity.invoice.Invoice;
import id.hardana.entity.invoice.InvoiceItemDiscount;
import id.hardana.entity.invoice.InvoiceItems;
import id.hardana.entity.invoice.InvoicePricing;
import id.hardana.entity.invoice.InvoiceTransactionDiscount;
import id.hardana.entity.invoice.enums.InvoiceStatusEnum;
import id.hardana.entity.profile.enums.DiscountApplyTypeEnum;
import id.hardana.entity.profile.enums.DiscountCalculationTypeEnum;
import id.hardana.entity.profile.enums.DiscountValueTypeEnum;
import id.hardana.entity.profile.enums.OutletStatusEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Operator;
import id.hardana.entity.profile.merchant.Outlet;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class UpdateInvoiceBean {

    private final String STATUS_KEY = "status";
    private final String INVOICE_STATUS_KEY = "invoiceStatus";
    private final String INVOICE_NUMBER_KEY = "invoiceNumber";
    private final String CLIENT_INVOICE_ID_KEY = "clientInvoiceId";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    @EJB
    private InventoryBean inventoryBean;

     private List<InvoiceItemHolder> parseItems(String items) {
        Type typeParam = new TypeToken<List<InvoiceItemHolder>>() {
        }.getType();
         
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DiscountValueTypeEnum.class, new DiscountValueTypeJsonDeserializer());
        gsonBuilder.registerTypeAdapter(DiscountCalculationTypeEnum.class, new DiscountCalculationTypeJsonDeserializer());
        gsonBuilder.registerTypeAdapter(DiscountApplyTypeEnum.class, new DiscountApplyTypeJsonDeserializer());
        
        return gsonBuilder.create().fromJson(items, typeParam);
    }

    private List<InvoicePricingHolder> parsePricing(String pricings) {
        JSONArray pricingsArray = new JSONArray(pricings);
        Type listTypeInvoicePricing = new TypeToken<List<InvoicePricingHolder>>() {
        }.getType();
        List<InvoicePricingHolder> listPricings = new Gson().fromJson(pricingsArray.toString(),
                listTypeInvoicePricing);
        return listPricings;
    }

    private InvoiceTransactionDiscountHolder parseTransactionDiscount(String transactionDiscount) {
        if (transactionDiscount == null || transactionDiscount.trim().length() == 0) {
            return null;
        }
        
        Type typeParam = new TypeToken<InvoiceTransactionDiscountHolder>() {
        }.getType();
         
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DiscountValueTypeEnum.class, new DiscountValueTypeJsonDeserializer());
        gsonBuilder.registerTypeAdapter(DiscountCalculationTypeEnum.class, new DiscountCalculationTypeJsonDeserializer());
        gsonBuilder.registerTypeAdapter(DiscountApplyTypeEnum.class, new DiscountApplyTypeJsonDeserializer());
        
        return gsonBuilder.create().fromJson(transactionDiscount, typeParam);
    }

    public JSONObject updateInvoice(String merchantCode, String userName,
            String outletId, String amount, String tableNumber,
            String items, String transactionDiscount, String pricings, String clientInvoiceId,
            String invoiceNumber) {
        JSONObject response = new JSONObject();

        Long outletIdLong;
        double amountDouble;
        List<InvoiceItemHolder> listItem;
        List<InvoicePricingHolder> listPricing;
        try {
            outletIdLong = Long.parseLong(outletId);
            amountDouble = Double.parseDouble(amount);
            listItem = parseItems(items);
            listPricing = parsePricing(pricings);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }
        BigDecimal amountBD = BigDecimal.valueOf(amountDouble);

        List<Merchant> merchantSearch = em.createNamedQuery("Merchant.findByCode", Merchant.class)
                .setParameter("code", merchantCode)
                .getResultList();
        if (merchantSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_MERCHANT_CODE.getResponseStatus());
            return response;
        }
        Merchant merchant = merchantSearch.get(0);
        Long merchantId = Long.parseLong(merchant.getId());
        double merchantFeeDouble = Double.parseDouble(merchant.getMerchantFee());
        BigDecimal merchantFeeBD = BigDecimal.valueOf(merchantFeeDouble);

        List<Outlet> listOutlet = em.createQuery("SELECT o FROM Outlet o WHERE o.merchantId = :merchantId "
                + "AND o.status = :status AND o.isDeleted = :isDeleted AND o.id = :id", Outlet.class)
                .setParameter("merchantId", merchantId)
                .setParameter("status", OutletStatusEnum.ACTIVE)
                .setParameter("isDeleted", false)
                .setParameter("id", outletIdLong)
                .getResultList();
        if (listOutlet.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_OUTLET_ID.getResponseStatus());
            return response;
        }

        List<Operator> operatorsSearch = em.createNamedQuery("Operator.findByUserNameAndMerchantId", Operator.class)
                .setParameter("userName", userName)
                .setParameter("merchantId", merchantId)
                .getResultList();
        if (operatorsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_USERNAME.getResponseStatus());
            return response;
        }
        Operator operatorSearch = operatorsSearch.get(0);
        Long operatorId = Long.parseLong(operatorSearch.getId());

        List<Invoice> listInvoice = em.createQuery("SELECT i FROM Invoice i "
                + "WHERE i.number = :number AND i.outletId = :outletId", Invoice.class)
                .setParameter("number", invoiceNumber)
                .setParameter("outletId", outletIdLong)
                .getResultList();
        if (listInvoice.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVOICE_NOT_FOUND.getResponseStatus());
            return response;
        }

        Invoice invoiceSearch = listInvoice.get(0);
        InvoiceStatusEnum status = invoiceSearch.getStatus();
        if (status.equals(InvoiceStatusEnum.CANCELED)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVOICE_CANCELED.getResponseStatus());
            return response;
        } else if (status.equals(InvoiceStatusEnum.PAID) || status.equals(InvoiceStatusEnum.UNFINISHED)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVOICE_PAID.getResponseStatus());
            return response;
        }

        invoiceSearch.setAmount(amountBD);
        invoiceSearch.setFee(merchantFeeBD);
        invoiceSearch.setMerchantId(merchantId);
        invoiceSearch.setOperatorId(operatorId);
        invoiceSearch.setOutletId(outletIdLong);
        invoiceSearch.setTableNumber(tableNumber);
        em.merge(invoiceSearch);

        Long invoiceId = Long.parseLong(invoiceSearch.getId());

        List<ItemRequestHolder> listOldItemRequest = new ArrayList<>();
        List<InvoiceItems> listOldInvoiceItems = em.createQuery("SELECT i FROM InvoiceItems i WHERE "
                + "i.invoiceId = :invoiceId", InvoiceItems.class)
                .setParameter("invoiceId", invoiceId)
                .getResultList();
        if (!listOldInvoiceItems.isEmpty()) {
            for (InvoiceItems invoiceItems : listOldInvoiceItems) {
                ItemRequestHolder itemRequestHolder = new ItemRequestHolder();
                itemRequestHolder.setItemId(Long.parseLong(invoiceItems.getItemId()));
                itemRequestHolder.setItemName(invoiceItems.getItemName());
                itemRequestHolder.setStock(Long.parseLong(invoiceItems.getItemQuantity()));
                listOldItemRequest.add(itemRequestHolder);
            }
        }
        inventoryBean.changeStock(merchantId, outletIdLong, operatorId, listOldItemRequest, 
                InventoryLogTypeEnum.VOID_INVOICE, invoiceId, new Date());


        // update invoice item and discount
        String deleteInvoiceItemDiscount = "DELETE FROM InvoiceItemDiscount d WHERE "
                        + "d.invoiceItemId IN (SELECT i.id FROM InvoiceItems i WHERE i.invoiceId = :invoiceId)";
        em.createQuery(deleteInvoiceItemDiscount)
            .setParameter("invoiceId", invoiceId)
            .executeUpdate();
        
        String deleteInvoiceItems = "DELETE FROM InvoiceItems i WHERE i.invoiceId = :invoiceId";
        em.createQuery(deleteInvoiceItems)
            .setParameter("invoiceId", invoiceId)
            .executeUpdate();
        
        for (InvoiceItemHolder itemHolder : listItem) {
            InvoiceItems invoiceItem = new InvoiceItems();
            invoiceItem.setInvoiceId(invoiceId);
            invoiceItem.setItemId(itemHolder.getItemId());
            invoiceItem.setItemName(itemHolder.getItemName());
            invoiceItem.setItemQuantity(Integer.parseInt(itemHolder.getItemQuantity()));
            invoiceItem.setItemSalesPrice(BigDecimal.valueOf(Double.parseDouble(itemHolder.getItemSalesPrice())));
            invoiceItem.setItemSubTotal(BigDecimal.valueOf(Double.parseDouble(itemHolder.getItemSubTotal())));
            invoiceItem.setItemSupplyPrice(BigDecimal.valueOf(Double.parseDouble(itemHolder.getItemSupplyPrice())));
            em.persist(invoiceItem);
            em.flush();
            
            // save item discount
            InvoiceItemDiscountHolder itemDiscountHolder = itemHolder.getItemDiscount();
            if (itemDiscountHolder != null) {
                InvoiceItemDiscount discount = new InvoiceItemDiscount();
                
                Long invoiceItemId = Long.valueOf(invoiceItem.getId());
                discount.setInvoiceItemId(invoiceItemId);    
                
                discount.setDiscountId(itemDiscountHolder.getDiscountId());
                discount.setDiscountName(itemDiscountHolder.getDiscountName());
                discount.setDiscountDescription(itemDiscountHolder.getDiscountDescription());
                discount.setDiscountValue(itemDiscountHolder.getDiscountValue());
                
                DiscountValueTypeEnum valueType = DiscountValueTypeEnum.getValue(itemDiscountHolder.getDiscountValueType());
                discount.setDiscountValueType(valueType);
                
                DiscountApplyTypeEnum applyType = DiscountApplyTypeEnum.getValue(itemDiscountHolder.getDiscountApplyType());
                discount.setDiscountApplyType(applyType);
                
                DiscountCalculationTypeEnum calcType = DiscountCalculationTypeEnum .getValue(itemDiscountHolder.getDiscountCalculationType());
                discount.setDiscountCalculationType(calcType);
                
                discount.setDiscountAmount(itemDiscountHolder.getDiscountAmount());
                
                em.persist(discount);
            }
        }
        
        // update transaction discount
        String deleteInvoiceTransactionDiscount = "DELETE FROM InvoiceTransactionDiscount itd "
                        + "WHERE itd.invoiceId = :invoiceId";
        em.createQuery(deleteInvoiceTransactionDiscount)
            .setParameter("invoiceId", invoiceId)
            .executeUpdate();
        
        InvoiceTransactionDiscountHolder trxDiscountHolder = parseTransactionDiscount(transactionDiscount);
        if (trxDiscountHolder != null) {
            InvoiceTransactionDiscount discount = new InvoiceTransactionDiscount();
            discount.setInvoiceId(invoiceId);    

            discount.setDiscountId(trxDiscountHolder.getDiscountId());
            discount.setDiscountName(trxDiscountHolder.getDiscountName());
            discount.setDiscountDescription(trxDiscountHolder.getDiscountDescription());
            discount.setDiscountValue(trxDiscountHolder.getDiscountValue());

            DiscountValueTypeEnum valueType = DiscountValueTypeEnum.getValue(trxDiscountHolder.getDiscountValueType());
            discount.setDiscountValueType(valueType);
            
            DiscountApplyTypeEnum applyType = DiscountApplyTypeEnum.getValue(trxDiscountHolder.getDiscountApplyType());
            discount.setDiscountApplyType(applyType);
            
            DiscountCalculationTypeEnum calcType = DiscountCalculationTypeEnum.getValue(trxDiscountHolder.getDiscountCalculationType());
            discount.setDiscountCalculationType(calcType);
            
            discount.setDiscountAmount(trxDiscountHolder.getDiscountAmount());

            em.persist(discount);
        }
         
        // update pricing
        String deleteInvoicePricing = "DELETE FROM InvoicePricing i WHERE i.invoiceId = :invoiceId";
        em.createQuery(deleteInvoicePricing)
            .setParameter("invoiceId", invoiceId)
            .executeUpdate();
        
        for (InvoicePricingHolder pricingHolder : listPricing) {
            InvoicePricing invoicePricing = new InvoicePricing();
            invoicePricing.setInvoiceId(invoiceId);
            invoicePricing.setPricingAmount(BigDecimal.valueOf(Double.parseDouble(pricingHolder.getPricingAmount())));
            invoicePricing.setPricingId(pricingHolder.getPricingId());
            invoicePricing.setPricingType(pricingHolder.getPricingType());
            invoicePricing.setPricingValue(BigDecimal.valueOf(Double.parseDouble(pricingHolder.getPricingValue())));
            invoicePricing.setPricingLevel(pricingHolder.getPricingLevel());
            
            em.persist(invoicePricing);
        }

        List<ItemRequestHolder> listItemRequest = new ArrayList<>();
        for (InvoiceItemHolder itemHolder : listItem) {
            ItemRequestHolder itemRequest = new ItemRequestHolder();
            itemRequest.setItemId(itemHolder.getItemId());
            itemRequest.setItemName(itemHolder.getItemName());
            itemRequest.setStock(Long.parseLong(itemHolder.getItemQuantity()));
            listItemRequest.add(itemRequest);
        }
        inventoryBean.changeStock(merchantId, outletIdLong, operatorId, listItemRequest,
                InventoryLogTypeEnum.INVOICE, invoiceId, new Date());

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(INVOICE_STATUS_KEY, invoiceSearch.getStatus());
        response.put(INVOICE_NUMBER_KEY, invoiceSearch.getNumber());
        response.put(CLIENT_INVOICE_ID_KEY, clientInvoiceId);

        return response;
    }

}
