package gov.dot.its.codehub.adminapi.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.SecureRandom;
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

import gov.dot.its.codehub.adminapi.business.CloudfrontService;
import gov.dot.its.codehub.adminapi.model.ApiError;
import gov.dot.its.codehub.adminapi.model.ApiResponse;
import gov.dot.its.codehub.adminapi.model.CFInvalidation;

@RunWith(SpringRunner.class)
@WebMvcTest(CloudfrontController.class)
@AutoConfigureRestDocs(outputDir="target/generated-snippets", uriHost="example.com", uriPort=3007, uriScheme="http")
public class CloudfrontControllerTest {
	
	private static final String SERVER_SERVLET_CONTEXT_PATH = "server.servlet.context-path";
	private static final String HEADER_HOST = "Host";
	private static final String HEADER_CONTENT_LENGTH = "Content-Length";
	private static final String INVALIDATE_TEMPLATE = "%s/v1/invalidate";
	private static final String ERROR_DESCRIPTION = "Error Description";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private Environment env;

	@MockBean
	private CloudfrontService cloudfrontService;

	public CloudfrontControllerTest() {
		new SecureRandom();
	}

	@Test
	public void testInvalidate() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");

		CFInvalidation cfInvalidation = new CFInvalidation();
		cfInvalidation.setPath("testPath12345");

		ApiResponse<String> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, "fakeCloudfrontId1235", null, null, request);

		when(cloudfrontService.invalidate(any(HttpServletRequest.class), any(CFInvalidation.class))).thenReturn(apiResponse);

		String cfInvalidationStr = objectMapper.writeValueAsString(cfInvalidation);

		ResultActions resultActions = this.mockMvc.perform(
				post(String.format(INVALIDATE_TEMPLATE,env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.content(cfInvalidationStr)
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/invalidate/data",
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
		assertTrue(responseApi.getMessages() == null);
	}
	
	@Test
	public void testInvalidateFailure() throws Exception { //NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		
		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(ERROR_DESCRIPTION));

		CFInvalidation cfInvalidation = new CFInvalidation();
		cfInvalidation.setPath("testPath12345");

		ApiResponse<String> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, errors, request);
		
		when(cloudfrontService.invalidate(any(HttpServletRequest.class), any(CFInvalidation.class))).thenReturn(apiResponse);

		String cfInvalidationStr = objectMapper.writeValueAsString(cfInvalidation);

		ResultActions resultActions = this.mockMvc.perform(
				post(String.format(INVALIDATE_TEMPLATE,env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contentType(MediaType.APPLICATION_JSON)
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.content(cfInvalidationStr)
				)
				.andExpect(status().isOk())
				.andDo(document("api/v1/invalidate/error",
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
		assertTrue(!responseApi.getErrors().isEmpty());
		assertTrue(responseApi.getMessages() == null);
	}

}
