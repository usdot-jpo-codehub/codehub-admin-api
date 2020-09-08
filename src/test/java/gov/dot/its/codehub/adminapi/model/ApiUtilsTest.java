package gov.dot.its.codehub.adminapi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import gov.dot.its.codehub.adminapi.utils.ApiUtils;

@RunWith(SpringRunner.class)
public class ApiUtilsTest {
	final String TEST_MESSAGE = "Testing...";
	final String TEST_KEY = "key";
	final int TEST_INT_VAL = 1;

	@Test
	public void testInstance() {
		ApiUtils apiUtils = new ApiUtils();
		assertNotNull(apiUtils);
	}

	@Test
	public void testGetQueryParamStringDefault() {
		Map<String, String> params = new HashMap<>();
		params.put(TEST_KEY, "value");

		ApiUtils apiUtils = new ApiUtils();
		String result = apiUtils.getQueryParamString(params, "T", TEST_MESSAGE);
		assertEquals(TEST_MESSAGE, result);
	}

	@Test
	public void testGetQueryParamStringFound() {
		Map<String, String> params = new HashMap<>();
		params.put(TEST_KEY, TEST_MESSAGE);

		ApiUtils apiUtils = new ApiUtils();
		String result = apiUtils.getQueryParamString(params, TEST_KEY, null);
		assertEquals(TEST_MESSAGE, result);
	}

	@Test
	public void testGetQueryParamIntegerDefault() {
		Map<String, String> params = new HashMap<>();
		params.put(TEST_KEY, String.valueOf(TEST_INT_VAL));

		ApiUtils apiUtils = new ApiUtils();
		int result = apiUtils.getQueryParamInteger(params, "r", TEST_INT_VAL);
		assertEquals(TEST_INT_VAL, result);
	}

	@Test
	public void testGetQueryParamIntegerInvalidNumber() {
		Map<String, String> params = new HashMap<>();
		params.put(TEST_KEY, "A");

		ApiUtils apiUtils = new ApiUtils();
		int result = apiUtils.getQueryParamInteger(params, TEST_KEY, TEST_INT_VAL);
		assertEquals(TEST_INT_VAL, result);
	}

	@Test
	public void testGetQueryParamIntegerFound() {
		Map<String, String> params = new HashMap<>();
		params.put(TEST_KEY, String.valueOf(TEST_INT_VAL));

		ApiUtils apiUtils = new ApiUtils();
		int result = apiUtils.getQueryParamInteger(params, TEST_KEY, 0);
		assertEquals(TEST_INT_VAL, result);
	}
}
