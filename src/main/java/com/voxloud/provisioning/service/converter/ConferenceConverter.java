package com.voxloud.provisioning.service.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxloud.provisioning.config.ProvisioningConfigProperties;
import com.voxloud.provisioning.entity.Device.DeviceModel;
import com.voxloud.provisioning.exception.InvalidOverrideConfigurationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class ConferenceConverter extends ConfigurationConverter {

    public ConferenceConverter(ObjectMapper objectMapper, ProvisioningConfigProperties provisioningConfigProperties) {
        super(objectMapper, provisioningConfigProperties, DeviceModel.CONFERENCE);
    }

    @Override
    protected void overrideConfigurationByFragment(Map<String, Object> deviceProperties, String overrideFragment) {
        try {
            Map<String, Object> overrideMap = objectMapper.readValue(overrideFragment, new TypeReference<Map<String, Object>>() {});
            deviceProperties.putAll(overrideMap);
        } catch (JsonProcessingException e) {
            log.error("Cannot parse override fragment for device model {}", DeviceModel.CONFERENCE, e);
            throw new InvalidOverrideConfigurationException(DeviceModel.CONFERENCE);
        }
    }

    @Override
    protected String formatToResponse(Map<String, Object> deviceProperties) {
        try {
            return objectMapper.writeValueAsString(deviceProperties);
        } catch (JsonProcessingException e) {
            log.error("Cannot format device configuration to json for device model {}", DeviceModel.CONFERENCE, e);
            throw new RuntimeException(e);
        }
    }
}
