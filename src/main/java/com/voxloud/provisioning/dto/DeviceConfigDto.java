package com.voxloud.provisioning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DeviceConfigDto {
    private String username;
    private String password;
    private String domain;
    private String port;
    private List<String> codecs;
}
