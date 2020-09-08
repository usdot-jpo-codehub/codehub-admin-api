package gov.dot.its.codehub.adminapi.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.dot.its.codehub.adminapi.MockDataRepository;
import gov.dot.its.codehub.adminapi.business.RepositoryService;
import gov.dot.its.codehub.adminapi.model.ApiError;
import gov.dot.its.codehub.adminapi.model.ApiMessage;
import gov.dot.its.codehub.adminapi.model.ApiResponse;
import gov.dot.its.codehub.adminapi.model.CHRepository;

@RunWith(SpringRunner.class)
@WebMvcTest(RepositoryController.class)
@AutoConfigureRestDocs(outputDir="target/generated-snippets", uriHost="example.com", uriPort=3007, uriScheme="http")
public class RepositoryControllerTest {

	private static final String TEST_DATAASSETS_URL = "%s/v1/repositories";
	private static final String SERVER_SERVLET_CONTEXT_PATH = "server.servlet.context-path";
	private static final String HEADER_HOST = "Host";
	private static final String HEADER_CONTENT_LENGTH = "Content-Length";
	private static final String ERROR_DESCRIPTION = "Error Description";
	private static final String MESSAGE_DESCRIPTION = "Message description";
	private static final String VERB_DELETE = "DELETE";
	private static final String HASH_ID_1 = "91128507a8ae9c8046c33ee0f31e37f8";
	private static final String HASH_ID_2 = "7f3bac27fc81d39ffa8ede58b39c8fb6";
	private static final String HASH_ID_3 = "fc4e98b95dc24f763621c54fe50ded24";

	private MockDataRepository mockData;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private Environment env;

	@MockBean
	private RepositoryService repositoryService;

	public RepositoryControllerTest() {
		mockData = new MockDataRepository();
	}

	@Test
	public void testInvalidToken() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(ERROR_DESCRIPTION));

		ApiResponse<List<CHRepository>> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.UNAUTHORIZED, null, null, errors, request);

		when(repositoryService.getAll(any(HttpServletRequest.class), anyMap())).thenReturn(apiResponse);

		ResultActions resultActions = this.mockMvc.perform(
				get(String.format(TEST_DATAASSETS_URL, env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/invalid-token",
						Preprocessors.preprocessRequest(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								),
						Preprocessors.preprocessResponse(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								)

						));

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<List<CHRepository>>> valueType = new TypeReference<ApiResponse<List<CHRepository>>>(){};
		ApiResponse<List<CHRepository>> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.UNAUTHORIZED.value(), responseApi.getCode());
		assertTrue(responseApi.getErrors() != null);
		assertTrue(responseApi.getMessages() == null);
		assertTrue(responseApi.getResult() == null);
	}

	@Test
	public void testRepositories() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");

		List<CHRepository> chRepositories = mockData.getFakeListOfRepositories();

		ApiResponse<List<CHRepository>> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, chRepositories, null, null, request);

		when(repositoryService.getAll(any(HttpServletRequest.class), anyMap())).thenReturn(apiResponse);

		ResultActions resultActions = this.mockMvc.perform(
				get(String.format(TEST_DATAASSETS_URL, env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/repositories/get-data",
						Preprocessors.preprocessRequest(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								),
						Preprocessors.preprocessResponse(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								)

						));

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<List<CHRepository>>> valueType = new TypeReference<ApiResponse<List<CHRepository>>>(){};
		ApiResponse<List<CHRepository>> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getMessages() == null);
		assertTrue(responseApi.getResult() != null);
		assertTrue(!responseApi.getResult().isEmpty());
	}

	@Test
	public void testRepositoriesNoData() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");

		ApiResponse<List<CHRepository>> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.NO_CONTENT, null, null, null, request);

		when(repositoryService.getAll(any(HttpServletRequest.class), anyMap())).thenReturn(apiResponse);

		ResultActions resultActions = this.mockMvc.perform(
				get(String.format(TEST_DATAASSETS_URL, env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/repositories/get-no-data",
						Preprocessors.preprocessRequest(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								),
						Preprocessors.preprocessResponse(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								)

						));

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<List<CHRepository>>> valueType = new TypeReference<ApiResponse<List<CHRepository>>>(){};
		ApiResponse<List<CHRepository>> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.NO_CONTENT.value(), responseApi.getCode());
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getMessages() == null);
		assertTrue(responseApi.getResult() == null);
	}

	@Test
	public void testRepositoriesError() throws Exception { //NOSONAR
		final String verb = "GET";
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(verb);

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(ERROR_DESCRIPTION));

		ApiResponse<List<CHRepository>> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, errors, request);

		when(repositoryService.getAll(any(HttpServletRequest.class), anyMap())).thenReturn(apiResponse);

		ResultActions resultActions = this.mockMvc.perform(
				get(String.format(TEST_DATAASSETS_URL, env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/repositories/get-error",
						Preprocessors.preprocessRequest(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								),
						Preprocessors.preprocessResponse(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								)

						));

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<List<CHRepository>>> valueType = new TypeReference<ApiResponse<List<CHRepository>>>(){};
		ApiResponse<List<CHRepository>> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseApi.getCode());
		assertTrue(responseApi.getErrors() != null);
		assertTrue(responseApi.getMessages() == null);
		assertTrue(responseApi.getResult() == null);
		assertEquals(verb, responseApi.getVerb());
	}

	@Test
	public void testAddRepository() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");

		CHRepository chRepository = mockData.getFakeCHRepository();

		ApiResponse<CHRepository> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, chRepository, null, null, request);

		when(repositoryService.addRepository(any(HttpServletRequest.class), any(CHRepository.class))).thenReturn(apiResponse);

		String chRepositoryStr = objectMapper.writeValueAsString(chRepository);

		ResultActions resultActions = this.mockMvc.perform(
				post(String.format(TEST_DATAASSETS_URL,env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.content(chRepositoryStr)
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/repositories/add-ok",
						Preprocessors.preprocessRequest(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								),
						Preprocessors.preprocessResponse(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								)

						));

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<CHRepository>> valueType = new TypeReference<ApiResponse<CHRepository>>(){};
		ApiResponse<CHRepository> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getResult() != null);
		assertTrue(responseApi.getResult() instanceof CHRepository);
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getMessages() == null);
	}

	@Test
	public void testAddRepositoryConflict() throws Exception { //NOSONAR
		final String verb = "POST";
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(verb);

		CHRepository chRepository = mockData.getFakeCHRepository();

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(ERROR_DESCRIPTION));

		ApiResponse<CHRepository> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.CONFLICT, chRepository, null, errors, request);

		when(repositoryService.addRepository(any(HttpServletRequest.class), any(CHRepository.class))).thenReturn(apiResponse);

		String chRepositoryStr = objectMapper.writeValueAsString(chRepository);

		ResultActions resultActions = this.mockMvc.perform(
				post(String.format(TEST_DATAASSETS_URL,env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.content(chRepositoryStr)
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/repositories/add-conflict",
						Preprocessors.preprocessRequest(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								),
						Preprocessors.preprocessResponse(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								)

						));

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<CHRepository>> valueType = new TypeReference<ApiResponse<CHRepository>>(){};
		ApiResponse<CHRepository> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.CONFLICT.value(), responseApi.getCode());
		assertTrue(responseApi.getResult() != null);
		assertTrue(responseApi.getResult() instanceof CHRepository);
		assertTrue(responseApi.getErrors() != null);
		assertTrue(responseApi.getMessages() == null);
	}

	@Test
	public void testAddRepositoryError() throws Exception { //NOSONAR
		final String verb = "POST";
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(verb);

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(ERROR_DESCRIPTION));

		CHRepository chRepository = mockData.getFakeCHRepository();

		ApiResponse<CHRepository> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, errors, request);

		when(repositoryService.addRepository(any(HttpServletRequest.class), any(CHRepository.class))).thenReturn(apiResponse);

		String chRepositoryStr = objectMapper.writeValueAsString(chRepository);

		ResultActions resultActions = this.mockMvc.perform(
				post(String.format(TEST_DATAASSETS_URL,env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.content(chRepositoryStr)
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/repositories/add-error",
						Preprocessors.preprocessRequest(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								),
						Preprocessors.preprocessResponse(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								)

						));

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<CHRepository>> valueType = new TypeReference<ApiResponse<CHRepository>>(){};
		ApiResponse<CHRepository> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseApi.getCode());
		assertTrue(responseApi.getResult() == null);
		assertTrue(responseApi.getErrors() != null);
		assertTrue(responseApi.getMessages() == null);
	}

	@Test
	public void testUpdateRepository() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("PUT");

		CHRepository chRepository = mockData.getFakeCHRepository();
		String chRepositoryStr = objectMapper.writeValueAsString(chRepository);

		ApiResponse<CHRepository> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, chRepository, null, null, request);

		when(repositoryService.updateRepository(any(HttpServletRequest.class), any(CHRepository.class))).thenReturn(apiResponse);

		ResultActions resultActions = this.mockMvc.perform(
				put(String.format(TEST_DATAASSETS_URL,env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.content(chRepositoryStr)
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/repositories/update-ok",
						Preprocessors.preprocessRequest(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								),
						Preprocessors.preprocessResponse(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								)

						));

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<CHRepository>> valueType = new TypeReference<ApiResponse<CHRepository>>(){};
		ApiResponse<CHRepository> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getResult() != null);
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getMessages() == null);
		assertTrue(responseApi.getResult() instanceof CHRepository);
	}

	@Test
	public void testUpdateRepositoryError() throws Exception { //NOSONAR
		final String verb = "PUT";
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(verb);

		CHRepository chRepository = mockData.getFakeCHRepository();
		String chRepositoryStr = objectMapper.writeValueAsString(chRepository);

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(ERROR_DESCRIPTION));
		ApiResponse<CHRepository> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, errors, request);

		when(repositoryService.updateRepository(any(HttpServletRequest.class), any(CHRepository.class))).thenReturn(apiResponse);

		ResultActions resultActions = this.mockMvc.perform(
				put(String.format(TEST_DATAASSETS_URL,env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.content(chRepositoryStr)
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/repositories/update-error",
						Preprocessors.preprocessRequest(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								),
						Preprocessors.preprocessResponse(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								)

						));

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<CHRepository>> valueType = new TypeReference<ApiResponse<CHRepository>>(){};
		ApiResponse<CHRepository> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseApi.getCode());
		assertTrue(responseApi.getResult() == null);
		assertTrue(responseApi.getErrors() != null);
	}

	@Test
	public void testDeleteRepository() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(VERB_DELETE);

		CHRepository chRepository = mockData.getFakeCHRepository();

		ApiResponse<String> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, chRepository.getId(), null, null, request);

		when(repositoryService.deleteRepository(any(HttpServletRequest.class), any(String.class))).thenReturn(apiResponse);

		ResultActions resultActions = this.mockMvc.perform(
				delete(String.format(TEST_DATAASSETS_URL+"/%s",env.getProperty(SERVER_SERVLET_CONTEXT_PATH),chRepository.getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/repositories/deleteOne-ok",
						Preprocessors.preprocessRequest(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								),
						Preprocessors.preprocessResponse(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								)

						));

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<String>> valueType = new TypeReference<ApiResponse<String>>(){};
		ApiResponse<String> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getResult() != null);
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getResult() instanceof String);
		assertTrue(responseApi.getMessages() == null);
	}

	@Test
	public void testDeleteRepositoryNotFound() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(VERB_DELETE);

		CHRepository chRepository = mockData.getFakeCHRepository();
		List<ApiMessage> messages = new ArrayList<>();
		messages.add(new ApiMessage(MESSAGE_DESCRIPTION));

		ApiResponse<String> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.NOT_FOUND, chRepository.getId(), messages, null, request);

		when(repositoryService.deleteRepository(any(HttpServletRequest.class), any(String.class))).thenReturn(apiResponse);

		ResultActions resultActions = this.mockMvc.perform(
				delete(String.format(TEST_DATAASSETS_URL+"/%s",env.getProperty(SERVER_SERVLET_CONTEXT_PATH),chRepository.getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/repositories/deleteOne-not-found",
						Preprocessors.preprocessRequest(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								),
						Preprocessors.preprocessResponse(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								)

						));

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<String>> valueType = new TypeReference<ApiResponse<String>>(){};
		ApiResponse<String> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.NOT_FOUND.value(), responseApi.getCode());
		assertTrue(responseApi.getResult() != null);
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getResult() instanceof String);
		assertTrue(responseApi.getMessages() != null);
	}

	@Test
	public void testDeleteRepositoryError() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(VERB_DELETE);

		CHRepository chRepository = mockData.getFakeCHRepository();
		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(ERROR_DESCRIPTION));

		ApiResponse<String> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, errors, request);

		when(repositoryService.deleteRepository(any(HttpServletRequest.class), any(String.class))).thenReturn(apiResponse);

		ResultActions resultActions = this.mockMvc.perform(
				delete(String.format(TEST_DATAASSETS_URL+"/%s",env.getProperty(SERVER_SERVLET_CONTEXT_PATH),chRepository.getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/repositories/deleteOne-error",
						Preprocessors.preprocessRequest(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								),
						Preprocessors.preprocessResponse(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								)

						));

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<String>> valueType = new TypeReference<ApiResponse<String>>(){};
		ApiResponse<String> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseApi.getCode());
		assertTrue(responseApi.getErrors() != null);
		assertTrue(responseApi.getResult() == null);
		assertTrue(responseApi.getMessages() == null);
	}

	@Test
	public void testDeleteRepositories() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(VERB_DELETE);

		List<String> ids = new ArrayList<>();
		ids.add(HASH_ID_3);
		ids.add(HASH_ID_2);
		ids.add(HASH_ID_1);

		List<ApiMessage> messages = new ArrayList<>();
		messages.add(new ApiMessage(MESSAGE_DESCRIPTION));
		List<ApiError> errors = new ArrayList<>();

		ApiResponse<List<String>> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, ids, messages, errors, request);

		when(repositoryService.deleteRepositories(any(HttpServletRequest.class), anyList())).thenReturn(apiResponse);

		String listStr = objectMapper.writeValueAsString(ids);

		ResultActions resultActions = this.mockMvc.perform(
				delete(String.format(TEST_DATAASSETS_URL,env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.content(listStr)
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/repositories/deleteMany-ok",
						Preprocessors.preprocessRequest(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								),
						Preprocessors.preprocessResponse(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								)

						));

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<List<String>>> valueType = new TypeReference<ApiResponse<List<String>>>(){};
		ApiResponse<List<String>> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getResult() != null);
		assertTrue(responseApi.getMessages() != null);
		assertTrue(responseApi.getErrors() == null);
	}

	@Test
	public void testDeleteRepositoriesError() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(VERB_DELETE);

		List<String> ids = new ArrayList<>();
		ids.add(HASH_ID_1);
		ids.add(HASH_ID_2);
		ids.add(HASH_ID_3);

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(ERROR_DESCRIPTION));
		List<ApiMessage> messages = new ArrayList<>();
		messages.add(new ApiMessage(MESSAGE_DESCRIPTION));

		ApiResponse<List<String>> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, messages, errors, request);

		when(repositoryService.deleteRepositories(any(HttpServletRequest.class), anyList())).thenReturn(apiResponse);

		String listStr = objectMapper.writeValueAsString(ids);

		ResultActions resultActions = this.mockMvc.perform(
				delete(String.format(TEST_DATAASSETS_URL,env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.content(listStr)
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/repositories/deleteMany-error",
						Preprocessors.preprocessRequest(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								),
						Preprocessors.preprocessResponse(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								)

						));

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<List<String>>> valueType = new TypeReference<ApiResponse<List<String>>>(){};
		ApiResponse<List<String>> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseApi.getCode());
		assertTrue(responseApi.getResult() == null);
		assertTrue(responseApi.getErrors() != null);
		assertTrue(!responseApi.getErrors().isEmpty());
		assertTrue(responseApi.getMessages() != null);

	}

	@Test
	public void testResetCache() throws Exception { //NOSONAR
		final String verb = "PATCH";
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(verb);

		List<String> ids = new ArrayList<>();
		ids.add(HASH_ID_2);
		ids.add(HASH_ID_1);
		ids.add(HASH_ID_3);

		List<ApiError> errors = new ArrayList<>();
		List<ApiMessage> messages = new ArrayList<>();
		messages.add(new ApiMessage(MESSAGE_DESCRIPTION));

		ApiResponse<List<String>> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, ids, messages, errors, request);

		when(repositoryService.resetCache(any(HttpServletRequest.class), anyList())).thenReturn(apiResponse);

		String listStr = objectMapper.writeValueAsString(ids);

		ResultActions resultActions = this.mockMvc.perform(
				patch(String.format(TEST_DATAASSETS_URL+"/resetcache",env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.content(listStr)
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/repositories/resetcache-ok",
						Preprocessors.preprocessRequest(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								),
						Preprocessors.preprocessResponse(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								)

						));

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<List<String>>> valueType = new TypeReference<ApiResponse<List<String>>>(){};
		ApiResponse<List<String>> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getMessages() != null);
		assertEquals(verb, responseApi.getVerb());
		assertTrue(responseApi.getResult() != null);
	}

	@Test
	public void testResetCacheError() throws Exception { //NOSONAR
		final String verb = "PATCH";
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(verb);

		List<String> ids = new ArrayList<>();
		ids.add(HASH_ID_1);
		ids.add(HASH_ID_3);
		ids.add(HASH_ID_2);

		List<ApiError> errors = new ArrayList<>();
		List<ApiMessage> messages = new ArrayList<>();
		errors.add(new ApiError(ERROR_DESCRIPTION));
		messages.add(new ApiMessage(MESSAGE_DESCRIPTION));

		ApiResponse<List<String>> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, ids, messages, errors, request);

		when(repositoryService.resetCache(any(HttpServletRequest.class), anyList())).thenReturn(apiResponse);

		String listStr = objectMapper.writeValueAsString(ids);

		ResultActions resultActions = this.mockMvc.perform(
				patch(String.format(TEST_DATAASSETS_URL+"/resetcache",env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.content(listStr)
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/repositories/resetcache-error",
						Preprocessors.preprocessRequest(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								),
						Preprocessors.preprocessResponse(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								)

						));

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<List<String>>> valueType = new TypeReference<ApiResponse<List<String>>>(){};
		ApiResponse<List<String>> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseApi.getCode());
		assertTrue(responseApi.getResult() != null);
		assertTrue(responseApi.getMessages() != null);
		assertTrue(responseApi.getErrors() != null);
		assertEquals(verb, responseApi.getVerb());
	}

}
