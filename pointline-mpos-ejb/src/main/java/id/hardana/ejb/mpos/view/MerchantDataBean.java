/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.view;

import id.hardana.ejb.mpos.log.LoggingInterceptor;
import id.hardana.entity.profile.enums.CardStatusEnum;
import id.hardana.entity.profile.enums.OutletStatusEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Outlet;
import id.hardana.entity.profile.other.City;
import id.hardana.entity.profile.other.Province;
import id.hardana.entity.sys.config.Config;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
import id.hardana.entity.transaction.enums.TransactionFeeTypeEnum;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Trisna
 */
@Stateless
@LocalBean
//@Interceptors({LoggingInterceptor.class})

public class MerchantDataBean {

    private final double MAX_DISTANCE = 1; // in KM
    private final String STATUS_KEY = "status";
    private final String NEARBY_OUTLET_KEY = "nearbyOutlet";
    private final String CITY_KEY = "city";
    private final String PROVINCE_ID_KEY = "provinceId";
    private final String PROVINCE_KEY = "province";
    private final String CARD_KEY = "card";
    private final String BLACKLISTED_KEY = "blacklisted";
    private final String DISCARDED_KEY = "discarded";
    private final String BLOCKED_KEY = "blocked";
    private final String CARD_FEE_KEY = "cardFee";
    private final String CASH_FEE_KEY = "cashFee";
    private final String CREDIT_CARD_EDC_FEE_KEY = "creditCardEDCFee";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject getMerchantData(String merchantCode, String latitude, String longitude) {
        JSONObject response = new JSONObject();

        Double latitudeDouble;
        Double longitudeDouble;
        try {
            latitudeDouble = Double.parseDouble(latitude);
            longitudeDouble = Double.parseDouble(longitude);
        } catch (Exception e) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_LOCATION.getResponseStatus());
            return response;
        }

        List<String> configNames = new ArrayList<>();
        configNames.add("debitKey");
        List<Config> configsSearch = em.createQuery("SELECT c FROM Config c WHERE c.configKey IN :configKey", Config.class)
                .setParameter("configKey", configNames)
                .getResultList();
        if (configsSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INTERNAL_ERROR.getResponseStatus());
            return response;
        }

        List<Merchant> merchantSearch = em.createNamedQuery("Merchant.findByCode", Merchant.class)
                .setParameter("code", merchantCode)
                .getResultList();
        if (merchantSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_MERCHANT_CODE.getResponseStatus());
            return response;
        }
        Merchant merchant = merchantSearch.get(0);
        Long merchantId = Long.parseLong(merchant.getId());

        boolean isDeleted = false;
        int status = Integer.parseInt(OutletStatusEnum.ACTIVE.getOutletStatusId());

        List<Outlet> outletsSearch = em.createNativeQuery("SELECT *, ( 6371 * acos( cos( radians(?1) ) "
                + "* cos( radians( latitude ) ) * cos( radians( longitude ) - radians(?2) ) "
                + "+ sin( radians(?3) ) * sin(radians(latitude)) ) ) AS distance "
                + "FROM outlet WHERE merchantid = ?8 AND isdeleted = ?9 AND status = ?10 "
                + "GROUP BY outlet.id "
                + "HAVING ( 6371 * acos( cos( radians(?4) ) * cos( radians( outlet.latitude ) ) "
                + "* cos( radians( outlet.longitude ) - radians(?5) ) + sin( radians(?6) ) "
                + "* sin(radians(outlet.latitude)) ) ) < ?7 "
                + "ORDER BY distance",
                Outlet.class)
                .setParameter("1", latitudeDouble)
                .setParameter("2", longitudeDouble)
                .setParameter("3", latitudeDouble)
                .setParameter("4", latitudeDouble)
                .setParameter("5", longitudeDouble)
                .setParameter("6", latitudeDouble)
                .setParameter("7", MAX_DISTANCE)
                .setParameter("8", merchantId)
                .setParameter("9", isDeleted)
                .setParameter("10", status)
                .getResultList();

        List<CardStatusEnum> cardStatusList = new ArrayList<CardStatusEnum>();
        cardStatusList.add(CardStatusEnum.BLACKLISTED);
        cardStatusList.add(CardStatusEnum.BLOCKED);
        cardStatusList.add(CardStatusEnum.DISCARDED);
        List<Object> cardSearch = em.createQuery("SELECT c.statusId, c.cardId FROM Card c WHERE c.statusId IN :listStatus", Object.class)
                .setParameter("listStatus", cardStatusList)
                .getResultList();

        response.put(CARD_KEY, cardSearch);
        JSONArray cardSearchArray = response.getJSONArray(CARD_KEY);
        JSONArray listBlacklisted = new JSONArray();
        JSONArray listBlocked = new JSONArray();
        JSONArray listDiscarded = new JSONArray();
        response.remove(CARD_KEY);
        for (int i = 0; i < cardSearchArray.length(); i++) {
            JSONArray arrayInside = cardSearchArray.getJSONArray(i);
            String cardStatus = arrayInside.getJSONObject(0).getString("cardStatus");
            String cardId = arrayInside.getString(1);
            if (cardStatus.equals(CardStatusEnum.BLACKLISTED.getCardStatus())) {
                listBlacklisted.put(cardId);
            } else if (cardStatus.equals(CardStatusEnum.BLOCKED.getCardStatus())) {
                listBlocked.put(cardId);
            } else if (cardStatus.equals(CardStatusEnum.DISCARDED.getCardStatus())) {
                listDiscarded.put(cardId);
            }
        }
        JSONObject cardObjectListHolder = new JSONObject();
        cardObjectListHolder.put(BLACKLISTED_KEY, listBlacklisted);
        cardObjectListHolder.put(BLOCKED_KEY, listBlocked);
        cardObjectListHolder.put(DISCARDED_KEY, listDiscarded);

        for (Config config : configsSearch) {
            em.refresh(config);
            String configKey = config.getConfigKey();
            String configValue = config.getConfigValue();
            response.put(configKey, configValue);
        }

        response.put(NEARBY_OUTLET_KEY, outletsSearch);
        JSONArray outletArray = response.getJSONArray(NEARBY_OUTLET_KEY);
        response.remove(NEARBY_OUTLET_KEY);
        for (int i = 0; i < outletArray.length(); i++) {
            JSONObject outletObject = outletArray.getJSONObject(i);
            outletObject.remove("merchantId");
            outletObject.remove("isDeleted");
            outletObject.remove("lastUpdated");
            String cityIdString = outletObject.getString("cityId");

            String cityName = null;
            String provinceIdString = null;
            String provinceName = null;
            try {
                Long cityId = Long.parseLong(cityIdString);

                List<City> citySearch = em.createNamedQuery("City.findById", City.class)
                        .setParameter("id", cityId)
                        .getResultList();
                City city = citySearch.get(0);
                cityName = city.getName();
                provinceIdString = city.getProvinceId();

                List<Province> provinceSearch = em.createNamedQuery("Province.findById", Province.class)
                        .setParameter("id", Long.parseLong(provinceIdString))
                        .getResultList();
                Province province = provinceSearch.get(0);
                provinceName = province.getName();
            } catch (Exception e) {
            }

            outletObject.put(CITY_KEY, cityName);
            outletObject.put(PROVINCE_ID_KEY, provinceIdString);
            outletObject.put(PROVINCE_KEY, provinceName);
        }
        response.put(NEARBY_OUTLET_KEY, outletArray);
        response.put(CARD_FEE_KEY, TransactionFeeTypeEnum.PAYMENTCASHCARD.getFee(em));
        response.put(CASH_FEE_KEY, TransactionFeeTypeEnum.PAYMENTCASH.getFee(em));
        response.put(CREDIT_CARD_EDC_FEE_KEY, TransactionFeeTypeEnum.PAYMENTCCEDC.getFee(em));
        response.put(CARD_KEY, cardObjectListHolder);
        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());

        return response;
    }

}
