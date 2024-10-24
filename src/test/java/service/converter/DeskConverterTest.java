package service.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxloud.provisioning.config.ProvisioningConfigProperties;
import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.exception.InvalidOverrideConfigurationException;
import com.voxloud.provisioning.service.converter.ConfigurationConverter;
import com.voxloud.provisioning.service.converter.DeskConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static com.voxloud.provisioning.entity.Device.DeviceModel.CONFERENCE;

@ExtendWith(MockitoExtension.class)
public class DeskConverterTest {

    private static final String TEST_USERNAME = "testUsername";
    private static final String TEST_PASSWORD = "testPassword";

    private static final String TEST_DEFAULT_PORT = "5555";
    private static final String TEST_DEFAULT_DOMAIN = "test.connect.com";
    private static final List<String> TEST_DEFAULT_CODECS = Arrays.asList("CD444", "OPICS", "TEST");

    private ConfigurationConverter configurationConverter;

    @BeforeEach
    public void setup() {
        configurationConverter = new DeskConverter(new ObjectMapper(), generateProvisioningProperties());
    }

    @Test
    void supportDeviceWhenDeviceHasCorrectModelShouldReturnTrue() {
        Device device = new Device();
        device.setModel(Device.DeviceModel.DESK);
        Assertions.assertTrue(configurationConverter.supportDevice(device));
    }

    @Test
    void supportDeviceWhenDeviceHasNotCorrectModelShouldReturnFalse() {
        Device device = new Device();
        device.setModel(Device.DeviceModel.CONFERENCE);

        Assertions.assertFalse(configurationConverter.supportDevice(device));
        Assertions.assertFalse(configurationConverter.supportDevice(new Device()));
    }

    @Test
    void convertWhenDeviceModelIsNotSupportedByConverterShouldThrowException() {
        Device device = new Device();
        device.setModel(CONFERENCE);

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> configurationConverter.convert(device));
        Assertions.assertEquals("Misconfigured converter, expected device with model DESK", exception.getMessage());
    }

    @ParameterizedTest
    @EmptySource
    @NullSource
    void convertWhenOverrideFragmentIsEmptyShouldUseValuesFromConfigProperties(String overrideFragment) {
        Device device = generateDeskDevice(overrideFragment);
        String convertedResult = configurationConverter.convert(device);

        String expectedResult = "username=testUsername\npassword=testPassword\ndomain=test.connect.com\n" +
                "port=5555\ncodecs=CD444,OPICS,TEST";

        Assertions.assertEquals(expectedResult, convertedResult);
    }

    @Test
    void convertWhenOverrideFragmentIsPresentWithoutNewConfigsShouldReplaceDefaultProperties() {
        String overrideFragment = "domain=test.anotherconnect.com\nport=5161";

        Device device = generateDeskDevice(overrideFragment);
        String convertedResult = configurationConverter.convert(device);

        String expectedResult = "username=testUsername\npassword=testPassword\ndomain=test.anotherconnect.com\n" +
                "port=5161\ncodecs=CD444,OPICS,TEST";

        Assertions.assertEquals(expectedResult, convertedResult);
    }

    @Test
    void convertWhenOverrideFragmentIsPresentWithNewConfigsShouldReplaceDefaultPropertiesAndAddNew() {
        String overrideFragment = "domain=test.anotherconnect.com\nport=5161\nsomeNewProperty=testNew";

        Device device = generateDeskDevice(overrideFragment);
        String convertedResult = configurationConverter.convert(device);

        String expectedResult = "username=testUsername\npassword=testPassword\ndomain=test.anotherconnect.com\n" +
                "port=5161\ncodecs=CD444,OPICS,TEST\nsomeNewProperty=testNew";

        Assertions.assertEquals(expectedResult, convertedResult);
    }

    @Test
    void convertWhenOverrideFragmentIsInvalidFormatShouldThrowException() {
        String overrideFragment = "someInvalidFormat";

        Device device = generateDeskDevice(overrideFragment);
        InvalidOverrideConfigurationException exception = Assertions.assertThrows(InvalidOverrideConfigurationException.class,
                () -> configurationConverter.convert(device));

        Assertions.assertEquals("Override config for requested device with type DESK is invalid", exception.getMessage());
    }

    private static ProvisioningConfigProperties generateProvisioningProperties() {
        ProvisioningConfigProperties provisioningConfigProperties = new ProvisioningConfigProperties();
        provisioningConfigProperties.setPort(TEST_DEFAULT_PORT);
        provisioningConfigProperties.setDomain(TEST_DEFAULT_DOMAIN);
        provisioningConfigProperties.setCodecs(TEST_DEFAULT_CODECS);

        return provisioningConfigProperties;
    }

    private static Device generateDeskDevice(String overrideFragment) {
        Device device = new Device();
        device.setUsername(TEST_USERNAME);
        device.setPassword(TEST_PASSWORD);
        device.setModel(Device.DeviceModel.DESK);
        device.setOverrideFragment(overrideFragment);

        return device;
    }
}
