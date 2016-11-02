/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.hardana.ejb.merchantweb;

import id.hardana.ejb.merchantweb.extension.OperatorExt;
import id.hardana.ejb.merchantweb.log.LoggingInterceptor;
import id.hardana.entity.profile.enums.IdentityTypeEnum;
import id.hardana.entity.profile.enums.OperatorStatusEnum;
import id.hardana.entity.profile.enums.OutletStatusEnum;
import id.hardana.entity.profile.merchant.Company;
import id.hardana.entity.profile.merchant.Merchant;
import id.hardana.entity.profile.merchant.Operator;
import id.hardana.entity.profile.merchant.OperatorLevel;
import id.hardana.entity.profile.merchant.OperatorLevelModuleAccessor;
import id.hardana.entity.profile.merchant.Outlet;
import id.hardana.entity.profile.other.City;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.json.JSONObject;

/**
 *
 * @author hanafi
 */
@Stateless
@Interceptors(LoggingInterceptor.class)
public class MerchantFacade  {
    @PersistenceContext(unitName = "JPAPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public MerchantFacade() {
    }
    
   
    

    public String registerMerchant(String merchantCode, String brandDesc, String lob, String majorProd, String ownerIdNumber, String ownerIdType, String ownerName, String ownerPhone, String ownerEmail, String contactIdNumber, String contactIdType, String contactName, String contactPhone) {
        //Merchant entity = em.find(Merchant.class, merchantCode);
    
        //convert integers:
        // lob
        // ownerIdType
        // repIdType
        Integer lobInt = Integer.valueOf(lob);
        Integer ownerIdTypeInt = Integer.valueOf(ownerIdType);
        Integer contactIdTypeInt = Integer.valueOf(contactIdType);
        
        // find entity by merchantcode
        Merchant entity = em.createNamedQuery("Merchant.findByCode", Merchant.class)
                .setParameter("code", merchantCode)
                .getSingleResult();
        
        //entity.setBrandName(brandName);
        entity.setBrandDescription(brandDesc);
        entity.setLob(lobInt.longValue());
        entity.setMajorProduct(majorProd);
        entity.setOwnerIdNumber(ownerIdNumber);
        entity.setOwnerIdType(IdentityTypeEnum.getIdentityType(ownerIdTypeInt));
        entity.setOwnerName(ownerName);
        entity.setOwnerPhone(ownerPhone);
        entity.setOwnerEmail(ownerEmail);
        entity.setContactIdNumber(contactIdNumber);
        entity.setContactIdType(IdentityTypeEnum.getIdentityType(contactIdTypeInt));
        entity.setContactName(contactName);
        entity.setContactPhone(contactPhone);
        //entity.setContactEmail(contactEmail);
        
        //not sent
        entity.setMerchantFee(BigDecimal.ZERO);
        
        em.merge(entity);
        //em.refresh(entity);
        // update the owner op real name
        // get merchantId
        
        Operator entityOp = em.createNamedQuery("Operator.findByMerchantId", Operator.class)
                .setParameter("merchantId", Long.valueOf(entity.getId()))
                .getSingleResult();
        
        entityOp.setFullName(ownerName);
        
        em.merge(entityOp);
        em.flush();
        
        return "SUCCESS";
    }
    
    public String editMerchant(String merchantCode, String brandName, String brandDesc, String lob, String majorProd, String ownerIdNumber, String ownerIdType, String ownerName, String ownerPhone, String ownerEmail, String contactIdNumber, String contactIdType, String contactName, String contactPhone, String contactEmail) {
        
        //convert integers:
        // lob
        // ownerIdType
        // repIdType
        Integer lobInt = Integer.valueOf(lob);
        Integer ownerIdTypeInt = Integer.valueOf(ownerIdType);
        Integer contactIdTypeInt = Integer.valueOf(contactIdType);
        
        // find entity by merchantcode
        Merchant entity = em.createNamedQuery("Merchant.findByCode", Merchant.class)
                .setParameter("code", merchantCode)
                .getSingleResult();
        
        entity.setBrandName(brandName);
        entity.setBrandDescription(brandDesc);
        entity.setLob(lobInt.longValue());
        entity.setMajorProduct(majorProd);
        entity.setOwnerIdNumber(ownerIdNumber);
        entity.setOwnerIdType(IdentityTypeEnum.getIdentityType(ownerIdTypeInt));
        entity.setOwnerName(ownerName);
        entity.setOwnerPhone(ownerPhone);
        entity.setOwnerEmail(ownerEmail);
        entity.setContactIdNumber(contactIdNumber);
        entity.setContactIdType(IdentityTypeEnum.getIdentityType(contactIdTypeInt));
        entity.setContactName(contactName);
        entity.setContactPhone(contactPhone);
        entity.setContactEmail(contactEmail);
        
        em.merge(entity);
        
        return "SUCCESS";
    }
    
    public String registerBank(String merchantCode, String bankAccNum, String bankAccName, String bankId, String bankBranch) {
        
        // convert integers
        // bankId
        Integer bankIdInt = Integer.valueOf(bankId);

        //Merchant entity = em.find(Merchant.class, merchantCode);
        
        Merchant entity = (Merchant) em.createNamedQuery("Merchant.findByCode")
                .setParameter("code", merchantCode)
                .getSingleResult();
        // find entity by merchantcode
        
        entity.setBankAccountNumber(bankAccNum);
        entity.setBankAccountName(bankAccName);
        entity.setBankId(bankIdInt.longValue());
        entity.setBankBranch(bankBranch);
        
        em.merge(entity);
        
        return "SUCCESS";
    }

    public String registerOutlet(String merchantCode, String outletName, String outletAddress, String outletCity, String outletLongitude, String outletLatitude, String outletStatus) {
        
        // convert integers
        // outletCity
        // outletStatus
        Integer outletCityInt = Integer.valueOf(outletCity);
        Integer outletStatusInt = Integer.valueOf(outletStatus);
        
        // convert doubles
        // outletLongitude
        // outletLatitude
        Double outletLongitudeDouble = Double.valueOf(outletLongitude);
        Double outletLatitudeDouble = Double.valueOf(outletLatitude);
        
        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);

        Outlet entity = new Outlet();
        
        entity.setMerchantId(merchantId.longValue());
        entity.setName(outletName);
        entity.setAddress(outletAddress);
        entity.setCityId(outletCityInt.longValue());
        entity.setLatitude(outletLatitudeDouble); // lat part
        entity.setLongitude(outletLongitudeDouble); // long part
        entity.setStatus(OutletStatusEnum.getOutletStatus(outletStatusInt));
        // not sent
        entity.setIsDeleted(Boolean.FALSE);
        
        em.persist(entity);
        
        return "SUCCESS";
    }

    public HashMap<String,String> editOutlet(String outletId, String outletName, String outletAddress, String outletCity, String outletLongitude, String outletLatitude,  String outletStatus) {
        HashMap<String,String> result = new HashMap<>();
        
        // convert integers
        // outletCity
        // outletStatus
        Integer outletCityInt = Integer.valueOf(outletCity);
        Integer outletStatusInt = Integer.valueOf(outletStatus);

        // convert doubles
        // outletLongitude
        // outletLatitude
        Double outletLongitudeDouble = Double.valueOf(outletLongitude);
        Double outletLatitudeDouble = Double.valueOf(outletLatitude);
        
        //Long merchantId = getMerchantIdFromMerchantCode(merchantCode);
        
        // get outlet
        List<Outlet> resultList = em.createNamedQuery("Outlet.findById", Outlet.class)
                .setParameter("id", Long.valueOf(outletId))
                .getResultList();
        
        if(resultList.isEmpty()){
            result.put("status", "NOTFOUND");
            return result;
        }
        
        Outlet entity = resultList.get(0);
        
        entity.setName(outletName);
        entity.setAddress(outletAddress);
        entity.setCityId(outletCityInt.longValue());
        entity.setLatitude(outletLatitudeDouble); // lat part
        entity.setLongitude(outletLongitudeDouble); // long part
        entity.setStatus(OutletStatusEnum.getOutletStatus(outletStatusInt));
        
        em.merge(entity);
        
        result.put("status", "SUCCESS");
        return result;
    }

    public String registerOperatorLevel(String merchantCode, String opLevelName, String opLevelDesc) {
        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);
        
        OperatorLevel entity = new OperatorLevel();
        
        entity.setMerchantId(merchantId.longValue());
        entity.setName(opLevelName);
        entity.setDescription(opLevelDesc);
        
        em.persist(entity);
        
        return "SUCCESS";
    }

    public HashMap<String,String> editOperatorLevel(String opLevelId, String opLevelName, String opLevelDesc) {
        HashMap<String,String> result = new HashMap<>();
        
        List<OperatorLevel> resultList = em.createNamedQuery("OperatorLevel.findById", OperatorLevel.class)
                .setParameter("id", Long.valueOf(opLevelId))
                .getResultList();
        
        if(resultList.isEmpty()){
            result.put("status", "NOTFOUND");
            return result;
        }
        
        OperatorLevel entity = resultList.get(0);
        
        entity.setName(opLevelName);
        entity.setDescription(opLevelDesc);
        
        em.merge(entity);
        
        result.put("status", "SUCCESS");
        return result;
    }

    public String registerOperator(String merchantCode, String userNameReg, String opPassword, String opFullName, String employeeNum, String opLevel, String opPic, String opStatus) {
        
        // convert integers
        // opLevel
        // opStatus
        Integer opLevelInt = Integer.valueOf(opLevel);
        Integer opStatusInt = Integer.valueOf(opStatus);

        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);
        
        // check if username exist
        List<Operator> operatorList = em.createNamedQuery("Operator.getOpId", Operator.class)
                .setParameter("merchantId", merchantId)
                .setParameter("userName", userNameReg)
                .getResultList();
        
        
        if(operatorList.isEmpty()){
            
            Operator entity = new Operator();
        
            entity.setUserName(userNameReg);
            entity.setPassword(opPassword);
            entity.setFullName(opFullName);
            entity.setEmployeeNumber(employeeNum);
            entity.setOperatorLevelId(opLevelInt.longValue());
            entity.setProfilePic(opPic);
            entity.setStatus(OperatorStatusEnum.getOperatorStatus(opStatusInt));
            entity.setMerchantId(merchantId.longValue());
            // not sent
            entity.setIsOwner(Boolean.FALSE);
            
            em.persist(entity);
        } else {
            
            // return operator with username already exist
            return "OPERATOREXIST";
        }
        
        return "SUCCESS";
    }

    
    public HashMap<String,String> editOperator(String opId, String userName, String opPassword, String opRealName, String employeeNum, String opLevel, String opPic, String opStatus) {
        HashMap<String,String> result = new HashMap<>();
        // convert integers
        // opLevel
        // opStatus
        Integer opLevelInt = Integer.valueOf(opLevel);
        Integer opStatusInt = Integer.valueOf(opStatus);
        
        // get opId from merchantId + userName
        List<Operator> resultList = em.createNamedQuery("Operator.findById", Operator.class)
                .setParameter("id", Long.valueOf(opId))
                .getResultList();
 
        if(resultList.isEmpty()){
            result.put("status", "NOTFOUND");
            return result;
        }
        
        Operator entity = resultList.get(0);
        
        entity.setUserName(userName);
        entity.setPassword(opPassword);
        entity.setFullName(opRealName);
        entity.setEmployeeNumber(employeeNum);
        entity.setOperatorLevelId(opLevelInt.longValue());
        entity.setProfilePic(opPic);
        entity.setStatus(OperatorStatusEnum.getOperatorStatus(opStatusInt));
        entity.setLastUpdated(new Date());

        em.merge(entity);
        
        result.put("status", "SUCCESS");
        return result;
    }

    public String registerCompany(String companyName, String companyAddress, String companySIUP, String companyAkta, String companyNPWP, String companyCityId, String companyZipCode, String companyPhone, String companyEmail, String companyOwner, String password) {
        
        // convert integers
        // companyCityId
        Integer companyCityIdInt = Integer.valueOf(companyCityId);
        
        Company entity = new Company();
        
        
        // generate code 
        String companyCode = null;
        Boolean isUnique = false;
        while(!isUnique){
            companyCode = generateCompanyCode();
            Long countZero = (Long) em.createQuery("SELECT COUNT (c.code) FROM Company c WHERE c.code = :code")
                    .setParameter("code", companyCode).getSingleResult();
            if(countZero == 0L){
                isUnique=true;
            }
        }
        
        entity.setCode(companyCode);
        
        entity.setName(companyName);
        entity.setAddress(companyAddress);
        entity.setSiup(companySIUP);
        entity.setAkta(companyAkta);
        entity.setNpwp(companyNPWP);
        entity.setCityId(companyCityIdInt.longValue());
        entity.setZipCode(companyZipCode);
        entity.setPhone(companyPhone);
        entity.setEmail(companyEmail);
        entity.setOwner(companyOwner);
        entity.setPassword(password);
        
        em.persist(entity);
        
        return "SUCCESS";
    }

    public HashMap<String,String> addCompany(String merchantCode, String companyCode, String companyPassword) {
        HashMap<String,String> result = new HashMap<>();
        
        // get company by companyCode
        List<Company> listCompany = em.createNamedQuery("Company.findByCompanyCode", Company.class)
                .setParameter("companyCode", companyCode)
                .getResultList();
        
        if (listCompany.isEmpty()){
            result.put("status", "NOTFOUND");
            return result;
            // company doesn't exist. for logging
            // return wrong code or password
        } 
        
        Company entityCompany = listCompany.get(0);
        
        if(!companyPassword.equals(entityCompany.getPassword())){
            result.put("status", "WRONGPASSWORD");
            return result;
        }
        
        Merchant entityMerchant = em.createNamedQuery("Merchant.findByCode", Merchant.class)
                .setParameter("code", merchantCode)
                .getSingleResult();
        
        entityMerchant.setCompanyId(Long.valueOf(entityCompany.getId()));
        
        em.merge(entityMerchant);
        em.flush();
        
        result.put("status","SUCCESS");
        return result;
    }

    public HashMap<String,String> editCompany(String companyCode, String companyPassword, String companyName, String companyAddress, String companySIUP, String companyAkta, String companyNPWP, String companyCityId, String companyZipCode, String companyPhone, String companyEmail, String companyOwner) {
        HashMap<String,String> result = new HashMap<>();
        // convert integers
        // companyCityId
        Integer companyCityIdInt = Integer.valueOf(companyCityId);

        // get company by companyCode
        List<Company> listCompany = em.createNamedQuery("Company.findByCompanyCode", Company.class)
                .setParameter("companyCode", companyCode)
                .getResultList();
        
        if (listCompany.isEmpty()){
            result.put("status", "NOTFOUND");
            return result;
            // company doesn't exist. for logging
            // return wrong code or password
        } 
        
        Company entity = listCompany.get(0);
        
        if(!companyPassword.equals(entity.getPassword())){
            result.put("status", "WRONGPASSWORD");
        }
        
        
        entity.setName(companyName);
        entity.setAddress(companyAddress);
        entity.setSiup(companySIUP);
        entity.setAkta(companyAkta);
        entity.setNpwp(companyNPWP);
        entity.setCityId(companyCityIdInt.longValue());
        entity.setZipCode(companyZipCode);
        entity.setPhone(companyPhone);
        entity.setEmail(companyEmail);
        entity.setOwner(companyOwner);
        
        em.merge(entity);
        
        result.put("status", "SUCCESS");
        return result;
    }

    public HashMap<String,Object> getMerchant(String merchantCode) {
        Merchant entity = new Merchant();
        HashMap<String,Object> result = new HashMap<>();
        
        List<Merchant> resultList = em.createNamedQuery("Merchant.findByCode", Merchant.class)
                .setParameter("code", merchantCode)
                .getResultList();
        
        if(resultList.isEmpty()){
            result.put("status", "NOTFOUND");
            return result;
        }
        
        entity = resultList.get(0);
        
        result.put("merchant", entity);
        result.put("status", "SUCCESS");
        
        return result;
    }

    public HashMap<String,Object> getOutlet(String merchantCode) {
        Outlet entity = new Outlet();
        HashMap<String,Object> result = new HashMap<>();
        
        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);

        List<Outlet> resultList = em.createNamedQuery("Outlet.findByMerchantId", Outlet.class)
                .setParameter("merchantId", merchantId)
                .getResultList();
        
        if(resultList.isEmpty()){
            result.put("status", "NOTFOUND");
            return result;
        }
        
        for(Outlet o : resultList){
            City entityCity = em.createNamedQuery("City.findById", City.class)
                    .setParameter("id", Long.valueOf(o.getCityId()))
                    .getSingleResult();
            o.setCityName(entityCity.getName());
            o.setProvinceId(entityCity.getProvinceId());
        }
        
        result.put("outletList", resultList);
        result.put("status", "SUCCESS");
    
        return result;
    }
    
    public HashMap<String,Object> getOperatorLevel(String merchantCode) {
        OperatorLevel entity = new OperatorLevel();
        HashMap<String,Object> result = new HashMap<>();
        
        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);
        
        List<OperatorLevel> resultList = em.createNamedQuery("OperatorLevel.findByMerchantId", OperatorLevel.class)
                .setParameter("merchantId", merchantId)
                .getResultList();

        if(resultList.isEmpty()){
            result.put("status", "NOTFOUND");
            return result;
        }

        //entity = resultList.get(0);
        
        result.put("operatorLevels", resultList);
        result.put("status", "SUCCESS");
        
        return result;
    }
    
    public HashMap<String,Object> getOperator(String merchantCode) {
        HashMap<String,Object> result = new HashMap<>();
        
        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);
        
        List<Operator> resultList =  em.createNamedQuery("Operator.findByMerchantId", Operator.class)
                .setParameter("merchantId", merchantId)
                .getResultList();

        if(resultList.isEmpty()){
            result.put("status", "NOTFOUND");
            return result;
        }

        for(Operator o : resultList){
            if(Boolean.valueOf(o.getIsOwner())){
                o.setOperatorLevelName("owner");
            } else{
                OperatorLevel entityOpLevel = em.createNamedQuery("OperatorLevel.findById", OperatorLevel.class)
                    .setParameter("id", Long.valueOf(o.getOperatorLevelId()))
                    .getSingleResult();
                o.setOperatorLevelName(entityOpLevel.getName());
            }
        }
        
        result.put("operators", resultList);
        result.put("status", "SUCCESS");
        
        return result;
    }
    
    
    public String getAllPrivileges(String merchantCode) {
        // get merchantId from merchantCode
        Long merchantId = getMerchantIdFromMerchantCode(merchantCode);
        
        // get all operatorlevel by merchantid
        List<OperatorLevel> opLevelList = em.createNamedQuery("OperatorLevel.findByMerchantId", OperatorLevel.class)
                .setParameter("merchantId", merchantId)
                .getResultList();
        
        HashMap<String,List<OperatorLevelModuleAccessor>> opLevelModuleAccessorMap = null;

        for(OperatorLevel oplv : opLevelList){
            // get operatorlevel module accessor by id
            opLevelModuleAccessorMap.put(oplv.getId(), em.createNamedQuery("OperatorLevelModuleAccessor.findByOperatorLevelId", OperatorLevelModuleAccessor.class)
            .setParameter("opLevelId", oplv)
            .getResultList());
            
        }
        
        return new JSONObject(opLevelModuleAccessorMap).toString();
            
    }
    
    private Long getMerchantIdFromMerchantCode(String merchantCode){
        Merchant entityMerchantId = (Merchant) em.createNamedQuery("Merchant.findByCode")
                .setParameter("code", merchantCode)
                .getSingleResult();
        
        return Long.valueOf(entityMerchantId.getId());
    }

    public static String generateCompanyCode() {
        String ALPHA_NUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int ndx = (int) (Math.random() * ALPHA_NUM.length());
            sb.append(ALPHA_NUM.charAt(ndx));
        }

        return sb.toString();
    }
    

    
    private String JSONObject(String result, Merchant entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
