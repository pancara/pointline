/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.history.bill;

import com.google.gson.Gson;
import id.hardana.ejb.history.invoice.InvoiceSearchBean;
import id.hardana.ejb.system.validation.MerchantValidatorBean;
import id.hardana.entity.bill.Bill;
import id.hardana.entity.bill.enums.BillStatusEnum;
import id.hardana.entity.bill.enums.BillTypeEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.holder.bill.BillMerchantReportHolder;
import id.hardana.holder.bill.BillMerchantSummaryReportHolder;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
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

public class MerchantReportBillBean {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final String STATUS_KEY = "status";
    private final String DATA_KEY = "data";
    private final String COUNT_KEY = "count";
    private final String MERCHANT_KEY = "merchant";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    @EJB
    private MerchantValidatorBean merchantValidatorBean;
    @EJB
    private InvoiceSearchBean invoiceSearchBean;

    public JSONObject getInvoiceFromBill(String merchantCode, String billNumber, String invoiceNumber) {
        JSONObject response = new JSONObject();

        HashMap validateMerchantCodeResponse = merchantValidatorBean.validateMerchantCode(merchantCode);
        if (!validateMerchantCodeResponse.get(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return new JSONObject(validateMerchantCodeResponse);
        }
        Merchant merchant = (Merchant) validateMerchantCodeResponse.get(MERCHANT_KEY);
        Long merchantId = Long.parseLong(merchant.getId());

        List<Bill> listBill = em.createQuery("SELECT b FROM Bill b LEFT JOIN Invoice i ON b.invoiceId = i.id "
                + "WHERE i.number = :invoiceNumber AND b.number = :billNumber AND b.type = :type "
                + "AND b.creatorId = :merchantId", Bill.class)
                .setParameter("invoiceNumber", invoiceNumber)
                .setParameter("billNumber", billNumber)
                .setParameter("type", BillTypeEnum.MERCHANT_BILL)
                .setParameter("merchantId", merchantId)
                .getResultList();
        if (listBill.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_BILL_NUMBER.getResponseStatus());
            return response;
        }

        return invoiceSearchBean.getInvoiceFromBillMerchant(invoiceNumber, merchantId);
    }

    public JSONObject summaryReportBill(String merchantCode, String startCreatedDate, String endCreatedDate,
            String startDueDate, String endDueDate, String billName, String billNumber, String status,
            String payer, String info1, String info2, String info3, String payerName) {

        JSONObject response = new JSONObject();

        boolean isCreatedDate = false;
        boolean isDueDate = false;
        boolean isBillName = false;
        boolean isBillNumber = false;
        boolean isStatus = false;
        boolean isPayer = false;
        boolean isInfo1 = false;
        boolean isInfo2 = false;
        boolean isInfo3 = false;
        boolean isPayerName = false;

        if (startCreatedDate != null && endCreatedDate != null) {
            isCreatedDate = true;
        }
        if (startDueDate != null && endDueDate != null) {
            isDueDate = true;
        }
        if (billName != null) {
            isBillName = true;
        }
        if (billNumber != null) {
            isBillNumber = true;
        }
        if (status != null) {
            isStatus = true;
        }
        if (payer != null) {
            isPayer = true;
        }
        if (info1 != null) {
            isInfo1 = true;
        }
        if (info2 != null) {
            isInfo2 = true;
        }
        if (info3 != null) {
            isInfo3 = true;
        }
        if (payerName != null) {
            isPayerName = true;
        }

        Date cStartDueDate = null;
        Date cEndDueDate = null;
        Date cStartCreatedDate = null;
        Date cEndCreatedDate = null;
        List<BillStatusEnum> listOpenStatus = new ArrayList<>();
        List<BillStatusEnum> listClosedStatus = new ArrayList<>();
        List<String> listAccountPayer = new ArrayList<>();

        try {
            if (isCreatedDate) {
                cStartCreatedDate = DATE_FORMAT.parse(startCreatedDate);
                cEndCreatedDate = DATE_FORMAT.parse(endCreatedDate);
            }
            if (isDueDate) {
                cStartDueDate = DATE_FORMAT.parse(startDueDate);
                cEndDueDate = DATE_FORMAT.parse(endDueDate);
            }
            if (isStatus) {
                JSONArray statusArray = new JSONArray(status);
                for (int i = 0; i < statusArray.length(); i++) {
                    BillStatusEnum statusBill = BillStatusEnum.getBillStatus(statusArray.getInt(i));
                    if (statusBill == null) {
                        response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
                        return response;
                    }
                    if (statusBill.equals(BillStatusEnum.APPROVED)
                            || statusBill.equals(BillStatusEnum.WAITING_RESPONSE)) {
                        listOpenStatus.add(statusBill);
                    } else {
                        listClosedStatus.add(statusBill);
                    }
                }
            }
            if (isPayer) {
                JSONArray payerArray = new JSONArray(payer);
                for (int i = 0; i < payerArray.length(); i++) {
                    listAccountPayer.add(payerArray.getString(i));
                }
            }
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        HashMap validateMerchantCodeResponse = merchantValidatorBean.validateMerchantCode(merchantCode);
        if (!validateMerchantCodeResponse.get(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return new JSONObject(validateMerchantCodeResponse);
        }
        Merchant merchant = (Merchant) validateMerchantCodeResponse.get(MERCHANT_KEY);
        Long merchantId = Long.parseLong(merchant.getId());

        String billSummaryString = "SELECT COUNT(b.id), SUM(b.billAmount) "
                + "FROM Bill b LEFT JOIN PersonalInfo p ON b.payerId = p.id "
                + "WHERE b.type = :type AND b.creatorId = :creatorId AND b.status IN :status ";
        if (isCreatedDate) {
            billSummaryString += "AND b.createdDate BETWEEN :startCreatedDate AND :endCreatedDate ";
        }
        if (isDueDate) {
            billSummaryString += "AND b.dueDate BETWEEN :startDueDate AND :endDueDate ";
        }
        if (isBillName) {
            billSummaryString += "AND UPPER(b.name) LIKE :billName ";
        }
        if (isBillNumber) {
            billSummaryString += "AND UPPER(b.number) LIKE :billNumber ";
        }
        if (isPayer) {
            billSummaryString += "AND p.account IN :payer ";
        }
        if (isInfo1) {
            billSummaryString += "AND b.info1 = :info1 ";
        }
        if (isInfo2) {
            billSummaryString += "AND b.info2 = :info2 ";
        }
        if (isInfo3) {
            billSummaryString += "AND b.info3 = :info3 ";
        }
        if (isPayerName) {
            billSummaryString += "AND ((UPPER(p.firstName) LIKE :payerName) OR (UPPER(p.lastName) LIKE :payerName)) ";
        }

        Query billSummaryQuery = em.createQuery(billSummaryString, Object.class);

        billSummaryQuery.setParameter("type", BillTypeEnum.MERCHANT_BILL);
        billSummaryQuery.setParameter("creatorId", merchantId);
        if (isCreatedDate) {
            billSummaryQuery.setParameter("startCreatedDate", cStartCreatedDate);
            billSummaryQuery.setParameter("endCreatedDate", cEndCreatedDate);
        }
        if (isDueDate) {
            billSummaryQuery.setParameter("startDueDate", cStartDueDate);
            billSummaryQuery.setParameter("endDueDate", cEndDueDate);
        }
        if (isBillName) {
            billSummaryQuery.setParameter("billName", "%" + billName.toUpperCase() + "%");
        }
        if (isBillNumber) {
            billSummaryQuery.setParameter("billNumber", "%" + billNumber.toUpperCase() + "%");
        }
        if (isPayer) {
            billSummaryQuery.setParameter("payer", listAccountPayer);
        }
        if (isInfo1) {
            billSummaryQuery.setParameter("info1", info1);
        }
        if (isInfo2) {
            billSummaryQuery.setParameter("info2", info2);
        }
        if (isInfo3) {
            billSummaryQuery.setParameter("info3", info3);
        }
        if (isPayerName) {
            billSummaryQuery.setParameter("payerName", "%" + payerName.toUpperCase() + "%");
        }

        if (!isStatus) {
            listOpenStatus.add(BillStatusEnum.APPROVED);
            listOpenStatus.add(BillStatusEnum.WAITING_RESPONSE);
            listClosedStatus.add(BillStatusEnum.CANCELED);
            listClosedStatus.add(BillStatusEnum.PAID);
            listClosedStatus.add(BillStatusEnum.REJECTED);
            listClosedStatus.add(BillStatusEnum.UNKNOWN);
        }

        Object[] returnQueryOpen = new Object[2];
        Object[] returnQueryClosed = new Object[2];
        if (!listOpenStatus.isEmpty()) {
            returnQueryOpen = (Object[]) billSummaryQuery
                    .setParameter("status", listOpenStatus)
                    .getSingleResult();
        }
        if (!listClosedStatus.isEmpty()) {
            returnQueryClosed = (Object[]) billSummaryQuery
                    .setParameter("status", listClosedStatus)
                    .getSingleResult();
        }

        Long numberOfOpenBill = (Long) (returnQueryOpen[0] == null ? 0L : returnQueryOpen[0]);
        Long numberOfClosedBill = (Long) (returnQueryClosed[0] == null ? 0L : returnQueryClosed[0]);
        Long numberOfBill = numberOfOpenBill + numberOfClosedBill;
        BigDecimal amountOfOpenBill = (BigDecimal) (returnQueryOpen[1] == null ? BigDecimal.ZERO : returnQueryOpen[1]);
        BigDecimal amountOfClosedBill = (BigDecimal) (returnQueryClosed[1] == null ? BigDecimal.ZERO : returnQueryClosed[1]);
        BigDecimal amountOfBill = amountOfOpenBill.add(amountOfClosedBill);

        double percentageOfNumberOpenBill = 0;
        double percentageOfNumberClosedBill = 0;
        if (numberOfBill.doubleValue() > 0) {
            percentageOfNumberOpenBill = (numberOfOpenBill.doubleValue() / numberOfBill.doubleValue()) * 100;
            percentageOfNumberClosedBill = (numberOfClosedBill.doubleValue() / numberOfBill.doubleValue()) * 100;
        }
        double amountOfOpenBillDouble = amountOfOpenBill.doubleValue();
        double amountOfClosedBillDouble = amountOfClosedBill.doubleValue();
        double amountOfBillDouble = amountOfBill.doubleValue();
        double percentageOfAmountOpenBill = 0;
        double percentageOfAmountClosedBill = 0;
        if (amountOfBillDouble > 0) {
            percentageOfAmountOpenBill = (amountOfOpenBillDouble / amountOfBillDouble) * 100;
            percentageOfAmountClosedBill = (amountOfClosedBillDouble / amountOfBillDouble) * 100;
        }

        BillMerchantSummaryReportHolder summaryReport = new BillMerchantSummaryReportHolder();
        summaryReport.setNumberOfOpenBill(numberOfOpenBill);
        summaryReport.setNumberOfClosedBill(numberOfClosedBill);
        summaryReport.setNumberOfBill(numberOfBill);
        summaryReport.setAmountOfOpenBill(amountOfOpenBill);
        summaryReport.setAmountOfClosedBill(amountOfClosedBill);
        summaryReport.setAmountOfBill(amountOfBill);
        summaryReport.setPercentageOfNumberOpenBill(percentageOfNumberOpenBill);
        summaryReport.setPercentageOfNumberClosedBill(percentageOfNumberClosedBill);
        summaryReport.setPercentageOfAmountOpenBill(percentageOfAmountOpenBill);
        summaryReport.setPercentageOfAmountClosedBill(percentageOfAmountClosedBill);

        Gson gson = new Gson();
        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(DATA_KEY, gson.toJson(summaryReport));
        return response;

    }

    public JSONObject reportBill(String merchantCode, String limit, String page,
            String startCreatedDate, String endCreatedDate, String startDueDate, String endDueDate,
            String billName, String billNumber, String status, String payer,
            String info1, String info2, String info3, String payerName) {

        JSONObject response = new JSONObject();

        Integer limitInteger;
        Integer pageInteger;

        boolean isCreatedDate = false;
        boolean isDueDate = false;
        boolean isBillName = false;
        boolean isBillNumber = false;
        boolean isStatus = false;
        boolean isPayer = false;
        boolean isInfo1 = false;
        boolean isInfo2 = false;
        boolean isInfo3 = false;
        boolean isPayerName = false;

        if (startCreatedDate != null && endCreatedDate != null) {
            isCreatedDate = true;
        }
        if (startDueDate != null && endDueDate != null) {
            isDueDate = true;
        }
        if (billName != null) {
            isBillName = true;
        }
        if (billNumber != null) {
            isBillNumber = true;
        }
        if (status != null) {
            isStatus = true;
        }
        if (payer != null) {
            isPayer = true;
        }
        if (info1 != null) {
            isInfo1 = true;
        }
        if (info2 != null) {
            isInfo2 = true;
        }
        if (info3 != null) {
            isInfo3 = true;
        }
        if (payerName != null) {
            isPayerName = true;
        }

        Date cStartDueDate = null;
        Date cEndDueDate = null;
        Date cStartCreatedDate = null;
        Date cEndCreatedDate = null;
        List<BillStatusEnum> listStatus = new ArrayList<>();
        List<String> listAccountPayer = new ArrayList<>();

        try {
            limitInteger = Integer.parseInt(limit);
            pageInteger = Integer.parseInt(page);
            if (isCreatedDate) {
                cStartCreatedDate = DATE_FORMAT.parse(startCreatedDate);
                cEndCreatedDate = DATE_FORMAT.parse(endCreatedDate);
            }
            if (isDueDate) {
                cStartDueDate = DATE_FORMAT.parse(startDueDate);
                cEndDueDate = DATE_FORMAT.parse(endDueDate);
            }
            if (isStatus) {
                JSONArray statusArray = new JSONArray(status);
                for (int i = 0; i < statusArray.length(); i++) {
                    BillStatusEnum statusBill = BillStatusEnum.getBillStatus(statusArray.getInt(i));
                    if (statusBill == null) {
                        response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
                        return response;
                    }
                    listStatus.add(statusBill);
                }
            }
            if (isPayer) {
                JSONArray payerArray = new JSONArray(payer);
                for (int i = 0; i < payerArray.length(); i++) {
                    listAccountPayer.add(payerArray.getString(i));
                }
            }
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        if (pageInteger < 1 || limitInteger < 1) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        int firstResult = ((pageInteger - 1) * limitInteger);

        HashMap validateMerchantCodeResponse = merchantValidatorBean.validateMerchantCode(merchantCode);
        if (!validateMerchantCodeResponse.get(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return new JSONObject(validateMerchantCodeResponse);
        }
        Merchant merchant = (Merchant) validateMerchantCodeResponse.get(MERCHANT_KEY);
        Long merchantId = Long.parseLong(merchant.getId());

        String billReportQueryCountString = "SELECT COUNT(b.id) "
                + "FROM Bill b LEFT JOIN PersonalInfo p ON b.payerId = p.id "
                + "LEFT JOIN Invoice i ON b.invoiceId = i.id "
                + "WHERE b.type = :type AND b.creatorId = :creatorId ";

        String billReportQueryString = "SELECT NEW id.hardana.holder.bill.BillMerchantReportHolder(b.number, "
                + "b.name, b.executionType, b.status, b.createdDate, b.dueDate, b.lateFeeType, b.lateFeeValue, "
                + "b.lateFeeDay, b.paidDate, b.billAmount, b.totalLateFeeAmount, b.totalBillAmount, b.info1, "
                + "b.info2, b.info3, p.account, p.firstName, p.lastName, i.number, b.payerRejectReason, b.payerRejectDesc, tp.type) "
                + "FROM Bill b LEFT JOIN PersonalInfo p ON b.payerId = p.id "
                + "LEFT JOIN Invoice i ON b.invoiceId = i.id "
                + "LEFT JOIN TransactionPayment tp ON tp.invoiceId = i.id "
                + "WHERE b.type = :type AND b.creatorId = :creatorId ";

        if (isCreatedDate) {
            billReportQueryCountString += "AND b.createdDate BETWEEN :startCreatedDate AND :endCreatedDate ";
            billReportQueryString += "AND b.createdDate BETWEEN :startCreatedDate AND :endCreatedDate ";
        }
        if (isDueDate) {
            billReportQueryCountString += "AND b.dueDate BETWEEN :startDueDate AND :endDueDate ";
            billReportQueryString += "AND b.dueDate BETWEEN :startDueDate AND :endDueDate ";
        }
        if (isBillName) {
            billReportQueryCountString += "AND UPPER(b.name) LIKE :billName ";
            billReportQueryString += "AND UPPER(b.name) LIKE :billName ";
        }
        if (isBillNumber) {
            billReportQueryCountString += "AND UPPER(b.number) LIKE :billNumber ";
            billReportQueryString += "AND UPPER(b.number) LIKE :billNumber ";
        }
        if (isStatus) {
            billReportQueryCountString += "AND b.status IN :status ";
            billReportQueryString += "AND b.status IN :status ";
        }
        if (isPayer) {
            billReportQueryCountString += "AND p.account IN :payer ";
            billReportQueryString += "AND p.account IN :payer ";
        }
        if (isInfo1) {
            billReportQueryCountString += "AND b.info1 = :info1 ";
            billReportQueryString += "AND b.info1 = :info1 ";
        }
        if (isInfo2) {
            billReportQueryCountString += "AND b.info2 = :info2 ";
            billReportQueryString += "AND b.info2 = :info2 ";
        }
        if (isInfo3) {
            billReportQueryCountString += "AND b.info3 = :info3 ";
            billReportQueryString += "AND b.info3 = :info3 ";
        }
        if (isPayerName) {
            billReportQueryCountString += "AND ((UPPER(p.firstName) LIKE :payerName) OR (UPPER(p.lastName) LIKE :payerName)) ";
            billReportQueryString += "AND ((UPPER(p.firstName) LIKE :payerName) OR (UPPER(p.lastName) LIKE :payerName)) ";
        }
        billReportQueryString += "ORDER BY b.id DESC ";

        Query billReportQueryCount = em.createQuery(billReportQueryCountString, Long.class
        );
        billReportQueryCount.setParameter(
                "type", BillTypeEnum.MERCHANT_BILL);
        billReportQueryCount.setParameter(
                "creatorId", merchantId);
        if (isCreatedDate) {
            billReportQueryCount.setParameter("startCreatedDate", cStartCreatedDate);
            billReportQueryCount.setParameter("endCreatedDate", cEndCreatedDate);
        }
        if (isDueDate) {
            billReportQueryCount.setParameter("startDueDate", cStartDueDate);
            billReportQueryCount.setParameter("endDueDate", cEndDueDate);
        }
        if (isBillName) {
            billReportQueryCount.setParameter("billName", "%" + billName.toUpperCase() + "%");
        }
        if (isBillNumber) {
            billReportQueryCount.setParameter("billNumber", "%" + billNumber.toUpperCase() + "%");
        }
        if (isStatus) {
            billReportQueryCount.setParameter("status", listStatus);
        }
        if (isPayer) {
            billReportQueryCount.setParameter("payer", listAccountPayer);
        }
        if (isInfo1) {
            billReportQueryCount.setParameter("info1", info1);
        }
        if (isInfo2) {
            billReportQueryCount.setParameter("info2", info2);
        }
        if (isInfo3) {
            billReportQueryCount.setParameter("info3", info3);
        }
        if (isPayerName) {
            billReportQueryCount.setParameter("payerName", "%" + payerName.toUpperCase() + "%");
        }
        Long billReportCount = (Long) billReportQueryCount.getSingleResult();

        Query billReportQuery = em.createQuery(billReportQueryString, BillMerchantReportHolder.class);

        billReportQuery.setParameter("type", BillTypeEnum.MERCHANT_BILL);
        billReportQuery.setParameter("creatorId", merchantId);
        if (isCreatedDate) {
            billReportQuery.setParameter("startCreatedDate", cStartCreatedDate);
            billReportQuery.setParameter("endCreatedDate", cEndCreatedDate);
        }
        if (isDueDate) {
            billReportQuery.setParameter("startDueDate", cStartDueDate);
            billReportQuery.setParameter("endDueDate", cEndDueDate);
        }
        if (isBillName) {
            billReportQuery.setParameter("billName", "%" + billName.toUpperCase() + "%");
        }
        if (isBillNumber) {
            billReportQuery.setParameter("billNumber", "%" + billNumber.toUpperCase() + "%");
        }
        if (isStatus) {
            billReportQuery.setParameter("status", listStatus);
        }
        if (isPayer) {
            billReportQuery.setParameter("payer", listAccountPayer);
        }
        if (isInfo1) {
            billReportQuery.setParameter("info1", info1);
        }
        if (isInfo2) {
            billReportQuery.setParameter("info2", info2);
        }
        if (isInfo3) {
            billReportQuery.setParameter("info3", info3);
        }
        if (isPayerName) {
            billReportQuery.setParameter("payerName", "%" + payerName.toUpperCase() + "%");
        }

        billReportQuery.setFirstResult(firstResult);

        billReportQuery.setMaxResults(limitInteger);

        List<BillMerchantReportHolder> listBill = billReportQuery
                .getResultList();

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(DATA_KEY, listBill);

        response.put(COUNT_KEY, billReportCount);
        return response;

    }

}
