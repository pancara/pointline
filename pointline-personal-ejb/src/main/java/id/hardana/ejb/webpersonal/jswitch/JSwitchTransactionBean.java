/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.webpersonal.jswitch;

import id.hardana.ejb.webpersonal.holder.JSwitchTransactionInfoHolder;
import id.hardana.ejb.webpersonal.log.LoggingInterceptor;
import id.hardana.ejb.webpersonal.util.ChartTableItem;
import id.hardana.ejb.webpersonal.util.ChartTableParser;
import id.hardana.ejb.webpersonal.util.PaymentInfo;
import id.hardana.ejb.webpersonal.util.Util;
import id.hardana.entity.invoice.Invoice;
import id.hardana.entity.invoice.InvoiceItems;
import id.hardana.entity.jswitch.JSwitchInquiry;
import id.hardana.entity.jswitch.JSwitchPayment;
import id.hardana.entity.jswitch.JSwitchProduct;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.personal.Personal;
import id.hardana.entity.transaction.TransactionPayment;
import id.hardana.entity.transaction.TransactionTbl;

import id.hardana.jswitch.constant.EmoMerchant;
import id.hardana.jswitch.ejb.JSwitchPersonalTransactionBeanRemote;
import id.hardana.jswitch.exception.JSwitchEjbException;
import id.hardana.jswitch.exception.JSwitchRequestException;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author pancara
 */
@Stateless
@LocalBean
public class JSwitchTransactionBean {

    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    private static final String JNDI_JSWITCH_BEAN = "java:global/jswitch-ear/jswitch-ejb/id.hardana.jswitch.ejb.JSwitchPersonalTransactionBean";

    public JSwitchInquiry inquiry(String accountNumber, String destinationNumber, Long productId)
            throws JSwitchRequestException, NamingException, JSwitchEjbException {
        Personal personal = this.findPersonalByAccountNumber(accountNumber);

        JSwitchPersonalTransactionBeanRemote personalTransactionBean = (JSwitchPersonalTransactionBeanRemote) Util.lookup(JNDI_JSWITCH_BEAN);
        Long personalId = Long.valueOf(personal.getId());

        JSwitchProduct product = em.find(JSwitchProduct.class, productId);
        em.refresh(product);

        String productCode = product.getCode();
        return personalTransactionBean.doInquiry(personalId, destinationNumber, productCode);
    }

    public List<ChartTableItem> getMarkupItemPrice(Long inquiryId) {
        JSwitchInquiry inquiry = em.find(JSwitchInquiry.class, inquiryId);

        Merchant emoMerchant = em.find(Merchant.class, EmoMerchant.MERCHANT_ID);
        em.refresh(emoMerchant);

        String strMerchantFee = emoMerchant.getMerchantFee();
        BigDecimal merchantFee = strMerchantFee == null ? BigDecimal.ZERO : new BigDecimal(strMerchantFee);

        List<ChartTableItem> markupItems = new LinkedList<>();
        if (inquiry != null) {
            if (BigDecimal.ZERO.equals(inquiry.getTotalAmount())) {
                 List<ChartTableItem> items = ChartTableParser.parseInquiry(inquiry.getChartTable());
                for (ChartTableItem item : items) {
                    ChartTableItem markupItem = new ChartTableItem();
                    markupItem.setDenomination(item.getDenomination());

                    BigDecimal price = new BigDecimal(item.getPrice());
                    BigDecimal markupPrice = price.add(merchantFee);
                    markupItem.setPrice(markupPrice.toPlainString());

                    markupItems.add(markupItem);
                }
            } else {
                ChartTableItem markupItem = new ChartTableItem();
                markupItem.setDenomination(inquiry.getTotalAmount().toPlainString());
                
                BigDecimal markupPrice = inquiry.getTotalAmount().add(merchantFee);
                markupItem.setPrice(markupPrice.toPlainString());
                
                markupItems.add(markupItem);
               
            }
        }
        return markupItems;
    }

    public JSwitchPayment pay(String accountNumber, Long inquiryId, BigDecimal denomination)
            throws JSwitchEjbException, JSwitchRequestException, NamingException {
        Personal personal = this.findPersonalByAccountNumber(accountNumber);
        JSwitchPersonalTransactionBeanRemote personalTransactionBean = (JSwitchPersonalTransactionBeanRemote) Util.lookup(JNDI_JSWITCH_BEAN);
        Long personalId = Long.valueOf(personal.getId());
        return personalTransactionBean.doPayment(personalId, inquiryId, denomination);
    }

    private Personal findPersonalByAccountNumber(String accountNumber) {
        String ql = "SELECT p FROM Personal p LEFT JOIN PersonalInfo pi ON p.personalInfoId = pi.id "
                + "WHERE pi.account = :accountNumber";
        try {
            return em.createQuery(ql, Personal.class)
                    .setParameter("accountNumber", accountNumber)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public JSwitchTransactionInfoHolder populateTransactionInfo(JSwitchPayment payment) {
        JSwitchTransactionInfoHolder holder = new JSwitchTransactionInfoHolder();

        TransactionTbl trx = em.find(TransactionTbl.class, payment.getTransactionId());
        holder.setReferenfeNumber(trx.getReferenceNumber());
        holder.setTotalAmount(new BigDecimal(trx.getTotalAmount()));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        if (trx.getDateTime() != null) {
            try {
                holder.setDateTime(dateFormat.parse(trx.getDateTime()));
            } catch (ParseException ex) {
                Logger.getLogger(JSwitchTransactionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        String ql = "SELECT it FROM InvoiceItems it JOIN Invoice i ON it.invoiceId = i.id "
                + "JOIN TransactionPayment tp ON i.id = tp.invoiceId WHERE tp.transactionId = :transactionId";
        try {
            InvoiceItems item = em.createQuery(ql, InvoiceItems.class)
                    .setParameter("transactionId", payment.getTransactionId())
                    .setMaxResults(1)
                    .getSingleResult();
            holder.setItemName(item.getItemName());
        } catch (NoResultException nre) {

        }
        
        
        List<PaymentInfo> paymentInfo = ChartTableParser.parsePayment(payment.getChartTable());
        holder.setPaymentInfo(paymentInfo);
        return holder;
    }

}
