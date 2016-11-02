/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.history.bill;

import com.google.gson.Gson;
import id.hardana.entity.bill.enums.BillStatusEnum;
import id.hardana.entity.bill.enums.BillTypeEnum;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.holder.bill.BillPayerReportHolder;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean

public class PayerSearchOpenBillBean {

    private final String STATUS_KEY = "status";
    private final String DATA_KEY = "data";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject searchOpenBillMerchant(String billNumber, String merchantCode) {
        return searchOpenBill(billNumber, merchantCode, null);
    }

    public JSONObject searchOpenBillPersonal(String billNumber, String creatorAccount) {
        return searchOpenBill(billNumber, null, creatorAccount);
    }

    public JSONObject searchOpenBill(String billNumber, String merchantCode, String creatorAccount) {

        JSONObject response = new JSONObject();

        boolean isMerchantBill = false;
        boolean isPersonalBill = false;

        if (merchantCode != null) {
            isMerchantBill = true;
        }
        if (creatorAccount != null) {
            isPersonalBill = true;
        }

        if ((isPersonalBill && isMerchantBill) || (!isPersonalBill && !isMerchantBill)) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
            return response;
        }

        String billReportQueryString = "SELECT NEW id.hardana.holder.bill.BillPayerReportHolder(b.number, "
                + "b.name, b.executionType, b.status, b.createdDate, b.dueDate, b.lateFeeType, b.lateFeeValue, "
                + "b.lateFeeDay, b.paidDate, b.billAmount, b.totalLateFeeAmount, b.totalBillAmount, b.info1, "
                + "b.info2, b.info3, b.type, p.account, p.firstName, p.lastName, b.billDesc, t.referenceNumber, "
                + "m.brandName, i.number) "
                + "FROM Bill b LEFT JOIN PersonalInfo p ON b.creatorId = p.id "
                + "LEFT JOIN TransactionTbl t ON b.transactionId = t.id "
                + "LEFT JOIN Invoice i ON b.invoiceId = i.id "
                + "LEFT JOIN Merchant m ON b.creatorId = m.id "
                + "WHERE b.payerId IS NULL AND b.number = :billNumber "
                + "AND b.status = :status AND b.type = :type ";

        if (isMerchantBill) {
            billReportQueryString += "AND m.code = :merchantCode ";
        }
        if (isPersonalBill) {
            billReportQueryString += "AND p.account = :account ";
        }

        Query billReportQuery = em.createQuery(billReportQueryString, BillPayerReportHolder.class);
        billReportQuery.setParameter("status", BillStatusEnum.WAITING_RESPONSE);
        billReportQuery.setParameter("billNumber", billNumber);
        if (isMerchantBill) {
            billReportQuery.setParameter("merchantCode", merchantCode);
            billReportQuery.setParameter("type", BillTypeEnum.MERCHANT_BILL);
        }
        if (isPersonalBill) {
            billReportQuery.setParameter("account", creatorAccount);
            billReportQuery.setParameter("type", BillTypeEnum.PERSONAL_BILL);
        }

        BillPayerReportHolder billData;
        
        try {
            billData = (BillPayerReportHolder) billReportQuery.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            response.put(STATUS_KEY, ResponseStatusEnum.BILL_NOT_FOUND.getResponseStatus());
            return response;
        }

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(DATA_KEY, new Gson().toJson(billData));
        return response;

    }

}
