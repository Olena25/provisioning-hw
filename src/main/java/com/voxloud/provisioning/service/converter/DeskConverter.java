package com.voxloud.provisioning.service.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxloud.provisioning.config.ProvisioningConfigProperties;
import com.voxloud.provisioning.entity.Device.DeviceModel;
import com.voxloud.provisioning.exception.InvalidOverrideConfigurationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DeskConverter extends ConfigurationConverter {

    public DeskConverter(ObjectMapper objectMapper, ProvisioningConfigProperties provisioningConfigProperties) {
        super(objectMapper, provisioningConfigProperties, DeviceModel.DESK);
    }

    @Override
    protected void overrideConfigurationByFragment(Map<String, Object> deviceProperties, String overrideFragment) {
        try {
            Properties properties = new Properties();
            properties.load(new StringReader(overrideFragment));
            validateProperties(properties);

            for (Map.Entry<Object, Object> overrideEntry : properties.entrySet()) {
                deviceProperties.put(overrideEntry.getKey().toString(), overrideEntry.getValue());
            }
        } catch (IOException e) {
            log.error("Cannot parse override fragment for device model {}", DeviceModel.DESK, e);
            throw new InvalidOverrideConfigurationException(DeviceModel.DESK);
        }
    }

    @Override
    protected String formatToResponse(Map<String, Object> properties) {
        List<String> responseProperties = new ArrayList<>();

        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String value = entry.getValue().toString();
            if (entry.getValue() instanceof List) {
                value = formatList((List<Object>) entry.getValue());
            }
            responseProperties.add(entry.getKey() + "=" + value);
        }
        return String.join("\n", responseProperties);
    }

    private String formatList(List<Object> list) {
        return list.stream().map(Object::toString).collect(Collectors.joining(","));
    }

    private void validateProperties(Properties properties) {
        boolean isEmptyValues = properties.values().stream()
                .filter(value -> value instanceof String)
                .anyMatch(value -> ((String) value).isEmpty());

        if(properties.isEmpty() || isEmptyValues) {
            log.error("Cannot parse override fragment for device model {}", DeviceModel.DESK);
            throw new InvalidOverrideConfigurationException(DeviceModel.DESK);
        }
    }
}

