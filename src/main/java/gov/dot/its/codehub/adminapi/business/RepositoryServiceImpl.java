package gov.dot.its.codehub.adminapi.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.ElasticsearchStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import gov.dot.its.codehub.adminapi.dao.RepositoryDao;
import gov.dot.its.codehub.adminapi.model.ApiError;
import gov.dot.its.codehub.adminapi.model.ApiMessage;
import gov.dot.its.codehub.adminapi.model.ApiResponse;
import gov.dot.its.codehub.adminapi.model.CHRepository;
import gov.dot.its.codehub.adminapi.utils.ApiUtils;
import gov.dot.its.codehub.adminapi.utils.HeaderUtils;

@Service
public class RepositoryServiceImpl implements RepositoryService {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryServiceImpl.class);

	private static final String CHTOKEN_KEY = "CHTOKEN";
	private static final String MESSAGE_INVALID_TOKEN = "Invalid token";
	private static final String MESSAGE_TEMPLATE = "%s : %s %s";
	private static final String NOT_FOUND = "NOT_FOUND";

	@Autowired
	private RepositoryDao repositoryDao;

	@Autowired
	private ApiUtils apiUtils;

	@Autowired
	private HeaderUtils headerUtils;

	@Value("${codehub.admin.api.debug}")
	private boolean debug;

	@Override
	public ApiResponse<List<CHRepository>> getAll(HttpServletRequest request, Map<String, String> params) {
		logger.info("Request: Get Repos.");
		final String RESPONSE_GET_ALL = "Response: Get Repos. ";

		ApiResponse<List<CHRepository>> apiResponse = new ApiResponse<>();
		List<ApiError> errors = new ArrayList<>();

		String msg = apiUtils.stringFormat(MESSAGE_TEMPLATE, RESPONSE_GET_ALL, MESSAGE_INVALID_TOKEN);
		if (!headerUtils.validateToken(request.getHeader(CHTOKEN_KEY))) {
			logger.warn(msg);
			errors.add(new ApiError(MESSAGE_INVALID_TOKEN));
			apiResponse.setResponse(HttpStatus.UNAUTHORIZED, null, null, errors, request);
			return apiResponse;
		}

		int limit = apiUtils.getQueryParamInteger(params, "limit", 1000);

		try {

			List<CHRepository> data = repositoryDao.getAll(limit);

			if (data != null && !data.isEmpty()) {
				apiResponse.setResponse(HttpStatus.OK, data, null, null, request);
				msg = apiUtils.stringFormat(MESSAGE_TEMPLATE, RESPONSE_GET_ALL+HttpStatus.OK.toString(), String.valueOf(data.size()));
				logger.info(msg);
				return apiResponse;
			} else {
				apiResponse.setResponse(HttpStatus.NO_CONTENT, null, null, null, request);
				msg = apiUtils.stringFormat(MESSAGE_TEMPLATE, RESPONSE_GET_ALL + HttpStatus.NO_CONTENT.toString());
				logger.info(msg);
				return apiResponse;
			}
		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}

	}

	@Override
	public ApiResponse<CHRepository> addRepository(HttpServletRequest request, CHRepository chRepository) {
		chRepository.setId(apiUtils.getMd5(chRepository.getSourceData().getRepositoryUrl()));

		String msg = apiUtils.stringFormat(MESSAGE_TEMPLATE,"Request: Add Repository.", chRepository.getId());
		logger.info(msg);
		final String RESPONSE_MESSAGE = "Response: Add Repository. ";

		ApiResponse<CHRepository> apiResponse = new ApiResponse<>();
		List<ApiError> errors = new ArrayList<>();
		List<ApiMessage> messages = new ArrayList<>();

		if (!headerUtils.validateToken(request.getHeader(CHTOKEN_KEY))) {
			msg = apiUtils.stringFormat(MESSAGE_TEMPLATE, RESPONSE_MESSAGE, MESSAGE_INVALID_TOKEN);
			logger.warn(msg);
			errors.add(new ApiError(MESSAGE_INVALID_TOKEN));
			apiResponse.setResponse(HttpStatus.UNAUTHORIZED, null, null, errors, request);
			return apiResponse;
		}

		try {
			
			if (repositoryDao.docExists(chRepository.getId())) {
				errors.add(new ApiError(String.format("Repository already exists: %s", chRepository.getId())));
				apiResponse.setResponse(HttpStatus.CONFLICT, chRepository, null, errors, request);
				return apiResponse;
			}

			String result = repositoryDao.addRepository(chRepository);
			messages.add(new ApiMessage(result));

			apiResponse.setResponse(HttpStatus.OK, chRepository, messages, null, request);
			msg = apiUtils.stringFormat(MESSAGE_TEMPLATE,RESPONSE_MESSAGE+HttpStatus.OK.toString(), chRepository.getId());
			logger.info(msg);
			return apiResponse;

		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}

	}

	@Override
	public ApiResponse<CHRepository> updateRepository(HttpServletRequest request, CHRepository chRepository) {
		String msg = apiUtils.stringFormat(MESSAGE_TEMPLATE,"Request: Update Repository.", chRepository.getId());
		logger.info(msg);
		final String RESPONSE_MESSAGE = "Response: Update Repository. ";

		ApiResponse<CHRepository> apiResponse = new ApiResponse<>();
		List<ApiError> errors = new ArrayList<>();

		if (!headerUtils.validateToken(request.getHeader(CHTOKEN_KEY))) {
			msg = apiUtils.stringFormat(MESSAGE_TEMPLATE, RESPONSE_MESSAGE, MESSAGE_INVALID_TOKEN);
			logger.warn(msg);
			errors.add(new ApiError(MESSAGE_INVALID_TOKEN));
			apiResponse.setResponse(HttpStatus.UNAUTHORIZED, null, null, errors, request);
			return apiResponse;
		}

		try {

			String result = repositoryDao.updateRepository(chRepository);
			List<ApiMessage> messages = new ArrayList<>();
			msg = apiUtils.stringFormat(MESSAGE_TEMPLATE, result, chRepository.getId());
			messages.add(new ApiMessage(msg));

			apiResponse.setResponse(HttpStatus.OK, chRepository, messages, null, request);
			msg = apiUtils.stringFormat(MESSAGE_TEMPLATE,RESPONSE_MESSAGE+HttpStatus.OK.toString(), result, chRepository.getId());
			logger.info(msg);
			return apiResponse;

		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<String> deleteRepository(HttpServletRequest request, String id) {
		String msg = apiUtils.stringFormat(MESSAGE_TEMPLATE,"Request: Delete Repository.",id);
		logger.info(msg);
		final String RESPONSE_MESSAGE = "Response: Delete Repository. ";

		ApiResponse<String> apiResponse = new ApiResponse<>();
		List<ApiError> errors = new ArrayList<>();

		if (!headerUtils.validateToken(request.getHeader(CHTOKEN_KEY))) {
			msg = apiUtils.stringFormat(MESSAGE_TEMPLATE, RESPONSE_MESSAGE, MESSAGE_INVALID_TOKEN);
			logger.warn(msg);
			errors.add(new ApiError(MESSAGE_INVALID_TOKEN));
			apiResponse.setResponse(HttpStatus.UNAUTHORIZED, null, null, errors, request);
			return apiResponse;
		}

		try {

			String result = repositoryDao.deleteRepository(id);
			List<ApiMessage> messages = new ArrayList<>();
			msg = apiUtils.stringFormat(MESSAGE_TEMPLATE, result, id);
			messages.add(new ApiMessage(msg));

			if (result.compareToIgnoreCase(NOT_FOUND)== 0) {
				apiResponse.setResponse(HttpStatus.NOT_FOUND, id, messages, null, request);
				msg = apiUtils.stringFormat(MESSAGE_TEMPLATE, RESPONSE_MESSAGE+HttpStatus.NOT_FOUND.toString(), result, id);
				logger.info(msg);
			} else {
				apiResponse.setResponse(HttpStatus.OK, id, messages, null, request);
				msg = apiUtils.stringFormat(MESSAGE_TEMPLATE, RESPONSE_MESSAGE+HttpStatus.OK.toString(), result, id);
				logger.info(msg);
			}
			return apiResponse;

		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<List<String>> deleteRepositories(HttpServletRequest request, List<String> repositoryIds) {
		String msg = apiUtils.stringFormat(MESSAGE_TEMPLATE,"Request: Delete Repositories.", String.valueOf(repositoryIds.size()));
		logger.info(msg);
		final String RESPONSE_MESSAGE = "Response: Delete Repositories. ";

		ApiResponse<List<String>> apiResponse = new ApiResponse<>();
		List<ApiError> errors = new ArrayList<>();

		if (!headerUtils.validateToken(request.getHeader(CHTOKEN_KEY))) {
			msg = apiUtils.stringFormat(MESSAGE_TEMPLATE, RESPONSE_MESSAGE, MESSAGE_INVALID_TOKEN);
			logger.warn(msg);
			errors.add(new ApiError(MESSAGE_INVALID_TOKEN));
			apiResponse.setResponse(HttpStatus.UNAUTHORIZED, null, null, errors, request);
			return apiResponse;
		}

		List<ApiMessage> messages = new ArrayList<>();

		for(String id : repositoryIds) {
			try {
				String result = repositoryDao.deleteRepository(id);
				msg = apiUtils.stringFormat(MESSAGE_TEMPLATE, result, id);
				messages.add(new ApiMessage(msg));

				if (result.compareToIgnoreCase(NOT_FOUND)== 0) {
					msg = apiUtils.stringFormat(MESSAGE_TEMPLATE, RESPONSE_MESSAGE+HttpStatus.NOT_FOUND.toString(), result + " : " + id);
					logger.info(msg);
				} else {
					msg = apiUtils.stringFormat(MESSAGE_TEMPLATE, RESPONSE_MESSAGE+HttpStatus.OK.toString(), result+" : "+id);
					logger.info(msg);
				}

			} catch(ElasticsearchStatusException | IOException e) {
				apiUtils.getErrorsFromException(errors, e);
			}
		}

		if(!errors.isEmpty()) {
			apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, messages, errors, request);
		} else {
			apiResponse.setResponse(HttpStatus.OK, repositoryIds, messages, errors, request);
		}
		return apiResponse;
	}

	@Override
	public ApiResponse<List<String>> resetCache(HttpServletRequest request, List<String> repositoryIds) {
		String msg = apiUtils.stringFormat(MESSAGE_TEMPLATE,"Request: Reset cache.", String.valueOf(repositoryIds.size()));
		logger.info(msg);
		final String RESPONSE_MESSAGE = "Response: Reset cache. ";
		List<ApiError> errors = new ArrayList<>();
		List<ApiMessage> messages = new ArrayList<>();
		ApiResponse<List<String>> apiResponse = new ApiResponse<>();

		if (!headerUtils.validateToken(request.getHeader(CHTOKEN_KEY))) {
			errors.add(new ApiError(MESSAGE_INVALID_TOKEN));
			apiResponse.setResponse(HttpStatus.UNAUTHORIZED, null, null, errors, request);
			msg = apiUtils.stringFormat(MESSAGE_TEMPLATE, RESPONSE_MESSAGE, MESSAGE_INVALID_TOKEN);
			logger.warn(msg);
			return apiResponse;
		}

		for(String id : repositoryIds) {
			try {
				String result = repositoryDao.resetCache(id);
				msg = apiUtils.stringFormat(MESSAGE_TEMPLATE, result, id);
				messages.add(new ApiMessage(msg));

				if (result.compareToIgnoreCase(NOT_FOUND) != 0) {
					msg = apiUtils.stringFormat(MESSAGE_TEMPLATE, RESPONSE_MESSAGE+HttpStatus.OK.toString(), result+" : "+id);
					logger.info(msg);
				} else {
					msg = apiUtils.stringFormat(MESSAGE_TEMPLATE, RESPONSE_MESSAGE+HttpStatus.NOT_FOUND.toString(), result + " : " + id);
					logger.info(msg);
				}

			} catch(ElasticsearchStatusException | IOException e) {
				apiUtils.getErrorsFromException(errors, e);
			}
		}

		if(errors.isEmpty()) {
			apiResponse.setResponse(HttpStatus.OK, repositoryIds, messages, errors, request);
		} else {
			apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, messages, errors, request);
		}
		return apiResponse;
	}

}
