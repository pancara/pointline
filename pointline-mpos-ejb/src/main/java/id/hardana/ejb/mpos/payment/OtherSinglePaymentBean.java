/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.payment;

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
import id.hardana.ejb.system.tools.CodeGenerator;
import id.hardana.entity.inventory.enums.InventoryLogTypeEnum;
import id.hardana.entity.invoice.Invoice;
import id.hardana.entity.invoice.InvoiceItemDiscount;
import id.hardana.entity.invoice.InvoiceItems;
import id.hardana.entity.invoice.InvoicePricing;
import id.hardana.entity.invoice.InvoiceTransactionDiscount;
import id.hardana.entity.invoice.enums.InvoiceStatusEnum;
import id.hardana.entity.profile.enums.OutletStatusEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Operator;
import id.hardana.entity.profile.merchant.Outlet;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.TransactionMerchantHistory;
import id.hardana.entity.transaction.TransactionPayment;
import id.hardana.entity.transaction.TransactionPaymentCreditCard;
import id.hardana.entity.transaction.TransactionPaymentCustomerInfo;
import id.hardana.entity.transaction.TransactionTbl;
import id.hardana.entity.invoice.InvoiceUID;
import id.hardana.entity.profile.enums.DiscountApplyTypeEnum;
import id.hardana.entity.profile.enums.DiscountCalculationTypeEnum;
import id.hardana.entity.profile.enums.DiscountValueTypeEnum;
import id.hardana.entity.transaction.enums.TransactionPaymentTypeEnum;
import id.hardana.entity.transaction.enums.TransactionTypeEnum;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class OtherSinglePaymentBean {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private static final SimpleDateFormat DATE_FORMAT2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final String STATUS_KEY = "status";
    private final String INVOICE_STATUS_KEY = "invoiceStatus";
    private final String INVOICE_NUMBER_KEY = "invoiceNumber";
    private final String CLIENT_INVOICE_ID_KEY = "clientInvoiceId";
    private final String SEND_EMAIL_KEY = "sendEmailStatus";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    private final int CASH_PAYMENT = 0;
    private final int CREDITCARDEDC_PAYMENT = 1;
    private final int DEBITCARD_PAYMENT = 2;
    @EJB
    private InventoryBean inventoryBean;

    public JSONObject cashPay(String merchantCode, String userName, String outletId,
            String amount, String transactionFee, String totalAmount,
            String tableNumber, String items, String transactionDiscount, String pricings, String clientTransRefnum,
            String customerName, String customerEmail, String customerPhone,
            String clientInvoiceId, String clientDateTime, String uid) {
        return pay(merchantCode, userName, outletId, amount, transactionFee, totalAmount,
                tableNumber, items, transactionDiscount, pricings, clientTransRefnum, customerName, customerEmail,
                customerPhone, CASH_PAYMENT, null, null, null, clientInvoiceId, clientDateTime, uid);
    }

    public JSONObject otherCardPay(String merchantCode, String userName, String outletId,
            String amount, String transactionFee, String totalAmount,
            String tableNumber, String items, String transactionDiscount, String pricings, String clientTransRefnum,
            String customerName, String customerEmail, String customerPhone,
            String creditCardType, String creditCardHolderName, String approvalCode,
            String clientInvoiceId, String clientDateTime, String uid, String debitCredit) {

        JSONObject response = new JSONObject();
        Integer debitCreditTag;
        try {
            debitCreditTag = Integer.parseInt(debitCredit);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        if (debitCreditTag != CREDITCARDEDC_PAYMENT && debitCreditTag != DEBITCARD_PAYMENT) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        return pay(merchantCode, userName, outletId, amount, transactionFee, totalAmount,
                tableNumber, items, transactionDiscount, pricings, clientTransRefnum, customerName, customerEmail,
                customerPhone, CREDITCARDEDC_PAYMENT, creditCardType, creditCardHolderName,
                approvalCode, clientInvoiceId, clientDateTime, uid);
    }

    private List<InvoiceItemHolder> parseItems(String items) {
       Type typeParam = new TypeToken<List<InvoiceItemHolder>>() {
        }.getType();
         
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DiscountValueTypeEnum.class, new DiscountValueTypeJsonDeserializer());
        gsonBuilder.registerTypeAdapter(DiscountCalculationTypeEnum.class, new DiscountCalculationTypeJsonDeserializer());
        gsonBuilder.registerTypeAdapter(DiscountApplyTypeEnum.class, new DiscountApplyTypeJsonDeserializer());
        
        return gsonBuilder.create().fromJson(items, typeParam);
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
 
    private List<InvoicePricingHolder> parsePricing(String pricings) {
        JSONArray pricingsArray = new JSONArray(pricings);
        Type listTypeInvoicePricing = new TypeToken<List<InvoicePricingHolder>>() {
        }.getType();
        List<InvoicePricingHolder> listPricings = new Gson().fromJson(pricingsArray.toString(),
                listTypeInvoicePricing);
        return listPricings;
    }

    private JSONObject pay(String merchantCode, String userName, String outletId,
            String amount, String transactionFee, String totalAmount, 
            String tableNumber, String items, String transactionDiscount, String pricings, String clientTransRefnum,
            String customerName, String customerEmail, String customerPhone,
            int paymentType, String cardType, String cardHolderName, String approvalCode,
            String clientInvoiceId, String clientDateTime, String uid) {
        JSONObject response = new JSONObject();

        Long outletIdLong;
        double amountDouble;
        double transactionFeeDouble;
        double totalAmountDouble;
        List<InvoiceItemHolder> listItem;
        List<InvoicePricingHolder> listPricing;
        Date cDateTime;
        try {
            outletIdLong = Long.parseLong(outletId);
            amountDouble = Double.parseDouble(amount);
            transactionFeeDouble = Double.parseDouble(transactionFee);
            totalAmountDouble = Double.parseDouble(totalAmount);
            listItem = parseItems(items);
            listPricing = parsePricing(pricings);
            cDateTime = DATE_FORMAT.parse(clientDateTime);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }
        BigDecimal amountBD = BigDecimal.valueOf(amountDouble);
        BigDecimal transactionFeeBD = BigDecimal.valueOf(transactionFeeDouble);
        BigDecimal totalAmountBD = BigDecimal.valueOf(totalAmountDouble);

        Date nowDate = new Date();

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

        List<InvoiceUID> listUID = em.createQuery("SELECT i FROM InvoiceUID i WHERE "
                + "i.merchantId = :merchantId AND i.outletId = :outletId AND "
                + "i.generatedDate = :generatedDate AND i.uniqueId = :uniqueId", InvoiceUID.class)
                .setParameter("merchantId", merchantId)
                .setParameter("outletId", outletIdLong)
                .setParameter("generatedDate", nowDate, TemporalType.DATE)
                .setParameter("uniqueId", uid)
                .getResultList();
        if (listUID.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.UID_NOT_FOUND.getResponseStatus());
            return response;
        }
        InvoiceUID invoiceUID = listUID.get(0);
        em.refresh(invoiceUID);
        Boolean uidIsUsed = Boolean.valueOf(invoiceUID.getIsUsed());
        if (uidIsUsed) {
            response.put(STATUS_KEY, ResponseStatusEnum.DUPLICATE_TRANSACTION.getResponseStatus());
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

        boolean invoiceIsEmpty = false;
        String invoiceNumberGenerate = null;
        List<Invoice> listInvoice;

        while (!invoiceIsEmpty) {
            invoiceNumberGenerate = CodeGenerator.generateReferenceNumber();
            listInvoice = em.createQuery("SELECT i FROM Invoice i WHERE i.number = :number", Invoice.class)
                    .setParameter("number", invoiceNumberGenerate)
                    .getResultList();
            invoiceIsEmpty = listInvoice.isEmpty();
        }

        boolean transactionIsEmpty = false;
        String referenceNumberGenerate = null;
        List<TransactionTbl> transactionTbl;

        while (!transactionIsEmpty) {
            referenceNumberGenerate = CodeGenerator.generateReferenceNumber();
            transactionTbl = em.createQuery("SELECT t FROM TransactionTbl t WHERE t.referenceNumber = :referenceNumber", TransactionTbl.class)
                    .setParameter("referenceNumber", referenceNumberGenerate)
                    .getResultList();
            transactionIsEmpty = transactionTbl.isEmpty();
        }

        Invoice newInvoice = new Invoice();
        newInvoice.setAmount(amountBD);
        newInvoice.setDateTime(nowDate);
        newInvoice.setFee(merchantFeeBD);
        newInvoice.setMerchantId(merchantId);
        newInvoice.setNumber(invoiceNumberGenerate);
        newInvoice.setOperatorId(operatorId);
        newInvoice.setOutletId(outletIdLong);
        newInvoice.setStatus(InvoiceStatusEnum.PAID);
        newInvoice.setTableNumber(tableNumber);
        newInvoice.setClientDateTime(cDateTime);
        
        em.persist(newInvoice);
        em.flush();

        Long invoiceId = Long.parseLong(newInvoice.getId());

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

        // save transaction discount
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

        TransactionTbl newTransaction = new TransactionTbl();
        newTransaction.setAmount(amountBD);
        newTransaction.setClientTransRefnum(clientTransRefnum);
        newTransaction.setDateTime(nowDate);
        newTransaction.setFee(transactionFeeBD);
        newTransaction.setReferenceNumber(referenceNumberGenerate);
        newTransaction.setStatus(ResponseStatusEnum.SUCCESS);
        newTransaction.setTotalAmount(totalAmountBD);
        newTransaction.setType(TransactionTypeEnum.PAYMENT);
        newTransaction.setClientDateTime(cDateTime);
        em.persist(newTransaction);
        em.flush();

        Long transactionId = Long.parseLong(newTransaction.getId());

        TransactionPayment newTransactionPayment = new TransactionPayment();
        newTransactionPayment.setInvoiceId(invoiceId);
        newTransactionPayment.setTransactionId(transactionId);
        newTransactionPayment.setSourceId(null);
        if (paymentType == CASH_PAYMENT) {
            newTransactionPayment.setType(TransactionPaymentTypeEnum.CASH);
        } else if (paymentType == CREDITCARDEDC_PAYMENT) {
            newTransactionPayment.setType(TransactionPaymentTypeEnum.CREDITCARDEDC);
        } else if (paymentType == DEBITCARD_PAYMENT) {
            newTransactionPayment.setType(TransactionPaymentTypeEnum.DEBITCARD);
        }
        em.persist(newTransactionPayment);
        em.flush();

        Long transactionPaymentId = Long.parseLong(newTransactionPayment.getId());

        if (paymentType == CREDITCARDEDC_PAYMENT || paymentType == DEBITCARD_PAYMENT) {
            TransactionPaymentCreditCard newTransactionPaymentCreditCard = new TransactionPaymentCreditCard();
            newTransactionPaymentCreditCard.setApprovalCode(approvalCode);
            newTransactionPaymentCreditCard.setCardHolderName(cardHolderName);
            newTransactionPaymentCreditCard.setCardType(cardType);
            newTransactionPaymentCreditCard.setTransactionPaymentId(transactionPaymentId);
            em.persist(newTransactionPaymentCreditCard);
        }

        TransactionMerchantHistory merchantHistory = new TransactionMerchantHistory();
        merchantHistory.setMerchantId(merchantId);
        merchantHistory.setRefType(TransactionMerchantHistory.TRANSACTION);
        merchantHistory.setRefId(transactionId);
        em.persist(merchantHistory);

        TransactionPaymentCustomerInfo customerInfo = new TransactionPaymentCustomerInfo();
        customerInfo.setCustomerEmail(customerEmail);
        customerInfo.setCustomerName(customerName);
        customerInfo.setCustomerPhone(customerPhone);
        customerInfo.setTransactionPaymentId(transactionPaymentId);
        em.persist(customerInfo);

        invoiceUID.setIsUsed(true);
        invoiceUID.setInvoiceId(invoiceId);
        em.merge(invoiceUID);

        List<ItemRequestHolder> listItemRequest = new ArrayList<>();
        for (InvoiceItemHolder itemHolder : listItem) {
            ItemRequestHolder itemRequest = new ItemRequestHolder();
            itemRequest.setItemId(itemHolder.getItemId());
            itemRequest.setItemName(itemHolder.getItemName());
            itemRequest.setStock(Long.parseLong(itemHolder.getItemQuantity()));
            listItemRequest.add(itemRequest);
        }
        inventoryBean.changeStock(merchantId, outletIdLong, operatorId, listItemRequest,
                InventoryLogTypeEnum.INVOICE, invoiceId, cDateTime);

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(INVOICE_STATUS_KEY, newInvoice.getStatus());
        response.put(INVOICE_NUMBER_KEY, newInvoice.getNumber());
        response.put(CLIENT_INVOICE_ID_KEY, clientInvoiceId);

        return response;
    }
}
