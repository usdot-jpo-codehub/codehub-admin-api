package gov.dot.its.codehub.adminapi.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

import gov.dot.its.codehub.adminapi.MockDataRepository;
import gov.dot.its.codehub.adminapi.dao.RepositoryDao;
import gov.dot.its.codehub.adminapi.model.ApiError;
import gov.dot.its.codehub.adminapi.model.ApiResponse;
import gov.dot.its.codehub.adminapi.model.CHRepository;
import gov.dot.its.codehub.adminapi.utils.ApiUtils;
import gov.dot.its.codehub.adminapi.utils.HeaderUtils;

@RunWith(SpringRunner.class)
public class RepositoryServiceTest {

	private final String TEST_HTTP_GET = "GET";
	private final String TEST_HTTP_POST = "POST";
	private final String TEST_HTTP_PUT = "PUT";
	private final String TEST_HTTP_DELETE = "DELETE";
	private final String TEST_TOKEN_KEY = "CHTOKEN";
	private final String TEST_TOKEN_VALUE = "change-it";
	private final String TEST_MESSAGE = "Testing...";
	private final String TEST_ID = "1";
	private final String TEST_PARAM_LIMIT = "limit";
	private final String TEST_PARAM_LIMIT_VALUE = "10";
	private final String TEST_NOT_FOUND = "NOT_FOUND";

	private MockDataRepository mockData;

	@InjectMocks
	private RepositoryServiceImpl repositoryService;

	@Mock
	private RepositoryDao repositoryDao;

	@Mock
	private HeaderUtils headerUtils;

	@Mock
	private ApiUtils apiUtils;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mockData = new MockDataRepository();
	}

	@Test
	public void testGetAllAuth() {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);

		Map<String, String> params = new HashMap<>();

		when(headerUtils.validateToken(anyString())).thenReturn(false);

		ApiResponse<List<CHRepository>> apiResponse = repositoryService.getAll(request, params);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), apiResponse.getCode());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testGetAllData() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		Map<String, String> params = new HashMap<>();
		params.put(TEST_PARAM_LIMIT, TEST_PARAM_LIMIT_VALUE);

		List<CHRepository> repositories = mockData.getFakeListOfRepositories();

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(repositoryDao.getAll(anyInt())).thenReturn(repositories);

		ApiResponse<List<CHRepository>> apiResponse = repositoryService.getAll(request, params);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.OK.value(), apiResponse.getCode());
		assertFalse(apiResponse.getResult().isEmpty());
	}

	@Test
	public void testGetAllNoData() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		Map<String, String> params = new HashMap<>();
		params.put(TEST_PARAM_LIMIT, TEST_PARAM_LIMIT_VALUE);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(repositoryDao.getAll(anyInt())).thenReturn(new ArrayList<>());

		ApiResponse<List<CHRepository>> apiResponse = repositoryService.getAll(request, params);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.NO_CONTENT.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
	}

	@Test
	public void testGetAllException() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		Map<String, String> params = new HashMap<>();
		params.put(TEST_PARAM_LIMIT, TEST_PARAM_LIMIT_VALUE);

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(TEST_MESSAGE));

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(apiUtils.getErrorsFromException(anyList(), any(Exception.class))).thenReturn(errors);
		when(repositoryDao.getAll(anyInt())).thenThrow(new IOException());

		ApiResponse<List<CHRepository>> apiResponse = repositoryService.getAll(request, params);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testAddRepositoryAuth() {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);

		CHRepository repository = mockData.getFakeCHRepository();

		when(headerUtils.validateToken(anyString())).thenReturn(false);

		ApiResponse<CHRepository> apiResponse = repositoryService.addRepository(request, repository);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), apiResponse.getCode());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testAddRepositoryConflict() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		CHRepository repository = mockData.getFakeCHRepository();

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(apiUtils.getMd5(any(String.class))).thenReturn(TEST_ID);
		when(repositoryDao.docExists(any(String.class))).thenReturn(true);

		ApiResponse<CHRepository> apiResponse = repositoryService.addRepository(request, repository);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.CONFLICT.value(), apiResponse.getCode());
		assertNotNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testAddRepositoryAdd() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		CHRepository repository = mockData.getFakeCHRepository();

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(apiUtils.getMd5(any(String.class))).thenReturn(TEST_ID);
		when(repositoryDao.docExists(any(String.class))).thenReturn(false);
		when(repositoryDao.addRepository(any(CHRepository.class))).thenReturn(TEST_MESSAGE);

		ApiResponse<CHRepository> apiResponse = repositoryService.addRepository(request, repository);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.OK.value(), apiResponse.getCode());
		assertNotNull(apiResponse.getResult());
		assertFalse(apiResponse.getMessages().isEmpty());
	}

	@Test
	public void testAddRepositoryException() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		CHRepository repository = mockData.getFakeCHRepository();
		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(TEST_MESSAGE));

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(apiUtils.getMd5(any(String.class))).thenReturn(TEST_ID);
		when(apiUtils.getErrorsFromException(anyList(), any(Exception.class))).thenReturn(errors);
		when(repositoryDao.docExists(any(String.class))).thenReturn(false);
		when(repositoryDao.addRepository(any(CHRepository.class))).thenThrow(new IOException());

		ApiResponse<CHRepository> apiResponse = repositoryService.addRepository(request, repository);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testUpdateRepositoryAuth() {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_PUT);

		CHRepository repository = mockData.getFakeCHRepository();

		when(headerUtils.validateToken(anyString())).thenReturn(false);

		ApiResponse<CHRepository> apiResponse = repositoryService.updateRepository(request, repository);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), apiResponse.getCode());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testUpdateRepositoryOK() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_PUT);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		CHRepository repository = mockData.getFakeCHRepository();

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(repositoryDao.updateRepository(any(CHRepository.class))).thenReturn(TEST_MESSAGE);

		ApiResponse<CHRepository> apiResponse = repositoryService.updateRepository(request, repository);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.OK.value(), apiResponse.getCode());
		assertNotNull(apiResponse.getResult());
		assertFalse(apiResponse.getMessages().isEmpty());
	}

	@Test
	public void testUpdateRepositoryException() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_PUT);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		CHRepository repository = mockData.getFakeCHRepository();
		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(TEST_MESSAGE));

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(apiUtils.getErrorsFromException(anyList(), any(Exception.class))).thenReturn(errors);
		when(repositoryDao.updateRepository(any(CHRepository.class))).thenThrow(new IOException());

		ApiResponse<CHRepository> apiResponse = repositoryService.updateRepository(request, repository);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testDeleteRepositoryAuth() {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_DELETE);

		when(headerUtils.validateToken(anyString())).thenReturn(false);

		ApiResponse<String> apiResponse = repositoryService.deleteRepository(request, TEST_ID);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), apiResponse.getCode());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testDeleteRepositoryNotFound() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_DELETE);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(repositoryDao.deleteRepository(anyString())).thenReturn(TEST_NOT_FOUND);

		ApiResponse<String> apiResponse = repositoryService.deleteRepository(request, TEST_ID);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.NOT_FOUND.value(), apiResponse.getCode());
		assertNotNull(apiResponse.getResult());
		assertFalse(apiResponse.getMessages().isEmpty());
	}

	@Test
	public void testDeleteRepositoryOk() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_DELETE);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(repositoryDao.deleteRepository(anyString())).thenReturn(TEST_MESSAGE);

		ApiResponse<String> apiResponse = repositoryService.deleteRepository(request, TEST_ID);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.OK.value(), apiResponse.getCode());
		assertNotNull(apiResponse.getResult());
		assertFalse(apiResponse.getMessages().isEmpty());
	}

	@Test
	public void testDeleteRepositoryException() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_DELETE);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(TEST_MESSAGE));

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(apiUtils.getErrorsFromException(anyList(), any(Exception.class))).thenReturn(errors);
		when(repositoryDao.deleteRepository(anyString())).thenThrow(new IOException());

		ApiResponse<String> apiResponse = repositoryService.deleteRepository(request, TEST_ID);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testDeleteRepositoriesAuth() {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_DELETE);

		List<String> repositoryIds = mockData.getFakeListOfRepositories().stream().map( x -> x.getId()).collect(Collectors.toList());

		when(headerUtils.validateToken(anyString())).thenReturn(false);

		ApiResponse<List<String>> apiResponse = repositoryService.deleteRepositories(request, repositoryIds);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), apiResponse.getCode());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testDeleteRepositoriesNotFound() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_DELETE);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		List<String> repositoryIds = mockData.getFakeListOfRepositories().stream().map( x -> x.getId()).collect(Collectors.toList());

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(repositoryDao.deleteRepository(anyString())).thenReturn(TEST_NOT_FOUND);

		ApiResponse<List<String>> apiResponse = repositoryService.deleteRepositories(request, repositoryIds);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.OK.value(), apiResponse.getCode());
		assertNotNull(apiResponse.getResult());
		assertFalse(apiResponse.getMessages().isEmpty());
	}

	@Test
	public void testDeleteRepositoriesOk() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_DELETE);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		List<String> repositoryIds = mockData.getFakeListOfRepositories().stream().map( x -> x.getId()).collect(Collectors.toList());

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(repositoryDao.deleteRepository(anyString())).thenReturn(TEST_MESSAGE);

		ApiResponse<List<String>> apiResponse = repositoryService.deleteRepositories(request, repositoryIds);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.OK.value(), apiResponse.getCode());
		assertNotNull(apiResponse.getResult());
		assertFalse(apiResponse.getMessages().isEmpty());
	}

	@Test
	public void testDeleteRepositoriesErrors() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_DELETE);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		List<String> repositoryIds = mockData.getFakeListOfRepositories().stream().map( x -> x.getId()).collect(Collectors.toList());
		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(TEST_MESSAGE));

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(apiUtils.getErrorsFromException(anyList(), any(Exception.class))).thenReturn(errors);
		when(repositoryDao.deleteRepository(anyString())).thenThrow(new IOException());

		ApiResponse<List<String>> apiResponse = repositoryService.deleteRepositories(request, repositoryIds);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testResetCacheAuth() {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);

		List<String> repositoryIds = mockData.getFakeListOfRepositories().stream().map( x -> x.getId()).collect(Collectors.toList());

		when(headerUtils.validateToken(anyString())).thenReturn(false);

		ApiResponse<List<String>> apiResponse = repositoryService.resetCache(request, repositoryIds);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), apiResponse.getCode());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testResetCacheNotFound() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		List<String> repositoryIds = mockData.getFakeListOfRepositories().stream().map( x -> x.getId()).collect(Collectors.toList());

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(repositoryDao.resetCache(anyString())).thenReturn(TEST_NOT_FOUND);


		ApiResponse<List<String>> apiResponse = repositoryService.resetCache(request, repositoryIds);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.OK.value(), apiResponse.getCode());
		assertNotNull(apiResponse.getResult());
		assertFalse(apiResponse.getMessages().isEmpty());
	}

	@Test
	public void testResetCacheFound() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		List<String> repositoryIds = mockData.getFakeListOfRepositories().stream().map( x -> x.getId()).collect(Collectors.toList());

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(repositoryDao.resetCache(anyString())).thenReturn(TEST_MESSAGE);


		ApiResponse<List<String>> apiResponse = repositoryService.resetCache(request, repositoryIds);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.OK.value(), apiResponse.getCode());
		assertNotNull(apiResponse.getResult());
		assertFalse(apiResponse.getMessages().isEmpty());
	}

	@Test
	public void testResetCacheErrors() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_POST);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		List<String> repositoryIds = mockData.getFakeListOfRepositories().stream().map( x -> x.getId()).collect(Collectors.toList());
		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(TEST_MESSAGE));

		when(headerUtils.validateToken(anyString())).thenReturn(true);
		when(apiUtils.getErrorsFromException(anyList(), any(Exception.class))).thenReturn(errors);
		when(repositoryDao.resetCache(anyString())).thenThrow(new IOException());


		ApiResponse<List<String>> apiResponse = repositoryService.resetCache(request, repositoryIds);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}
}
