package gov.dot.its.codehub.adminapi.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import gov.dot.its.codehub.adminapi.MockDataConfiguration;
import gov.dot.its.codehub.adminapi.MockESUtils;
import gov.dot.its.codehub.adminapi.model.CHCategory;
import gov.dot.its.codehub.adminapi.model.CHConfiguration;
import gov.dot.its.codehub.adminapi.model.CHEngagementPopup;

@RunWith(SpringRunner.class)
public class ConfigurationDaoTest {

	private static final String TEST_ID = "1";

	private MockESUtils mockESUtils;
	private MockDataConfiguration mockData;

	@InjectMocks
	ConfigurationDaoImpl configurationDao;

	@Mock
	ESClientDao esClientDao;

	@Mock
	ObjectMapper objectMapper;

	@Before
	public void setUp() {
		ReflectionTestUtils.setField(configurationDao, "configurationsIndex", "unitTextIndex");
		ReflectionTestUtils.setField(configurationDao, "configurationId", "configurationId");
		ReflectionTestUtils.setField(configurationDao, "imagesList", "http://imageList.test.com");
		ReflectionTestUtils.setField(configurationDao, "imagesPath", "imagesPath");

		mockESUtils = new MockESUtils();
		mockData = new MockDataConfiguration();

	}

	@Test
	public void testGetConfigurationNoData() throws IOException {
		GetResponse getResponse = mockESUtils.generateFakeGetResponse(null);
		when(esClientDao.get(any(GetRequest.class), any(RequestOptions.class))).thenReturn(getResponse);

		CHConfiguration configuration = configurationDao.getConfiguration();
		assertNull(configuration);
	}

	@Test
	public void testGetConfigurationData() throws IOException {
		CHConfiguration fakeConfiguration = mockData.getFakeConfiguration();
		GetResponse getResponse = mockESUtils.generateFakeGetResponse(fakeConfiguration);
		when(esClientDao.get(any(GetRequest.class), any(RequestOptions.class))).thenReturn(getResponse);

		CHConfiguration configuration = configurationDao.getConfiguration();
		assertNotNull(configuration);
		assertNotNull(configuration.getCategories());
		assertFalse(configuration.getCategories().isEmpty());
		assertNotNull(configuration.getEngagementPopups());
		assertFalse(configuration.getEngagementPopups().isEmpty());
	}

	@Test
	public void testGetCategoriesNoData() throws IOException {
		GetResponse getResponse = mockESUtils.generateFakeGetResponse(null);
		when(esClientDao.get(any(GetRequest.class), any(RequestOptions.class))).thenReturn(getResponse);

		List<CHCategory> categories = configurationDao.getCategories();
		assertTrue(categories.isEmpty());
	}

	@Test
	public void testGetCategoriesData() throws IOException {
		CHConfiguration fakeConfiguration = mockData.getFakeConfiguration();
		GetResponse getResponse = mockESUtils.generateFakeGetResponse(fakeConfiguration);
		when(esClientDao.get(any(GetRequest.class), any(RequestOptions.class))).thenReturn(getResponse);

		List<CHCategory> categories = configurationDao.getCategories();
		assertFalse(categories.isEmpty());
	}

	@Test
	public void testAddCategory() throws IOException {
		CHCategory fakeCategory = mockData.getFakeCategory(TEST_ID);
		UpdateResponse updateResponse = mockESUtils.generateFakeUpdateResponse(fakeCategory);
		when(esClientDao.update(any(UpdateRequest.class),any(RequestOptions.class))).thenReturn(updateResponse);

		String result = configurationDao.addCategory(fakeCategory);
		assertNotNull(result);
		assertEquals(Result.UPDATED.toString(), result);
	}

	@Test
	public void testUpdateCategory() throws IOException {
		CHCategory fakeCategory = mockData.getFakeCategory(TEST_ID);
		UpdateResponse updateResponse = mockESUtils.generateFakeUpdateResponse(fakeCategory);
		when(esClientDao.update(any(UpdateRequest.class),any(RequestOptions.class))).thenReturn(updateResponse);

		String result = configurationDao.updateCategory(fakeCategory);
		assertNotNull(result);
		assertEquals(Result.UPDATED.toString(), result);
	}

	@Test
	public void testGetCategoryByIdInvalidConfiguration() throws IOException {
		GetResponse getResponse = mockESUtils.generateFakeGetResponse(null);
		when(esClientDao.get(any(GetRequest.class),any(RequestOptions.class))).thenReturn(getResponse);

		CHCategory category = configurationDao.getCategoryById(TEST_ID);
		assertNull(category);
	}

	@Test
	public void testGetCategoryByIdFound() throws IOException {
		CHConfiguration fakeConfiguration = mockData.getFakeConfiguration();
		GetResponse getResponse = mockESUtils.generateFakeGetResponse(fakeConfiguration);
		when(esClientDao.get(any(GetRequest.class),any(RequestOptions.class))).thenReturn(getResponse);

		CHCategory category = configurationDao.getCategoryById(TEST_ID);
		assertNotNull(category);
	}

	@Test
	public void testGetCategoryByIdNotFound() throws IOException {
		CHConfiguration fakeConfiguration = mockData.getFakeConfiguration();
		GetResponse getResponse = mockESUtils.generateFakeGetResponse(fakeConfiguration);
		when(esClientDao.get(any(GetRequest.class),any(RequestOptions.class))).thenReturn(getResponse);

		CHCategory category = configurationDao.getCategoryById("not exists");
		assertNull(category);
	}

	@Test
	public void testDeleteCategoryById() throws IOException {
		CHCategory fakeCategory = mockData.getFakeCategory(TEST_ID);
		UpdateResponse updateResponse = mockESUtils.generateFakeUpdateResponse(fakeCategory);
		when(esClientDao.update(any(UpdateRequest.class),any(RequestOptions.class))).thenReturn(updateResponse);

		Boolean result = configurationDao.deleteCategoryById(TEST_ID);
		assertTrue(result);
	}

	@Test
	public void testGetCategoryImages() throws IOException {

		List<String> fakeImageList = mockData.getFakeListOfImages();

		when(objectMapper.readValue(any(URL.class), any(TypeReference.class))).thenReturn(fakeImageList);
		List<String> images = configurationDao.getCategoryImages();
		assertFalse(images.isEmpty());
	}

	@Test
	public void testGetEngagementPopupsNoData() throws IOException {
		GetResponse getResponse = mockESUtils.generateFakeGetResponse(null);
		when(esClientDao.get(any(GetRequest.class),any(RequestOptions.class))).thenReturn(getResponse);

		List<CHEngagementPopup> engagementPopups = configurationDao.getEngagementPopups();
		assertTrue(engagementPopups.isEmpty());
	}

	@Test
	public void testGetEngagementPopupsData() throws IOException {
		CHConfiguration configuration = mockData.getFakeConfiguration();
		GetResponse getResponse = mockESUtils.generateFakeGetResponse(configuration);
		when(esClientDao.get(any(GetRequest.class),any(RequestOptions.class))).thenReturn(getResponse);

		List<CHEngagementPopup> engagementPopups = configurationDao.getEngagementPopups();
		assertFalse(engagementPopups.isEmpty());
	}

	@Test
	public void testUpdateEngagementPopup() throws IOException {
		CHEngagementPopup engagementPopup = mockData.getFakeEngagementPopup(TEST_ID);
		UpdateResponse updateResponse = mockESUtils.generateFakeUpdateResponse(engagementPopup);
		when(esClientDao.update(any(UpdateRequest.class),any(RequestOptions.class))).thenReturn(updateResponse);

		String result = configurationDao.updateEngagementPopup(engagementPopup);
		assertNotNull(result);
		assertEquals(Result.UPDATED.toString(), result);
	}

	@Test
	public void testAddEngagementPopup() throws IOException {
		CHEngagementPopup engagementPopup = mockData.getFakeEngagementPopup(TEST_ID);
		UpdateResponse updateResponse = mockESUtils.generateFakeUpdateResponse(engagementPopup);
		when(esClientDao.update(any(UpdateRequest.class),any(RequestOptions.class))).thenReturn(updateResponse);

		String result = configurationDao.addEngagementPopup(engagementPopup);
		assertNotNull(result);
		assertEquals(Result.UPDATED.toString(), result);
	}

	@Test
	public void testRemoveEngagementPopup() throws IOException {
		CHEngagementPopup engagementPopup = mockData.getFakeEngagementPopup(TEST_ID);
		UpdateResponse updateResponse = mockESUtils.generateFakeUpdateResponse(engagementPopup);
		when(esClientDao.update(any(UpdateRequest.class),any(RequestOptions.class))).thenReturn(updateResponse);

		Boolean result = configurationDao.removeEngagementPopup(TEST_ID);
		assertTrue(result);
	}
}
