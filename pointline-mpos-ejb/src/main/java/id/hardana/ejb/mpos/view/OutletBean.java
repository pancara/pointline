/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.mpos.view;

import id.hardana.ejb.mpos.log.LoggingInterceptor;
import id.hardana.entity.profile.enums.OutletStatusEnum;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Outlet;
import id.hardana.entity.profile.other.City;
import id.hardana.entity.profile.other.Province;
import id.hardana.entity.sys.enums.ResponseStatusEnum;
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
@Interceptors({LoggingInterceptor.class})

public class OutletBean {

    private final String STATUS_KEY = "status";
    private final String OUTLET_KEY = "outlet";
    private final String MERCHANT_KEY = "merchant";
    private final String CITY_KEY = "city";
    private final String PROVINCE_ID_KEY = "provinceId";
    private final String PROVINCE_KEY = "province";
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    public JSONObject getAllOutlet(String merchantCode) {
        JSONObject response = new JSONObject();

        List<Merchant> merchantSearch = em.createNamedQuery("Merchant.findByCode", Merchant.class)
                .setParameter("code", merchantCode)
                .getResultList();
        if (merchantSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_MERCHANT_CODE.getResponseStatus());
            return response;
        }
        Merchant merchant = merchantSearch.get(0);
        Long merchantId = Long.parseLong(merchant.getId());

        List<Outlet> listOutlet = em.createQuery("SELECT o FROM Outlet o WHERE o.merchantId = :merchantId "
                + "AND o.status = :status AND o.isDeleted = :isDeleted", Outlet.class)
                .setParameter("merchantId", merchantId)
                .setParameter("status", OutletStatusEnum.ACTIVE)
                .setParameter("isDeleted", false)
                .getResultList();
        response.put(OUTLET_KEY, listOutlet);
        JSONArray outletArray = response.getJSONArray(OUTLET_KEY);
        response.remove(OUTLET_KEY);
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

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(OUTLET_KEY, outletArray);

        return response;
    }
    
    public JSONObject getOutlet(String merchantCode, String outletId) {
        JSONObject response = new JSONObject();

        List<Merchant> merchantSearch = em.createNamedQuery("Merchant.findByCode", Merchant.class)
                .setParameter("code", merchantCode)
                .getResultList();
        if (merchantSearch.isEmpty()) {
            response.put(STATUS_KEY, ResponseStatusEnum.INVALID_MERCHANT_CODE.getResponseStatus());
            return response;
        }
        Merchant merchant = merchantSearch.get(0);
        Long merchantId = Long.parseLong(merchant.getId());
        Long outletIdLong;
        outletIdLong = Long.parseLong(outletId);

        List<Outlet> listOutlet = em.createQuery("SELECT o FROM Outlet o WHERE o.merchantId = :merchantId "
                + "AND o.status = :status AND o.isDeleted = :isDeleted AND o.id = :id", Outlet.class)
                .setParameter("merchantId", merchantId)
                .setParameter("status", OutletStatusEnum.ACTIVE)
                .setParameter("isDeleted", false)
                .setParameter("id", outletIdLong)
                .getResultList();
        response.put(OUTLET_KEY, listOutlet);
        JSONArray outletArray = response.getJSONArray(OUTLET_KEY);
        response.remove(OUTLET_KEY);
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

        response.put(STATUS_KEY, ResponseStatusEnum.SUCCESS.getResponseStatus());
        response.put(OUTLET_KEY, outletArray);
        response.put(MERCHANT_KEY, merchant.getBrandName());

        return response;
    }

}
