/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.history.bill;

import id.hardana.ejb.history.transaction.TransferToPersonalSearchBean;
import id.hardana.ejb.system.validation.PersonalValidatorBean;
import id.hardana.entity.bill.Bill;
import id.hardana.entity.bill.enums.BillStatusEnum;
import id.hardana.entity.bill.enums.BillTypeEnum;
import id.hardana.entity.profile.personal.PersonalInfo;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.holder.bill.BillPersonalReportHolder;
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

public class PersonalReportBillBean {

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
                + "AND b.creator = :personalInfoId", Bill.class)
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

    public JSONObject reportBill(String account, String limit, String page,
            String startCreatedDate, String endCreatedDate, String startDueDate, String endDueDate,
            String billName, String billNumber, String status, String payer,
            String info1, String info2, String info3) {

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

        HashMap validatePersonalResponse = personalValidatorBean.validateAccount(account);
        if (!validatePersonalResponse.get(STATUS_KEY).equals(ResponseStatusEnum.SUCCESS.getResponseStatus())) {
            return new JSONObject(validatePersonalResponse);
        }
        PersonalInfo personalInfo = (PersonalInfo) validatePersonalResponse.get(PERSONAL_INFO_KEY);
        Long personalInfoId = Long.parseLong(personalInfo.getId());

        String billReportQueryCountString = "SELECT COUNT(b.id) "
                + "FROM Bill b LEFT JOIN PersonalInfo p ON b.payerId = p.id "
                + "LEFT JOIN TransactionTbl t ON b.transactionId = t.id "
                + "WHERE b.type = :type AND b.creatorId = :creatorId ";

        String billReportQueryString = "SELECT NEW id.hardana.holder.bill.BillPersonalReportHolder(b.number, b.name, "
                + "b.executionType, b.status, b.createdDate, b.dueDate, b.lateFeeType, b.lateFeeValue, b.lateFeeDay, "
                + "b.paidDate, b.billAmount, b.totalLateFeeAmount, b.totalBillAmount, b.info1, b.info2, b.info3, "
                + "p.account, p.firstName, p.lastName, b.billDesc, t.referenceNumber, b.payerRejectReason, b.payerRejectDesc) "
                + "FROM Bill b LEFT JOIN PersonalInfo p ON b.payerId = p.id "
                + "LEFT JOIN TransactionTbl t ON b.transactionId = t.id "
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
        billReportQueryString += "ORDER BY b.id DESC ";

        Query billReportQueryCount = em.createQuery(billReportQueryCountString, Long.class);
        billReportQueryCount.setParameter("type", BillTypeEnum.PERSONAL_BILL);
        billReportQueryCount.setParameter("creatorId", personalInfoId);
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
        Long billReportCount = (Long) billReportQueryCount.getSingleResult();

        Query billReportQuery = em.createQuery(billReportQueryString, BillPersonalReportHolder.class);

        billReportQuery.setParameter("type", BillTypeEnum.PERSONAL_BILL);
        billReportQuery.setParameter("creatorId", personalInfoId);
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
        billReportQuery.setFirstResult(firstResult);
        billReportQuery.setMaxResults(limitInteger);

        List<BillPersonalReportHolder> listBill = billReportQuery
                .getResultList();

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(DATA_KEY, listBill);
        response.put(COUNT_KEY, billReportCount);
        return response;

    }

}
