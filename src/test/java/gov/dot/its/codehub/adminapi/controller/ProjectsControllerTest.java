package gov.dot.its.codehub.adminapi.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
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

import gov.dot.its.codehub.adminapi.business.ProjectsService;
import gov.dot.its.codehub.adminapi.model.ApiError;
import gov.dot.its.codehub.adminapi.model.ApiMessage;
import gov.dot.its.codehub.adminapi.model.ApiResponse;
import gov.dot.its.codehub.adminapi.model.CHProject;
import gov.dot.its.codehub.adminapi.model.CHProjectBadges;

@RunWith(SpringRunner.class)
@WebMvcTest(ProjectsController.class)
@AutoConfigureRestDocs(outputDir="target/generated-snippets", uriHost="example.com", uriPort=3007, uriScheme="http")
class ProjectsControllerTest {

	private static final String TEST_DATAASSETS_URL = "%s/v1/projects";
	private static final String SERVER_SERVLET_CONTEXT_PATH = "server.servlet.context-path";
	private static final String HEADER_HOST = "Host";
	private static final String HEADER_CONTENT_LENGTH = "Content-Length";
	private static final String HASH_ID_1 = "91128507a8ae9c8046c33ee0f31e37f8";
	private static final String HASH_ID_2 = "7f3bac27fc81d39ffa8ede58b39c8fb6";
	private static final String HASH_ID_3 = "fc4e98b95dc24f763621c54fe50ded24";
	private static final String ERROR_DESCRIPTION = "Error description.";
	private static final String MESSAGE_DESCRIPTION = "Message description";
	private static final String VERB_DELETE = "DELETE";

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private Environment env;

	@Autowired
	private MockMvc mockMvc;


	@MockBean
	private ProjectsService projectsService;

	@Test
	void testProjects() throws Exception { //NOSONAR
		final String verb = "GET";
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(verb);

		List<CHProject> chProjects = this.getFakeListOfProjects();

		ApiResponse<List<CHProject>> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, chProjects, null, null, request);

		when(projectsService.getProjects(any(HttpServletRequest.class), anyMap())).thenReturn(apiResponse);

		ResultActions resultActions = this.mockMvc.perform(
				get(String.format(TEST_DATAASSETS_URL, env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/projects/get-data",
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

		TypeReference<ApiResponse<List<CHProject>>> valueType = new TypeReference<ApiResponse<List<CHProject>>>(){};
		ApiResponse<List<CHProject>> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getMessages() == null);
		assertTrue(responseApi.getResult() != null);
		assertTrue(!responseApi.getResult().isEmpty());
		assertEquals(verb, responseApi.getVerb());
	}

	@Test
	void testProjectsNoData() throws Exception { //NOSONAR
		final String verb = "GET";
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(verb);

		ApiResponse<List<CHProject>> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.NO_CONTENT, null, null, null, request);

		when(projectsService.getProjects(any(HttpServletRequest.class), anyMap())).thenReturn(apiResponse);

		ResultActions resultActions = this.mockMvc.perform(
				get(String.format(TEST_DATAASSETS_URL, env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/projects/get-no-data",
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

		TypeReference<ApiResponse<List<CHProject>>> valueType = new TypeReference<ApiResponse<List<CHProject>>>(){};
		ApiResponse<List<CHProject>> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.NO_CONTENT.value(), responseApi.getCode());
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getResult() == null);
		assertTrue(responseApi.getMessages() == null);
		assertEquals(verb, responseApi.getVerb());
	}

	@Test
	void testProjectsError() throws Exception { //NOSONAR
		final String verb = "GET";
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(verb);

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(ERROR_DESCRIPTION));
		ApiResponse<List<CHProject>> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, errors, request);

		when(projectsService.getProjects(any(HttpServletRequest.class), anyMap())).thenReturn(apiResponse);

		ResultActions resultActions = this.mockMvc.perform(
				get(String.format(TEST_DATAASSETS_URL, env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/projects/get-error",
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

		TypeReference<ApiResponse<List<CHProject>>> valueType = new TypeReference<ApiResponse<List<CHProject>>>(){};
		ApiResponse<List<CHProject>> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseApi.getCode());
		assertEquals(verb, responseApi.getVerb());
		assertTrue(responseApi.getErrors() != null);
		assertTrue(responseApi.getResult() == null);
		assertTrue(responseApi.getMessages() == null);
	}

	@Test
	void testAddProject() throws Exception { //NOSONAR
		final String verb = "POST";
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(verb);

		CHProject chProject = this.getFakeCHProject();

		List<ApiMessage> messages = new ArrayList<>();
		messages.add(new ApiMessage(MESSAGE_DESCRIPTION));

		ApiResponse<CHProject> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, chProject, messages, null, request);

		when(projectsService.addProject(any(HttpServletRequest.class), any(CHProject.class))).thenReturn(apiResponse);

		String chProjectStr = objectMapper.writeValueAsString(chProject);

		ResultActions resultActions = this.mockMvc.perform(
				post(String.format(TEST_DATAASSETS_URL,env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.content(chProjectStr)
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/projects/add-ok",
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

		TypeReference<ApiResponse<CHProject>> valueType = new TypeReference<ApiResponse<CHProject>>(){};
		ApiResponse<CHProject> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getMessages() != null);
		assertTrue(responseApi.getResult() != null);
		assertEquals(verb, responseApi.getVerb());
	}

	@Test
	void testAddProjectConflict() throws Exception { //NOSONAR
		final String verb = "POST";
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(verb);

		CHProject chProject = this.getFakeCHProject();

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(ERROR_DESCRIPTION));

		ApiResponse<CHProject> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.CONFLICT, chProject, null, errors, request);

		when(projectsService.addProject(any(HttpServletRequest.class), any(CHProject.class))).thenReturn(apiResponse);

		String chProjectStr = objectMapper.writeValueAsString(chProject);

		ResultActions resultActions = this.mockMvc.perform(
				post(String.format(TEST_DATAASSETS_URL,env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.content(chProjectStr)
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/projects/add-conflict",
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

		TypeReference<ApiResponse<CHProject>> valueType = new TypeReference<ApiResponse<CHProject>>(){};
		ApiResponse<CHProject> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.CONFLICT.value(), responseApi.getCode());
		assertEquals(verb, responseApi.getVerb());
		assertTrue(responseApi.getErrors() != null);
		assertTrue(responseApi.getMessages() == null);
		assertTrue(responseApi.getResult() != null);
	}


	@Test
	void testAddProjectError() throws Exception { //NOSONAR
		final String verb = "POST";
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(verb);

		CHProject chProject = this.getFakeCHProject();

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(ERROR_DESCRIPTION));

		ApiResponse<CHProject> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, errors, request);

		when(projectsService.addProject(any(HttpServletRequest.class), any(CHProject.class))).thenReturn(apiResponse);

		String chProjectStr = objectMapper.writeValueAsString(chProject);

		ResultActions resultActions = this.mockMvc.perform(
				post(String.format(TEST_DATAASSETS_URL,env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.content(chProjectStr)
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/projects/add-error",
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

		TypeReference<ApiResponse<CHProject>> valueType = new TypeReference<ApiResponse<CHProject>>(){};
		ApiResponse<CHProject> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseApi.getCode());
		assertEquals(verb, responseApi.getVerb());
		assertTrue(responseApi.getErrors() != null);
		assertTrue(responseApi.getResult() == null);
		assertTrue(responseApi.getMessages() == null);
	}

	@Test
	void testUpdateProject() throws Exception { //NOSONAR
		final String verb = "PUT";
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(verb);

		CHProject chProject = this.getFakeCHProject();
		String chProjectStr = objectMapper.writeValueAsString(chProject);

		List<ApiMessage> messages = new ArrayList<>();
		messages.add(new ApiMessage(MESSAGE_DESCRIPTION));
		ApiResponse<CHProject> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, chProject, messages, null, request);

		when(projectsService.updateProject(any(HttpServletRequest.class), any(CHProject.class))).thenReturn(apiResponse);

		ResultActions resultActions = this.mockMvc.perform(
				put(String.format(TEST_DATAASSETS_URL,env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.content(chProjectStr)
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/projects/update-ok",
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

		TypeReference<ApiResponse<CHProject>> valueType = new TypeReference<ApiResponse<CHProject>>(){};
		ApiResponse<CHProject> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertEquals(verb, responseApi.getVerb());
		assertTrue(responseApi.getResult() != null);
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getMessages() != null);
		assertTrue(responseApi.getResult() instanceof CHProject);
	}

	@Test
	void testUpdateProjectError() throws Exception { //NOSONAR
		final String verb = "PUT";
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(verb);

		CHProject chProject = this.getFakeCHProject();
		String chProjectStr = objectMapper.writeValueAsString(chProject);

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(ERROR_DESCRIPTION));
		ApiResponse<CHProject> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, errors, request);

		when(projectsService.updateProject(any(HttpServletRequest.class), any(CHProject.class))).thenReturn(apiResponse);

		ResultActions resultActions = this.mockMvc.perform(
				put(String.format(TEST_DATAASSETS_URL,env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.content(chProjectStr)
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/projects/update-error",
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

		TypeReference<ApiResponse<CHProject>> valueType = new TypeReference<ApiResponse<CHProject>>(){};
		ApiResponse<CHProject> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseApi.getCode());
		assertTrue(responseApi.getErrors() != null);
		assertEquals(verb, responseApi.getVerb());
		assertTrue(responseApi.getResult() == null);
		assertTrue(responseApi.getMessages() == null);
	}

	@Test
	void testDeleteProject() throws Exception { //NOSONAR
		final String verb = VERB_DELETE; 
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(verb);

		CHProject chProject = this.getFakeCHProject();

		List<ApiMessage> messages = new ArrayList<>();
		messages.add(new ApiMessage(MESSAGE_DESCRIPTION));
		ApiResponse<String> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, chProject.getId(), messages, null, request);

		when(projectsService.deleteProject(any(HttpServletRequest.class), any(String.class))).thenReturn(apiResponse);

		ResultActions resultActions = this.mockMvc.perform(
				delete(String.format(TEST_DATAASSETS_URL+"/%s",env.getProperty(SERVER_SERVLET_CONTEXT_PATH),chProject.getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/projects/deleteOne-ok",
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
		assertEquals(verb, responseApi.getVerb());
		assertTrue(responseApi.getResult() instanceof String);
		assertTrue(responseApi.getResult() != null);
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getMessages() != null);
	}

	@Test
	void testDeleteProjectNotFound() throws Exception { //NOSONAR
		final String verb = VERB_DELETE; 
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(verb);

		CHProject chProject = this.getFakeCHProject();

		List<ApiMessage> messages = new ArrayList<>();
		messages.add(new ApiMessage(MESSAGE_DESCRIPTION));
		ApiResponse<String> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.NOT_FOUND, chProject.getId(), messages, null, request);

		when(projectsService.deleteProject(any(HttpServletRequest.class), any(String.class))).thenReturn(apiResponse);

		ResultActions resultActions = this.mockMvc.perform(
				delete(String.format(TEST_DATAASSETS_URL+"/%s",env.getProperty(SERVER_SERVLET_CONTEXT_PATH),chProject.getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/projects/deleteOne-not-found",
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
		assertEquals(verb, responseApi.getVerb());
		assertTrue(responseApi.getResult() instanceof String);
		assertTrue(responseApi.getResult() != null);
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getMessages() != null);
	}

	@Test
	void testDeleteProjectError() throws Exception { //NOSONAR
		final String verb = VERB_DELETE; 
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(verb);

		CHProject chProject = this.getFakeCHProject();

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(ERROR_DESCRIPTION));
		ApiResponse<String> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, errors, request);

		when(projectsService.deleteProject(any(HttpServletRequest.class), any(String.class))).thenReturn(apiResponse);

		ResultActions resultActions = this.mockMvc.perform(
				delete(String.format(TEST_DATAASSETS_URL+"/%s",env.getProperty(SERVER_SERVLET_CONTEXT_PATH),chProject.getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/projects/deleteOne-error",
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
		assertTrue(responseApi.getResult() == null);
		assertEquals(verb, responseApi.getVerb());
		assertTrue(responseApi.getErrors() != null);
		assertTrue(responseApi.getMessages() == null);
	}

	@Test
	void testDeleteProjects() throws Exception { //NOSONAR
		final String verb = VERB_DELETE;
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(verb);

		List<String> ids = new ArrayList<>();
		ids.add(HASH_ID_1);
		ids.add(HASH_ID_2);
		ids.add(HASH_ID_3);

		List<ApiMessage> messages = new ArrayList<>();
		messages.add(new ApiMessage(MESSAGE_DESCRIPTION));
		ApiResponse<List<String>> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, ids, messages, null, request);

		when(projectsService.deleteProjects(any(HttpServletRequest.class), anyList())).thenReturn(apiResponse);

		String listStr = objectMapper.writeValueAsString(ids);

		ResultActions resultActions = this.mockMvc.perform(
				delete(String.format(TEST_DATAASSETS_URL,env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.content(listStr)
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/projects/deleteMany-ok",
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
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getMessages() != null);
		assertEquals(verb, responseApi.getVerb());
	}

	@Test
	void testDeleteProjectsError() throws Exception { //NOSONAR
		final String verb = VERB_DELETE;
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(verb);

		List<String> ids = new ArrayList<>();
		ids.add(HASH_ID_2);
		ids.add(HASH_ID_1);
		ids.add(HASH_ID_3);

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(ERROR_DESCRIPTION));
		ApiResponse<List<String>> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, errors, request);

		when(projectsService.deleteProjects(any(HttpServletRequest.class), anyList())).thenReturn(apiResponse);

		String listStr = objectMapper.writeValueAsString(ids);

		ResultActions resultActions = this.mockMvc.perform(
				delete(String.format(TEST_DATAASSETS_URL,env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.content(listStr)
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/projects/deleteMany-error",
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
		assertEquals(verb, responseApi.getVerb());
		assertTrue(responseApi.getResult() == null);
		assertTrue(responseApi.getErrors() != null);
		assertTrue(responseApi.getMessages() == null);
	}

	private List<CHProject> getFakeListOfProjects() {
		List<CHProject> projects = new ArrayList<>();
		for(int i=1 ; i<=3; i++) {
			CHProject chProject = getFakeCHProject(
					String.format("[MD5-HASH-%d]",i),
					String.format("http://www.example.com/owner%d/repository%d", i, i)
					);
			projects.add(chProject);
		}

		return projects;
	}

	private CHProject getFakeCHProject(String id, String url) {
		CHProject chProject = this.getFakeCHProject();
		chProject.setId(id);
		chProject.setRepository_url(url);

		return chProject;
	}

	private CHProject getFakeCHProject() {
		CHProjectBadges badges = new CHProjectBadges();
		badges.setStatus("ready-only");

		CHProject chProject = new CHProject();
		chProject.setId(HASH_ID_1);
		chProject.setRepository_url("http://www.example.com/owner/repository");

		chProject.setBadges(badges);

		return chProject;
	}
}
