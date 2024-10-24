package com.voxloud.provisioning.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "provisioning")
@Data
public class ProvisioningConfigProperties {
    private String domain;
    private String port;
    private List<String> codecs;
}