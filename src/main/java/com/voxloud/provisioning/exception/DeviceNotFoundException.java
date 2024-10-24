package com.voxloud.provisioning.exception;

public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(String macAddress) {
        super("Device with mac address is not found: " + macAddress);
    }
}
