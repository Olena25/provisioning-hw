package com.voxloud.provisioning.service;

import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.exception.DeviceModelNotSupportedException;
import com.voxloud.provisioning.exception.DeviceNotFoundException;
import com.voxloud.provisioning.repository.DeviceRepository;
import com.voxloud.provisioning.service.converter.ConfigurationConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProvisioningServiceImpl implements ProvisioningService {
    private final DeviceRepository deviceRepository;
    private final List<ConfigurationConverter> configurationConverters;


    public String getProvisioningFile(String macAddress) {
        log.info("Starting to search for device with mac address {}", macAddress);

       Device device = deviceRepository.findById(macAddress).orElseThrow(() -> new DeviceNotFoundException(macAddress));

        log.info("Found device with mac address {} and model {}", macAddress, device.getModel());

        return configurationConverters.stream()
               .filter(configurationConverter -> configurationConverter.supportDevice(device))
               .findFirst()
               .map(configurationConverter -> configurationConverter.convert(device))
               .orElseThrow(() -> new DeviceModelNotSupportedException(device.getModel()));
    }
}
