/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.service.mpos.messaging;

import id.atv.pointline.misc.RupiahFormatter;
import id.hardana.ejb.system.tools.RupiahCurrencyFormat;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author arya
 */
public class CashPaymentEmailContent {
    
    RupiahFormatter rcf = new RupiahFormatter();

    private String setInvoiceContent(BigDecimal subTotalBeforeTax, List<String> listPricingName, List<String> listPricingAmount, String total)
    {
        String invoiceContent = "";
        if(!listPricingAmount.isEmpty()) {
            invoiceContent =  " <tfoot style=\"font-family:sans-serif;box-sizing:border-box;\" >\n";
            invoiceContent += setSubTotal(subTotalBeforeTax); 
            for (int i=0; i<listPricingAmount.size(); i++)
            {                
                 invoiceContent += "<tr style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                        "        <td colspan=\"2\" class=\"label\" style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc39a;text-align:right;background-color:rgba(162, 240, 218, 0.5);font-size:0.9rem;color:#333;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;font-weight:bold;letter-spacing:2px;\" >"+listPricingName.get(i)+"</td>\n" +
                        "        <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;border-width:1px;border-style:solid;border-color:#0cc39a;font-weight:bold;font-size:0.9rem;text-align:right;background-color:rgba(162, 240, 218, 0.1);\" > "+"Rp. "+rcf.toRupiahFormat(listPricingAmount.get(i)) +"</td>\n" +
                        "      </tr>\n" ;
            }
            
            invoiceContent += " <tr class=\"total\" style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                "        <td colspan=\"2\" class=\"label\" style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc39a;text-align:right;background-color:#0CC79D;font-size:1rem;color:#333;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;font-weight:bold;letter-spacing:2px;\" >TOTAL</td>\n" +
                "        <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;border-width:1px;border-style:solid;border-color:#0cc39a;font-weight:bold;font-size:1rem;text-align:right;background-color:rgba(162, 240, 218, 0.1);\" >"+"Rp. "+rcf.toRupiahFormat(total)+"</td>\n" +
                "      </tr>\n" +
                "      </tfoot>\n" +
                "    </table>\n" +
                "\n";
                    
        } else {
            invoiceContent =  "<tr>\n" +
                                            "<td>&nbsp;</td>\n" +
                        "			<td style=\"text-align: right;\">SubTotal</td>\n" +
                        "			<td style=\"text-align: right;\">"+"Rp. "+rcf.toRupiahFormat(subTotalBeforeTax.toString())+"</td>\n" +
                        "		</tr>\n" +
                        "		<tr>\n" +
                        "			<td>&nbsp;</td>\n" +
                        "			<td>TOTAL</td>\n" +
                        "			<td style=\"text-align: right;\">"+"Rp. "+rcf.toRupiahFormat(total)+"</td>\n" +
                        "		</tr>\n" +
                        "	</tbody>\n" +
                        "</table>\n" +
                        "\n" ;
        }
        return invoiceContent;
    }
    
    private String setInvoiceContentWithDiscount(BigDecimal subTotalBeforeTax, BigDecimal serviceTaxAmount, String itemsDiscountName, BigDecimal totalItemsDiscount,
                String transactionDiscountName, BigDecimal totalTransactionDiscount, List<String> listPricingName, List<String> listPricingAmount, String total)
    {
        String invoiceContent = " <tfoot style=\"font-family:sans-serif;box-sizing:border-box;\" >\n";
        String sTotalItemsDiscount = totalItemsDiscount.toString();
        String sTotalTransactionDiscount = totalTransactionDiscount.toString();
                
        if(!listPricingAmount.isEmpty()) {
            if(!sTotalItemsDiscount.equalsIgnoreCase("0") && sTotalTransactionDiscount.equalsIgnoreCase("0")) { 
                invoiceContent += setSubTotal(subTotalBeforeTax); 
                invoiceContent += "<tr style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    "        <td colspan=\"2\" class=\"label\" style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc39a;text-align:right;background-color:rgba(162, 240, 218, 0.5);font-size:0.9rem;color:#333;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;font-weight:bold;letter-spacing:2px;\" > " + itemsDiscountName + "</td>\n" +
                    "        <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;border-width:1px;border-style:solid;border-color:#0cc39a;font-weight:bold;font-size:0.9rem;text-align:right;background-color:rgba(162, 240, 218, 0.1);\" > "+"-Rp. "+rcf.toRupiahFormat(totalItemsDiscount.toString()) +"</td>\n" +
                    "      </tr>\n";        
                
                for (int i=0; i<listPricingAmount.size(); i++)
                {                
                    invoiceContent += "<tr style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                        "        <td colspan=\"2\" class=\"label\" style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc39a;text-align:right;background-color:rgba(162, 240, 218, 0.5);font-size:0.9rem;color:#333;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;font-weight:bold;letter-spacing:2px;\" >"+listPricingName.get(i)+"</td>\n" +
                        "        <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;border-width:1px;border-style:solid;border-color:#0cc39a;font-weight:bold;font-size:0.9rem;text-align:right;background-color:rgba(162, 240, 218, 0.1);\" > "+"Rp. "+rcf.toRupiahFormat(listPricingAmount.get(i)) +"</td>\n" +
                        "      </tr>\n" ;
                }
                invoiceContent += " <tr class=\"total\" style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    "        <td colspan=\"2\" class=\"label\" style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc39a;text-align:right;background-color:#0CC79D;font-size:1rem;color:#333;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;font-weight:bold;letter-spacing:2px;\" >TOTAL</td>\n" +
                    "        <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;border-width:1px;border-style:solid;border-color:#0cc39a;font-weight:bold;font-size:1rem;text-align:right;background-color:rgba(162, 240, 218, 0.1);\" >"+"Rp. "+rcf.toRupiahFormat(total)+"</td>\n" +
                    "      </tr>\n" +
                    "      </tfoot>\n" +
                    "    </table>\n";
            } 
            else if(!sTotalItemsDiscount.equalsIgnoreCase("0") && !sTotalTransactionDiscount.equalsIgnoreCase("0")) {
                invoiceContent += setSubTotal(subTotalBeforeTax); 
                invoiceContent += "<tr style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    "        <td colspan=\"2\" class=\"label\" style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc39a;text-align:right;background-color:rgba(162, 240, 218, 0.5);font-size:0.9rem;color:#333;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;font-weight:bold;letter-spacing:2px;\" > " + itemsDiscountName + "</td>\n" +
                    "        <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;border-width:1px;border-style:solid;border-color:#0cc39a;font-weight:bold;font-size:0.9rem;text-align:right;background-color:rgba(162, 240, 218, 0.1);\" > "+"-Rp. "+rcf.toRupiahFormat(totalItemsDiscount.toString()) +"</td>\n" +
                    "      </tr>\n" +
                    "<tr style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    "        <td colspan=\"2\" class=\"label\" style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc39a;text-align:right;background-color:rgba(162, 240, 218, 0.5);font-size:0.9rem;color:#333;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;font-weight:bold;letter-spacing:2px;\" > " + transactionDiscountName + "</td>\n" +
                    "        <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;border-width:1px;border-style:solid;border-color:#0cc39a;font-weight:bold;font-size:0.9rem;text-align:right;background-color:rgba(162, 240, 218, 0.1);\" > "+"-Rp. "+rcf.toRupiahFormat(totalTransactionDiscount.toString()) +"</td>\n" +
                    "      </tr>\n";
                
                if(!listPricingAmount.isEmpty()) {
                    for (int i=0; i<listPricingAmount.size(); i++)
                    {                
                        invoiceContent += "<tr style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                        "        <td colspan=\"2\" class=\"label\" style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc39a;text-align:right;background-color:rgba(162, 240, 218, 0.5);font-size:0.9rem;color:#333;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;font-weight:bold;letter-spacing:2px;\" >"+listPricingName.get(i)+"</td>\n" +
                        "        <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;border-width:1px;border-style:solid;border-color:#0cc39a;font-weight:bold;font-size:0.9rem;text-align:right;background-color:rgba(162, 240, 218, 0.1);\" > "+"Rp. "+rcf.toRupiahFormat(listPricingAmount.get(i)) +"</td>\n" +
                        "      </tr>\n" ;
                    }
                }
                
                invoiceContent += " <tr class=\"total\" style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    "        <td colspan=\"2\" class=\"label\" style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc39a;text-align:right;background-color:#0CC79D;font-size:1rem;color:#333;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;font-weight:bold;letter-spacing:2px;\" >TOTAL</td>\n" +
                    "        <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;border-width:1px;border-style:solid;border-color:#0cc39a;font-weight:bold;font-size:1rem;text-align:right;background-color:rgba(162, 240, 218, 0.1);\" >"+"Rp. "+rcf.toRupiahFormat(total)+"</td>\n" +
                    "      </tr>\n" +
                    "      </tfoot>\n" +
                    "    </table>\n";
            }
            else if(sTotalItemsDiscount.equalsIgnoreCase("0") && !sTotalTransactionDiscount.equalsIgnoreCase("0")) { 
                invoiceContent += setSubTotal(subTotalBeforeTax); 
                invoiceContent += "<tr style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    "        <td colspan=\"2\" class=\"label\" style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc39a;text-align:right;background-color:rgba(162, 240, 218, 0.5);font-size:0.9rem;color:#333;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;font-weight:bold;letter-spacing:2px;\" > " + transactionDiscountName + "</td>\n" +
                    "        <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;border-width:1px;border-style:solid;border-color:#0cc39a;font-weight:bold;font-size:0.9rem;text-align:right;background-color:rgba(162, 240, 218, 0.1);\" > "+"-Rp. "+rcf.toRupiahFormat(totalTransactionDiscount.toString()) +"</td>\n" +
                    "      </tr>\n";  
                
                if(!listPricingAmount.isEmpty()) {
                    for (int i=0; i<listPricingAmount.size(); i++)
                    {                
                        invoiceContent += "<tr style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                        "        <td colspan=\"2\" class=\"label\" style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc39a;text-align:right;background-color:rgba(162, 240, 218, 0.5);font-size:0.9rem;color:#333;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;font-weight:bold;letter-spacing:2px;\" >"+listPricingName.get(i)+"</td>\n" +
                        "        <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;border-width:1px;border-style:solid;border-color:#0cc39a;font-weight:bold;font-size:0.9rem;text-align:right;background-color:rgba(162, 240, 218, 0.1);\" > "+"Rp. "+rcf.toRupiahFormat(listPricingAmount.get(i)) +"</td>\n" +
                        "      </tr>\n" ;
                    }
                }
                invoiceContent += " <tr class=\"total\" style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    "        <td colspan=\"2\" class=\"label\" style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc39a;text-align:right;background-color:#0CC79D;font-size:1rem;color:#333;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;font-weight:bold;letter-spacing:2px;\" >TOTAL</td>\n" +
                    "        <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;border-width:1px;border-style:solid;border-color:#0cc39a;font-weight:bold;font-size:1rem;text-align:right;background-color:rgba(162, 240, 218, 0.1);\" >"+"Rp. "+rcf.toRupiahFormat(total)+"</td>\n" +
                    "      </tr>\n" +
                    "      </tfoot>\n" +
                    "    </table>\n";
            }
        } else {
            if(!sTotalItemsDiscount.equalsIgnoreCase("0") && sTotalTransactionDiscount.equalsIgnoreCase("0")) { 
                invoiceContent += setSubTotal(subTotalBeforeTax); 
                invoiceContent += 
                    "<tr style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    "        <td colspan=\"2\" class=\"label\" style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc39a;text-align:right;background-color:rgba(162, 240, 218, 0.5);font-size:0.9rem;color:#333;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;font-weight:bold;letter-spacing:2px;\" > " + itemsDiscountName + "</td>\n" +
                    "        <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;border-width:1px;border-style:solid;border-color:#0cc39a;font-weight:bold;font-size:0.9rem;text-align:right;background-color:rgba(162, 240, 218, 0.1);\" > "+"-Rp. "+rcf.toRupiahFormat(totalItemsDiscount.toString()) +"</td>\n" +
                    "      </tr>\n" +
                    " <tr class=\"total\" style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    "        <td colspan=\"2\" class=\"label\" style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc39a;text-align:right;background-color:#0CC79D;font-size:1rem;color:#333;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;font-weight:bold;letter-spacing:2px;\" >TOTAL</td>\n" +
                    "        <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;border-width:1px;border-style:solid;border-color:#0cc39a;font-weight:bold;font-size:1rem;text-align:right;background-color:rgba(162, 240, 218, 0.1);\" >"+"Rp. "+rcf.toRupiahFormat(total)+"</td>\n" +
                    "      </tr>\n" +
                    "      </tfoot>\n" +
                    "    </table>\n";
            } 
            else if(!sTotalItemsDiscount.equalsIgnoreCase("0") && !sTotalTransactionDiscount.equalsIgnoreCase("0")) {
                invoiceContent += setSubTotal(subTotalBeforeTax); 
                invoiceContent +=         
                    "<tr style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    "        <td colspan=\"2\" class=\"label\" style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc39a;text-align:right;background-color:rgba(162, 240, 218, 0.5);font-size:0.9rem;color:#333;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;font-weight:bold;letter-spacing:2px;\" > " + itemsDiscountName + "</td>\n" +
                    "        <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;border-width:1px;border-style:solid;border-color:#0cc39a;font-weight:bold;font-size:0.9rem;text-align:right;background-color:rgba(162, 240, 218, 0.1);\" > "+"-Rp. "+rcf.toRupiahFormat(totalItemsDiscount.toString()) +"</td>\n" +
                    "      </tr>\n" +
                    "<tr style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    "        <td colspan=\"2\" class=\"label\" style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc39a;text-align:right;background-color:rgba(162, 240, 218, 0.5);font-size:0.9rem;color:#333;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;font-weight:bold;letter-spacing:2px;\" > " + transactionDiscountName + "</td>\n" +
                    "        <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;border-width:1px;border-style:solid;border-color:#0cc39a;font-weight:bold;font-size:0.9rem;text-align:right;background-color:rgba(162, 240, 218, 0.1);\" > "+"-Rp. "+rcf.toRupiahFormat(totalTransactionDiscount.toString()) +"</td>\n" +
                    "      </tr>\n" +
                    " <tr class=\"total\" style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    "        <td colspan=\"2\" class=\"label\" style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc39a;text-align:right;background-color:#0CC79D;font-size:1rem;color:#333;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;font-weight:bold;letter-spacing:2px;\" >TOTAL</td>\n" +
                    "        <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;border-width:1px;border-style:solid;border-color:#0cc39a;font-weight:bold;font-size:1rem;text-align:right;background-color:rgba(162, 240, 218, 0.1);\" >"+"Rp. "+rcf.toRupiahFormat(total)+"</td>\n" +
                    "      </tr>\n" +
                    "      </tfoot>\n" +
                    "    </table>\n";
            }
            else if(sTotalItemsDiscount.equalsIgnoreCase("0") && !sTotalTransactionDiscount.equalsIgnoreCase("0")) { 
                invoiceContent += setSubTotal(subTotalBeforeTax); 
                invoiceContent +=
                    "<tr style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    "        <td colspan=\"2\" class=\"label\" style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc39a;text-align:right;background-color:rgba(162, 240, 218, 0.5);font-size:0.9rem;color:#333;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;font-weight:bold;letter-spacing:2px;\" > " + transactionDiscountName + "</td>\n" +
                    "        <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;border-width:1px;border-style:solid;border-color:#0cc39a;font-weight:bold;font-size:0.9rem;text-align:right;background-color:rgba(162, 240, 218, 0.1);\" > "+"-Rp. "+rcf.toRupiahFormat(totalTransactionDiscount.toString()) +"</td>\n" +
                    "      </tr>\n" +
                    " <tr class=\"total\" style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    "        <td colspan=\"2\" class=\"label\" style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc39a;text-align:right;background-color:#0CC79D;font-size:1rem;color:#333;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;font-weight:bold;letter-spacing:2px;\" >TOTAL</td>\n" +
                    "        <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;border-width:1px;border-style:solid;border-color:#0cc39a;font-weight:bold;font-size:1rem;text-align:right;background-color:rgba(162, 240, 218, 0.1);\" >"+"Rp. "+rcf.toRupiahFormat(total)+"</td>\n" +
                    "      </tr>\n" +
                    "      </tfoot>\n" +
                    "    </table>\n";
            }
        }
        return invoiceContent;
    }
    
    private String setTransactionInfo(String customerName, String merchantName, String outletName, String outletAddress, String timeStamp, 
                            String userName, String tableNumber){
        
        String transactionInfoResult = "";
        String tableNumberHeadText = setHeaderTags(customerName, merchantName, outletName, outletAddress, timeStamp, userName);
        
        String tableNumberTailText =      "<tr class='bill-profile-detail' style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
            "      <td class='label' style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
            "        Table Number\n" +
            "      </td>\n" +
            "      <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    tableNumber + "\n" +
            "      </td>\n" +
            "    </tr>\n" ;
        
        String htmlScriptAdded =  "  </table>\n" +
                                  "</div>";
        
        if(tableNumber.isEmpty())
            transactionInfoResult = tableNumberHeadText + htmlScriptAdded;
        else
            transactionInfoResult = tableNumberHeadText + tableNumberTailText + htmlScriptAdded;
        return transactionInfoResult;
    }
    
    private String setHeaderTags(String customerName, String merchantName, String outletName, String outletAddress, String timeStamp, String operatorName){
        String headerTags= " <html class=\"html\" lang=\"en-US\" style=\"font-family:sans-serif;box-sizing:border-box;font-size:14px;\" >\n" +
            "<head style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
            "  <meta http-equiv=\"Content-type\" content=\"text/html;charset=UTF-8\" style=\"font-family:sans-serif;box-sizing:border-box;\" />\n" +
            "  <title style=\"font-family:sans-serif;box-sizing:border-box;\" >Home</title>\n" +
            "</head>\n" +
            "<body style=\"font-family:sans-serif;box-sizing:border-box;width:768px;background-color:#ffffff;background-image:none;background-repeat:repeat;background-position:top left;background-attachment:scroll;font-size:0.8rem;\" >\n" +
            "<style style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
            "  html {\n" +
            "    font-size: 14px; }\n" +
            "\n" +
            "  * {\n" +
            "    font-family: sans-serif;\n" +
            "    box-sizing: border-box; }\n" +
            "\n" +
            "  body {\n" +
            "    width: 768px;\n" +
            "    background: #f0f0f0;\n" +
            "    font-size: 0.8rem; }\n" +
            "\n" +
            "  h2 {\n" +
            "    font-size: 1.2rem;\n" +
            "    padding: 8px 8px 8px 8px;\n" +
            "    margin: 0px 0px 0px 0px;\n" +
            "    color: #FFF;\n" +
            "    text-shadow: 0px 0px 2px rgba(51, 51, 51, 0.2); }\n" +
            "\n" +
            "  div.header {\n" +
            "    background: #f0f0f0;\n" +
            "    text-align: center;\n" +
            "    padding-bottom: 48px; }\n" +
            "  div.header p {\n" +
            "    color: #666;\n" +
            "    font-size: 14px;\n" +
            "    line-height: 18px;\n" +
            "    margin: 0 0 0 0;\n" +
            "    padding: 0 0 0 0; }\n" +
            "  div.header p.cust-name {\n" +
            "    font-family: cabin, sans-serif;\n" +
            "    font-size: 24px;\n" +
            "    margin-top: 24px;\n" +
            "    margin-bottom: 12px;\n" +
            "    color: #21B2EF; }\n" +
            "\n" +
            "  div.bill-profile h2 {\n" +
            "    background: #c8c8c8;\n" +
            "    font-size: 1.2rem;\n" +
            "    font-weight: bold;\n" +
            "    color: #FFF;\n" +
            "    padding: 8px 8px 8px 8px; }\n" +
            "\n" +
            "  div.bill-profile table tbody tr td {\n" +
            "    color: #555;\n" +
            "    padding: 8px 16px 8px 8px; }\n" +
            "  div.bill-profile table tbody tr td.label {\n" +
            "    font-size: 0.85rem; }\n" +
            "  div.bill-profile table tbody tr td.value {\n" +
            "    font-weight: bold;\n" +
            "    font-size: 1rem; }\n" +
            "\n" +
            "  div.due-date h2 {\n" +
            "    background: #ffce73;\n" +
            "    font-size: 1.2rem;\n" +
            "    font-weight: bold;\n" +
            "    color: #FFF;\n" +
            "    padding: 8px 8px 8px 8px; }\n" +
            "\n" +
            "  div.due-date div {\n" +
            "    padding-top: 18px;\n" +
            "    padding-bottom: 18px;\n" +
            "    font-size: 1.2rem;\n" +
            "    font-weight: bold;\n" +
            "    color: #cc0000;\n" +
            "    text-align: center;\n" +
            "    text-shadow: 0px 0px 2px #FFF; }\n" +
            "\n" +
            "  div.bill-detail h2 {\n" +
            "    background: #7f7f7f;\n" +
            "    margin: 0 0 0 0;\n" +
            "    padding: 8px 8px 8px 8px;\n" +
            "    font-size: 16px; }\n" +
            "\n" +
            "  div.bill-detail table {\n" +
            "    margin: 8px 0 8px 0;\n" +
            "    width: 100%;\n" +
            "    border-collapse: collapse; }\n" +
            "  div.bill-detail table thead th {\n" +
            "    background-color: #0CC79D;\n" +
            "    color: #FFF;\n" +
            "    padding: 8px 8px 8px 8px;\n" +
            "    font-size: 0.9rem;\n" +
            "    text-align: left;\n" +
            "    border: 1px solid #0cc39a; }\n" +
            "  div.bill-detail table tbody td {\n" +
            "    padding: 8px 8px 8px 8px;\n" +
            "    border: 1px solid #0cc69c;\n" +
            "    background: #FFF;\n" +
            "    font-size: 0.9rem;\n" +
            "    color: #333; }\n" +
            "  div.bill-detail table tbody td.number {\n" +
            "    text-align: right;\n" +
            "    padding: 8px 8px 8px 8px; }\n" +
            "  div.bill-detail table tfoot tr td {\n" +
            "    padding: 8px 8px 8px 8px;\n" +
            "    border: 1px solid #0cc39a;\n" +
            "    text-align: right; }\n" +
            "  div.bill-detail table tfoot tr td.label {\n" +
            "    background-color: rgba(162, 240, 218, 0.5);\n" +
            "    font-size: 0.9rem;\n" +
            "    color: #333;\n" +
            "    padding: 8px 8px 8px 8px;\n" +
            "    font-weight: bold;\n" +
            "    letter-spacing: 2px; }\n" +
            "  div.bill-detail table tfoot tr td.value {\n" +
            "    font-weight: bold;\n" +
            "    font-size: 0.9rem;\n" +
            "    text-align: right;\n" +
            "    background-color: rgba(162, 240, 218, 0.1); }\n" +
            "  div.bill-detail table tfoot tr.total td.label {\n" +
            "    background-color: #0CC79D;\n" +
            "    font-size: 1rem;\n" +
            "    color: #333;\n" +
            "    padding: 8px 8px 8px 8px;\n" +
            "    font-weight: bold;\n" +
            "    letter-spacing: 2px; }\n" +
            "  div.bill-detail table tfoot tr.total td.value {\n" +
            "    font-weight: bold;\n" +
            "    font-size: 1rem;\n" +
            "    text-align: right;\n" +
            "    background-color: rgba(162, 240, 218, 0.1); }\n" +
            "\n" +
            "  div.payment-detail h2 {\n" +
            "    background: #7f7f7f;\n" +
            "    margin: 0 0 0 0;\n" +
            "    padding: 8px 8px 8px 8px;\n" +
            "    font-size: 1.2rem; }\n" +
            "\n" +
            "  div.payment-detail table td {\n" +
            "    font-size: 0.9rem;\n" +
            "    padding: 8px 16px 8px 8px; }\n" +
            "\n" +
            "  div.footer {\n" +
            "    padding: 8px 16px 8px 8px; }\n" +
            "  div.footer p {\n" +
            "    font-size: 0.9rem; }\n" +
            "  div.footer a {\n" +
            "    color: #2f6fad;\n" +
            "    font-size: 1rem; }\n" +
            "\n" +
            "  div.spacer {\n" +
            "    background-color: #FFBA00;\n" +
            "    max-height: 32px;\n" +
            "    margin: 0 0 0 0; }\n" +
            "  \n" +
            "</style>\n" +
            "<div class='banner' style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
            "  <img class=\"logo\" src=\"http://i.imgur.com/RMtnxSj.png\" width=\"99\" height=\"34\" alt=\"\" style=\"font-family:sans-serif;box-sizing:border-box; margin: 16px 16px 8px 16px;\" />\n" +
            "</div>\n" +
            "<div class='header' style=\"font-family:sans-serif;box-sizing:border-box;background-color:#f0f0f0;background-image:none;background-repeat:repeat;background-position:top left;background-attachment:scroll;text-align:center;padding-bottom:48px;\" >\n" +
            "  <p class='cust-name' style=\"box-sizing:border-box;line-height:18px;margin-right:0;margin-left:0;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;font-family:cabin, sans-serif;font-size:24px;margin-top:24px;margin-bottom:12px;color:#21B2EF;\" >"
                + "Dear Mr/Mrs "+ customerName + ", </p>\n" +
            "\n" +
            "  <p style=\"font-family:sans-serif;box-sizing:border-box;color:#666;font-size:14px;line-height:18px;margin-top:0;margin-bottom:0;margin-right:0;margin-left:0;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;\" >\n" +
            "<!--#if($mailTransactionObject.mailType.name() == \"DUE_DATE_MERCHANT_BILL_NOTIFICATION\")\n" +
            "You have an outstanding invoice that is already past due date.\n" +
            "#elseif($mailTransactionObject.mailType.name() == \"PAID_MERCHANT_BILL_NOTIFICATION\")\n" +
            "Thank you for your payment.\n" +
            "#else\n" +
            "-->\n" +
            "Below is information about details of your transaction:\n" +
            "\n" +
            "  </p>\n" +
            "  <!--p style=\"font-family:sans-serif;box-sizing:border-box;color:#666;font-size:14px;line-height:18px;margin-top:0;margin-bottom:0;margin-right:0;margin-left:0;padding-top:0;padding-bottom:0;padding-right:0;padding-left:0;\" >\n" +
            "#if($mailTransactionObject.mailType.name() == \"PAID_MERCHANT_BILL_NOTIFICATION\")\n" +
            "The following is the detail transaction.\n" +
            "#else\n" +
            "The following invoice is prepared for you.\n" +
            "#end\n" +
            "  </p-->\n" +
            "</div>\n" +
            "<div class='bill-profile' style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
            "  <h2 style=\"font-family:sans-serif;box-sizing:border-box;margin-top:0px;margin-bottom:0px;margin-right:0px;margin-left:0px;text-shadow:0px 0px 2px rgba(51, 51, 51, 0.2);background-color:#c8c8c8;background-image:none;background-repeat:repeat;background-position:top left;background-attachment:scroll;font-size:1.2rem;font-weight:bold;color:#FFF;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;\" >Transaction Info</h2>\n" +
            "  <table style=\"font-family:sans-serif;box-sizing:border-box;width:100%;\" >\n" +
            "    <tr style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
            "      <td  class='label' style=\"font-family:sans-serif;box-sizing:border-box;width:200px;\" >\n" +
            "        Merchant Name\n" +
            "      </td>\n" +
            "      <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    merchantName + "\n" +
            "      </td>\n" +
            "    </tr>\n" +
            "    <tr class='bill-profile-detail' style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
            "      <td class='label' style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
            "        Outlet Name\n" +
            "      </td>\n" +
            "      <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    outletName +"\n" +
            "      </td>\n" +
            "    </tr>\n" +
            "    <tr class='bill-profile-detail' style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
            "      <td class='label' style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
            "        Outlet Address\n" +
            "      </td>\n" +
            "      <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    outletAddress + "\n" +
            "      </td>\n" +
            "    </tr>\n" +
            "	<tr class='bill-profile-detail' style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
            "      <td class='label' style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
            "        Date - Time\n" +
            "      </td>\n" +
            "      <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    timeStamp + "\n" +
            "      </td>\n" +
            "    </tr>\n" +
            "	<tr class='bill-profile-detail' style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
            "      <td class='label' style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
            "        Operator Name\n" +
            "      </td>\n" +
            "      <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    operatorName + "\n" +
            "      </td>\n" +
            "    </tr>\n";
        
        return headerTags;
    }
    
    private String setTableTitle(String invoiceNumber){
        String tableNumberText= "<div class='due-date' style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
            "  <div style=\"font-family:sans-serif;box-sizing:border-box;padding-top:18px;padding-bottom:18px;font-size:1.2rem;font-weight:bold;color:#cc0000;text-align:center;text-shadow:0px 0px 2px #FFF;\" >\n" +
//            "   \n" +
            "  </div>\n" +
            "</div>\n" +
         //   "\n" +
            "<div class='bill-detail' style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
            "  <h2 style=\"font-family:sans-serif;box-sizing:border-box;color:#FFF;text-shadow:0px 0px 2px rgba(51, 51, 51, 0.2);background-color:#7f7f7f;background-image:none;background-repeat:repeat;background-position:top left;background-attachment:scroll;margin-top:0;margin-bottom:0;margin-right:0;margin-left:0;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;font-size:16px;\" >Invoice "+ invoiceNumber + "</h2>\n" +
            "\n" +
            "  <div style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
            "    <table style=\"font-family:sans-serif;box-sizing:border-box;margin-top:8px;margin-bottom:8px;margin-right:0;margin-left:0;width:100%;border-collapse:collapse;\" >\n" +
            "      <thead style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
            "      <tr style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
            "        <th style=\"font-family:sans-serif;box-sizing:border-box;background-color:#0CC79D;color:#FFF;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;font-size:0.9rem;text-align:left;border-width:1px;border-style:solid;border-color:#0cc39a;\" >Name</th>\n" +
            "        <th style=\"font-family:sans-serif;box-sizing:border-box;background-color:#0CC79D;color:#FFF;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;font-size:0.9rem;text-align:left;border-width:1px;border-style:solid;border-color:#0cc39a;\" >Quantity</th>\n" +
            "        <th style=\"font-family:sans-serif;box-sizing:border-box;background-color:#0CC79D;color:#FFF;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;font-size:0.9rem;text-align:left;border-width:1px;border-style:solid;border-color:#0cc39a;\" >Amount</th>\n" +
            "      </tr>\n" +
            "      </thead>\n";
        return tableNumberText;
    }   

    public String setEmailCashPayment(String customerName, String userName, String merchantName, String outletName, String tableNumber, String outletAddress, String timeStamp, String uid,
                    String invoiceNumber, List<String> listItem, List<String> listQty, List<String> listPrc, List<String> listSubTotal, List<String> listPricingName, List<String> listPricingAmount, String total) {
       
        String emailText = "";
        String itemsText = "" ;
        String invoiceTail = "";
        String concatedItems = "";   
        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal subTotalBeforeTax = BigDecimal.ZERO;
        
        if  ("0".equals(tableNumber)) {  
            emailText = setTransactionInfo(customerName, merchantName, outletName, outletAddress, timeStamp, userName, ""); //uid, invoiceNumber,      
            String tableNumberText = setTableTitle(invoiceNumber);
            
            for (int i=0; i<listItem.size(); i++)
            {
                itemsText = "<tr style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    "<td style=\"font-family:sans-serif;box-sizing:border-box;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;border-width:1px;border-style:solid;border-color:#0cc69c;background-color:#FFF;background-image:none;background-repeat:repeat;background-position:top left;background-attachment:scroll;font-size:0.9rem;color:#333;\" >" +
                            listItem.get(i) + "</td>\n" +
                    "<td class='number' style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc69c;background-color:#FFF;background-image:none;background-repeat:repeat;background-position:top left;background-attachment:scroll;font-size:0.9rem;color:#333;text-align:right;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;\" > " + 
                            listQty.get(i) + "</td>\n" +
                    "<td class='number' style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc69c;background-color:#FFF;background-image:none;background-repeat:repeat;background-position:top left;background-attachment:scroll;font-size:0.9rem;color:#333;text-align:right;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;\" > " + 
                                    "Rp. " + rcf.toRupiahFormat(listSubTotal.get(i)) + "</td>\n" +
                    "</tr>\n" ;
                
                concatedItems += itemsText;
                subTotalBeforeTax = subTotalBeforeTax.add(subTotal.add(new BigDecimal(listSubTotal.get(i))));
            }
            
            concatedItems += "</tbody>\n" ;
            emailText += tableNumberText + " <tbody style=\"font-family:sans-serif;box-sizing:border-box;\" > \n" ;
            
            invoiceTail = setInvoiceContent(subTotalBeforeTax, listPricingName, listPricingAmount, total); 
        }
        
        else if (!"0".equals(tableNumber)) {           
            emailText = setTransactionInfo(customerName, merchantName, outletName, outletAddress, timeStamp, userName, tableNumber); //uid, invoiceNumber,
            String tableNumberText = setTableTitle(invoiceNumber);
            
            for (int i=0; i<listItem.size(); i++)
            {                
                itemsText = "<tr style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    "<td style=\"font-family:sans-serif;box-sizing:border-box;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;border-width:1px;border-style:solid;border-color:#0cc69c;background-color:#FFF;background-image:none;background-repeat:repeat;background-position:top left;background-attachment:scroll;font-size:0.9rem;color:#333;\" >" +
                            listItem.get(i) + "</td>\n" +
                    "<td class='number' style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc69c;background-color:#FFF;background-image:none;background-repeat:repeat;background-position:top left;background-attachment:scroll;font-size:0.9rem;color:#333;text-align:right;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;\" > " + 
                            listQty.get(i) + "</td>\n" +
                    "<td class='number' style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc69c;background-color:#FFF;background-image:none;background-repeat:repeat;background-position:top left;background-attachment:scroll;font-size:0.9rem;color:#333;text-align:right;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;\" > " + 
                            "Rp. " + rcf.toRupiahFormat(listSubTotal.get(i)) + "</td>\n" +
                    "</tr>\n" ;
                
                concatedItems += itemsText;
                subTotalBeforeTax = subTotalBeforeTax.add(subTotal.add(new BigDecimal(listSubTotal.get(i))));   
            }
            concatedItems += "</tbody>\n" ;
            emailText += tableNumberText + " <tbody style=\"font-family:sans-serif;box-sizing:border-box;\" > \n" ;
          
            invoiceTail = setInvoiceContent(subTotalBeforeTax, listPricingName, listPricingAmount, total);  
        }

        String emailTextResult = emailText + concatedItems + invoiceTail + setFooter(merchantName);
        
        listItem.clear();
        listQty.clear();
        listPrc.clear();
        listSubTotal.clear();
        
        return emailTextResult;
    }
    
    public String setEmailCashPaymentWithDiscount(String customerName, String userName, String merchantName, String outletName, String tableNumber, String outletAddress, String timeStamp, String uid,
                    String invoiceNumber, List<String> listItem, List<String> listQty, List<String> listPrc, List<String> listSubTotal, 
                    String itemsDiscountName, String totalItemsDiscount, String transactionDiscountName, String totalTransactionDiscount, 
                    List<String> listPricingName, List<String> listPricingAmount, String total) {
       
        String emailText = "";
        String invoiceTail = "";
        String concatedItems = "";   
        String itemsText = "";  
        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal subTotalBeforeTax = BigDecimal.ZERO;
        BigDecimal serviceTaxAmount = BigDecimal.ZERO;
        
        if  ("0".equals(tableNumber)) {  
            emailText = setTransactionInfo(customerName, merchantName, outletName, outletAddress, timeStamp, userName, ""); //uid, invoiceNumber,      
            String tableNumberText = setTableTitle(invoiceNumber);
             
            for (int i=0; i<listItem.size(); i++)
            {                
               itemsText = "<tr style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    "<td style=\"font-family:sans-serif;box-sizing:border-box;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;border-width:1px;border-style:solid;border-color:#0cc69c;background-color:#FFF;background-image:none;background-repeat:repeat;background-position:top left;background-attachment:scroll;font-size:0.9rem;color:#333;\" >" +
                            listItem.get(i) + "</td>\n" +
                    "<td class='number' style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc69c;background-color:#FFF;background-image:none;background-repeat:repeat;background-position:top left;background-attachment:scroll;font-size:0.9rem;color:#333;text-align:right;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;\" > " + 
                            listQty.get(i) + "</td>\n" +
                    "<td class='number' style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc69c;background-color:#FFF;background-image:none;background-repeat:repeat;background-position:top left;background-attachment:scroll;font-size:0.9rem;color:#333;text-align:right;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;\" > " + 
                                    "Rp. " + rcf.toRupiahFormat(listSubTotal.get(i)) + "</td>\n" +
                    "</tr>\n" ;
               
               concatedItems += itemsText;
               subTotalBeforeTax = subTotalBeforeTax.add(subTotal.add(new BigDecimal(listSubTotal.get(i))));
            }
            concatedItems += "</tbody>\n" ;
            emailText += tableNumberText + " <tbody style=\"font-family:sans-serif;box-sizing:border-box;\" > \n" ;

            invoiceTail = setInvoiceContentWithDiscount(subTotalBeforeTax, serviceTaxAmount, itemsDiscountName, new BigDecimal(totalItemsDiscount), transactionDiscountName, new BigDecimal(totalTransactionDiscount), listPricingName, listPricingAmount, total);            
        }
        
        else if (!"0".equals(tableNumber)) {           
            emailText = setTransactionInfo(customerName, merchantName, outletName, outletAddress, timeStamp, userName, tableNumber); //uid, invoiceNumber,      
            String tableNumberText = setTableTitle(invoiceNumber);
            
            for (int i=0; i<listItem.size(); i++)
            {                
                itemsText = "<tr style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    "<td style=\"font-family:sans-serif;box-sizing:border-box;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;border-width:1px;border-style:solid;border-color:#0cc69c;background-color:#FFF;background-image:none;background-repeat:repeat;background-position:top left;background-attachment:scroll;font-size:0.9rem;color:#333;\" >" +
                            listItem.get(i) + "</td>\n" +
                    "<td class='number' style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc69c;background-color:#FFF;background-image:none;background-repeat:repeat;background-position:top left;background-attachment:scroll;font-size:0.9rem;color:#333;text-align:right;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;\" > " + 
                            listQty.get(i) + "</td>\n" +
                    "<td class='number' style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc69c;background-color:#FFF;background-image:none;background-repeat:repeat;background-position:top left;background-attachment:scroll;font-size:0.9rem;color:#333;text-align:right;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;\" > " + 
                                    "Rp. " + rcf.toRupiahFormat(listSubTotal.get(i)) + "</td>\n" +
                    "</tr>\n" ;
                
               concatedItems += itemsText;
               subTotalBeforeTax = subTotalBeforeTax.add(subTotal.add(new BigDecimal(listSubTotal.get(i))));   
            }
            
            concatedItems += "</tbody>\n" ;
            emailText += tableNumberText + " <tbody style=\"font-family:sans-serif;box-sizing:border-box;\" > \n" ;
            invoiceTail = setInvoiceContentWithDiscount(subTotalBeforeTax, serviceTaxAmount, itemsDiscountName, new BigDecimal(totalItemsDiscount), transactionDiscountName, new BigDecimal(totalTransactionDiscount), listPricingName, listPricingAmount, total); 
        }

        String emailTextResult = emailText + concatedItems + invoiceTail + setFooter(merchantName);
        listItem.clear();
        listQty.clear();
        listPrc.clear();
        listSubTotal.clear();
        return emailTextResult;
    }
    
    /* html tags */      
    private String setFooter(String brandName){
        
        if(null == brandName){
            brandName = "-";
        }
        
        String footer = "\n" +
        "  <br style=\"font-family:sans-serif;box-sizing:border-box;\" />\n" +
        "  <br style=\"font-family:sans-serif;box-sizing:border-box;\" />\n" +
        "\n" +
        "  <p style=\"font-family:sans-serif;box-sizing:border-box;font-size:0.9rem;\" >Thank you,</p>\n" +
        "  <p style=\"font-family:sans-serif;box-sizing:border-box;font-size:0.9rem;\" >Best Regards,</p>\n" +
        "  <p style=\"font-family:sans-serif;box-sizing:border-box;font-size:0.9rem;\" > " + brandName  + "</p>\n" +
         "  <br style=\"font-family:sans-serif;box-sizing:border-box;\" />\n" +
        "\n" +
        "  <p style=\"font-family:sans-serif;box-sizing:border-box;text-align:right;font-size:12px;\" ><em style=\"font-family:sans-serif;box-sizing:border-box;\" >Powered by EMO</em></p>\n" +
        "</div>\n" +
        "</body>\n" +
        "</html>";
        
        return footer;
    }
    
    
    private String setSubTotal(BigDecimal subTotalBeforeTax) {
        String subTotal = "<tr style=\"font-family:sans-serif;box-sizing:border-box;\" >\n" +
                    "        <td colspan=\"2\" class=\"label\" style=\"font-family:sans-serif;box-sizing:border-box;border-width:1px;border-style:solid;border-color:#0cc39a;text-align:right;background-color:rgba(162, 240, 218, 0.5);font-size:0.9rem;color:#333;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;font-weight:bold;letter-spacing:2px;\" >Sub Total</td>\n" +
                    "        <td class='value' style=\"font-family:sans-serif;box-sizing:border-box;padding-top:8px;padding-bottom:8px;padding-right:8px;padding-left:8px;border-width:1px;border-style:solid;border-color:#0cc39a;font-weight:bold;font-size:0.9rem;text-align:right;background-color:rgba(162, 240, 218, 0.1);\" > "+"Rp. "+rcf.toRupiahFormat(subTotalBeforeTax.toString()) +"</td>\n" +
                    "      </tr>\n";
        return subTotal;
    }
}
