package com.dzy.userservice8092.service.impl;

import com.dzy.common.exception.BusinessException;
import com.dzy.userservice8092.entity.Address;
import com.dzy.userservice8092.dto.AddressRequest;
import com.dzy.userservice8092.mapper.AddressMapper;
import com.dzy.userservice8092.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public List<Address> listByUser(Long userId) {
        return addressMapper.selectByUserId(userId);
    }

    @Override
    public Address getDefault(Long userId) {
        return addressMapper.selectDefaultByUserId(userId);
    }

    @Override
    @Transactional
    public Address addAddress(Long userId, AddressRequest request) {
        // 如果该用户还没有任何地址，则新增的地址自动设为默认
        int count = addressMapper.countByUserId(userId);
        boolean shouldBeDefault = (count == 0) || (request.getIsDefault() != null && request.getIsDefault());

        Address address = new Address();
        address.setUserId(userId);
        address.setReceiverName(request.getReceiverName());
        address.setPhone(request.getPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetail(request.getDetail());
        address.setIsDefault(shouldBeDefault);

        // 如果设为默认，先清除该用户其他默认标记
        if (shouldBeDefault) {
            addressMapper.clearDefault(userId);
        }

        addressMapper.insert(address);
        return address;
    }

    @Override
    @Transactional
    public Address updateAddress(Long userId, Long addressId, AddressRequest request) {
        Address existing = addressMapper.selectByIdAndUser(addressId, userId);
        if (existing == null) {
            throw new BusinessException("地址不存在或不属于当前用户");
        }

        // 如果要把该地址设为默认，清除其他默认
        if (request.getIsDefault() != null && request.getIsDefault() && !existing.getIsDefault()) {
            addressMapper.clearDefault(userId);
        }

        existing.setReceiverName(request.getReceiverName());
        existing.setPhone(request.getPhone());
        existing.setProvince(request.getProvince());
        existing.setCity(request.getCity());
        existing.setDistrict(request.getDistrict());
        existing.setDetail(request.getDetail());
        // 只有明确传入 isDefault 才更新，否则保留原值
        if (request.getIsDefault() != null) {
            existing.setIsDefault(request.getIsDefault());
        }

        addressMapper.update(existing);
        return existing;
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressMapper.selectByIdAndUser(addressId, userId);
        if (address == null) {
            throw new BusinessException("地址不存在或不属于当前用户");
        }

        addressMapper.deleteById(addressId, userId);

        // 如果删除的是默认地址，则把用户最新的地址设为默认（按 id 倒序取第一条）
        if (address.getIsDefault()) {
            // 查询剩余地址
            List<Address> remaining = addressMapper.selectByUserId(userId);
            if (!remaining.isEmpty()) {
                // 取第一条（最新的）设为默认
                Address newDefault = remaining.get(0);
                addressMapper.setDefault(newDefault.getId(), userId);
            }
            // 如果没有剩余地址，则无需操作
        }
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long userId, Long addressId) {
        // 验证地址是否存在
        Address address = addressMapper.selectByIdAndUser(addressId, userId);
        if (address == null) {
            throw new BusinessException("地址不存在或不属于当前用户");
        }
        // 清除所有默认
        addressMapper.clearDefault(userId);
        // 设置该地址为默认
        addressMapper.setDefault(addressId, userId);
    }
}