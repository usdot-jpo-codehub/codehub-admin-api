package gov.dot.its.codehub.adminapi.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.ElasticsearchStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import gov.dot.its.codehub.adminapi.dao.ConfigurationDao;
import gov.dot.its.codehub.adminapi.dao.RepositoryDao;
import gov.dot.its.codehub.adminapi.model.ApiError;
import gov.dot.its.codehub.adminapi.model.ApiMessage;
import gov.dot.its.codehub.adminapi.model.ApiResponse;
import gov.dot.its.codehub.adminapi.model.CHCategory;
import gov.dot.its.codehub.adminapi.model.CHConfiguration;
import gov.dot.its.codehub.adminapi.utils.ApiUtils;
import gov.dot.its.codehub.adminapi.utils.HeaderUtils;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

	private static final String CHTOKEN_KEY = "CHTOKEN";
	private static final String MESSAGE_INVALID_TOKEN = "Invalid token";
	private static final String MESSAGE_TEMPLATE = "%s : %s ";

	@Autowired
	private ConfigurationDao configurationDao;

	@Autowired
	private RepositoryDao repositoryDao;

	@Autowired
	private ApiUtils apiUtils;

	@Autowired
	private HeaderUtils headerUtils;

	@Value("${codehub.admin.api.debug}")
	private boolean debug;

	@Override
	public ApiResponse<CHConfiguration> configurations(HttpServletRequest request) {
		logger.info("Request: configurations");
		final String RESPONSE_MSG = "Response: GET Configurations. ";

		ApiResponse<CHConfiguration> apiResponse = new ApiResponse<>();
		List<ApiError> errors = new ArrayList<>();

		if (!headerUtils.validateToken(request.getHeader(CHTOKEN_KEY))) {
			logger.warn(String.format(MESSAGE_TEMPLATE, RESPONSE_MSG, MESSAGE_INVALID_TOKEN));
			errors.add(new ApiError(MESSAGE_INVALID_TOKEN));
			apiResponse.setResponse(HttpStatus.UNAUTHORIZED, null, null, errors, request);
			return apiResponse;
		}

		try {

			CHConfiguration configuration = configurationDao.getConfiguration();

			if (configuration != null) {
				apiResponse.setResponse(HttpStatus.OK, configuration, null, null, request);
				logger.info(String.format(MESSAGE_TEMPLATE, RESPONSE_MSG,HttpStatus.OK.toString()));
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.NO_CONTENT, null, null, null, request);
			logger.info(String.format(MESSAGE_TEMPLATE, RESPONSE_MSG, HttpStatus.NO_CONTENT.toString()));
			return apiResponse;


		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<List<CHCategory>> categories(HttpServletRequest request) {
		logger.info("Request: categories");
		final String RESPONSE_MSG = "Response: GET Categories. ";

		ApiResponse<List<CHCategory>> apiResponse = new ApiResponse<>();
		List<ApiError> errors = new ArrayList<>();

		if (!headerUtils.validateToken(request.getHeader(CHTOKEN_KEY))) {
			logger.warn(String.format(MESSAGE_TEMPLATE, RESPONSE_MSG, MESSAGE_INVALID_TOKEN));
			errors.add(new ApiError(MESSAGE_INVALID_TOKEN));
			apiResponse.setResponse(HttpStatus.UNAUTHORIZED, null, null, errors, request);
			return apiResponse;
		}

		try {

			List<CHCategory> categories = configurationDao.getCategories();

			if (categories != null && !categories.isEmpty()) {
				apiResponse.setResponse(HttpStatus.OK, categories, null, null, request);
				logger.info(String.format(MESSAGE_TEMPLATE, RESPONSE_MSG,HttpStatus.OK.toString()+" "+categories.size()));
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.NO_CONTENT, null, null, null, request);
			logger.info(String.format(MESSAGE_TEMPLATE, RESPONSE_MSG, HttpStatus.NO_CONTENT.toString()));
			return apiResponse;


		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<CHCategory> addCategory(HttpServletRequest request, CHCategory chCategory) {
		logger.info("Request: Add Category");
		final String RESPONSE_MSG = "Response: POST Categories. ";

		ApiResponse<CHCategory> apiResponse = new ApiResponse<>();
		List<ApiError> errors = new ArrayList<>();
		List<ApiMessage> messages = new ArrayList<>();

		if (!headerUtils.validateToken(request.getHeader(CHTOKEN_KEY))) {
			logger.warn(String.format(MESSAGE_TEMPLATE, RESPONSE_MSG, MESSAGE_INVALID_TOKEN));
			errors.add(new ApiError(MESSAGE_INVALID_TOKEN));
			apiResponse.setResponse(HttpStatus.UNAUTHORIZED, null, null, errors, request);
			return apiResponse;
		}

		try {
			chCategory.setId(apiUtils.getUUID());
			chCategory.setLastModified(apiUtils.getCurrentUtc());
			String result = configurationDao.addCategory(chCategory);

			if (result != null) {
				messages.add(new ApiMessage(result));
				apiResponse.setResponse(HttpStatus.OK, chCategory, messages, null, request);
				logger.info(String.format(MESSAGE_TEMPLATE, RESPONSE_MSG,HttpStatus.OK.toString()+" "+result));
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, null, request);
			logger.info(String.format(MESSAGE_TEMPLATE, RESPONSE_MSG, HttpStatus.INTERNAL_SERVER_ERROR.toString()));
			return apiResponse;


		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<CHCategory> updateCategory(HttpServletRequest request, CHCategory chCategory) {
		logger.info("Request: Update Category");
		final String RESPONSE_MSG = "Response: PUT Categories. ";

		List<ApiError> errors = new ArrayList<>();
		List<ApiMessage> messages = new ArrayList<>();
		ApiResponse<CHCategory> apiResponse = new ApiResponse<>();

		if (!headerUtils.validateToken(request.getHeader(CHTOKEN_KEY))) {
			errors.add(new ApiError(MESSAGE_INVALID_TOKEN));
			apiResponse.setResponse(HttpStatus.UNAUTHORIZED, null, null, errors, request);
			logger.warn(String.format(MESSAGE_TEMPLATE, RESPONSE_MSG, MESSAGE_INVALID_TOKEN));
			return apiResponse;
		}

		try {
			chCategory.setLastModified(apiUtils.getCurrentUtc());
			String result = configurationDao.updateCategory(chCategory);

			if (result != null) {
				messages.add(new ApiMessage(result));
				logger.info(String.format(MESSAGE_TEMPLATE, RESPONSE_MSG,HttpStatus.OK.toString()+" "+result));
				apiResponse.setResponse(HttpStatus.OK, chCategory, messages, null, request);
				return apiResponse;
			}

			logger.info(String.format(MESSAGE_TEMPLATE, RESPONSE_MSG, HttpStatus.INTERNAL_SERVER_ERROR.toString()));
			apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, null, request);
			return apiResponse;


		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<CHCategory> category(HttpServletRequest request, String id) {
		logger.info("Request: Get by ID Category");
		final String RESPONSE_MSG = "Response: GET Category. ";

		List<ApiError> errors = new ArrayList<>();
		ApiResponse<CHCategory> apiResponse = new ApiResponse<>();
		List<ApiMessage> messages = new ArrayList<>();

		if (!headerUtils.validateToken(request.getHeader(CHTOKEN_KEY))) {
			errors.add(new ApiError(MESSAGE_INVALID_TOKEN));
			logger.warn(String.format(MESSAGE_TEMPLATE, RESPONSE_MSG, MESSAGE_INVALID_TOKEN));
			apiResponse.setResponse(HttpStatus.UNAUTHORIZED, null, null, errors, request);
			return apiResponse;
		}

		try {

			CHCategory category = configurationDao.getCategoryById(id);

			if (category != null) {
				apiResponse.setResponse(HttpStatus.OK, category, messages, null, request);
				logger.info(String.format(MESSAGE_TEMPLATE, RESPONSE_MSG,HttpStatus.OK.toString()));
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.NOT_FOUND, null, null, null, request);
			logger.info(String.format(MESSAGE_TEMPLATE, RESPONSE_MSG, HttpStatus.NOT_FOUND.toString()));
			return apiResponse;


		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<CHCategory> deleteCategory(HttpServletRequest request, String id) {
		logger.info("Request: DELETE Category");
		final String RESPONSE_MSG = "Response: DELETE Category. ";

		ApiResponse<CHCategory> apiResponse = new ApiResponse<>();
		List<ApiMessage> messages = new ArrayList<>();
		List<ApiError> errors = new ArrayList<>();

		if (!headerUtils.validateToken(request.getHeader(CHTOKEN_KEY))) {
			errors.add(new ApiError(MESSAGE_INVALID_TOKEN));
			logger.warn(String.format(MESSAGE_TEMPLATE, RESPONSE_MSG, MESSAGE_INVALID_TOKEN));
			apiResponse.setResponse(HttpStatus.UNAUTHORIZED, null, null, errors, request);
			return apiResponse;
		}

		try {

			boolean result = configurationDao.deleteCategoryById(id);

			if (result) {

				String msg = repositoryDao.removeCategory(id);
				if (!StringUtils.isEmpty(msg)) {
					messages.add(new ApiMessage(msg));
				}

				messages.add(new ApiMessage(id));
				apiResponse.setResponse(HttpStatus.OK, null, messages, null, request);
				logger.info(String.format(MESSAGE_TEMPLATE, RESPONSE_MSG,HttpStatus.OK.toString()));
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.NOT_FOUND, null, null, null, request);
			logger.info(String.format(MESSAGE_TEMPLATE, RESPONSE_MSG, HttpStatus.NOT_FOUND.toString()));
			return apiResponse;


		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

}
