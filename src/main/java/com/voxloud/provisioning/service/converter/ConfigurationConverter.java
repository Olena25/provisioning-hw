package com.voxloud.provisioning.service.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxloud.provisioning.config.ProvisioningConfigProperties;
import com.voxloud.provisioning.dto.DeviceConfigDto;
import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.entity.Device.DeviceModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@AllArgsConstructor
@Slf4j
public abstract class ConfigurationConverter {

    protected ObjectMapper objectMapper;
    protected ProvisioningConfigProperties provisioningConfigProperties;
    protected DeviceModel supportedConvertingModel;

    protected abstract void overrideConfigurationByFragment(Map<String, Object> deviceProperties, String overrideFragment);

    protected abstract String formatToResponse(Map<String, Object> deviceProperties);

    public String convert(Device device) {
        if(!supportDevice(device)) {
            log.error("Cannot convert device of model {} using converter for model {}", device.getModel(), supportedConvertingModel);

            throw new RuntimeException("Misconfigured converter, expected device with model " + supportedConvertingModel);
        }

        log.info("Starting to convert device with mac address {} with config", device.getMacAddress());

        Map<String, Object> deviceProperties = populateDeviceConfiguration(device);

        String overrideFragment = device.getOverrideFragment();

        if (overrideFragment != null && !overrideFragment.trim().isEmpty()) {
            log.info("Starting to override config by fragment {} for device with mac {}",
                    device.getMacAddress(), device.getOverrideFragment());

            overrideConfigurationByFragment(deviceProperties, overrideFragment);
        }

        return formatToResponse(deviceProperties);
    }

    public boolean supportDevice(Device device) {
        return supportedConvertingModel == device.getModel();
    }

    protected Map<String, Object> populateDeviceConfiguration(Device device) {
        DeviceConfigDto deviceConfigDto = DeviceConfigDto.builder()
                .username(device.getUsername())
                .password(device.getPassword())
                .domain(provisioningConfigProperties.getDomain())
                .port(provisioningConfigProperties.getPort())
                .codecs(provisioningConfigProperties.getCodecs())
                .build();

        return objectMapper.convertValue(deviceConfigDto, new TypeReference<Map<String, Object>>() {});
    }
}
