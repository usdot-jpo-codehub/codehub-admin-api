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

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

import gov.dot.its.codehub.adminapi.business.ConfigurationService;
import gov.dot.its.codehub.adminapi.model.ApiResponse;
import gov.dot.its.codehub.adminapi.model.CHCategory;
import gov.dot.its.codehub.adminapi.model.CHConfiguration;

@RunWith(SpringRunner.class)
@WebMvcTest(ConfigurationController.class)
@AutoConfigureRestDocs(outputDir="target/generated-snippets", uriHost="example.com", uriPort=3007, uriScheme="http")
class ConfigurationControllerTest {

	private static final String SERVER_SERVLET_CONTEXT_PATH = "server.servlet.context-path";
	private static final String HEADER_HOST = "Host";
	private static final String HEADER_CONTENT_LENGTH = "Content-Length";
	private static final String URL_CATEGORIES_TEMPLATE = "%s/v1/configurations/categories";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private Environment env;

	@MockBean
	private ConfigurationService configurationService;

	private SecureRandom random;
	public ConfigurationControllerTest() {
		this.random = new SecureRandom();
	}

	@Test
	void testConfigurations() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");

		CHConfiguration configuration = this.getFakeConfiguration();

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
	void testCategories() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");

		CHConfiguration configuration = this.getFakeConfiguration();

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
	void testCategory() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");

		CHCategory category = this.getFakeCategory();

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
	void testAddCategory() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");

		CHCategory category = this.getFakeCategory();

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
	void testUpdateCategory() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("PUT");

		CHCategory category = this.getFakeCategory();

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
	void testDeleteCategory() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("DELETE");

		CHCategory chCategory = this.getFakeCategory();

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
	void testCategoryImages() throws Exception { //NOSONAR
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

	private CHConfiguration getFakeConfiguration() {
		CHConfiguration configuration = new CHConfiguration();
		configuration.setId("default-configuration");
		configuration.setName(configuration.getId());

		List<CHCategory> categories = new ArrayList<>();
		for (int i=0; i<3; i++) {
			CHCategory category = this.getFakeCategory();
			categories.add(category);
		}

		configuration.setCategories(categories);

		return configuration;
	}

	private CHCategory getFakeCategory() {
		int id = this.random.nextInt(100);
		CHCategory category = new CHCategory();
		category.setDescription(String.format("Description-%s", id));
		category.setEnabled(true);
		category.setId(UUID.randomUUID().toString());
		category.setLastModified(new Date());
		category.setName(String.format("Category-%s", id));
		category.setImageFileName("http://path.to.the.image/image1.png");
		category.setOrderPopular(1L);
		category.setPopular(true);

		return category;
	}
}
