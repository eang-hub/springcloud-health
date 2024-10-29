package com.springhealth.device.services;

import brave.Span;
import brave.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springhealth.device.domain.Device;
import com.springhealth.device.repository.DeviceRepository;


@Service
public class DeviceService {

	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private Tracer tracer;

	public Device getDeviceByCode(String deviceCode) {

		Span newSpan = tracer.nextSpan().name("findByDeviceCode").start();
//    	ScopedSpan newSpan = tracer.startScopedSpan("findByDeviceCode");

		try {
			return deviceRepository.findDeviceByDeviceCode(deviceCode);
		}
		finally{
			newSpan.tag("device", "findByDeviceCode");
			newSpan.annotate("deviceObtained");
			newSpan.finish();
		}
	}
}

