package id.hardana.ejb.webpersonal.util;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by pancara on 7/11/16.
 */
public class ChartTableParser {

    public static List<ChartTableItem> parseInquiry(String chartTableText) {
        List<ChartTableItem> items = new LinkedList<>();
        String[] itemTexts = chartTableText.split("\n");
        for (String itemText : itemTexts) {
            String[] keyValue = itemText.split(":");

            String key = keyValue[0].trim();
            String value = keyValue[1].trim();
            items.add(new ChartTableItem(key, value));
        }

        return items;
    }

    public static List<PaymentInfo> parsePayment(String chartTableText) {
        List<PaymentInfo> paymentInfoList = new LinkedList<>();
        String[] itemTexts = chartTableText.split("\n");

        String[] includedFields = {"NO METER", "IDPEL", "NAMA", "TARIF/DAYA", "REFNUM", "STROOM/TOKEN"};

        for (String itemText : itemTexts) {
            if (itemText != null && itemText.trim().length() > 0) {
                int colonIndex = itemText.trim().indexOf(":");
                if (colonIndex != -1) {
                    String key = itemText.substring(0, colonIndex).trim();
                    if (ArrayUtil.contains(includedFields, key))  {
                        String value = itemText.substring(colonIndex + 1, itemText.length()).trim();
                        paymentInfoList.add(new PaymentInfo(key, value));
                    }
                }

            }
        }
        return paymentInfoList;
    }

}
