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
import id.hardana.entity.profile.enums.OutletStatusEnum;
import id.hardana.entity.profile.enums.PricingTypeEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Operator;
import id.hardana.entity.profile.merchant.Outlet;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.TransactionMerchantHistory;
import id.hardana.entity.transaction.TransactionPayment;
import id.hardana.entity.transaction.TransactionPaymentCreditCard;
import id.hardana.entity.transaction.TransactionPaymentCustomerInfo;
import id.hardana.entity.transaction.TransactionTbl;
import id.hardana.entity.transaction.enums.TransactionFeeTypeEnum;
import id.hardana.entity.transaction.enums.TransactionPaymentTypeEnum;
import id.hardana.entity.transaction.enums.TransactionTypeEnum;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
@Interceptors({LoggingInterceptor.class})

public class OtherSinglePayInvoiceBean {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final String STATUS_KEY = "status";
    private final String INVOICE_STATUS_KEY = "invoiceStatus";
    private final String INVOICE_NUMBER_KEY = "invoiceNumber";
    private final String CLIENT_INVOICE_ID_KEY = "clientInvoiceId";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    private final int CASH_PAYMENT = 0;
    private final int CREDITCARDEDC_PAYMENT = 1;
    private final int DEBITCARD_PAYMENT = 2;

    public JSONObject cashPay(String merchantCode, String userName, String outletId,
            String amount, String transactionFee, String totalAmount, String clientTransRefnum,
            String customerName, String customerEmail, String customerPhone,
            String clientInvoiceId, String invoiceNumber, String clientDateTime) {
        return payInvoice(merchantCode, userName, outletId, amount, transactionFee, totalAmount,
                clientTransRefnum, customerName, customerEmail, customerPhone, CASH_PAYMENT,
                null, null, null, clientInvoiceId, invoiceNumber, clientDateTime);
    }

    public JSONObject otherCardPay(String merchantCode, String userName, String outletId,
            String amount, String transactionFee, String totalAmount, String clientTransRefnum,
            String customerName, String customerEmail, String customerPhone,
            String cardType, String cardHolderName, String approvalCode,
            String clientInvoiceId, String invoiceNumber, String clientDateTime, String debitCredit) {

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

        return payInvoice(merchantCode, userName, outletId, amount, transactionFee, totalAmount,
                clientTransRefnum, customerName, customerEmail, customerPhone, debitCreditTag,
                cardType, cardHolderName, approvalCode, clientInvoiceId,
                invoiceNumber, clientDateTime);
    }

    private JSONObject payInvoice(String merchantCode, String userName, String outletId,
            String amount, String transactionFee, String totalAmount,
            String clientTransRefnum, String customerName, String customerEmail,
            String customerPhone, int paymentType, String cardType,
            String cardHolderName, String approvalCode,
            String clientInvoiceId, String invoiceNumber, String clientDateTime) {
        JSONObject response = new JSONObject();

        boolean isFromBillPayment = false;
//        if (transactionFee.equals("-") && totalAmount.equals("-")) {
//            isFromBillPayment = true;
//        }

        Long outletIdLong;
        double amountDouble;
        Date cDateTime;
        try {
            outletIdLong = Long.parseLong(outletId);
            amountDouble = Double.parseDouble(amount);
            cDateTime = DATE_FORMAT.parse(clientDateTime);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        BigDecimal amountBD = BigDecimal.valueOf(amountDouble);

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
        BigDecimal invoiceAmount = new BigDecimal(invoiceSearch.getAmount());
        Long invoiceId = Long.parseLong(invoiceSearch.getId());

        if (status.equals(InvoiceStatusEnum.CANCELED)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVOICE_CANCELED.getResponseStatus());
            return response;
        } else if (status.equals(InvoiceStatusEnum.PAID) || status.equals(InvoiceStatusEnum.UNFINISHED)) {
            response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
            response.put(INVOICE_STATUS_KEY, invoiceSearch.getStatus());
            response.put(INVOICE_NUMBER_KEY, invoiceSearch.getNumber());
            return response;
        }

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

        BigDecimal transactionFeeBD;
        BigDecimal totalAmountBD;
        if (isFromBillPayment) {
            try {
                if (paymentType == CASH_PAYMENT) {
                    transactionFeeBD = new BigDecimal(TransactionFeeTypeEnum.PAYMENTCASH.getFee(em));
                } else {
                    transactionFeeBD = new BigDecimal(TransactionFeeTypeEnum.PAYMENTCCEDC.getFee(em));
                }
                totalAmountBD = amountBD.add(transactionFeeBD);
            } catch (Exception e) {
                response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
                return response;
            }
        } else {
            try {
                transactionFeeBD = new BigDecimal(transactionFee);
                totalAmountBD = new BigDecimal(totalAmount);
            } catch (Exception e) {
                response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
                return response;
            }
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
