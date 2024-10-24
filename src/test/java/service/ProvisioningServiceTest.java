package service;

import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.exception.DeviceModelNotSupportedException;
import com.voxloud.provisioning.exception.DeviceNotFoundException;
import com.voxloud.provisioning.repository.DeviceRepository;
import com.voxloud.provisioning.service.ProvisioningService;
import com.voxloud.provisioning.service.ProvisioningServiceImpl;
import com.voxloud.provisioning.service.converter.ConfigurationConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static com.voxloud.provisioning.entity.Device.DeviceModel.CONFERENCE;

@ExtendWith(MockitoExtension.class)
public class ProvisioningServiceTest {

    private static final String MAC_ADDRESS = "abcd";
    private static final String TEST_RESULT = "123ddd";

    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private ConfigurationConverter configurationConverter;

    private ProvisioningService provisioningService;

    @BeforeEach
    public void setup() {
        provisioningService = new ProvisioningServiceImpl(deviceRepository, Collections.singletonList(configurationConverter));
    }

    @Test
    void getProvisioningFileWhenDeviceFoundShouldCallConverter() {
        Device device = new Device();
        device.setModel(CONFERENCE);
        Mockito.when(deviceRepository.findById(MAC_ADDRESS)).thenReturn(Optional.of(device));
        Mockito.when(configurationConverter.supportDevice(device)).thenReturn(true);
        Mockito.when(configurationConverter.convert(device)).thenReturn(TEST_RESULT);

        String actualResult = provisioningService.getProvisioningFile(MAC_ADDRESS);

        Assertions.assertEquals(TEST_RESULT, actualResult);
        Mockito.verify(configurationConverter, Mockito.times(1)).convert(device);
    }

    @Test
    void getProvisioningFileWhenDeviceIsNotFoundShouldThrowException() {
        Mockito.when(deviceRepository.findById(MAC_ADDRESS)).thenReturn(Optional.empty());

        DeviceNotFoundException exception = Assertions.assertThrows(DeviceNotFoundException.class, () -> provisioningService.getProvisioningFile(MAC_ADDRESS));
        Assertions.assertEquals("Device with mac address is not found: " + MAC_ADDRESS, exception.getMessage());
    }

    @Test
    void getProvisioningFileWhenDeviceModelCannotBeConvertedShouldThrowException() {
        Device device = new Device();
        device.setModel(CONFERENCE);
        Mockito.when(deviceRepository.findById(MAC_ADDRESS)).thenReturn(Optional.of(device));
        Mockito.when(configurationConverter.supportDevice(device)).thenReturn(false);

        DeviceModelNotSupportedException exception = Assertions.assertThrows(DeviceModelNotSupportedException.class,
                () -> provisioningService.getProvisioningFile(MAC_ADDRESS));
        Assertions.assertEquals("Device model " + CONFERENCE + " is not currently supported", exception.getMessage());
    }
}
