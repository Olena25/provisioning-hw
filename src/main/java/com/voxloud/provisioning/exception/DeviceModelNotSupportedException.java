package com.voxloud.provisioning.exception;

import static com.voxloud.provisioning.entity.Device.DeviceModel;

public class DeviceModelNotSupportedException extends RuntimeException {

    public DeviceModelNotSupportedException(DeviceModel deviceModel) {
        super("Device model " + deviceModel + " is not currently supported");
    }
}
