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
import id.hardana.ejb.mpos.holder.TransactionMultiPaymentHolder;
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
import id.hardana.entity.invoice.InvoiceUID;
import id.hardana.entity.invoice.enums.InvoiceStatusEnum;
import id.hardana.entity.profile.card.Card;
import id.hardana.entity.profile.enums.CardStatusEnum;
import id.hardana.entity.profile.enums.DiscountApplyTypeEnum;
import id.hardana.entity.profile.enums.DiscountCalculationTypeEnum;
import id.hardana.entity.profile.enums.DiscountValueTypeEnum;
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
import id.hardana.entity.transaction.TransactionPaymentCreditCard;
import id.hardana.entity.transaction.TransactionPaymentCustomerInfo;
import id.hardana.entity.transaction.TransactionTbl;
import id.hardana.entity.transaction.enums.TransactionPaymentTypeEnum;
import id.hardana.entity.transaction.enums.TransactionTypeEnum;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
 *
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class MultiPaymentBean {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final String STATUS_KEY = "status";
    private final String DETAIL_STATUS_KEY = "detailStatus";
    private final String INVOICE_STATUS_KEY = "invoiceStatus";
    private final String INVOICE_NUMBER_KEY = "invoiceNumber";
    private final String CLIENT_INVOICE_ID_KEY = "clientInvoiceId";

    private final String PAYMENTS_KEY = "payments";
    private final String DETAIL_VALUE = "TRANSACTION NUMBER : ";
    private final String MERCHANT_ID_KEY = "merchantId";
    private final String OPERATOR_ID_KEY = "operatorId";
    private final String INVOICE_UID_KEY = "invoiceUID";
    private final String MERCHANT_FEE_KEY = "merchantFee";
    private final String INVOICE_ID_KEY = "invoiceId";
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

    private List<TransactionMultiPaymentHolder> parsePayment(String payments) {
        JSONArray paymentsArray = new JSONArray(payments);
        Type listTypePayment = new TypeToken<List<TransactionMultiPaymentHolder>>() {
        }.getType();
        List<TransactionMultiPaymentHolder> listPayment = new Gson().fromJson(paymentsArray.toString(),
                listTypePayment);
        return listPayment;
    }

    private HashMap<String, Object> checkNullPayment(TransactionMultiPaymentHolder payment,
            int transactionCount) {
        HashMap<String, Object> response = new HashMap<>();

        TransactionPaymentTypeEnum paymentType = payment.getPaymentType();
        String clientTransRefnum = payment.getClientTransRefnum();
        BigDecimal amount = payment.getAmount();
        BigDecimal fee = payment.getFee();
        BigDecimal totalAmount = payment.getTotalAmount();

        if (paymentType == null || clientTransRefnum == null
                || amount == null || fee == null || totalAmount == null) {
            response.put(STATUS_KEY, ResponseStatusEnum.EMPTY_PARAMETER.getResponseStatus());
            response.put(DETAIL_STATUS_KEY, DETAIL_VALUE + transactionCount);
            return response;
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response;
    }

    private HashMap<String, Object> checkNullCashCardPayment(TransactionMultiPaymentHolder payment,
            int transactionCount) {
        HashMap<String, Object> response = new HashMap<>();

        String cardUID = payment.getCardUID();
        BigDecimal debetAmount = payment.getDebetAmount();
        BigDecimal cardFinalBalance = payment.getCardFinalBalance();

        if (cardUID == null || debetAmount == null || cardFinalBalance == null) {
            response.put(STATUS_KEY, ResponseStatusEnum.EMPTY_PARAMETER.getResponseStatus());
            response.put(DETAIL_STATUS_KEY, DETAIL_VALUE + transactionCount);
            return response;
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response;
    }

    private HashMap<String, Object> checkNullOtherCardPayment(TransactionMultiPaymentHolder payment,
            int transactionCount) {
        HashMap<String, Object> response = new HashMap<>();

        String creditCardType = payment.getCreditCardType();
        String creditCardHolderName = payment.getCreditCardHolderName();
        String approvalCode = payment.getApprovalCode();

        if (creditCardType == null || creditCardHolderName == null || approvalCode == null) {
            response.put(STATUS_KEY, ResponseStatusEnum.EMPTY_PARAMETER.getResponseStatus());
            response.put(DETAIL_STATUS_KEY, DETAIL_VALUE + transactionCount);
            return response;
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        return response;
    }

    private HashMap<String, Object> validateCashCardPayment(TransactionMultiPaymentHolder payment,
            int transactionCount, Date nowDate) {
        HashMap<String, Object> response = new HashMap<>();

        HashMap<String, Object> nullStatusResponse = checkNullCashCardPayment(payment, transactionCount);
        if (!nullStatusResponse.get(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return nullStatusResponse;
        }

        String cardUID = payment.getCardUID();
        BigDecimal totalAmount = payment.getTotalAmount();

        List<Card> cardSearch = em.createQuery("SELECT c FROM Card c WHERE c.cardId = :cardId", Card.class)
                .setParameter("cardId", cardUID)
                .getResultList();
        if (cardSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_NOT_FOUND.getResponseStatus());
            response.put(DETAIL_STATUS_KEY, DETAIL_VALUE + transactionCount);
            return response;
        }
        Card card = cardSearch.get(0);
        em.refresh(card);
        CardStatusEnum cardStatus = card.getStatusId();
        if (cardStatus.equals(CardStatusEnum.DISCARDED)) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_DISCARDED.getResponseStatus());
            response.put(DETAIL_STATUS_KEY, DETAIL_VALUE + transactionCount);
            return response;
        } else if (cardStatus.equals(CardStatusEnum.INACTIVE)) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_INACTIVE.getResponseStatus());
            response.put(DETAIL_STATUS_KEY, DETAIL_VALUE + transactionCount);
            return response;
        }
        Date cardExpirydate = null;
        try {
            cardExpirydate = DATE_FORMAT.parse(card.getExpiryDate());
        } catch (ParseException ex) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_DATE.getResponseStatus());
            response.put(DETAIL_STATUS_KEY, DETAIL_VALUE + transactionCount);
            return response;
        }
        if (nowDate.after(cardExpirydate)) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_EXPIRED.getResponseStatus());
            response.put(DETAIL_STATUS_KEY, DETAIL_VALUE + transactionCount);
            return response;
        }
        double balanceDouble = Double.parseDouble(card.getBalance());
        BigDecimal balance = BigDecimal.valueOf(balanceDouble);
        BigDecimal balanceAfterDebet = balance.subtract(totalAmount);
        if (balanceAfterDebet.compareTo(BigDecimal.ZERO) < 0) {
            response.put(STATUS_KEY, ResponseStatusEnum.CARD_INSUFICIENT_BALANCE.getResponseStatus());
            response.put(DETAIL_STATUS_KEY, DETAIL_VALUE + transactionCount);
            return response;
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(DETAIL_STATUS_KEY, card);
        return response;
    }

    private HashMap<String, Object> validateAllPayments(List<TransactionMultiPaymentHolder> listPayment, Date nowDate) {
        HashMap<String, Object> response = new HashMap<>();
        List<TransactionMultiPaymentHolder> newListPayment = new ArrayList<>();

        for (int i = 0; i < listPayment.size(); i++) {
            TransactionMultiPaymentHolder payment = listPayment.get(i);
            HashMap<String, Object> nullStatusResponse = checkNullPayment(payment, i);
            if (!nullStatusResponse.get(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
                return nullStatusResponse;
            }
            TransactionPaymentTypeEnum paymentType = payment.getPaymentType();
            if (paymentType.equals(TransactionPaymentTypeEnum.CASHCARD)) {
                HashMap<String, Object> validateCashCardStatusResponse = validateCashCardPayment(payment, i, nowDate);
                if (!validateCashCardStatusResponse.get(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
                    return validateCashCardStatusResponse;
                }
                payment.setCard((Card) validateCashCardStatusResponse.get(DETAIL_STATUS_KEY));
                newListPayment.add(i, payment);
            } else if (paymentType.equals(TransactionPaymentTypeEnum.CREDITCARDEDC)
                    || paymentType.equals(TransactionPaymentTypeEnum.DEBITCARD)) {
                HashMap<String, Object> nullStatusCreditCardEDCResponse = checkNullOtherCardPayment(payment, i);
                if (!nullStatusCreditCardEDCResponse.get(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
                    return nullStatusCreditCardEDCResponse;
                }
                newListPayment.add(i, payment);
            } else {
                newListPayment.add(i, payment);
            }
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(PAYMENTS_KEY, newListPayment);
        return response;
    }

    private HashMap<String, Object> validateMerchantOutletUIDAndOperator(String merchantCode,
            String userName, Long outletIdLong, String uid, Date nowDate) {
        HashMap<String, Object> response = new HashMap<>();
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

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(MERCHANT_ID_KEY, merchantId);
        response.put(MERCHANT_FEE_KEY, merchantFeeBD);
        response.put(OPERATOR_ID_KEY, operatorId);
        response.put(INVOICE_UID_KEY, invoiceUID);
        return response;
    }

    private HashMap<String, Object> insertInvoice(BigDecimal invoiceAmount, BigDecimal merchantFee,
            Date nowDate, Long merchantId, Long operatorId, Long outletIdLong, String tableNumber,
            List<InvoiceItemHolder> listItem, InvoiceTransactionDiscountHolder trxDiscountHolder,
            List<InvoicePricingHolder> listPricing, Date cDateTime) {
        HashMap<String, Object> response = new HashMap<>();

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

        Invoice newInvoice = new Invoice();
        newInvoice.setAmount(invoiceAmount);
        newInvoice.setDateTime(nowDate);
        newInvoice.setFee(merchantFee);
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

                DiscountCalculationTypeEnum calcType = DiscountCalculationTypeEnum.getValue(itemDiscountHolder.getDiscountCalculationType());
                discount.setDiscountCalculationType(calcType);

                discount.setDiscountAmount(itemDiscountHolder.getDiscountAmount());

                em.persist(discount);
            }
        }

        // save invoice transaction discount
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

        response.put(INVOICE_ID_KEY, invoiceId);
        response.put(INVOICE_NUMBER_KEY, invoiceNumberGenerate);
        response.put(INVOICE_STATUS_KEY, newInvoice.getStatus());
        return response;
    }

    private List<TransactionMultiPaymentHolder> processAllPayment(List<TransactionMultiPaymentHolder> newListPayment,
            Long invoiceId, Long merchantId, Date nowDate, String customerName,
            String customerEmail, String customerPhone, Date cDateTime) throws OptimisticLockException {

        for (TransactionMultiPaymentHolder payment : newListPayment) {
            TransactionPaymentTypeEnum paymentType = payment.getPaymentType();
            if (paymentType.equals(TransactionPaymentTypeEnum.CASHCARD)) {
                Card card = payment.getCard();
                double balanceDouble = Double.parseDouble(card.getBalance());
                BigDecimal balance = BigDecimal.valueOf(balanceDouble);
                BigDecimal balanceAfterDebet = balance.subtract(payment.getTotalAmount());

                card.setBalance(balanceAfterDebet);
                em.merge(card);

                payment.setBalanceAfterDebet(balanceAfterDebet);
            }
        }

        for (TransactionMultiPaymentHolder payment : newListPayment) {

            boolean transactionIsEmpty = false;
            String referenceNumberGenerate = null;
            List<TransactionTbl> transactionTbl;

            while (!transactionIsEmpty) {
                referenceNumberGenerate = CodeGenerator.generateReferenceNumber();
                transactionTbl = em.createQuery("SELECT t FROM TransactionTbl t "
                        + "WHERE t.referenceNumber = :referenceNumber", TransactionTbl.class)
                        .setParameter("referenceNumber", referenceNumberGenerate)
                        .getResultList();
                transactionIsEmpty = transactionTbl.isEmpty();
            }
            TransactionTbl newTransaction = new TransactionTbl();
            newTransaction.setAmount(payment.getAmount());
            newTransaction.setClientTransRefnum(payment.getClientTransRefnum());
            newTransaction.setDateTime(nowDate);
            newTransaction.setFee(payment.getFee());
            newTransaction.setReferenceNumber(referenceNumberGenerate);
            newTransaction.setStatus(ResponseStatusEnum.SUCCESS);
            newTransaction.setTotalAmount(payment.getTotalAmount());
            newTransaction.setType(TransactionTypeEnum.PAYMENT);
            newTransaction.setClientDateTime(cDateTime);
            em.persist(newTransaction);
            em.flush();

            Long transactionId = Long.parseLong(newTransaction.getId());

            TransactionPaymentTypeEnum paymentType = payment.getPaymentType();
            TransactionPayment newTransactionPayment = new TransactionPayment();
            newTransactionPayment.setInvoiceId(invoiceId);
            if (paymentType.equals(TransactionPaymentTypeEnum.CASHCARD)) {
                newTransactionPayment.setSourceId(Long.parseLong(payment.getCard().getId()));
            }
            newTransactionPayment.setTransactionId(transactionId);
            newTransactionPayment.setType(payment.getPaymentType());
            em.persist(newTransactionPayment);
            em.flush();

            Long transactionPaymentId = Long.parseLong(newTransactionPayment.getId());

            if (paymentType.equals(TransactionPaymentTypeEnum.CREDITCARDEDC)
                    || paymentType.equals(TransactionPaymentTypeEnum.DEBITCARD)) {
                TransactionPaymentCreditCard newTransactionPaymentCreditCard = new TransactionPaymentCreditCard();
                newTransactionPaymentCreditCard.setApprovalCode(payment.getApprovalCode());
                newTransactionPaymentCreditCard.setCardHolderName(payment.getCreditCardHolderName());
                newTransactionPaymentCreditCard.setCardType(payment.getCreditCardType());
                newTransactionPaymentCreditCard.setTransactionPaymentId(transactionPaymentId);
                em.persist(newTransactionPaymentCreditCard);
            }

            if (paymentType.equals(TransactionPaymentTypeEnum.CASHCARD)) {
                Card card = payment.getCard();
                BigDecimal balanceAfterDebet = payment.getBalanceAfterDebet();
                Long cardIdLong = Long.parseLong(card.getId());

                TransactionCardHistory cardHistory = new TransactionCardHistory();
                cardHistory.setCardId(cardIdLong);
                cardHistory.setTransactionId(transactionId);
                cardHistory.setCurrentBalanceP(payment.getCardFinalBalance());
                cardHistory.setMovementP(payment.getDebetAmount().negate());
                cardHistory.setCurrentBalanceS(balanceAfterDebet);
                cardHistory.setMovementS(payment.getTotalAmount().negate());
                cardHistory.setCardStatus(card.getStatusId());
                em.persist(cardHistory);

                TransactionCardHistoryPhysic cardHistoryPhysic = new TransactionCardHistoryPhysic();
                cardHistoryPhysic.setCardId(cardIdLong);
                cardHistoryPhysic.setClientDateTime(cDateTime);
                cardHistoryPhysic.setCurrentBalanceP(payment.getCardFinalBalance());
                cardHistoryPhysic.setMovementP(payment.getDebetAmount().negate());
                cardHistoryPhysic.setTransactionId(transactionId);
                em.persist(cardHistoryPhysic);

                TransactionCardHistoryServer cardHistoryServer = new TransactionCardHistoryServer();
                cardHistoryServer.setCardId(cardIdLong);
                cardHistoryServer.setCurrentBalanceS(balanceAfterDebet);
                cardHistoryServer.setDateTime(nowDate);
                cardHistoryServer.setMovementS(payment.getTotalAmount().negate());
                cardHistoryServer.setTransactionId(transactionId);
                em.persist(cardHistoryServer);
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

            payment.setReferenceNumber(referenceNumberGenerate);
            payment.setStatus(newTransaction.getStatus());
            payment.setTransactionDate(nowDate);
            payment.setCard(null);
        }

        return newListPayment;
    }

    public JSONObject multiPay(String merchantCode, String userName, String outletId,
            String invoiceAmount, String tableNumber, String items, String transactionDiscount,
            String pricings, String payments, String customerName,
            String customerEmail, String customerPhone, String clientInvoiceId,
            String clientDateTime, String uid) {

        JSONObject response = new JSONObject();
        Date nowDate = new Date();

        Long outletIdLong;
        double invoiceAmountDouble;
        List<InvoiceItemHolder> listItem;
        InvoiceTransactionDiscountHolder trxDiscountHolder;
        List<InvoicePricingHolder> listPricing;
        List<TransactionMultiPaymentHolder> listPayment;
        Date cDateTime;
        try {
            outletIdLong = Long.parseLong(outletId);
            invoiceAmountDouble = Double.parseDouble(invoiceAmount);
            listItem = parseItems(items);
            trxDiscountHolder = parseTransactionDiscount(transactionDiscount);
            listPricing = parsePricing(pricings);
            listPayment = parsePayment(payments);
            cDateTime = DATE_FORMAT.parse(clientDateTime);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        HashMap<String, Object> validateMerchantOutletUIDAndOperatorResult = validateMerchantOutletUIDAndOperator(merchantCode,
                userName, outletIdLong, uid, nowDate);
        if (!validateMerchantOutletUIDAndOperatorResult.get(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return new JSONObject(validateMerchantOutletUIDAndOperatorResult);
        }
        Long merchantId = (Long) validateMerchantOutletUIDAndOperatorResult.get(MERCHANT_ID_KEY);
        BigDecimal merchantFee = (BigDecimal) validateMerchantOutletUIDAndOperatorResult.get(MERCHANT_FEE_KEY);
        Long operatorId = (Long) validateMerchantOutletUIDAndOperatorResult.get(OPERATOR_ID_KEY);
        InvoiceUID invoiceUID = (InvoiceUID) validateMerchantOutletUIDAndOperatorResult.get(INVOICE_UID_KEY);

        HashMap<String, Object> validateAllPaymentsResults = validateAllPayments(listPayment, nowDate);
        if (!validateAllPaymentsResults.get(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return new JSONObject(validateAllPaymentsResults);
        }
        List<TransactionMultiPaymentHolder> newListPayment = (List<TransactionMultiPaymentHolder>) validateAllPaymentsResults.get(PAYMENTS_KEY);

        BigDecimal invoiceAmountBD = BigDecimal.valueOf(invoiceAmountDouble);

        BigDecimal totalAllPaymentAmount = new BigDecimal(0);
        for (TransactionMultiPaymentHolder newPayment : newListPayment) {
            totalAllPaymentAmount = totalAllPaymentAmount.add(newPayment.getAmount());
        }
        if (totalAllPaymentAmount.compareTo(invoiceAmountBD) != 0) {
            response.put(STATUS_KEY, ResponseStatusEnum.SUM_OF_ALL_TRANSACTION_AMOUNT_DOES_NOT_EQUAL_INVOICE_AMOUNT.getResponseStatus());
            return response;
        }

        HashMap<String, Object> insertInvoiceResponse = insertInvoice(invoiceAmountBD, merchantFee,
                nowDate, merchantId, operatorId, outletIdLong, tableNumber, listItem, trxDiscountHolder,
                listPricing, cDateTime);
        Long invoiceId = (Long) insertInvoiceResponse.get(INVOICE_ID_KEY);
        String invoiceNumber = (String) insertInvoiceResponse.get(INVOICE_NUMBER_KEY);
        InvoiceStatusEnum invoiceStatus = (InvoiceStatusEnum) insertInvoiceResponse.get(INVOICE_STATUS_KEY);

        List<TransactionMultiPaymentHolder> listPaymentResponse;
        try {
            listPaymentResponse = processAllPayment(newListPayment, invoiceId, merchantId, nowDate,
                    customerName, customerEmail, customerPhone, cDateTime);
        } catch (OptimisticLockException e) {
            context.setRollbackOnly();
            response.put(STATUS_KEY, ResponseStatusEnum.CONFLICT_DATA_ACCESS.getResponseStatus());
            return response;
        }

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
        response.put(DETAIL_STATUS_KEY, listPaymentResponse);
        response.put(INVOICE_STATUS_KEY, invoiceStatus);
        response.put(INVOICE_NUMBER_KEY, invoiceNumber);
        response.put(CLIENT_INVOICE_ID_KEY, clientInvoiceId);

        return response;
    }

}
