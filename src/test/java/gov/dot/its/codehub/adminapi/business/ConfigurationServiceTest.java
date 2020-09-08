package gov.dot.its.codehub.adminapi.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

import gov.dot.its.codehub.adminapi.MockDataConfiguration;
import gov.dot.its.codehub.adminapi.dao.ConfigurationDao;
import gov.dot.its.codehub.adminapi.dao.RepositoryDao;
import gov.dot.its.codehub.adminapi.model.ApiError;
import gov.dot.its.codehub.adminapi.model.ApiResponse;
import gov.dot.its.codehub.adminapi.model.CHCategory;
import gov.dot.its.codehub.adminapi.model.CHConfiguration;
import gov.dot.its.codehub.adminapi.model.CHEngagementPopup;
import gov.dot.its.codehub.adminapi.utils.ApiUtils;
import gov.dot.its.codehub.adminapi.utils.HeaderUtils;

@RunWith(SpringRunner.class)
public class ConfigurationServiceTest {

	private final String TEST_HTTP_GET = "GET";
	private final String TEST_HTTP_POST = "POST";
	private final String TEST_TOKEN_KEY = "CHTOKEN";
	private final String TEST_TOKEN_VALUE = "change-it";
	private final String TEST_MESSAGE = "Testing...";
	private final String TEST_ID = "1";

	private MockDataConfiguration mockData;

	@InjectMocks
	private ConfigurationServiceImpl configurationService;

	@Mock
	private ConfigurationDao configurationDao;

	@Mock
	private RepositoryDao repositoryDao;

	@Mock
	private HeaderUtils headerUtils;

	@Mock
	private ApiUtils apiUtils;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mockData = new MockDataConfiguration();
	}

	@Test
	public void testConfigurationUnauthorized() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);

		when(headerUtils.validateToken(anyString())).thenReturn(false);

		ApiResponse<CHConfiguration> apiResponse = configurationService.configurations(request);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), apiResponse.getCode());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testConfigurationData() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		CHConfiguration configuration = this.mockData.getFakeConfiguration();

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(configurationDao.getConfiguration()).thenReturn(configuration);

		ApiResponse<CHConfiguration> apiResponse = configurationService.configurations(request);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.OK.value(), apiResponse.getCode());
		assertNotNull(apiResponse.getResult());
	}

	@Test
	public void testConfigurationNoData() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(configurationDao.getConfiguration()).thenReturn(null);

		ApiResponse<CHConfiguration> apiResponse = configurationService.configurations(request);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.NO_CONTENT.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
	}

	@Test
	public void testConfigurationError() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);
		final List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(TEST_MESSAGE));
		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(apiUtils.getErrorsFromException(anyList(), any(Exception.class))).thenReturn(errors);
		when(configurationDao.getConfiguration()).thenThrow(new IOException());

		ApiResponse<CHConfiguration> apiResponse = configurationService.configurations(request);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testCategoriesAuth() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);

		when(headerUtils.validateToken(anyString())).thenReturn(false);
		when(configurationDao.getConfiguration()).thenThrow(new IOException());

		ApiResponse<List<CHCategory>> apiResponse = configurationService.categories(request);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testCategoriesData() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		List<CHCategory> categories = this.mockData.getFakeCategories();

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(configurationDao.getCategories()).thenReturn(categories);

		ApiResponse<List<CHCategory>> apiResponse = configurationService.categories(request);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.OK.value(), apiResponse.getCode());
		assertNotNull(apiResponse.getResult());
	}

	@Test
	public void testCategoriesNoData() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(configurationDao.getCategories()).thenReturn(new ArrayList<>());

		ApiResponse<List<CHCategory>> apiResponse = configurationService.categories(request);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.NO_CONTENT.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
	}

	@Test
	public void testCategoriesError() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(TEST_MESSAGE));

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(apiUtils.getErrorsFromException(anyList(), any(Exception.class))).thenReturn(errors);
		when(configurationDao.getCategories()).thenThrow(new IOException());

		ApiResponse<List<CHCategory>> apiResponse = configurationService.categories(request);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testAddCategoryAuth() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);

		CHCategory category = this.mockData.getFakeCategory(TEST_ID);

		when(headerUtils.validateToken(anyString())).thenReturn(false);

		ApiResponse<CHCategory> apiResponse = configurationService.addCategory(request, category);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testAddCategoryOk() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		CHCategory category = this.mockData.getFakeCategory(TEST_ID);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(configurationDao.addCategory(any(CHCategory.class))).thenReturn(TEST_MESSAGE);

		ApiResponse<CHCategory> apiResponse = configurationService.addCategory(request, category);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.OK.value(), apiResponse.getCode());
		assertNotNull(apiResponse.getResult());
		assertFalse(apiResponse.getMessages().isEmpty());
	}

	@Test
	public void testAddCategoryFail() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		CHCategory category = this.mockData.getFakeCategory(TEST_ID);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(configurationDao.addCategory(any(CHCategory.class))).thenReturn(null);

		ApiResponse<CHCategory> apiResponse = configurationService.addCategory(request, category);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
	}

	@Test
	public void testAddCategoryException() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(TEST_MESSAGE));

		CHCategory category = this.mockData.getFakeCategory(TEST_ID);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(apiUtils.getErrorsFromException(anyList(), any(Exception.class))).thenReturn(errors);
		when(configurationDao.addCategory(any(CHCategory.class))).thenThrow(new IOException());

		ApiResponse<CHCategory> apiResponse = configurationService.addCategory(request, category);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testUpdateCategoryAuth() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);

		CHCategory category = this.mockData.getFakeCategory(TEST_ID);

		when(headerUtils.validateToken(anyString())).thenReturn(false);

		ApiResponse<CHCategory> apiResponse = configurationService.updateCategory(request, category);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testUpdateCategoryOk() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		CHCategory category = this.mockData.getFakeCategory(TEST_ID);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(configurationDao.updateCategory(any(CHCategory.class))).thenReturn(TEST_MESSAGE);

		ApiResponse<CHCategory> apiResponse = configurationService.updateCategory(request, category);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.OK.value(), apiResponse.getCode());
		assertNotNull(apiResponse.getResult());
		assertFalse(apiResponse.getMessages().isEmpty());
	}

	@Test
	public void testUpdateCategoryFail() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		CHCategory category = this.mockData.getFakeCategory(TEST_ID);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(configurationDao.updateCategory(any(CHCategory.class))).thenReturn(null);

		ApiResponse<CHCategory> apiResponse = configurationService.updateCategory(request, category);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
	}

	@Test
	public void testUpdateCategoryException() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(TEST_MESSAGE));

		CHCategory category = this.mockData.getFakeCategory(TEST_ID);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(apiUtils.getErrorsFromException(anyList(), any(Exception.class))).thenReturn(errors);
		when(configurationDao.updateCategory(any(CHCategory.class))).thenThrow(new IOException());

		ApiResponse<CHCategory> apiResponse = configurationService.updateCategory(request, category);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testCategoryAuth() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);

		when(headerUtils.validateToken(anyString())).thenReturn(false);

		ApiResponse<CHCategory> apiResponse = configurationService.category(request, TEST_ID);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testCategoryData() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);
		CHCategory category = this.mockData.getFakeCategory(TEST_ID);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(configurationDao.getCategoryById(anyString())).thenReturn(category);

		ApiResponse<CHCategory> apiResponse = configurationService.category(request, TEST_ID);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.OK.value(), apiResponse.getCode());
		assertNotNull(apiResponse.getResult());
	}

	@Test
	public void testCategoryNotFound() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(configurationDao.getCategoryById(anyString())).thenReturn(null);

		ApiResponse<CHCategory> apiResponse = configurationService.category(request, TEST_ID);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.NOT_FOUND.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
	}

	@Test
	public void testCategoryException() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(TEST_MESSAGE));

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(apiUtils.getErrorsFromException(anyList(), any(Exception.class))).thenReturn(errors);
		when(configurationDao.getCategoryById(anyString())).thenThrow(new IOException());

		ApiResponse<CHCategory> apiResponse = configurationService.category(request, TEST_ID);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testDeleteCategoryAuth() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);

		when(headerUtils.validateToken(anyString())).thenReturn(false);

		ApiResponse<CHCategory> apiResponse = configurationService.deleteCategory(request, TEST_ID);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testDeleteCategoryOK() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(configurationDao.deleteCategoryById(anyString())).thenReturn(true);
		when(repositoryDao.removeCategory(anyString())).thenReturn(TEST_MESSAGE);

		ApiResponse<CHCategory> apiResponse = configurationService.deleteCategory(request, TEST_ID);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.OK.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getMessages().isEmpty());
	}

	@Test
	public void testDeleteCategoryOKFailToRemoveFromRepositories() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(configurationDao.deleteCategoryById(anyString())).thenReturn(true);
		when(repositoryDao.removeCategory(anyString())).thenReturn(null);

		ApiResponse<CHCategory> apiResponse = configurationService.deleteCategory(request, TEST_ID);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.OK.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getMessages().isEmpty());
	}

	@Test
	public void testDeleteCategoryNotFound() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(configurationDao.deleteCategoryById(anyString())).thenReturn(false);

		ApiResponse<CHCategory> apiResponse = configurationService.deleteCategory(request, TEST_ID);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.NOT_FOUND.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
	}

	@Test
	public void testDeleteCategoryException() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(TEST_MESSAGE));

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(apiUtils.getErrorsFromException(anyList(), any(Exception.class))).thenReturn(errors);
		when(configurationDao.deleteCategoryById(anyString())).thenThrow(new IOException());

		ApiResponse<CHCategory> apiResponse = configurationService.deleteCategory(request, TEST_ID);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testCategoryImagesAuth() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);

		when(headerUtils.validateToken(anyString())).thenReturn(false);

		ApiResponse<List<String>> apiResponse = configurationService.categoryImages(request);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testCategoryImagesData() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		List<String> images = mockData.getFakeListOfImages();

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(configurationDao.getCategoryImages()).thenReturn(images);

		ApiResponse<List<String>> apiResponse = configurationService.categoryImages(request);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.OK.value(), apiResponse.getCode());
		assertFalse(apiResponse.getResult().isEmpty());
	}

	@Test
	public void testCategoryImagesNoData() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(configurationDao.getCategoryImages()).thenReturn(new ArrayList<>());

		ApiResponse<List<String>> apiResponse = configurationService.categoryImages(request);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.NO_CONTENT.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
	}

	@Test
	public void testCategoryImagesException() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(TEST_MESSAGE));

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(apiUtils.getErrorsFromException(anyList(), any(Exception.class))).thenReturn(errors);
		when(configurationDao.getCategoryImages()).thenThrow(new IOException());

		ApiResponse<List<String>> apiResponse = configurationService.categoryImages(request);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testEngagementPopupsAuth() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);

		when(headerUtils.validateToken(anyString())).thenReturn(false);

		ApiResponse<List<CHEngagementPopup>> apiResponse = configurationService.engagementpopups(request);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testEngagementPopupsData() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		List<CHEngagementPopup> engagementPopups = mockData.getFakeEngagementPopups();

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(configurationDao.getEngagementPopups()).thenReturn(engagementPopups);

		ApiResponse<List<CHEngagementPopup>> apiResponse = configurationService.engagementpopups(request);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.OK.value(), apiResponse.getCode());
		assertNotNull(apiResponse.getResult());
	}

	@Test
	public void testEngagementPopupsNoData() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(configurationDao.getEngagementPopups()).thenReturn(new ArrayList<>());

		ApiResponse<List<CHEngagementPopup>> apiResponse = configurationService.engagementpopups(request);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.NO_CONTENT.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
	}

	@Test
	public void testEngagementPopupsException() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(TEST_MESSAGE));

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(apiUtils.getErrorsFromException(anyList(), any(Exception.class))).thenReturn(errors);
		when(configurationDao.getEngagementPopups()).thenThrow(new IOException());

		ApiResponse<List<CHEngagementPopup>> apiResponse = configurationService.engagementpopups(request);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testUpdateEngagementPopupAuth() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);

		CHEngagementPopup engagementPopup = mockData.getFakeEngagementPopup(TEST_ID);

		when(headerUtils.validateToken(anyString())).thenReturn(false);

		ApiResponse<CHEngagementPopup> apiResponse = configurationService.updateEngagementPopup(request, engagementPopup);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testUpdateEngagementPopupOk() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		CHEngagementPopup engagementPopup = mockData.getFakeEngagementPopup(TEST_ID);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(configurationDao.updateEngagementPopup(any(CHEngagementPopup.class))).thenReturn(TEST_MESSAGE);

		ApiResponse<CHEngagementPopup> apiResponse = configurationService.updateEngagementPopup(request, engagementPopup);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.OK.value(), apiResponse.getCode());
		assertNotNull(apiResponse.getResult());
	}

	@Test
	public void testUpdateEngagementPopupFail() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		CHEngagementPopup engagementPopup = mockData.getFakeEngagementPopup(TEST_ID);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(configurationDao.updateEngagementPopup(any(CHEngagementPopup.class))).thenReturn(null);

		ApiResponse<CHEngagementPopup> apiResponse = configurationService.updateEngagementPopup(request, engagementPopup);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
	}

	@Test
	public void testUpdateEngagementPopupException() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(TEST_MESSAGE));

		CHEngagementPopup engagementPopup = mockData.getFakeEngagementPopup(TEST_ID);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(apiUtils.getErrorsFromException(anyList(), any(Exception.class))).thenReturn(errors);
		when(configurationDao.updateEngagementPopup(any(CHEngagementPopup.class))).thenThrow(new IOException());

		ApiResponse<CHEngagementPopup> apiResponse = configurationService.updateEngagementPopup(request, engagementPopup);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testAddEngagementPopupAuth() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);

		CHEngagementPopup engagementPopup = mockData.getFakeEngagementPopup(TEST_ID);

		when(headerUtils.validateToken(anyString())).thenReturn(false);

		ApiResponse<CHEngagementPopup> apiResponse = configurationService.addEngagementPopup(request, engagementPopup);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testAddEngagementPopupOk() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		CHEngagementPopup engagementPopup = mockData.getFakeEngagementPopup(TEST_ID);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(configurationDao.addEngagementPopup(any(CHEngagementPopup.class))).thenReturn(TEST_MESSAGE);

		ApiResponse<CHEngagementPopup> apiResponse = configurationService.addEngagementPopup(request, engagementPopup);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.OK.value(), apiResponse.getCode());
		assertNotNull(apiResponse.getResult());
	}

	@Test
	public void testAddEngagementPopupFail() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		CHEngagementPopup engagementPopup = mockData.getFakeEngagementPopup(TEST_ID);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(configurationDao.addEngagementPopup(any(CHEngagementPopup.class))).thenReturn(null);

		ApiResponse<CHEngagementPopup> apiResponse = configurationService.addEngagementPopup(request, engagementPopup);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
	}

	@Test
	public void testAddEngagementPopupException() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		CHEngagementPopup engagementPopup = mockData.getFakeEngagementPopup(TEST_ID);
		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(TEST_MESSAGE));

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(apiUtils.getErrorsFromException(anyList(), any(Exception.class))).thenReturn(errors);
		when(configurationDao.addEngagementPopup(any(CHEngagementPopup.class))).thenThrow(new IOException());

		ApiResponse<CHEngagementPopup> apiResponse = configurationService.addEngagementPopup(request, engagementPopup);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testDeleteEngagementPopupAuth() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);

		when(headerUtils.validateToken(anyString())).thenReturn(false);

		ApiResponse<CHEngagementPopup> apiResponse = configurationService.deleteEngagementPopup(request, TEST_ID);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testDeleteEngagementPopupOk() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(configurationDao.removeEngagementPopup(anyString())).thenReturn(true);

		ApiResponse<CHEngagementPopup> apiResponse = configurationService.deleteEngagementPopup(request, TEST_ID);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.OK.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
	}

	@Test
	public void testDeleteEngagementPopupNotFound() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(configurationDao.removeEngagementPopup(anyString())).thenReturn(false);

		ApiResponse<CHEngagementPopup> apiResponse = configurationService.deleteEngagementPopup(request, TEST_ID);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.NOT_FOUND.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
	}

	@Test
	public void testDeleteEngagementPopupException() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(TEST_MESSAGE));

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(apiUtils.getErrorsFromException(anyList(), any(Exception.class))).thenReturn(errors);
		when(configurationDao.removeEngagementPopup(anyString())).thenThrow(new IOException());

		ApiResponse<CHEngagementPopup> apiResponse = configurationService.deleteEngagementPopup(request, TEST_ID);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}
}
