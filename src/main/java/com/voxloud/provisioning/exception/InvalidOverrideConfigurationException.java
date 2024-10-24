package com.voxloud.provisioning.exception;

import com.voxloud.provisioning.entity.Device.DeviceModel;

public class InvalidOverrideConfigurationException extends RuntimeException {
    public InvalidOverrideConfigurationException(DeviceModel deviceModel) {
        super("Override config for requested device with type "+ deviceModel + " is invalid");
    }
}
