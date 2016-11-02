/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb.payment;

import id.hardana.ejb.inventory.InventoryBean;
import id.hardana.ejb.merchantweb.inventory.extension.ItemRequestHolder;
import id.hardana.ejb.merchantweb.log.LoggingInterceptor;
import id.hardana.ejb.merchantweb.tools.CodeGenerator;
import id.hardana.entity.inventory.enums.InventoryLogTypeEnum;
import id.hardana.entity.invoice.Invoice;
import id.hardana.entity.invoice.InvoiceItemDiscount;
import id.hardana.entity.invoice.InvoiceItems;
import id.hardana.entity.invoice.InvoicePricing;
import id.hardana.entity.invoice.InvoiceReversal;
import id.hardana.entity.invoice.InvoiceTransactionDiscount;
import id.hardana.entity.invoice.enums.InvoiceStatusEnum;
import id.hardana.entity.profile.card.Card;
import id.hardana.entity.profile.enums.CardStatusEnum;
import id.hardana.entity.profile.enums.DiscountApplyTypeEnum;
import id.hardana.entity.profile.enums.DiscountCalculationTypeEnum;
import id.hardana.entity.profile.enums.DiscountValueTypeEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Operator;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.TransactionCardHistory;
import id.hardana.entity.transaction.TransactionCardHistoryPhysic;
import id.hardana.entity.transaction.TransactionCardHistoryServer;
import id.hardana.entity.transaction.TransactionMerchantHistory;
import id.hardana.entity.transaction.TransactionPayment;
import id.hardana.entity.transaction.TransactionTbl;
import id.hardana.entity.transaction.enums.TransactionPaymentTypeEnum;
import id.hardana.entity.transaction.enums.TransactionTypeEnum;
import java.math.BigDecimal;
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
import org.json.JSONObject;

/**
 *
 * @author Arya
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class PaymentReversalBean {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final String STATUS_KEY = "status";
    private final String REVERSAL_TYPE = "reversalType";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    @EJB
    private InventoryBean inventoryBean;
    private String transactionId = "";
    @Resource
    private EJBContext context;

    public JSONObject paymentReversal(String invoiceNumber) {
        JSONObject response = new JSONObject();
        Long invoiceIdLong = null;
        List<Invoice> invoiceList = getInvoiceList(invoiceNumber);
        for (Invoice invoice : invoiceList) {
            invoiceIdLong = Long.parseLong(invoice.getId());
        }

        List<TransactionPayment> transactionPaymentList = getTransactionPaymentList(invoiceIdLong);
        if (!transactionPaymentList.isEmpty()) {
            for (TransactionPayment transactionPayment : transactionPaymentList) {
                if (transactionPayment.getType() == TransactionPaymentTypeEnum.CASHCARD) {
                    transactionId = transactionPayment.getTransactionId();
                    response = paymentReversalCashCard(invoiceNumber);
                } else { //if (transactionPayment.getType() == TransactionPaymentTypeEnum.CASH || transactionPayment.getType() == TransactionPaymentTypeEnum.DEBITCARD) {
                    //response = paymentReversalCashAndOtherCard(invoiceNumber, transactionPayment.getType());
                    response.put(STATUS_KEY, ResponseStatusEnum.UNSUPPORTED_REVERSAL_TRANSACTION.getResponseStatus());
                }
            }
        } else {
            response.put(STATUS_KEY, ResponseStatusEnum.TRANSACTION_NOT_FOUND.getResponseStatus());
        }

        return response;
    }

    private List<Invoice> getInvoiceList(String invoiceNumber) {
        List<Invoice> retList = em.createQuery("SELECT i FROM Invoice i WHERE i.number = :number AND i.status = :status", Invoice.class)
                .setParameter("number", invoiceNumber)
                .setParameter("status", InvoiceStatusEnum.PAID)
                .getResultList();
        return retList;
    }

    private List<InvoiceItems> getInvoiceItemList(Long invoiceIdLong) {
        List<InvoiceItems> returnListItem = em.createNamedQuery("InvoiceItems.findBySingleInvoiceId", InvoiceItems.class)
                .setParameter("invoiceId", invoiceIdLong)
                .getResultList();
        return returnListItem;
    }

    private List<TransactionPayment> getTransactionPaymentList(Long invoiceIdLong) {
        List<TransactionPayment> retTransactionPaymentList = em.createQuery("SELECT t FROM TransactionPayment t WHERE t.invoiceId = :invoiceId "
                + "AND t.type != :reversalCash "
                + "AND t.type != :reversalCreditCardEdc "
                + "AND t.type != :reversalCashCard "
                + "AND t.type != :reversalDebitCard ", TransactionPayment.class)
                .setParameter("invoiceId", invoiceIdLong)
                .setParameter("reversalCash", TransactionPaymentTypeEnum.REVERSAL_CASH)
                .setParameter("reversalCreditCardEdc", TransactionPaymentTypeEnum.REVERSAL_CREDITCARDEDC)
                .setParameter("reversalCashCard", TransactionPaymentTypeEnum.REVERSAL_CASH_CARD)
                .setParameter("reversalDebitCard", TransactionPaymentTypeEnum.REVERSAL_DEBITCARD)
                .getResultList();
        return retTransactionPaymentList;
    }

    private JSONObject paymentReversalCashCard(String invoiceNumber) {
        JSONObject response = new JSONObject();
        String merchantId = "";
        String amount = "";
        String outletId = "";
        String transactionFee = "";
        String operatorId = "";
        String clientDateTime = "";
        Long outletIdLong;
        Long invoiceIdLong = null;
        double amountDouble;
        double transactionFeeDouble;
        double totalAmountDouble;
        Date cDateTime;
        List<Invoice> listInvoice = getInvoiceList(invoiceNumber);
        List<ItemRequestHolder> listItemHolderRequest = new ArrayList<>();

        if (!listInvoice.isEmpty()) {
            for (Invoice invoice : listInvoice) {
                merchantId = invoice.getMerchantId();
                amount = invoice.getAmount();
                transactionFee = invoice.getFee();
                outletId = invoice.getOutletId();
                operatorId = invoice.getOperatorId();
                invoiceIdLong = Long.parseLong(invoice.getId());
                clientDateTime = invoice.getClientDateTime();
            }
            try {
                outletIdLong = Long.parseLong(outletId);
                amountDouble = Double.parseDouble(amount);
                transactionFeeDouble = Double.parseDouble(transactionFee);
                totalAmountDouble = amountDouble + transactionFeeDouble;
                cDateTime = DATE_FORMAT.parse(clientDateTime);
            } catch (Exception e) {
                response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
                return response;
            }
            BigDecimal amountBD = BigDecimal.valueOf(amountDouble);
            BigDecimal transactionFeeBD = BigDecimal.valueOf(transactionFeeDouble);
            BigDecimal totalAmountBD = BigDecimal.valueOf(totalAmountDouble);

            Date nowDate = new Date();
            TransactionCardHistory retTransactionCardHistory = em.createNamedQuery("TransactionCardHistory.findByTransactionId", TransactionCardHistory.class)
                    .setParameter("transactionId", Long.parseLong(transactionId))
                    .getSingleResult();

            Card card = em.createQuery("SELECT c FROM Card c WHERE c.id = :id", Card.class)
                    .setParameter("id", Long.parseLong(retTransactionCardHistory.getCardId()))
                    .getSingleResult();
            if (card != null) {
                em.refresh(card);
            } else {
                response.put(STATUS_KEY, ResponseStatusEnum.CARD_NOT_FOUND.getResponseStatus());
                return response;
            }

            Long cardIdLong = Long.parseLong(card.getId());
            CardStatusEnum cardStatus = card.getStatusId();
            if (cardStatus.equals(CardStatusEnum.DISCARDED)) {
                response.put(STATUS_KEY, ResponseStatusEnum.CARD_DISCARDED.getResponseStatus());
                return response;
            } else if (cardStatus.equals(CardStatusEnum.INACTIVE)) {
                response.put(STATUS_KEY, ResponseStatusEnum.CARD_INACTIVE.getResponseStatus());
                return response;
            }

            double balanceDouble = Double.parseDouble(card.getBalance());
            BigDecimal balance = BigDecimal.valueOf(balanceDouble);
            BigDecimal balanceAfterCredit = balance.add(totalAmountBD);
            if (balanceAfterCredit.compareTo(BigDecimal.ZERO) < 0) {
                response.put(STATUS_KEY, ResponseStatusEnum.CARD_INSUFICIENT_BALANCE.getResponseStatus());
                return response;
            }

            Long merchantIdLong = Long.parseLong(merchantId);
            List<Merchant> merchantSearch = em.createNamedQuery("Merchant.findById", Merchant.class)
                    .setParameter("id", merchantIdLong)
                    .getResultList();
            if (merchantSearch.isEmpty()) {
                response.put(STATUS_KEY, ResponseStatusEnum.INVALID_MERCHANT_CODE.getResponseStatus());
                return response;
            }

            Merchant merchant = merchantSearch.get(0);
            double merchantFeeDouble = Double.parseDouble(merchant.getMerchantFee());
            BigDecimal merchantFeeBD = BigDecimal.valueOf(merchantFeeDouble);

            List<Operator> operatorsSearch = em.createNamedQuery("Operator.findByMerchantId", Operator.class)
                    .setParameter("merchantId", merchantIdLong)
                    .getResultList();
            if (operatorsSearch.isEmpty()) {
                response.put(STATUS_KEY, ResponseStatusEnum.INVALID_USERNAME.getResponseStatus());
                return response;
            }

            try {
                card.setBalance(balanceAfterCredit);
                em.merge(card);
            } catch (OptimisticLockException e) {
                context.setRollbackOnly();
                response.put(STATUS_KEY, ResponseStatusEnum.CONFLICT_DATA_ACCESS.getResponseStatus());
                return response;
            }

            String invoiceNo = CodeGenerator.generateReferenceNumber();

            Invoice newInvoice = new Invoice();
            newInvoice.setAmount(amountBD);
            newInvoice.setDateTime(nowDate);
            newInvoice.setFee(merchantFeeBD);
            newInvoice.setMerchantId(merchantIdLong);
            newInvoice.setNumber(invoiceNo);
            newInvoice.setOperatorId(Long.parseLong(operatorId));
            newInvoice.setOutletId(outletIdLong);
            newInvoice.setStatus(InvoiceStatusEnum.REVERSAL);
            newInvoice.setClientDateTime(cDateTime);
            em.persist(newInvoice);
            em.flush();

            InvoiceReversal newInvoiceReversal = new InvoiceReversal();
            newInvoiceReversal.setInvoiceId(Long.parseLong(newInvoice.getId()));
            newInvoiceReversal.setInvoiceIdReference(invoiceIdLong);
            em.persist(newInvoiceReversal);
            em.flush();

            Long invoiceIdLongNew = Long.parseLong(newInvoice.getId());
            // save item and discount item.
            List<InvoiceItems> listItem = getInvoiceItemList(invoiceIdLong);
            for (InvoiceItems item : listItem) {
                InvoiceItems invoiceItem = new InvoiceItems();
                invoiceItem.setInvoiceId(invoiceIdLongNew);
                invoiceItem.setItemId(Long.parseLong(item.getItemId()));
                invoiceItem.setItemName(item.getItemName());
                invoiceItem.setItemQuantity(Integer.parseInt(item.getItemQuantity()));
                invoiceItem.setItemSalesPrice(BigDecimal.valueOf(Double.parseDouble(item.getItemSalesPrice())));
                invoiceItem.setItemSubTotal(BigDecimal.valueOf(Double.parseDouble(item.getItemSubTotal())));
                invoiceItem.setItemSupplyPrice(BigDecimal.valueOf(Double.parseDouble(item.getItemSupplyPrice())));
                em.persist(invoiceItem);
                em.flush();

                // save item discount
                List<InvoiceItemDiscount> listItemDiscount = em.createNamedQuery("InvoiceItemDiscount.findByInvoiceItemId", InvoiceItemDiscount.class)
                        .setParameter("invoiceItemId", invoiceIdLong)
                        .getResultList();

                if (!listItemDiscount.isEmpty()) {
                    for (InvoiceItemDiscount discount : listItemDiscount) {

                        Long invoiceItemId = Long.valueOf(invoiceItem.getId());
                        discount.setInvoiceItemId(invoiceItemId);
                        discount.setDiscountId(discount.getDiscountId());
                        discount.setDiscountName(discount.getDiscountName());
                        discount.setDiscountDescription(discount.getDiscountDescription());
                        discount.setDiscountValue(discount.getDiscountValue());

                        DiscountValueTypeEnum valueType = discount.getDiscountValueType();
                        discount.setDiscountValueType(valueType);

                        DiscountApplyTypeEnum applyType = discount.getDiscountApplyType();
                        discount.setDiscountApplyType(applyType);

                        DiscountCalculationTypeEnum calcType = discount.getDiscountCalculationType();
                        discount.setDiscountCalculationType(calcType);
                        discount.setDiscountAmount(discount.getDiscountAmount());

                        em.persist(discount);
                    }
                }

                ItemRequestHolder itemHolder;
                itemHolder = new ItemRequestHolder(null, null, " ");
                itemHolder.setOutletId(outletIdLong);
                itemHolder.setItemId(Long.parseLong(item.getItemId()));
                itemHolder.setItemName(item.getItemName());
                itemHolder.setStock(Long.parseLong(item.getItemQuantity()));
                listItemHolderRequest.add(itemHolder);
            }

            // save transaction discount 
            List<InvoiceTransactionDiscount> listTransactionDiscount = em.createNamedQuery("InvoiceTransactionDiscount.findByInvoiceId", InvoiceTransactionDiscount.class)
                    .setParameter("invoiceId", invoiceIdLong)
                    .getResultList();

            if (!listTransactionDiscount.isEmpty()) {
                for (InvoiceTransactionDiscount discount : listTransactionDiscount) {
                    discount.setInvoiceId(invoiceIdLongNew);

                    discount.setDiscountId(discount.getDiscountId());
                    discount.setDiscountName(discount.getDiscountName());
                    discount.setDiscountDescription(discount.getDiscountDescription());
                    discount.setDiscountValue(discount.getDiscountValue());

                    DiscountValueTypeEnum valueType = discount.getDiscountValueType();
                    discount.setDiscountValueType(valueType);

                    DiscountApplyTypeEnum applyType = discount.getDiscountApplyType();
                    discount.setDiscountApplyType(applyType);

                    DiscountCalculationTypeEnum calcType = discount.getDiscountCalculationType();
                    discount.setDiscountCalculationType(calcType);
                    discount.setDiscountAmount(discount.getDiscountAmount());

                    em.persist(discount);
                }
            }

            List<InvoicePricing> pricingList = em.createNamedQuery("InvoicePricing.findByInvoiceId", InvoicePricing.class)
                    .setParameter("invoiceId", invoiceIdLong)
                    .getResultList();

            if (!pricingList.isEmpty()) {
                for (InvoicePricing pricing : pricingList) {
                    InvoicePricing invoicePricing = new InvoicePricing();
                    invoicePricing.setInvoiceId(invoiceIdLongNew);
                    invoicePricing.setPricingAmount(BigDecimal.valueOf(Double.parseDouble(pricing.getPricingAmount())));
                    invoicePricing.setPricingId(Long.parseLong(pricing.getPricingId()));
                    invoicePricing.setPricingType(pricing.getPricingType());
                    invoicePricing.setPricingValue(BigDecimal.valueOf(Double.parseDouble(pricing.getPricingValue())));
                    invoicePricing.setPricingLevel(pricing.getPricingLevel());

                    em.persist(invoicePricing);
                }
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

            TransactionTbl newTransaction = new TransactionTbl();
            newTransaction.setAmount(amountBD);
            newTransaction.setDateTime(nowDate);
            newTransaction.setFee(transactionFeeBD);
            newTransaction.setStatus(ResponseStatusEnum.SUCCESS);
            newTransaction.setTotalAmount(totalAmountBD);
            newTransaction.setType(TransactionTypeEnum.PAYMENT);
            newTransaction.setClientDateTime(cDateTime);
            newTransaction.setReferenceNumber(referenceNumberGenerate);
            em.persist(newTransaction);
            em.flush();

            Long transactionIdLong = Long.parseLong(newTransaction.getId());
            TransactionPayment newTransactionPayment = new TransactionPayment();
            newTransactionPayment.setInvoiceId(invoiceIdLongNew);
            newTransactionPayment.setSourceId(cardIdLong);
            newTransactionPayment.setTransactionId(transactionIdLong);
            newTransactionPayment.setType(TransactionPaymentTypeEnum.REVERSAL_CASH_CARD);
            em.persist(newTransactionPayment);
            em.flush();

            //Read currentBalanceP
            BigDecimal currBalP = new BigDecimal(retTransactionCardHistory.getCurrentBalanceP());

            TransactionCardHistory cardHistory = new TransactionCardHistory();
            cardHistory.setCardId(cardIdLong);
            cardHistory.setTransactionId(transactionIdLong);
            cardHistory.setCurrentBalanceP(currBalP);
            cardHistory.setMovementP(BigDecimal.ZERO);
            cardHistory.setCurrentBalanceS(balanceAfterCredit);
            cardHistory.setMovementS(totalAmountBD);
            cardHistory.setCardStatus(cardStatus);
            em.persist(cardHistory);

            //add new row at TransactionCardHistoryPhysic
            TransactionCardHistoryPhysic cardHistoryPhysic = new TransactionCardHistoryPhysic();
            cardHistoryPhysic.setCardId(cardIdLong);
            cardHistoryPhysic.setClientDateTime(cDateTime);
            cardHistoryPhysic.setCurrentBalanceP(currBalP);
            cardHistoryPhysic.setMovementP(BigDecimal.ZERO);
            cardHistoryPhysic.setTransactionId(transactionIdLong);
            em.persist(cardHistoryPhysic);

            TransactionCardHistoryServer cardHistoryServer = new TransactionCardHistoryServer();
            cardHistoryServer.setCardId(cardIdLong);
            cardHistoryServer.setCurrentBalanceS(balanceAfterCredit);
            cardHistoryServer.setDateTime(nowDate);
            cardHistoryServer.setMovementS(totalAmountBD);
            cardHistoryServer.setTransactionId(transactionIdLong);
            em.persist(cardHistoryServer);

            TransactionMerchantHistory merchantHistory = new TransactionMerchantHistory();
            merchantHistory.setMerchantId(merchantIdLong);
            merchantHistory.setRefType(TransactionMerchantHistory.TRANSACTION);
            merchantHistory.setRefId(transactionIdLong);
            em.persist(merchantHistory);

            inventoryBean.changeStock(merchantIdLong, outletIdLong, Long.parseLong(operatorId), listItemHolderRequest,
                    InventoryLogTypeEnum.REVERSAL, invoiceIdLongNew, cDateTime);

            response.put(REVERSAL_TYPE, "REVERSAL CASH CARD");
            response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
            return response;
        } else {
            response.put(STATUS_KEY, ResponseStatusEnum.INVOICE_NOT_FOUND.getResponseStatus());
            return response;
        }
    }

    private JSONObject paymentReversalCashAndOtherCard(String invoiceNumber, TransactionPaymentTypeEnum transactionType) {
        JSONObject response = new JSONObject();
        String merchantId = "";
        String amount = "";
        String outletId = "";
        String transactionFee = "";
        String totalAmount = "";
        String operatorId = "";
        String clientDateTime = "";
        Long invoiceIdLong = null;
        double amountDouble;
        double transactionFeeDouble;
        double totalAmountDouble;
        Long outletIdLong;
        Date cDateTime;
        List<ItemRequestHolder> listItemHolderRequest = new ArrayList<>();
        List<Invoice> listInvoice = getInvoiceList(invoiceNumber);

        if (!listInvoice.isEmpty()) {
            for (Invoice invoice : listInvoice) {
                merchantId = invoice.getMerchantId();
                amount = invoice.getAmount();
                transactionFee = invoice.getFee();
                outletId = invoice.getOutletId();
                operatorId = invoice.getOperatorId();
                invoiceIdLong = Long.parseLong(invoice.getId());
                clientDateTime = invoice.getClientDateTime();
            }
            try {
                outletIdLong = Long.parseLong(outletId);
                amountDouble = Double.parseDouble(amount);
                transactionFeeDouble = Double.parseDouble(transactionFee);
                totalAmountDouble = Double.parseDouble(totalAmount);
                cDateTime = DATE_FORMAT.parse(clientDateTime);
            } catch (Exception e) {
                response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
                return response;
            }

            BigDecimal amountBD = BigDecimal.valueOf(amountDouble);
            BigDecimal transactionFeeBD = BigDecimal.valueOf(transactionFeeDouble);
            BigDecimal totalAmountBD = BigDecimal.valueOf(totalAmountDouble);
            Long merchantIdLong = Long.parseLong(merchantId);

            List<Merchant> merchantSearch = em.createNamedQuery("Merchant.findById", Merchant.class)
                    .setParameter("id", merchantIdLong)
                    .getResultList();
            if (merchantSearch.isEmpty()) {
                response.put(STATUS_KEY, ResponseStatusEnum.INVALID_MERCHANT_CODE.getResponseStatus());
                return response;
            }

            Merchant merchant = merchantSearch.get(0);
            double merchantFeeDouble = Double.parseDouble(merchant.getMerchantFee());
            BigDecimal merchantFeeBD = BigDecimal.valueOf(merchantFeeDouble);

            Date nowDate = new Date();
            String invoiceNo = CodeGenerator.generateReferenceNumber();

            Invoice newInvoice = new Invoice();
            newInvoice.setAmount(amountBD);
            newInvoice.setDateTime(nowDate);
            newInvoice.setFee(merchantFeeBD);
            newInvoice.setMerchantId(merchantIdLong);
            newInvoice.setNumber(invoiceNo);
            newInvoice.setOperatorId(Long.parseLong(operatorId));
            newInvoice.setOutletId(outletIdLong);
            newInvoice.setStatus(InvoiceStatusEnum.REVERSAL);
            newInvoice.setClientDateTime(cDateTime);
            em.persist(newInvoice);
            em.flush();

            InvoiceReversal newInvoiceReversal = new InvoiceReversal();
            newInvoiceReversal.setInvoiceId(Long.parseLong(newInvoice.getId()));
            newInvoiceReversal.setInvoiceIdReference(invoiceIdLong);
            em.persist(newInvoiceReversal);
            em.flush();

            Long invoiceIdLongNew = Long.parseLong(newInvoice.getId());
            // save item and discount item.

            List<InvoiceItems> listItem = getInvoiceItemList(invoiceIdLong);
            if (!listItem.isEmpty()) {
                for (InvoiceItems item : listItem) {
                    InvoiceItems invoiceItem = new InvoiceItems();
                    invoiceItem.setInvoiceId(invoiceIdLongNew);
                    invoiceItem.setItemId(Long.parseLong(item.getId()));
                    invoiceItem.setItemName(item.getItemName());
                    invoiceItem.setItemQuantity(Integer.parseInt(item.getItemQuantity()));
                    invoiceItem.setItemSalesPrice(BigDecimal.valueOf(Double.parseDouble(item.getItemSalesPrice())));
                    invoiceItem.setItemSubTotal(BigDecimal.valueOf(Double.parseDouble(item.getItemSubTotal())));
                    invoiceItem.setItemSupplyPrice(BigDecimal.valueOf(Double.parseDouble(item.getItemSupplyPrice())));

                    em.persist(invoiceItem);
                    em.flush();

                    ItemRequestHolder itemHolder;
                    itemHolder = new ItemRequestHolder(null, null, " ");
                    itemHolder.setOutletId(outletIdLong);
                    itemHolder.setItemId(Long.parseLong(item.getItemId()));
                    itemHolder.setItemName(item.getItemName());
                    itemHolder.setStock(Long.parseLong(item.getItemQuantity()));
                    listItemHolderRequest.add(itemHolder);

                    // save item discount
                    List<InvoiceItemDiscount> listItemDiscount = em.createNamedQuery("InvoiceItemDiscount.findByInvoiceItemId", InvoiceItemDiscount.class)
                            .setParameter("invoiceItemId", invoiceIdLong)
                            .getResultList();

                    if (!listItemDiscount.isEmpty()) {
                        for (InvoiceItemDiscount discount : listItemDiscount) {

                            Long invoiceItemId = Long.valueOf(invoiceItem.getId());
                            discount.setInvoiceItemId(invoiceItemId);

                            discount.setDiscountId(discount.getDiscountId());
                            discount.setDiscountName(discount.getDiscountName());
                            discount.setDiscountDescription(discount.getDiscountDescription());
                            discount.setDiscountValue(discount.getDiscountValue());

                            DiscountValueTypeEnum valueType = discount.getDiscountValueType();
                            discount.setDiscountValueType(valueType);

                            DiscountApplyTypeEnum applyType = discount.getDiscountApplyType();
                            discount.setDiscountApplyType(applyType);

                            DiscountCalculationTypeEnum calcType = discount.getDiscountCalculationType();
                            discount.setDiscountCalculationType(calcType);
                            discount.setDiscountAmount(discount.getDiscountAmount());

                            em.persist(discount);
                        }
                    }

                    TransactionTbl newTransaction = new TransactionTbl();
                    newTransaction.setAmount(amountBD);
                    newTransaction.setDateTime(nowDate);
                    newTransaction.setFee(transactionFeeBD);
                    newTransaction.setStatus(ResponseStatusEnum.SUCCESS);
                    newTransaction.setTotalAmount(totalAmountBD);
                    newTransaction.setType(TransactionTypeEnum.PAYMENT);
                    newTransaction.setClientDateTime(cDateTime);
                    em.persist(newTransaction);
                    em.flush();

                    Long transactionId = Long.parseLong(newTransaction.getId());
                    TransactionPayment newTransactionPayment = new TransactionPayment();
                    newTransactionPayment.setInvoiceId(invoiceIdLongNew);
                    newTransactionPayment.setTransactionId(transactionId);
                    newTransactionPayment.setSourceId(null);

                    if (transactionType == TransactionPaymentTypeEnum.CASH) {
                        newTransactionPayment.setType(TransactionPaymentTypeEnum.REVERSAL_CASH);
                    } else if (transactionType == TransactionPaymentTypeEnum.CREDITCARDEDC) {
                        newTransactionPayment.setType(TransactionPaymentTypeEnum.REVERSAL_CREDITCARDEDC);
                    } else if (transactionType == TransactionPaymentTypeEnum.DEBITCARD) {
                        newTransactionPayment.setType(TransactionPaymentTypeEnum.REVERSAL_DEBITCARD);
                    }
                    em.persist(newTransactionPayment);
                    em.flush();

                    TransactionMerchantHistory merchantHistory = new TransactionMerchantHistory();
                    merchantHistory.setMerchantId(Long.parseLong(merchantId));
                    merchantHistory.setRefType(TransactionMerchantHistory.TRANSACTION);
                    merchantHistory.setRefId(transactionId);
                    em.persist(merchantHistory);

                    inventoryBean.changeStock(merchantIdLong, outletIdLong, Long.parseLong(operatorId), listItemHolderRequest,
                            InventoryLogTypeEnum.REVERSAL, invoiceIdLongNew, cDateTime);
                    listItemHolderRequest.clear();
                }
            } else {
                response.put(STATUS_KEY, ResponseStatusEnum.INVOICE_NOT_FOUND.getResponseStatus());
                return response;
            }
            if (transactionType == TransactionPaymentTypeEnum.CASH) {
                response.put(REVERSAL_TYPE, "REVERSAL_CASH");
            } else if (transactionType == TransactionPaymentTypeEnum.CREDITCARDEDC) {
                response.put(REVERSAL_TYPE, "REVERSAL_CREDITCARDEDC");
            }

            response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        } else {
            response.put(STATUS_KEY, ResponseStatusEnum.INVOICE_NOT_FOUND.getResponseStatus());
            return response;
        }
        return response;
    }

}
