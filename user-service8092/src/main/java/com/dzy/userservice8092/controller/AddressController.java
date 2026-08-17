package com.dzy.userservice8092.controller;


import com.dzy.common.constants.Constants;
import com.dzy.common.entity.ResultJSON;
import com.dzy.userservice8092.dto.AddressRequest;
import com.dzy.userservice8092.entity.Address;
import com.dzy.userservice8092.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/me/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    // 获取当前用户所有地址
    @GetMapping
    public ResultJSON list(@RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        List<Address> addresses = addressService.listByUser(userId);
        return ResultJSON.success(addresses);
    }

    // 获取默认地址
    @GetMapping("/default")
    public ResultJSON getDefault(@RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        Address address = addressService.getDefault(userId);
        return ResultJSON.success(address);
    }

    // 新增地址
    @PostMapping
    public ResultJSON add(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                          @Valid @RequestBody AddressRequest request) {
        Address address = addressService.addAddress(userId, request);
        return ResultJSON.success(address);
    }

    // 修改地址
    @PutMapping("/{addressId}")
    public ResultJSON update(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                             @PathVariable Long addressId,
                             @Valid @RequestBody AddressRequest request) {
        Address address = addressService.updateAddress(userId, addressId, request);
        return ResultJSON.success(address);
    }

    // 删除地址
    @DeleteMapping("/{addressId}")
    public ResultJSON delete(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                             @PathVariable Long addressId) {
        addressService.deleteAddress(userId, addressId);
        return ResultJSON.success();
    }

    // 设为默认
    @PutMapping("/{addressId}/default")
    public ResultJSON setDefault(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                 @PathVariable Long addressId) {
        addressService.setDefaultAddress(userId, addressId);
        return ResultJSON.success();
    }
}