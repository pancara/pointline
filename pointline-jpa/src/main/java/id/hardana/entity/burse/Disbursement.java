/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.entity.burse;

import id.hardana.entity.burse.enums.BurseStatusEnum;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hanafi
 */
@Entity
@Table(name = "disbursement")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Disbursement.findAll", query = "SELECT d FROM Disbursement d"),
        @NamedQuery(name = "Disbursement.findById", query = "SELECT d FROM Disbursement d WHERE d.id = :id")})
public class Disbursement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Transient
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Column(name = "number", unique = true)
    private String number;

    @Column(name = "datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private BurseStatusEnum status;

    @Column(name = "merchantid")
    private Long merchantId;

    @Column(name = "paymentcard")
    private BigDecimal paymentCard;

    @Column(name = "feecard")
    private BigDecimal feeCard;

    @Column(name = "paymentpersonal")
    private BigDecimal paymentPersonal;

    @Column(name = "feepersonal")
    private BigDecimal feePersonal;

    @Column(name = "paymentcreditcard")
    private BigDecimal paymentCreditCard;

    @Column(name = "feecreditcard")
    private BigDecimal feeCreditCard;

    @Column(name = "paymentcreditcardedc")
    private BigDecimal paymentCreditCardEDC;

    @Column(name = "feecreditcardedc")
    private BigDecimal feeCreditCardEDC;

    @Column(name = "paymentcash")
    private BigDecimal paymentCash;

    @Column(name = "feecash")
    private BigDecimal feeCash;

    @Column(name = "paymentvoucher")
    private BigDecimal paymentVoucher;

    @Column(name = "feevoucher")
    private BigDecimal feeVoucher;

    @Column(name = "paymentadjustment")
    private BigDecimal paymentAdjustment;

    @Column(name = "feeadjustment")
    private BigDecimal feeAdjustment;

    @Column(name = "feedebetcard")
    private BigDecimal feeDebetCard;

    @Column(name = "paymentdebetcard")
    private BigDecimal paymentDebetCard;

    @Column(name = "feereversalcash")
    private BigDecimal feeReversalCash;

    @Column(name = "paymentreversalcash")
    private BigDecimal paymentReversalCash;

    @Column(name = "feereversalcashcard")
    private BigDecimal feeReversalCashCard;

    @Column(name = "paymentreversalcashcard")
    private BigDecimal paymentReversalCashCard;

    @Column(name = "feereversalcreditcardedc")
    private BigDecimal feeReversalCreditCardEdc;

    @Column(name = "paymentreversalcreditcardedc")
    private BigDecimal paymentReversalCreditCardEdc;

    @Column(name = "feereversaldebitcard")
    private BigDecimal feeReversalDebitCard;

    @Column(name = "paymentreversaldebitcard")
    private BigDecimal paymentReversalDebitCard;

    @Column(name = "merchantfee")
    private BigDecimal merchantFee;

    @Column(name = "creditemoprofitaccount")
    private BigDecimal creditEmoProfitAccount;

    @Column(name = "creditnonemoprofitaccount")
    private BigDecimal creditNonEmoProfitAccount;

    @Column(name = "debetescrowaccount")
    private BigDecimal debetEscrowAccount;

    @Column(name = "merchantdisbursement")
    private BigDecimal merchantDisbursement;

    public Disbursement() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public BurseStatusEnum getStatus() {
        return status;
    }

    public void setStatus(BurseStatusEnum status) {
        this.status = status;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public BigDecimal getPaymentCard() {
        return paymentCard;
    }

    public void setPaymentCard(BigDecimal paymentCard) {
        this.paymentCard = paymentCard;
    }

    public BigDecimal getFeeCard() {
        return feeCard;
    }

    public void setFeeCard(BigDecimal feeCard) {
        this.feeCard = feeCard;
    }

    public BigDecimal getPaymentPersonal() {
        return paymentPersonal;
    }

    public void setPaymentPersonal(BigDecimal paymentPersonal) {
        this.paymentPersonal = paymentPersonal;
    }

    public BigDecimal getFeePersonal() {
        return feePersonal;
    }

    public void setFeePersonal(BigDecimal feePersonal) {
        this.feePersonal = feePersonal;
    }

    public BigDecimal getPaymentCreditCard() {
        return paymentCreditCard;
    }

    public void setPaymentCreditCard(BigDecimal paymentCreditCard) {
        this.paymentCreditCard = paymentCreditCard;
    }

    public BigDecimal getFeeCreditCard() {
        return feeCreditCard;
    }

    public void setFeeCreditCard(BigDecimal feeCreditCard) {
        this.feeCreditCard = feeCreditCard;
    }

    public BigDecimal getPaymentCreditCardEDC() {
        return paymentCreditCardEDC;
    }

    public void setPaymentCreditCardEDC(BigDecimal paymentCreditCardEDC) {
        this.paymentCreditCardEDC = paymentCreditCardEDC;
    }

    public BigDecimal getFeeCreditCardEDC() {
        return feeCreditCardEDC;
    }

    public void setFeeCreditCardEDC(BigDecimal feeCreditCardEDC) {
        this.feeCreditCardEDC = feeCreditCardEDC;
    }

    public BigDecimal getPaymentCash() {
        return paymentCash;
    }

    public void setPaymentCash(BigDecimal paymentCash) {
        this.paymentCash = paymentCash;
    }

    public BigDecimal getFeeCash() {
        return feeCash;
    }

    public void setFeeCash(BigDecimal feeCash) {
        this.feeCash = feeCash;
    }

    public BigDecimal getPaymentVoucher() {
        return paymentVoucher;
    }

    public void setPaymentVoucher(BigDecimal paymentVoucher) {
        this.paymentVoucher = paymentVoucher;
    }

    public BigDecimal getFeeVoucher() {
        return feeVoucher;
    }

    public void setFeeVoucher(BigDecimal feeVoucher) {
        this.feeVoucher = feeVoucher;
    }

    public BigDecimal getPaymentAdjustment() {
        return paymentAdjustment;
    }

    public void setPaymentAdjustment(BigDecimal paymentAdjustment) {
        this.paymentAdjustment = paymentAdjustment;
    }

    public BigDecimal getFeeAdjustment() {
        return feeAdjustment;
    }

    public void setFeeAdjustment(BigDecimal feeAdjustment) {
        this.feeAdjustment = feeAdjustment;
    }

    public BigDecimal getFeeDebetCard() {
        return feeDebetCard;
    }

    public void setFeeDebetCard(BigDecimal feeDebetCard) {
        this.feeDebetCard = feeDebetCard;
    }

    public BigDecimal getPaymentDebetCard() {
        return paymentDebetCard;
    }

    public void setPaymentDebetCard(BigDecimal paymentDebetCard) {
        this.paymentDebetCard = paymentDebetCard;
    }

    public BigDecimal getFeeReversalCash() {
        return feeReversalCash;
    }

    public void setFeeReversalCash(BigDecimal feeReversalCash) {
        this.feeReversalCash = feeReversalCash;
    }

    public BigDecimal getPaymentReversalCash() {
        return paymentReversalCash;
    }

    public void setPaymentReversalCash(BigDecimal paymentReversalCash) {
        this.paymentReversalCash = paymentReversalCash;
    }

    public BigDecimal getFeeReversalCashCard() {
        return feeReversalCashCard;
    }

    public void setFeeReversalCashCard(BigDecimal feeReversalCashCard) {
        this.feeReversalCashCard = feeReversalCashCard;
    }

    public BigDecimal getPaymentReversalCashCard() {
        return paymentReversalCashCard;
    }

    public void setPaymentReversalCashCard(BigDecimal paymentReversalCashCard) {
        this.paymentReversalCashCard = paymentReversalCashCard;
    }

    public BigDecimal getFeeReversalCreditCardEdc() {
        return feeReversalCreditCardEdc;
    }

    public void setFeeReversalCreditCardEdc(BigDecimal feeReversalCreditCardEdc) {
        this.feeReversalCreditCardEdc = feeReversalCreditCardEdc;
    }

    public BigDecimal getPaymentReversalCreditCardEdc() {
        return paymentReversalCreditCardEdc;
    }

    public void setPaymentReversalCreditCardEdc(BigDecimal paymentReversalCreditCardEdc) {
        this.paymentReversalCreditCardEdc = paymentReversalCreditCardEdc;
    }

    public BigDecimal getFeeReversalDebitCard() {
        return feeReversalDebitCard;
    }

    public void setFeeReversalDebitCard(BigDecimal feeReversalDebitCard) {
        this.feeReversalDebitCard = feeReversalDebitCard;
    }

    public BigDecimal getPaymentReversalDebitCard() {
        return paymentReversalDebitCard;
    }

    public void setPaymentReversalDebitCard(BigDecimal paymentReversalDebitCard) {
        this.paymentReversalDebitCard = paymentReversalDebitCard;
    }

    public BigDecimal getMerchantFee() {
        return merchantFee;
    }

    public void setMerchantFee(BigDecimal merchantFee) {
        this.merchantFee = merchantFee;
    }

    public BigDecimal getMerchantDisbursement() {
        return merchantDisbursement;
    }

    public void setMerchantDisbursement(BigDecimal merchantDisbursement) {
        this.merchantDisbursement = merchantDisbursement;
    }

    public BigDecimal getCreditEmoProfitAccount() {
        return creditEmoProfitAccount;
    }

    public void setCreditEmoProfitAccount(BigDecimal creditProfitAccount) {
        this.creditEmoProfitAccount = creditProfitAccount;
    }

    public BigDecimal getCreditNonEmoProfitAccount() {
        return creditNonEmoProfitAccount;
    }

    public void setCreditNonEmoProfitAccount(BigDecimal creditNonEmoProfitAccount) {
        this.creditNonEmoProfitAccount = creditNonEmoProfitAccount;
    }

    public BigDecimal getDebetEscrowAccount() {
        return debetEscrowAccount;
    }

    public void setDebetEscrowAccount(BigDecimal debetEscrowAccount) {
        this.debetEscrowAccount = debetEscrowAccount;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Disbursement that = (Disbursement) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
