/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.history.transaction;

import com.google.gson.Gson;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.enums.TransactionTransferTypeEnum;
import id.hardana.entity.transaction.enums.TransactionTypeEnum;
import id.hardana.holder.transaction.TransactionTransferPersonalHistoryHolder;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean

public class TransferToPersonalSearchBean {

    private final String STATUS_KEY = "status";
    private final String TRANSACTION_KEY = "transaction";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject getTransferPersonal(Long fromId, Long toId, String referenceNumber) {
        JSONObject response = new JSONObject();

        String queryTransferString = "SELECT NEW id.hardana.holder.transaction.TransactionTransferPersonalHistoryHolder"
                + "(t.id, t.referenceNumber, t.amount, t.status, t.dateTime, p1.account, p1.firstName, p1.lastName, "
                + "p2.account, p2.firstName, p2.lastName) "
                + "FROM TransactionTbl t JOIN TransactionTransfer tt ON tt.transactionId = t.id "
                + "JOIN PersonalInfo p1 ON tt.fromId = p1.id "
                + "JOIN PersonalInfo p2 ON tt.toId = p2.id "
                + "WHERE t.referenceNumber = :referenceNumber AND p1.id = :fromId AND p2.id = :toId "
                + "AND t.type = :transactionType AND tt.type = :transferType";

        Query queryTransfer = em.createQuery(queryTransferString, TransactionTransferPersonalHistoryHolder.class);
        queryTransfer.setParameter("referenceNumber", referenceNumber);
        queryTransfer.setParameter("fromId", fromId);
        queryTransfer.setParameter("toId", toId);
        queryTransfer.setParameter("transactionType", TransactionTypeEnum.TRANSFER);
        queryTransfer.setParameter("transferType", TransactionTransferTypeEnum.PERSONALTOPERSONAL);

        List<TransactionTransferPersonalHistoryHolder> listTransactionTransfer = queryTransfer.getResultList();

        if (listTransactionTransfer.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.TRANSACTION_NOT_FOUND.getResponseStatus());
            return response;
        }
        
        TransactionTransferPersonalHistoryHolder transactionTransfer =  listTransactionTransfer.get(0);

        Gson gson = new Gson();
        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(TRANSACTION_KEY, gson.toJson(transactionTransfer));
        return response;
    }

}
