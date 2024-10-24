package service.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxloud.provisioning.config.ProvisioningConfigProperties;
import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.exception.InvalidOverrideConfigurationException;
import com.voxloud.provisioning.service.converter.ConferenceConverter;
import com.voxloud.provisioning.service.converter.ConfigurationConverter;
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
import static com.voxloud.provisioning.entity.Device.DeviceModel.DESK;

@ExtendWith(MockitoExtension.class)
public class ConferenceConverterTest {

    private static final String TEST_USERNAME = "testUsername";
    private static final String TEST_PASSWORD = "testPassword";

    private static final String TEST_DEFAULT_PORT = "5555";
    private static final String TEST_DEFAULT_DOMAIN = "test.connect.com";
    private static final List<String> TEST_DEFAULT_CODECS = Arrays.asList("CD444", "OPICS", "TEST");

    private ConfigurationConverter configurationConverter;

    @BeforeEach
    public void setup() {
        configurationConverter = new ConferenceConverter(new ObjectMapper(), generateProvisioningProperties());
    }

    @Test
    void supportDeviceWhenDeviceHasCorrectModelShouldReturnTrue() {
        Device device = new Device();
        device.setModel(CONFERENCE);
        Assertions.assertTrue(configurationConverter.supportDevice(device));
    }

    @Test
    void supportDeviceWhenDeviceHasNotCorrectModelShouldReturnFalse() {
        Device device = new Device();
        device.setModel(DESK);

        Assertions.assertFalse(configurationConverter.supportDevice(device));
        Assertions.assertFalse(configurationConverter.supportDevice(new Device()));
    }

    @Test
    void convertWhenDeviceModelIsNotSupportedByConverterShouldThrowException() {
        Device device = new Device();
        device.setModel(DESK);

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> configurationConverter.convert(device));
        Assertions.assertEquals("Misconfigured converter, expected device with model CONFERENCE", exception.getMessage());
    }

    @ParameterizedTest
    @EmptySource
    @NullSource
    void convertWhenOverrideFragmentIsEmptyShouldUseValuesFromConfigProperties(String overrideFragment) {
        Device device = generateConferenceDevice(overrideFragment);
        String convertedResult = configurationConverter.convert(device);

        String expectedResult = "{\"username\":\"testUsername\",\"password\":\"testPassword\",\"domain\":\"test.connect.com\"," +
                "\"port\":\"5555\",\"codecs\":[\"CD444\",\"OPICS\",\"TEST\"]}";

        Assertions.assertEquals(expectedResult, convertedResult);
    }

    @Test
    void convertWhenOverrideFragmentIsPresentWithoutNewConfigsShouldReplaceDefaultProperties() {
        String overrideFragment = "{\"domain\":\"test.anotherconnect.com\",\"port\":\"5161\"}";

        Device device = generateConferenceDevice(overrideFragment);
        String convertedResult = configurationConverter.convert(device);

        String expectedResult = "{\"username\":\"testUsername\",\"password\":\"testPassword\",\"domain\":\"test.anotherconnect.com\"," +
                "\"port\":\"5161\",\"codecs\":[\"CD444\",\"OPICS\",\"TEST\"]}";

        Assertions.assertEquals(expectedResult, convertedResult);
    }

    @Test
    void convertWhenOverrideFragmentIsPresentWithNewConfigsShouldReplaceDefaultPropertiesAndAddNew() {
        String overrideFragment = "{\"domain\":\"test.anotherconnect.com\",\"port\":\"5161\",\"someNewProperty\":\"testNew\"}";

        Device device = generateConferenceDevice(overrideFragment);
        String convertedResult = configurationConverter.convert(device);

        String expectedResult = "{\"username\":\"testUsername\",\"password\":\"testPassword\",\"domain\":\"test.anotherconnect.com\"," +
                "\"port\":\"5161\",\"codecs\":[\"CD444\",\"OPICS\",\"TEST\"],\"someNewProperty\":\"testNew\"}";

        Assertions.assertEquals(expectedResult, convertedResult);
    }

    @Test
    void convertWhenOverrideFragmentIsInvalidFormatShouldThrowException() {
        String overrideFragment = "someInvalidFormat";

        Device device = generateConferenceDevice(overrideFragment);
        InvalidOverrideConfigurationException exception = Assertions.assertThrows(InvalidOverrideConfigurationException.class,
                () -> configurationConverter.convert(device));

        Assertions.assertEquals("Override config for requested device with type CONFERENCE is invalid", exception.getMessage());
    }

    private static ProvisioningConfigProperties generateProvisioningProperties() {
        ProvisioningConfigProperties provisioningConfigProperties = new ProvisioningConfigProperties();
        provisioningConfigProperties.setPort(TEST_DEFAULT_PORT);
        provisioningConfigProperties.setDomain(TEST_DEFAULT_DOMAIN);
        provisioningConfigProperties.setCodecs(TEST_DEFAULT_CODECS);

        return provisioningConfigProperties;
    }

    private static Device generateConferenceDevice(String overrideFragment) {
        Device device = new Device();
        device.setUsername(TEST_USERNAME);
        device.setPassword(TEST_PASSWORD);
        device.setModel(CONFERENCE);
        device.setOverrideFragment(overrideFragment);

        return device;
    }
}
