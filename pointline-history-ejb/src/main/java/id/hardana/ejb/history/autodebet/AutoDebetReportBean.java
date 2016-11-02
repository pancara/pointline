/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.history.autodebet;

import id.hardana.ejb.system.validation.PersonalValidatorBean;
import id.hardana.entity.autodebet.enums.AutodebetStatusEnum;
import id.hardana.entity.profile.personal.PersonalInfo;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.enums.TransactionTransferTypeEnum;
import id.hardana.holder.autodebet.AutoDebetReportHolder;
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
import javax.persistence.TemporalType;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean

public class AutoDebetReportBean {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final String PERSONAL_INFO_KEY = "personalInfo";
    private final String STATUS_KEY = "status";
    private final String DATA_KEY = "data";
    private final String COUNT_KEY = "count";

    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    @EJB
    private PersonalValidatorBean personalValidatorBean;

    public JSONObject reportAutoDebet(String account, String limit, String page,
            String startCreatedDate, String endCreatedDate, String number, String status,
            String type, String destinationPan, String destinationAccount) {

        JSONObject response = new JSONObject();

        Integer limitInteger;
        Integer pageInteger;

        boolean isCreatedDate = false;
        boolean isNumber = false;
        boolean isStatus = false;
        boolean isType = false;
        boolean isDestinationPan = false;
        boolean isDestinationAccount = false;

        if (startCreatedDate != null && endCreatedDate != null) {
            isCreatedDate = true;
        }
        if (number != null) {
            isNumber = true;
        }
        if (status != null) {
            isStatus = true;
        }
        if (type != null) {
            isType = true;
        }
        if (destinationPan != null) {
            isDestinationPan = true;
        }
        if (destinationAccount != null) {
            isDestinationAccount = true;
        }

        Date cStartCreatedDate = null;
        Date cEndCreatedDate = null;
        List<AutodebetStatusEnum> listStatus = new ArrayList<>();
        List<TransactionTransferTypeEnum> listType = new ArrayList<>();
        List<String> listDestinationPAN = new ArrayList<>();
        List<String> listDestinationAccount = new ArrayList<>();

        try {
            limitInteger = Integer.parseInt(limit);
            pageInteger = Integer.parseInt(page);
            if (isCreatedDate) {
                cStartCreatedDate = DATE_FORMAT.parse(startCreatedDate);
                cEndCreatedDate = DATE_FORMAT.parse(endCreatedDate);
            }
            if (isStatus) {
                JSONArray statusArray = new JSONArray(status);
                for (int i = 0; i < statusArray.length(); i++) {
                    AutodebetStatusEnum statusAutoDebet = AutodebetStatusEnum.getAutoDebetStatus(statusArray.getInt(i));
                    if (statusAutoDebet == null) {
                        response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
                        return response;
                    }
                    listStatus.add(statusAutoDebet);
                }
            }
            if (isType) {
                JSONArray typeArray = new JSONArray(status);
                for (int i = 0; i < typeArray.length(); i++) {
                    TransactionTransferTypeEnum typeAutoDebet = TransactionTransferTypeEnum.getTransactionTransferType(typeArray.getInt(i));
                    if (typeAutoDebet == null) {
                        response.put(STATUS_KEY, ResponseStatusEnum.INVALID_FORMAT.getResponseStatus());
                        return response;
                    }
                    listType.add(typeAutoDebet);
                }
            }
            if (isDestinationPan) {
                JSONArray panArray = new JSONArray(destinationPan);
                for (int i = 0; i < panArray.length(); i++) {
                    listDestinationPAN.add(panArray.getString(i));
                }
            }
            if (isDestinationAccount) {
                JSONArray accountArray = new JSONArray(destinationAccount);
                for (int i = 0; i < accountArray.length(); i++) {
                    listDestinationAccount.add(accountArray.getString(i));
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

        String autoDebetReportQueryCountString = "SELECT COUNT(a.id) "
                + "FROM AutoDebet a LEFT JOIN PersonalInfo p ON a.toId = p.id "
                + "LEFT JOIN Card c ON a.toId = c.id "
                + "WHERE a.fromId = :fromId ";

        String autoDebetReportQueryString = "SELECT NEW id.hardana.holder.autodebet.AutoDebetReportHolder(a.number, "
                + "a.type, a.amount, a.status, a.createdDate, a.closedDate, a.executionDate, a.lastExecutionStatus, "
                + "a.failedAction, a.frequencyType, a.scheduleFrequency, a.scheduleNumber, a.successExecuteNumber, "
                + "a.failedExecuteNumber, p.account, p.firstName, p.lastName, c.pan, c.cardHolderName) "
                + "FROM AutoDebet a LEFT JOIN PersonalInfo p ON a.toId = p.id "
                + "LEFT JOIN Card c ON a.toId = c.id "
                + "WHERE a.fromId = :fromId ";

        if (isCreatedDate) {
            autoDebetReportQueryCountString += "AND a.createdDate BETWEEN :startCreatedDate AND :endCreatedDate ";
            autoDebetReportQueryString += "AND a.createdDate BETWEEN :startCreatedDate AND :endCreatedDate ";
        }
        if (isNumber) {
            autoDebetReportQueryCountString += "AND a.number = :number ";
            autoDebetReportQueryString += "AND a.number = :number ";
        }
        if (isStatus) {
            autoDebetReportQueryCountString += "AND a.status IN :status ";
            autoDebetReportQueryString += "AND a.status IN :status ";
        }
        if (isType) {
            autoDebetReportQueryCountString += "AND a.type IN :type ";
            autoDebetReportQueryString += "AND a.type IN :type ";
        }
        if (isDestinationPan) {
            autoDebetReportQueryCountString += "AND c.pan IN :pan ";
            autoDebetReportQueryString += "AND c.pan IN :pan ";
        }
        if (isDestinationAccount) {
            autoDebetReportQueryCountString += "AND p.account IN :account ";
            autoDebetReportQueryString += "AND p.account IN :account ";
        }
        autoDebetReportQueryString += "ORDER BY a.id DESC ";

        Query autoDebetReportQueryCount = em.createQuery(autoDebetReportQueryCountString, Long.class);
        autoDebetReportQueryCount.setParameter("fromId", personalInfoId);
        if (isCreatedDate) {
            autoDebetReportQueryCount.setParameter("startCreatedDate", cStartCreatedDate, TemporalType.DATE);
            autoDebetReportQueryCount.setParameter("endCreatedDate", cEndCreatedDate, TemporalType.DATE);
        }
        if (isNumber) {
            autoDebetReportQueryCount.setParameter("number", number);
        }
        if (isStatus) {
            autoDebetReportQueryCount.setParameter("status", listStatus);
        }
        if (isType) {
            autoDebetReportQueryCount.setParameter("type", listType);
        }
        if (isDestinationPan) {
            autoDebetReportQueryCount.setParameter("pan", listDestinationPAN);
        }
        if (isDestinationAccount) {
            autoDebetReportQueryCount.setParameter("account", listDestinationAccount);
        }

        Long autoDebetReportCount = (Long) autoDebetReportQueryCount.getSingleResult();

        Query autoDebetReportQuery = em.createQuery(autoDebetReportQueryString, AutoDebetReportHolder.class);

        autoDebetReportQuery.setParameter("fromId", personalInfoId);
        if (isCreatedDate) {
            autoDebetReportQuery.setParameter("startCreatedDate", cStartCreatedDate, TemporalType.DATE);
            autoDebetReportQuery.setParameter("endCreatedDate", cEndCreatedDate, TemporalType.DATE);
        }
        if (isNumber) {
            autoDebetReportQuery.setParameter("number", number);
        }
        if (isStatus) {
            autoDebetReportQuery.setParameter("status", listStatus);
        }
        if (isType) {
            autoDebetReportQuery.setParameter("type", listType);
        }
        if (isDestinationPan) {
            autoDebetReportQuery.setParameter("pan", listDestinationPAN);
        }
        if (isDestinationAccount) {
            autoDebetReportQuery.setParameter("account", listDestinationAccount);
        }
        autoDebetReportQuery.setFirstResult(firstResult);
        autoDebetReportQuery.setMaxResults(limitInteger);

        List<AutoDebetReportHolder> listAutoDebet = autoDebetReportQuery
                .getResultList();

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(DATA_KEY, listAutoDebet);
        response.put(COUNT_KEY, autoDebetReportCount);
        return response;

    }

}
