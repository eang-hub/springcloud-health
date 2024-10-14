package com.springhealth.intervention.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.springhealth.intervention.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.springhealth.intervention.client.UserServiceClient;
import com.springhealth.intervention.client.DeviceMapper;
import com.springhealth.intervention.client.DeviceServiceClient;
import com.springhealth.intervention.client.UserMapper;
import com.springhealth.intervention.domain.Intervention;
import com.springhealth.intervention.repository.InterventionRepository;

@Service
public class InterventionService {

//	@Autowired
//	private InterventionService self1;  // 自注入

	@Autowired
	private InterventionRepository interventionRepository;

	@Autowired
	private DeviceServiceClient deviceClient;

	@Autowired
	private UserServiceClient userClient;

	private static final Logger logger = LoggerFactory.getLogger(InterventionService.class);


	public Intervention generateIntervention(String userName, String deviceCode) {

		logger.debug("Generate intervention record with user: {} from device: {}", userName, deviceCode);

		Intervention intervention = new Intervention();

		//获取远程Device信息
		DeviceMapper device = this.getDevice(deviceCode);
		if (device == null) {
			return intervention;
		}
		logger.debug("Get remote device: {} is successful", deviceCode);

		//获取远程User信息
		User user = getUser(userName);
		if (user == null) {
			return intervention;
		}
		logger.debug("Get remote user: {} is successful", userName);


		//创建并保存Intervention信息
		intervention.setUserId(user.getId());
		intervention.setDeviceId(device.getId());
		intervention.setHealthData(device.getHealthData());
		intervention.setIntervention("InterventionForDemo");
		intervention.setCreateTime(new Date());

		interventionRepository.save(intervention);

		return intervention;
	}




	private User getUser(String userName) {
		return userClient.getUserByUserName(userName);
	}


	public DeviceMapper getDevice(String deviceCode) {
		return deviceClient.getDevice(deviceCode); // 远程调用
	}


}
