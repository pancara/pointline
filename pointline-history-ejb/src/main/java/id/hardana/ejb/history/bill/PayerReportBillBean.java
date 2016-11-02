/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.history.bill;

import id.hardana.ejb.history.invoice.InvoiceSearchBean;
import id.hardana.ejb.history.transaction.TransferToPersonalSearchBean;
import id.hardana.ejb.system.validation.PersonalValidatorBean;
import id.hardana.entity.bill.Bill;
import id.hardana.entity.bill.enums.BillStatusEnum;
import id.hardana.entity.bill.enums.BillTypeEnum;
import id.hardana.entity.profile.personal.PersonalInfo;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.holder.bill.BillPayerReportHolder;
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

public class PayerReportBillBean {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final String PERSONAL_INFO_KEY = "personalInfo";
    private final String STATUS_KEY = "status";
    private final String DATA_KEY = "data";
    private final String COUNT_KEY = "count";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    @EJB
    private PersonalValidatorBean personalValidatorBean;
    @EJB
    private InvoiceSearchBean invoiceSearchBean;
    @EJB
    private TransferToPersonalSearchBean transferToPersonalSearchBean;

    public JSONObject getTransactionBill(String account, String billNumber, String referenceNumber) {
        JSONObject response = new JSONObject();

        HashMap validatePersonalResponse = personalValidatorBean.validateAccount(account);
        if (!validatePersonalResponse.get(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return new JSONObject(validatePersonalResponse);
        }
        PersonalInfo personalInfo = (PersonalInfo) validatePersonalResponse.get(PERSONAL_INFO_KEY);
        Long personalInfoId = Long.parseLong(personalInfo.getId());

        List<Bill> listBill = em.createQuery("SELECT b FROM Bill b LEFT JOIN TransactionTbl t ON b.transactionId = t.id "
                + "WHERE t.referenceNumber = :referenceNumber AND b.number = :billNumber AND b.type = :type "
                + "AND b.payerId = :personalInfoId", Bill.class)
                .setParameter("referenceNumber", referenceNumber)
                .setParameter("billNumber", billNumber)
                .setParameter("type", BillTypeEnum.PERSONAL_BILL)
                .setParameter("personalInfoId", personalInfoId)
                .getResultList();
        if (listBill.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_BILL_NUMBER.getResponseStatus());
            return response;
        }
        
        Bill bill = listBill.get(0);
        Long fromId = Long.parseLong(bill.getPayerId());
        Long toId = Long.parseLong(bill.getCreatorId());

        return transferToPersonalSearchBean.getTransferPersonal(fromId, toId, referenceNumber);
    }

    public JSONObject getInvoiceFromBill(String account, String billNumber, String invoiceNumber) {
        JSONObject response = new JSONObject();

        HashMap validatePersonalResponse = personalValidatorBean.validateAccount(account);
        if (!validatePersonalResponse.get(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return new JSONObject(validatePersonalResponse);
        }
        PersonalInfo personalInfo = (PersonalInfo) validatePersonalResponse.get(PERSONAL_INFO_KEY);
        Long personalInfoId = Long.parseLong(personalInfo.getId());

        List<Bill> listBill = em.createQuery("SELECT b FROM Bill b LEFT JOIN Invoice i ON b.invoiceId = i.id "
                + "WHERE i.number = :invoiceNumber AND b.number = :billNumber AND b.type = :type "
                + "AND (b.payerId = :personalInfoId OR b.payerId IS NULL)", Bill.class)
                .setParameter("invoiceNumber", invoiceNumber)
                .setParameter("billNumber", billNumber)
                .setParameter("type", BillTypeEnum.MERCHANT_BILL)
                .setParameter("personalInfoId", personalInfoId)
                .getResultList();
        if (listBill.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_BILL_NUMBER.getResponseStatus());
            return response;
        }

        return invoiceSearchBean.getInvoiceFromBillPayer(invoiceNumber);
    }

    public JSONObject reportBill(String account, String limit, String page,
            String startCreatedDate, String endCreatedDate, String startDueDate, String endDueDate,
            String billName, String billNumber, String status, String billType, String merchants,
            String personalCreators, String info1, String info2, String info3) {

        JSONObject response = new JSONObject();

        Integer limitInteger;
        Integer pageInteger;

        boolean isCreatedDate = false;
        boolean isDueDate = false;
        boolean isBillName = false;
        boolean isBillNumber = false;
        boolean isStatus = false;
        boolean isInfo1 = false;
        boolean isInfo2 = false;
        boolean isInfo3 = false;
        boolean isBillType = false;
        boolean isMerchants = false;
        boolean isPersonalCreators = false;

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
        if (info1 != null) {
            isInfo1 = true;
        }
        if (info2 != null) {
            isInfo2 = true;
        }
        if (info3 != null) {
            isInfo3 = true;
        }
        if (billType != null) {
            isBillType = true;
        }
        if (merchants != null) {
            isMerchants = true;
        }
        if (personalCreators != null) {
            isPersonalCreators = true;
        }

        Date cStartDueDate = null;
        Date cEndDueDate = null;
        Date cStartCreatedDate = null;
        Date cEndCreatedDate = null;
        List<BillStatusEnum> listStatus = new ArrayList<>();
        List<BillTypeEnum> listType = new ArrayList<>();
        List<String> listPersonalCreator = new ArrayList<>();
        List<String> listMerchant = new ArrayList<>();

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
            if (isBillType) {
                JSONArray typeArray = new JSONArray(billType);
                for (int i = 0; i < typeArray.length(); i++) {
                    BillTypeEnum typeBill = BillTypeEnum.getBillType(typeArray.getInt(i));
                    if (typeBill == null) {
                        response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
                        return response;
                    }
                    listType.add(typeBill);
                }
            }
            if (isMerchants) {
                JSONArray merchantArray = new JSONArray(merchants);
                for (int i = 0; i < merchantArray.length(); i++) {
                    listMerchant.add(merchantArray.getString(i));
                }
            }
            if (isPersonalCreators) {
                JSONArray personalCreatorArray = new JSONArray(personalCreators);
                for (int i = 0; i < personalCreatorArray.length(); i++) {
                    listPersonalCreator.add(personalCreatorArray.getString(i));
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

        HashMap validatePersonalResponse = personalValidatorBean.validateAccount(account);
        if (!validatePersonalResponse.get(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return new JSONObject(validatePersonalResponse);
        }
        PersonalInfo personalInfo = (PersonalInfo) validatePersonalResponse.get(PERSONAL_INFO_KEY);
        Long personalInfoId = Long.parseLong(personalInfo.getId());

        String billReportQueryCountString = "SELECT COUNT(b.id) "
                + "FROM Bill b LEFT JOIN PersonalInfo p ON b.creatorId = p.id "
                + "LEFT JOIN TransactionTbl t ON b.transactionId = t.id "
                + "LEFT JOIN Invoice i ON b.invoiceId = i.id "
                + "LEFT JOIN Merchant m ON b.creatorId = m.id "
                + "WHERE b.payerId = :payerId ";

        String billReportQueryString = "SELECT NEW id.hardana.holder.bill.BillPayerReportHolder(b.number, "
                + "b.name, b.executionType, b.status, b.createdDate, b.dueDate, b.lateFeeType, b.lateFeeValue, "
                + "b.lateFeeDay, b.paidDate, b.billAmount, b.totalLateFeeAmount, b.totalBillAmount, b.info1, "
                + "b.info2, b.info3, b.type, p.account, p.firstName, p.lastName, b.billDesc, t.referenceNumber, "
                + "m.brandName, i.number) "
                + "FROM Bill b LEFT JOIN PersonalInfo p ON b.creatorId = p.id "
                + "LEFT JOIN TransactionTbl t ON b.transactionId = t.id "
                + "LEFT JOIN Invoice i ON b.invoiceId = i.id "
                + "LEFT JOIN Merchant m ON b.creatorId = m.id "
                + "WHERE b.payerId = :payerId ";

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
        if (isBillType) {
            billReportQueryCountString += "AND b.type IN :type ";
            billReportQueryString += "AND b.type IN :type ";
        }
        if (isMerchants) {
            billReportQueryCountString += "AND m.brandName IN :merchant ";
            billReportQueryString += "AND m.brandName IN :merchant ";
        }
        if (isPersonalCreators) {
            billReportQueryCountString += "AND p.account IN :account ";
            billReportQueryString += "AND p.account IN :account ";
        }
        billReportQueryString += "ORDER BY b.id DESC ";

        Query billReportQueryCount = em.createQuery(billReportQueryCountString, Long.class);
        billReportQueryCount.setParameter("payerId", personalInfoId);
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
        if (isInfo1) {
            billReportQueryCount.setParameter("info1", info1);
        }
        if (isInfo2) {
            billReportQueryCount.setParameter("info2", info2);
        }
        if (isInfo3) {
            billReportQueryCount.setParameter("info3", info3);
        }
        if (isBillType) {
            billReportQueryCount.setParameter("type", listType);
        }
        if (isMerchants) {
            billReportQueryCount.setParameter("merchant", listMerchant);
        }
        if (isPersonalCreators) {
            billReportQueryCount.setParameter("account", listPersonalCreator);
        }
        Long billReportCount = (Long) billReportQueryCount.getSingleResult();

        Query billReportQuery = em.createQuery(billReportQueryString, BillPayerReportHolder.class);
        billReportQuery.setParameter("payerId", personalInfoId);
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
        if (isInfo1) {
            billReportQuery.setParameter("info1", info1);
        }
        if (isInfo2) {
            billReportQuery.setParameter("info2", info2);
        }
        if (isInfo3) {
            billReportQuery.setParameter("info3", info3);
        }
        if (isBillType) {
            billReportQuery.setParameter("type", listType);
        }
        if (isMerchants) {
            billReportQuery.setParameter("merchant", listMerchant);
        }
        if (isPersonalCreators) {
            billReportQuery.setParameter("account", listPersonalCreator);
        }
        billReportQuery.setFirstResult(firstResult);
        billReportQuery.setMaxResults(limitInteger);

        List<BillPayerReportHolder> listBill = billReportQuery
                .getResultList();

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(DATA_KEY, listBill);
        response.put(COUNT_KEY, billReportCount);
        return response;

    }

}
