package com.jaagro.report.biz.service.impl;

import com.jaagro.constant.UserInfo;
import com.jaagro.report.api.constant.AuditStatus;
import com.jaagro.report.api.constant.CertificateType;
import com.jaagro.report.api.constant.Constants;
import com.jaagro.report.api.dto.customer.*;
import com.jaagro.report.api.dto.finance.CustomerBaseInfoDto;
import com.jaagro.report.api.dto.plant.PlantDto;
import com.jaagro.report.api.dto.plant.PlantImageDto;
import com.jaagro.report.api.enums.CustomerTypeEnum;
import com.jaagro.report.api.exception.BusinessException;
import com.jaagro.report.api.service.FinanceService;
import com.jaagro.report.biz.mapper.cbs.PlantMapperExt;
import com.jaagro.report.biz.service.CurrentUserService;
import com.jaagro.report.biz.service.CustomerClientService;
import com.jaagro.report.biz.service.OssSignUrlClientService;
import com.jaagro.utils.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.net.URL;
import java.util.List;

/**
 * 对接金融接口
 * @author yj
 * @date 2019/3/27 11:05
 */
@Service
@Slf4j
public class FinanceServiceImpl implements FinanceService {
    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private CustomerClientService customerClientService;
    @Autowired
    private OssSignUrlClientService ossSignUrlClientService;
    @Autowired
    private PlantMapperExt plantMapper;
    /**
     * 获取客户基本信息
     *
     * @return
     */
    @Override
    public CustomerBaseInfoDto getCustomerBaseInfo() {
        GetCustomerUserDto customerUser = currentUserService.getCustomerUserById();
        log.info("O CustomerBaseInfoDto customerUser={}",customerUser);
        if (customerUser == null){
            throw new BusinessException("当前登录用户信息不存在");
        }
        Integer relevanceId = customerUser.getRelevanceId();
        BaseResponse<CustomerReturnDto> response = customerClientService.getCustomerDetail(relevanceId);
        CustomerReturnDto customerReturnDto = response.getData();
        if (customerReturnDto == null){
            throw new BusinessException("客户不存在");
        }
        CustomerBaseInfoDto dto = new CustomerBaseInfoDto();
        Integer customerType = customerReturnDto.getCustomerType();
        dto.setCustomerId(dto.getCustomerId())
                .setCustomerName(dto.getCustomerName())
                .setCustomerType(CustomerTypeEnum.getDescByCode(customerType))
                .setHeadPortrait(convertToAbstractUrl(Constants.HEAD_PORTRAIT))
                .setIdCardNo(customerReturnDto.getCreditCode())
                .setOperatorCode(Constants.OPERATOR_CODE)
                .setOperatorName(Constants.OPERATOR_NAME)
                .setSource(Constants.SOURCE);
        List<CustomerContactsReturnDto> contactList = customerReturnDto.getCustomerContactsReturnDtos();
        if (!CollectionUtils.isEmpty(contactList)){
            dto.setPhoneNo(contactList.get(0).getPhone());
        }
        if (customerType != null && customerType.equals(CustomerTypeEnum.PERSON.getCode())){
            dto.setRealName(dto.getCustomerName());
        }else {
            dto.setRealName(contactList.get(0).getCustomerName());
            List<CustomerQualificationReturnDto> qualificationList = customerReturnDto.getQualifications();
            if (!CollectionUtils.isEmpty(qualificationList)){
                for (CustomerQualificationReturnDto qualificationReturnDto : qualificationList){
                    if (!AuditStatus.NORMAL_COOPERATION.equals(qualificationReturnDto.getCertificateStatus())){
                        continue;
                    }
                    if (CertificateType.BUSINESS_LICENSE.equals(qualificationReturnDto.getCertificateType())){
                        dto.setBusinessLicense(convertToAbstractUrl(qualificationReturnDto.getCertificateImageUrl()));
                    }
                    if (CertificateType.FRONT_ID_CARD.equals(qualificationReturnDto.getCertificateType())){
                        dto.setIdCardImgPositive(convertToAbstractUrl(qualificationReturnDto.getCertificateImageUrl()));
                    }
                    if (CertificateType.BACK_ID_CARD.equals(qualificationReturnDto.getCertificateType())){
                        dto.setIdCardImgNegative(convertToAbstractUrl(qualificationReturnDto.getCertificateImageUrl()));
                    }
                }
            }
        }
        List<PlantDto> plantDtoList = plantMapper.listByCustomerId(relevanceId);
        if (!CollectionUtils.isEmpty(plantDtoList)){
            PlantDto plantDto = plantDtoList.get(0);
            dto.setPlantDetailAddress(plantDto.getProvince()+plantDto.getCity()+plantDto.getCounty()+plantDto.getPlantName());
            List<PlantImageDto> plantImageDtoList = plantDto.getPlantImageDtoList();
            if (!CollectionUtils.isEmpty(plantImageDtoList)){
                dto.setPlantImageUrl(convertToAbstractUrl(plantImageDtoList.get(0).getImageUrl()));
            }
        }
        return dto;
    }

    private String convertToAbstractUrl(String relativeUrl){
        String[] strArray = {relativeUrl};
        List<URL> urls = ossSignUrlClientService.listSignedUrl(strArray);
        if (!CollectionUtils.isEmpty(urls)) {
            return urls.get(0).toString();
        }
        return null;
    }
}
