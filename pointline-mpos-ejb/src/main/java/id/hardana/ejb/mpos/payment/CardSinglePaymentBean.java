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
import id.hardana.entity.profile.card.Card;
import id.hardana.entity.profile.enums.CardStatusEnum;
import id.hardana.entity.profile.enums.OutletStatusEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Operator;
import id.hardana.entity.profile.merchant.Outlet;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.TransactionCardHistory;
import id.hardana.entity.transaction.TransactionCardHistoryPhysic;
import id.hardana.entity.transaction.TransactionCardHistoryServer;
import id.hardana.entity.transaction.TransactionMerchantHistory;
import id.hardana.entity.transaction.TransactionPayment;
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
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class CardSinglePaymentBean {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final String STATUS_KEY = "status";
    private final String INVOICE_STATUS_KEY = "invoiceStatus";
    private final String INVOICE_NUMBER_KEY = "invoiceNumber";
    private final String CLIENT_INVOICE_ID_KEY = "clientInvoiceId";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    @EJB
    private InventoryBean inventoryBean;
    @Resource
    private EJBContext context;

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

    public JSONObject pay(String merchantCode, String userName, String outletId,
            String amount, String transactionFee, String totalAmount,
            String tableNumber, String items, String transactionDiscount, String pricings,
            String clientTransRefnum, String cardId, String customerName, String customerEmail, String customerPhone,
            String debetAmount, String cardFinalBalance, String clientInvoiceId,
            String clientDateTime, String uid) {
        JSONObject response = new JSONObject();

        Long outletIdLong;
        double amountDouble;
        double transactionFeeDouble;
        double totalAmountDouble;
        List<InvoiceItemHolder> listItem;
        List<InvoicePricingHolder> listPricing;
        List<InvoiceItemDiscountHolder> listInvoiceItemDiscountHolder;
        InvoiceTransactionDiscountHolder invoiceTransactionDiscountHolder;
        double debetAmountDouble;
        double cardFinalBalanceDouble;
        Date cDateTime;
        try {
            outletIdLong = Long.parseLong(outletId);
            amountDouble = Double.parseDouble(amount);
            transactionFeeDouble = Double.parseDouble(transactionFee);
            totalAmountDouble = Double.parseDouble(totalAmount);
            debetAmountDouble = Double.parseDouble(debetAmount);
            cardFinalBalanceDouble = Double.parseDouble(cardFinalBalance);
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
        BigDecimal debetAmountBD = BigDecimal.valueOf(debetAmountDouble);
        BigDecimal cardFinalBalanceBD = BigDecimal.valueOf(cardFinalBalanceDouble);

        Date nowDate = new Date();

        List<Card> cardSearch = em.createQuery("SELECT c FROM Card c WHERE c.cardId = :cardId", Card.class)
                .setParameter("cardId", cardId)
                .getResultList();
        if (cardSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_NOT_FOUND.getResponseStatus());
            return response;
        }
        Card card = cardSearch.get(0);
        em.refresh(card);
        Long cardIdLong = Long.parseLong(card.getId());

        CardStatusEnum cardStatus = card.getStatusId();
        if (cardStatus.equals(CardStatusEnum.DISCARDED)) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_DISCARDED.getResponseStatus());
            return response;
        } else if (cardStatus.equals(CardStatusEnum.INACTIVE)) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_INACTIVE.getResponseStatus());
            return response;
        }
        Date cardExpirydate = null;
        try {
            cardExpirydate = DATE_FORMAT.parse(card.getExpiryDate());
        } catch (ParseException ex) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_DATE.getResponseStatus());
            return response;
        }
        if (nowDate.after(cardExpirydate)) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_EXPIRED.getResponseStatus());
            return response;
        }
        double balanceDouble = Double.parseDouble(card.getBalance());
        BigDecimal balance = BigDecimal.valueOf(balanceDouble);
        BigDecimal balanceAfterDebet = balance.subtract(totalAmountBD);
        if (balanceAfterDebet.compareTo(BigDecimal.ZERO) < 0) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_INSUFICIENT_BALANCE.getResponseStatus());
            return response;
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

        try {
            card.setBalance(balanceAfterDebet);
            em.merge(card);
        } catch (OptimisticLockException e) {
            context.setRollbackOnly();
            response.put(STATUS_KEY, ResponseStatusEnum.CONFLICT_DATA_ACCESS.getResponseStatus());
            return response;
        }

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

        // save item and discount item.
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

                DiscountCalculationTypeEnum calcType = DiscountCalculationTypeEnum.getValue(itemDiscountHolder.getDiscountCalculationType());
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
        newTransactionPayment.setSourceId(cardIdLong);
        newTransactionPayment.setTransactionId(transactionId);
        newTransactionPayment.setType(TransactionPaymentTypeEnum.CASHCARD);
        em.persist(newTransactionPayment);
        em.flush();

        Long transactionPaymentId = Long.parseLong(newTransactionPayment.getId());

        TransactionCardHistory cardHistory = new TransactionCardHistory();
        cardHistory.setCardId(cardIdLong);
        cardHistory.setTransactionId(transactionId);
        cardHistory.setCurrentBalanceP(cardFinalBalanceBD);
        cardHistory.setMovementP(debetAmountBD.negate());
        cardHistory.setCurrentBalanceS(balanceAfterDebet);
        cardHistory.setMovementS(totalAmountBD.negate());
        cardHistory.setCardStatus(cardStatus);
        em.persist(cardHistory);

        TransactionCardHistoryPhysic cardHistoryPhysic = new TransactionCardHistoryPhysic();
        cardHistoryPhysic.setCardId(cardIdLong);
        cardHistoryPhysic.setClientDateTime(cDateTime);
        cardHistoryPhysic.setCurrentBalanceP(cardFinalBalanceBD);
        cardHistoryPhysic.setMovementP(debetAmountBD.negate());
        cardHistoryPhysic.setTransactionId(transactionId);
        em.persist(cardHistoryPhysic);

        TransactionCardHistoryServer cardHistoryServer = new TransactionCardHistoryServer();
        cardHistoryServer.setCardId(cardIdLong);
        cardHistoryServer.setCurrentBalanceS(balanceAfterDebet);
        cardHistoryServer.setDateTime(nowDate);
        cardHistoryServer.setMovementS(totalAmountBD.negate());
        cardHistoryServer.setTransactionId(transactionId);
        em.persist(cardHistoryServer);

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
