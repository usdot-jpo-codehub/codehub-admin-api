package gov.dot.its.codehub.adminapi.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

import gov.dot.its.codehub.adminapi.MockDataConfiguration;
import gov.dot.its.codehub.adminapi.business.ConfigurationService;
import gov.dot.its.codehub.adminapi.model.ApiResponse;
import gov.dot.its.codehub.adminapi.model.CHCategory;
import gov.dot.its.codehub.adminapi.model.CHConfiguration;
import gov.dot.its.codehub.adminapi.model.CHEngagementPopup;

@RunWith(SpringRunner.class)
@WebMvcTest(ConfigurationController.class)
@AutoConfigureRestDocs(outputDir="target/generated-snippets", uriHost="example.com", uriPort=3007, uriScheme="http")
public class ConfigurationControllerTest {

	private static final String SERVER_SERVLET_CONTEXT_PATH = "server.servlet.context-path";
	private static final String HEADER_HOST = "Host";
	private static final String HEADER_CONTENT_LENGTH = "Content-Length";
	private static final String URL_CATEGORIES_TEMPLATE = "%s/v1/configurations/categories";
	private static final String URL_ENGAGEMENTPOPUP_TEMPLATE = "%s/v1/configurations/engagementpopups";
	private static final String TEST_ID = "1";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private Environment env;

	@MockBean
	private ConfigurationService configurationService;

	private MockDataConfiguration mockData;

	public ConfigurationControllerTest() {
		this.mockData = new MockDataConfiguration();
	}

	@Test
	public void testConfigurations() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");

		CHConfiguration configuration = this.mockData.getFakeConfiguration();

		ApiResponse<CHConfiguration> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, configuration, null, null, request);

		when(configurationService.configurations(any(HttpServletRequest.class))).thenReturn(apiResponse);

		ResultActions resultActions = this.prepareResultActions("%s/v1/configurations","api/v1/configurations/data");

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<CHConfiguration>> valueType = new TypeReference<ApiResponse<CHConfiguration>>(){};
		ApiResponse<CHConfiguration> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getResult() != null);
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getMessages() == null);
	}

	@Test
	public void testCategories() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");

		CHConfiguration configuration = this.mockData.getFakeConfiguration();

		ApiResponse<List<CHCategory>> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, configuration.getCategories(), null, null, request);

		when(configurationService.categories(any(HttpServletRequest.class))).thenReturn(apiResponse);

		ResultActions resultActions = this.prepareResultActions(URL_CATEGORIES_TEMPLATE,"api/v1/configurations/categories/data");

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<List<CHCategory>>> valueType = new TypeReference<ApiResponse<List<CHCategory>>>(){};
		ApiResponse<List<CHCategory>> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getResult() != null);
		assertTrue(responseApi.getMessages() == null);
	}

	@Test
	public void testCategory() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");

		CHCategory category = this.mockData.getFakeCategory(TEST_ID);

		ApiResponse<CHCategory> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, category, null, null, request);

		when(configurationService.category(any(HttpServletRequest.class),any(String.class))).thenReturn(apiResponse);

		ResultActions resultActions = this.prepareResultActions(URL_CATEGORIES_TEMPLATE+"/123","api/v1/configurations/categories/data-id");

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<CHCategory>> valueType = new TypeReference<ApiResponse<CHCategory>>(){};
		ApiResponse<CHCategory> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getMessages() == null);
		assertTrue(responseApi.getResult() != null);
	}

	@Test
	public void testAddCategory() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");

		CHCategory category = this.mockData.getFakeCategory(TEST_ID);

		ApiResponse<CHCategory> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, category, null, null, request);

		when(configurationService.addCategory(any(HttpServletRequest.class),any(CHCategory.class))).thenReturn(apiResponse);

		String categoryStr = objectMapper.writeValueAsString(category);

		ResultActions resultActions = this.mockMvc.perform(
				post(String.format(URL_CATEGORIES_TEMPLATE,env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.content(categoryStr)
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/configurations/categories/add-ok",
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

		TypeReference<ApiResponse<CHCategory>> valueType = new TypeReference<ApiResponse<CHCategory>>(){};
		ApiResponse<CHCategory> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getMessages() == null);
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getResult() != null);
	}

	@Test
	public void testUpdateCategory() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("PUT");

		CHCategory category = this.mockData.getFakeCategory(TEST_ID);

		ApiResponse<CHCategory> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, category, null, null, request);

		when(configurationService.updateCategory(any(HttpServletRequest.class),any(CHCategory.class))).thenReturn(apiResponse);

		String categoryStr = objectMapper.writeValueAsString(category);

		ResultActions resultActions = this.mockMvc.perform(
				put(String.format(URL_CATEGORIES_TEMPLATE,env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.content(categoryStr)
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/configurations/categories/update-ok",
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

		TypeReference<ApiResponse<CHCategory>> valueType = new TypeReference<ApiResponse<CHCategory>>(){};
		ApiResponse<CHCategory> responseApi = objectMapper.readValue(objString, valueType);

		assertTrue(responseApi.getResult() != null);
		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getMessages() == null);
		assertTrue(responseApi.getErrors() == null);
	}

	@Test
	public void testDeleteCategory() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("DELETE");

		CHCategory chCategory = this.mockData.getFakeCategory(TEST_ID);

		ApiResponse<CHCategory> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, chCategory, null, null, request);

		when(configurationService.deleteCategory(any(HttpServletRequest.class), any(String.class))).thenReturn(apiResponse);

		ResultActions resultActions = this.mockMvc.perform(
				delete(String.format(URL_CATEGORIES_TEMPLATE+"/%s",env.getProperty(SERVER_SERVLET_CONTEXT_PATH),chCategory.getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/configurations/categories/delete-ok",
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

		TypeReference<ApiResponse<CHCategory>> valueType = new TypeReference<ApiResponse<CHCategory>>(){};
		ApiResponse<CHCategory> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getResult() != null);
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getResult() instanceof CHCategory);
		assertTrue(responseApi.getMessages() == null);
	}

	@Test
	public void testCategoryImages() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");

		List<String> images = new ArrayList<>();
		images.add("Image-File-1");
		images.add("Image-File-2");

		ApiResponse<List<String>> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, images, null, null, request);

		when(configurationService.categoryImages(any(HttpServletRequest.class))).thenReturn(apiResponse);

		ResultActions resultActions = this.prepareResultActions("%s/v1/images/categories","api/v1/images/categories/data");

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<List<String>>> valueType = new TypeReference<ApiResponse<List<String>>>(){};
		ApiResponse<List<String>> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getMessages() == null);
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getResult() != null);
	}

	@Test
	public void testEngagementPopups() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");

		CHConfiguration configuration = this.mockData.getFakeConfiguration();

		ApiResponse<List<CHEngagementPopup>> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, configuration.getEngagementPopups(), null, null, request);

		when(configurationService.engagementpopups(any(HttpServletRequest.class))).thenReturn(apiResponse);

		ResultActions resultActions = this.prepareResultActions(URL_ENGAGEMENTPOPUP_TEMPLATE,"api/v1/configurations/engagementpopups/data");

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<List<CHEngagementPopup>>> valueType = new TypeReference<ApiResponse<List<CHEngagementPopup>>>(){};
		ApiResponse<List<CHEngagementPopup>> responseApi = objectMapper.readValue(objString, valueType);

		assertTrue(responseApi.getResult() != null);
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getMessages() == null);
		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
	}

	@Test
	public void testAddEngagementPopup() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");

		CHEngagementPopup engagementPopup = this.mockData.getFakeEngagementPopup(TEST_ID);

		ApiResponse<CHEngagementPopup> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, engagementPopup, null, null, request);

		when(configurationService.addEngagementPopup(any(HttpServletRequest.class),any(CHEngagementPopup.class))).thenReturn(apiResponse);

		String engagementPopupStr = objectMapper.writeValueAsString(engagementPopup);

		ResultActions resultActions = this.mockMvc.perform(
				post(String.format(URL_ENGAGEMENTPOPUP_TEMPLATE,env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.content(engagementPopupStr)
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/configurations/engagementpopups/add-ok",
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

		TypeReference<ApiResponse<CHEngagementPopup>> valueType = new TypeReference<ApiResponse<CHEngagementPopup>>(){};
		ApiResponse<CHEngagementPopup> responseApi = objectMapper.readValue(objString, valueType);

		assertTrue(responseApi.getMessages() == null);
		assertTrue(responseApi.getErrors() == null);
		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getResult() != null);
	}

	@Test
	public void testUpdateEngagementPopup() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("PUT");

		CHEngagementPopup engagementPopup = this.mockData.getFakeEngagementPopup(TEST_ID);

		ApiResponse<CHEngagementPopup> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, engagementPopup, null, null, request);

		when(configurationService.updateEngagementPopup(any(HttpServletRequest.class),any(CHEngagementPopup.class))).thenReturn(apiResponse);

		String engagementPopupStr = objectMapper.writeValueAsString(engagementPopup);

		ResultActions resultActions = this.mockMvc.perform(
				put(String.format(URL_ENGAGEMENTPOPUP_TEMPLATE,env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.content(engagementPopupStr)
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/configurations/engagementpopups/update-ok",
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

		TypeReference<ApiResponse<CHEngagementPopup>> valueType = new TypeReference<ApiResponse<CHEngagementPopup>>(){};
		ApiResponse<CHEngagementPopup> responseApi = objectMapper.readValue(objString, valueType);

		assertTrue(responseApi.getMessages() == null);
		assertTrue(responseApi.getResult() != null);
		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getErrors() == null);
	}

	@Test
	public void testDeleteEngagementPopup() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("DELETE");

		CHEngagementPopup engagementPopup = this.mockData.getFakeEngagementPopup(TEST_ID);

		ApiResponse<CHEngagementPopup> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, engagementPopup, null, null, request);

		when(configurationService.deleteEngagementPopup(any(HttpServletRequest.class), any(String.class))).thenReturn(apiResponse);

		ResultActions resultActions = this.mockMvc.perform(
				delete(String.format(URL_ENGAGEMENTPOPUP_TEMPLATE+"/%s",env.getProperty(SERVER_SERVLET_CONTEXT_PATH),engagementPopup.getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/configurations/engagementpopups/delete-ok",
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

		TypeReference<ApiResponse<CHEngagementPopup>> valueType = new TypeReference<ApiResponse<CHEngagementPopup>>(){};
		ApiResponse<CHEngagementPopup> responseApi = objectMapper.readValue(objString, valueType);

		assertTrue(responseApi.getResult() != null);
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getResult() instanceof CHEngagementPopup);
		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getMessages() == null);
	}


	private ResultActions prepareResultActions(String testUrlTemplate, String documentPath) throws Exception { //NOSONAR
		return this.mockMvc.perform(
				get(String.format(testUrlTemplate, env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				)
				.andExpect(status().isOk())
				.andDo(document(documentPath,
						Preprocessors.preprocessRequest(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								),
						Preprocessors.preprocessResponse(
								Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)
								)
						));
	}
}
