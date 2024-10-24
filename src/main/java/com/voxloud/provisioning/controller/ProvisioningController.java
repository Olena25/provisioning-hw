package com.voxloud.provisioning.controller;

import com.voxloud.provisioning.service.ProvisioningService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProvisioningController {
    private final ProvisioningService provisioningService;

    @GetMapping("/provisioning/{macAddress}")
    public String getProvisioningConfiguration(@PathVariable String macAddress) {
        return provisioningService.getProvisioningFile(macAddress);
    }
}