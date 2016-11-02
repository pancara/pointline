/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.history.invoice;

import com.google.gson.Gson;
import id.hardana.entity.invoice.InvoiceItemDiscount;
import id.hardana.entity.invoice.InvoiceTransactionDiscount;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.holder.invoice.InvoiceHolder;
import id.hardana.holder.invoice.InvoiceItemDiscountHolder;
import id.hardana.holder.invoice.InvoiceItemHolder;
import id.hardana.holder.invoice.InvoicePricingHolder;
import id.hardana.holder.invoice.InvoiceTransactionDiscountHolder;
import id.hardana.holder.transaction.TransactionPaymentHolder;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean

public class InvoiceSearchBean {

    private final String STATUS_KEY = "status";
    private final String INVOICE_KEY = "invoice";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;
    private final int FROM_BILL_MERCHANT_TYPE = 0;
    private final int FROM_BILL_PAYER_TYPE = 1;

    public JSONObject getInvoiceFromBillMerchant(String invoiceNumber, Long merchantId) {
        return getInvoiceJSON(invoiceNumber, merchantId, FROM_BILL_MERCHANT_TYPE);
    }

    public JSONObject getInvoiceFromBillPayer(String invoiceNumber) {
        return getInvoiceJSON(invoiceNumber, null, FROM_BILL_PAYER_TYPE);
    }

    public InvoiceHolder getInvoiceForEmail(String invoiceNumber) {
        System.out.println("InvoiceNumber getInvoiceForEmail : " + invoiceNumber);
        return getInvoice(invoiceNumber, null, FROM_BILL_PAYER_TYPE);
    }

    private JSONObject getInvoiceJSON(String invoiceNumber, Long merchantId, int type) {
        JSONObject response = new JSONObject();
        InvoiceHolder invoice = getInvoice(invoiceNumber, merchantId, type);
        if (invoice == null) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVOICE_NOT_FOUND.getResponseStatus());
            return response;
        }
        Gson gson = new Gson();
        String jsonString = gson.toJson(invoice);
        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(INVOICE_KEY, new JSONObject(jsonString));
        return response;
    }

    private InvoiceHolder getInvoice(String invoiceNumber, Long merchantId, int type) {

        System.out.println("InvoiceNumber getInvoice : " + invoiceNumber);
        String queryInvoiceSearchString = "SELECT NEW id.hardana.holder.invoice.InvoiceHolder"
                + "(i.id, i.number, i.amount, i.dateTime, i.tableNumber, i.status, o.userName, "
                + "ot.name, ot.latitude, ot.longitude) "
                + "FROM Invoice i LEFT JOIN Operator o ON i.operatorId = o.id "
                + "LEFT JOIN Outlet ot ON ot.id = i.outletId "
                + "WHERE i.number = :number ";

        if (type == FROM_BILL_MERCHANT_TYPE) {
            queryInvoiceSearchString += "AND i.merchantId = :merchantId ";
        }

        Query queryInvoiceSearch = em.createQuery(queryInvoiceSearchString, InvoiceHolder.class);
        queryInvoiceSearch.setParameter("number", invoiceNumber);
        if (type == FROM_BILL_MERCHANT_TYPE) {
            queryInvoiceSearch.setParameter("merchantId", merchantId);
        }

        List<InvoiceHolder> listInvoice = queryInvoiceSearch.getResultList();

        if (listInvoice.isEmpty()) {
            return null;
        }

        InvoiceHolder invoice = listInvoice.get(0);
        Long invoiceId = invoice.getId();

        String queryInvoiceItems = "SELECT NEW id.hardana.holder.invoice.InvoiceItemHolder(i.id, "
                + "i.itemId, i.itemName, i.itemSupplyPrice, i.itemSalesPrice, i.itemQuantity, i.itemSubTotal) "
                + "FROM InvoiceItems i WHERE i.invoiceId = :invoiceId";
        List<InvoiceItemHolder> listItems = em.createQuery(queryInvoiceItems, InvoiceItemHolder.class)
                .setParameter("invoiceId", invoiceId)
                .getResultList();

        // populate item discount
        for (InvoiceItemHolder invoiceItemHolder : listItems) {
            try {
                String jql = "SELECT i FROM InvoiceItemDiscount i WHERE i.invoiceItemId = :invoiceItemId";
                InvoiceItemDiscount itemDiscount = em.createQuery(jql, InvoiceItemDiscount.class)
                        .setParameter("invoiceItemId", invoiceItemHolder.getId())
                        .setMaxResults(1)
                        .getSingleResult();

                InvoiceItemDiscountHolder holder = new InvoiceItemDiscountHolder();

                holder.setDiscountId(itemDiscount.getDiscountId());
                holder.setDiscountName(itemDiscount.getDiscountName());

                holder.setDiscountAmount(itemDiscount.getDiscountAmount());

                holder.setDiscountApplyType(itemDiscount.getDiscountApplyType().getDiscountApplyTypeId());
                holder.setDiscountCalculationType(itemDiscount.getDiscountCalculationType().getDiscountCalculationTypeId());

                holder.setDiscountValue(itemDiscount.getDiscountValue());
                holder.setDiscountValueType(itemDiscount.getDiscountValueType().getDiscountValueTypeId());

                holder.setDiscountDescription(itemDiscount.getDiscountDescription());

                invoiceItemHolder.setItemDiscount(holder);
            } catch (NoResultException nre) {
                // if no result just ignore.
            }
        }

        invoice.setInvoiceItems(listItems);

        // get transaction discount
        String queryTrxDiscount = "SELECT trxd FROM InvoiceTransactionDiscount trxd WHERE trxd.invoiceId = :invoiceId";
        try {
            InvoiceTransactionDiscount trxDiscount = em.createQuery(queryTrxDiscount, InvoiceTransactionDiscount.class)
                    .setParameter("invoiceId", invoice.getId())
                    .setMaxResults(1)
                    .getSingleResult();

            InvoiceTransactionDiscountHolder trxDiscountHolder = new InvoiceTransactionDiscountHolder();
            trxDiscountHolder.setDiscountId(trxDiscount.getDiscountId());
            trxDiscountHolder.setDiscountName(trxDiscount.getDiscountName());
            trxDiscountHolder.setDiscountDescription(trxDiscount.getDiscountDescription());

            trxDiscountHolder.setDiscountValue(trxDiscount.getDiscountValue());
            trxDiscountHolder.setDiscountValueType(trxDiscount.getDiscountValueType().getDiscountValueTypeId());
            trxDiscountHolder.setDiscountAmount(trxDiscount.getDiscountAmount());

            trxDiscountHolder.setDiscountApplyType(trxDiscount.getDiscountApplyType().getDiscountApplyTypeId());
            trxDiscountHolder.setDiscountCalculationType(trxDiscount.getDiscountCalculationType().getDiscountCalculationTypeId());

            invoice.setTransactionDiscount(trxDiscountHolder);
        } catch (NoResultException nre) {

        }

        String queryInvoicePricing = "SELECT NEW id.hardana.holder.invoice.InvoicePricingHolder(i.id, p.name, "
                + "i.pricingType, i.pricingValue, i.pricingAmount, i.pricingLevel) "
                + "FROM InvoicePricing i LEFT JOIN Pricing p ON i.pricingId = p.id WHERE i.invoiceId = :invoiceId";
        List<InvoicePricingHolder> listPricing = em.createQuery(queryInvoicePricing, InvoicePricingHolder.class)
                .setParameter("invoiceId", invoiceId)
                .getResultList();
        invoice.setInvoicePricing(listPricing);

        String queryTransaction = "SELECT NEW id.hardana.holder.transaction.TransactionPaymentHolder(tp.type, "
                + "t.referenceNumber, t.clientTransRefnum, t.amount, t.fee, "
                + "t.totalAmount, t.status, t.dateTime, c.pan, c.cardHolderName, p.account, p.firstName, "
                + "p.lastName, tpcc.cardType, tpcc.approvalCode) FROM TransactionPayment tp "
                + "LEFT JOIN TransactionTbl t ON tp.transactionId = t.id "
                + "LEFT JOIN Card c ON tp.sourceId = c.id LEFT JOIN PersonalInfo p ON tp.sourceId = p.id "
                + "LEFT JOIN TransactionPaymentCreditCard tpcc ON tpcc.transactionPaymentId = tp.id "
                + "WHERE tp.invoiceId = :invoiceId";
        List<TransactionPaymentHolder> listTransaction = em.createQuery(queryTransaction, TransactionPaymentHolder.class)
                .setParameter("invoiceId", invoiceId)
                .getResultList();
        invoice.setTransactions(listTransaction);

        return invoice;
    }

}
