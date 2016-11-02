/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.payment;

import id.hardana.ejb.mpos.log.LoggingInterceptor;
import id.hardana.ejb.system.tools.CodeGenerator;
import id.hardana.entity.bill.Bill;
import id.hardana.entity.bill.BillScheduler;
import id.hardana.entity.bill.enums.BillExecutionTypeEnum;
import id.hardana.entity.bill.enums.BillScheduleStatusEnum;
import id.hardana.entity.bill.enums.BillStatusEnum;
import id.hardana.entity.invoice.Invoice;
import id.hardana.entity.invoice.InvoicePricing;
import id.hardana.entity.invoice.enums.InvoiceStatusEnum;
import id.hardana.entity.profile.card.Card;
import id.hardana.entity.profile.enums.CardStatusEnum;
import id.hardana.entity.profile.enums.OutletStatusEnum;
import id.hardana.entity.profile.enums.PricingTypeEnum;
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
import id.hardana.entity.transaction.enums.TransactionPaymentTypeEnum;
import id.hardana.entity.transaction.enums.TransactionTypeEnum;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
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
 * @author Trisna
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class CardSinglePayInvoiceBean {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final String STATUS_KEY = "status";
    private final String INVOICE_STATUS_KEY = "invoiceStatus";
    private final String INVOICE_NUMBER_KEY = "invoiceNumber";
    private final String CLIENT_INVOICE_ID_KEY = "clientInvoiceId";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    @Resource
    private EJBContext context;

    public JSONObject payInvoice(String merchantCode, String userName, String outletId,
            String amount, String transactionFee, String totalAmount,
            String clientTransRefnum, String cardId, String customerName,
            String customerEmail, String customerPhone, String debetAmount,
            String cardFinalBalance, String clientInvoiceId, String invoiceNumber,
            String clientDateTime) {
        JSONObject response = new JSONObject();

        Long outletIdLong;
        double amountDouble;
        double transactionFeeDouble;
        double totalAmountDouble;
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
            response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
            response.put(INVOICE_STATUS_KEY, invoiceSearch.getStatus());
            response.put(INVOICE_NUMBER_KEY, invoiceSearch.getNumber());
            return response;
        }
        BigDecimal invoiceAmount = new BigDecimal(invoiceSearch.getAmount());

        Long invoiceId = Long.parseLong(invoiceSearch.getId());
        boolean isFromBillPayment = false;
        Bill bill = null;
        BigDecimal totalLateFeeAmount = BigDecimal.ZERO;
        BillExecutionTypeEnum billExecType = BillExecutionTypeEnum.NOW;
        List<Bill> listBill = em.createNamedQuery("Bill.findByInvoiceId", Bill.class)
                .setParameter("invoiceId", invoiceId)
                .getResultList();
        if (!listBill.isEmpty()) {
            isFromBillPayment = true;
            bill = listBill.get(0);
            totalLateFeeAmount = new BigDecimal(bill.getTotalLateFeeAmount());
            billExecType = bill.getExecutionType();
        }

        invoiceAmount = invoiceAmount.add(totalLateFeeAmount);
        if (invoiceAmount.compareTo(amountBD) != 0) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_AMOUNT_TO_PAY.getResponseStatus());
            return response;
        }

        if (isFromBillPayment) {
            if (totalLateFeeAmount.compareTo(BigDecimal.ZERO) > 0) {
                InvoicePricing invoicePricing = new InvoicePricing();
                invoicePricing.setInvoiceId(invoiceId);
                invoicePricing.setPricingAmount(totalLateFeeAmount);
                invoicePricing.setPricingId(null);
                invoicePricing.setPricingLevel(1);
                invoicePricing.setPricingType(PricingTypeEnum.RUPIAH);
                invoicePricing.setPricingValue(totalLateFeeAmount);
                em.persist(invoicePricing);

                invoiceSearch.setAmount(invoiceAmount);
            }
        }

        try {
            card.setBalance(balanceAfterDebet);
            em.merge(card);
        } catch (OptimisticLockException e) {
            context.setRollbackOnly();
            response.put(STATUS_KEY, ResponseStatusEnum.CONFLICT_DATA_ACCESS.getResponseStatus());
            return response;
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

        invoiceSearch.setOperatorId(operatorId);
        invoiceSearch.setStatus(InvoiceStatusEnum.PAID);
        em.merge(invoiceSearch);

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

        if (isFromBillPayment) {
            bill.setPaidDate(nowDate);
            bill.setStatus(BillStatusEnum.PAID);
            em.merge(bill);

            if (billExecType.equals(BillExecutionTypeEnum.SCHEDULE)) {
                List<BillScheduler> listBillScheduler = em.createNamedQuery("BillScheduler.findByBillId",
                        BillScheduler.class)
                        .setParameter("billId", Long.parseLong(bill.getId()))
                        .getResultList();
                BillScheduler billScheduler = listBillScheduler.get(0);
                billScheduler.setScheduleStatus(BillScheduleStatusEnum.CLOSED);
                em.merge(billScheduler);
            }
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(INVOICE_STATUS_KEY, invoiceSearch.getStatus());
        response.put(INVOICE_NUMBER_KEY, invoiceSearch.getNumber());
        response.put(CLIENT_INVOICE_ID_KEY, clientInvoiceId);

        return response;
    }

}
