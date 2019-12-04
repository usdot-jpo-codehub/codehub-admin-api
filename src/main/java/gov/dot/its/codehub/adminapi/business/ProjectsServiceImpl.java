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

import gov.dot.its.codehub.adminapi.dao.ProjectsDao;
import gov.dot.its.codehub.adminapi.model.ApiError;
import gov.dot.its.codehub.adminapi.model.ApiMessage;
import gov.dot.its.codehub.adminapi.model.ApiResponse;
import gov.dot.its.codehub.adminapi.model.CHProject;
import gov.dot.its.codehub.adminapi.utils.ApiUtils;
import gov.dot.its.codehub.adminapi.utils.HeaderUtils;



@Service
public class ProjectsServiceImpl implements ProjectsService {

	private static final Logger logger = LoggerFactory.getLogger(ProjectsServiceImpl.class);

	private static final String CHTOKEN_KEY = "CHTOKEN";
	private static final String MESSAGE_INVALID_TOKEN = "Invalid token";
	private static final String MESSAGE_TEMPLATE = "%s : %s ";

	@Autowired
	private ProjectsDao projectsDao;

	@Autowired
	private ApiUtils apiUtils;

	@Autowired
	private HeaderUtils headerUtils;

	@Value("${codehub.admin.api.debug}")
	private boolean debug;

	@Override
	public ApiResponse<List<CHProject>> getProjects(HttpServletRequest request, Map<String, String> params) {
		logger.info("Request: Get Projects");
		final String RESPONSE_GET_ALL = "Response: Get Projects. ";

		ApiResponse<List<CHProject>> apiResponse = new ApiResponse<>();
		List<ApiError> errors = new ArrayList<>();

		if (!headerUtils.validateToken(request.getHeader(CHTOKEN_KEY))) {
			errors.add(new ApiError(MESSAGE_INVALID_TOKEN));
			logger.warn(String.format(MESSAGE_TEMPLATE, RESPONSE_GET_ALL, MESSAGE_INVALID_TOKEN));
			return apiResponse.setResponse(HttpStatus.UNAUTHORIZED, null, null, errors, request);
		}

		int limit = apiUtils.getQueryParamInteger(params, "limit", 1000);

		try {
			List<CHProject> data = projectsDao.getProjects(limit);

			if (data != null && !data.isEmpty()) {
				logger.info(String.format(MESSAGE_TEMPLATE, RESPONSE_GET_ALL+HttpStatus.OK.toString(), String.valueOf(data.size())));
				return apiResponse.setResponse(HttpStatus.OK, data, null, null, request);
			} else {
				logger.info(RESPONSE_GET_ALL + HttpStatus.NO_CONTENT.toString());
				return apiResponse.setResponse(HttpStatus.NO_CONTENT, null, null, null, request);
			}
		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	

	@Override
	public ApiResponse<CHProject> updateProject(HttpServletRequest request, CHProject chProject) {
		logger.info(String.format(MESSAGE_TEMPLATE,"Request: Update Project.", chProject.getId()));
		final String RESPONSE_MESSAGE = "Response: Update Project. ";

		ApiResponse<CHProject> apiResponse = new ApiResponse<>();
		List<ApiError> errors = new ArrayList<>();

		if (!headerUtils.validateToken(request.getHeader(CHTOKEN_KEY))) {
			logger.warn(String.format(MESSAGE_TEMPLATE, RESPONSE_MESSAGE, MESSAGE_INVALID_TOKEN));
			errors.add(new ApiError(MESSAGE_INVALID_TOKEN));
			return apiResponse.setResponse(HttpStatus.UNAUTHORIZED, null, null, errors, request);
		}

		try {

			String result = projectsDao.updateProject(chProject);
			List<ApiMessage> messages = new ArrayList<>();
			messages.add(new ApiMessage(result));

			apiResponse.setResponse(HttpStatus.OK, chProject, messages, null, request);
			logger.info(String.format(MESSAGE_TEMPLATE,RESPONSE_MESSAGE+HttpStatus.OK.toString(), chProject.getId()));
			return apiResponse;

		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<String> deleteProject(HttpServletRequest request, String id) {
		logger.info(String.format(MESSAGE_TEMPLATE,"Request: Delete Project.", id));
		final String RESPONSE_MESSAGE = "Response: Delete Project. ";

		ApiResponse<String> apiResponse = new ApiResponse<>();
		List<ApiError> errors = new ArrayList<>();

		if (!headerUtils.validateToken(request.getHeader(CHTOKEN_KEY))) {
			logger.warn(String.format(MESSAGE_TEMPLATE, RESPONSE_MESSAGE, MESSAGE_INVALID_TOKEN));
			errors.add(new ApiError(MESSAGE_INVALID_TOKEN));
			return apiResponse.setResponse(HttpStatus.UNAUTHORIZED, null, null, errors, request);
		}

		try {

			String result = projectsDao.deleteProject(id);
			List<ApiMessage> messages = new ArrayList<>();
			messages.add(new ApiMessage(result));

			if (result.compareToIgnoreCase("NOT_FOUND")== 0) {
				apiResponse.setResponse(HttpStatus.NOT_FOUND, id, messages, null, request);
				logger.info(String.format(MESSAGE_TEMPLATE, RESPONSE_MESSAGE+HttpStatus.NOT_FOUND.toString(), result + " : "+ id));
			} else {
				apiResponse.setResponse(HttpStatus.OK, id, messages, null, request);
				logger.info(String.format(MESSAGE_TEMPLATE, RESPONSE_MESSAGE+HttpStatus.OK.toString(), result + " : "+ id));
			}
			return apiResponse;

		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<CHProject> addProject(HttpServletRequest request, CHProject chProject) {
		chProject.setId(apiUtils.getMd5(chProject.getRepository_url()));
		
		logger.info(String.format(MESSAGE_TEMPLATE,"Request: Add Project.", chProject.getId()));
		final String RESPONSE_MESSAGE = "Response: Add Project. ";

		ApiResponse<CHProject> apiResponse = new ApiResponse<>();
		List<ApiError> errors = new ArrayList<>();

		if (!headerUtils.validateToken(request.getHeader(CHTOKEN_KEY))) {
			logger.warn(String.format(MESSAGE_TEMPLATE, RESPONSE_MESSAGE, MESSAGE_INVALID_TOKEN));
			errors.add(new ApiError(MESSAGE_INVALID_TOKEN));
			apiResponse.setResponse(HttpStatus.UNAUTHORIZED, null, null, errors, request);
			return apiResponse;
		}

		try {

			if (projectsDao.docExits(chProject.getId())) {
				errors.add(new ApiError(String.format("Project exists: %s", chProject.getId())));
				apiResponse.setResponse(HttpStatus.CONFLICT, chProject, null, errors, request);
				return apiResponse;
			}

			String result = projectsDao.addProject(chProject);
			List<ApiMessage> messages = new ArrayList<>();
			messages.add(new ApiMessage(result));

			apiResponse.setResponse(HttpStatus.OK, chProject, messages, null, request);
			logger.info(String.format(MESSAGE_TEMPLATE,RESPONSE_MESSAGE+HttpStatus.OK.toString(), chProject.getId()));
			return apiResponse;

		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<List<String>> deleteProjects(HttpServletRequest request, List<String> projectIds) {
		logger.info(String.format(MESSAGE_TEMPLATE,"Request: Delete Projects.", String.valueOf(projectIds.size())));
		final String RESPONSE_MESSAGE = "Response: Delete Projects. ";

		ApiResponse<List<String>> apiResponse = new ApiResponse<>();
		List<ApiError> errors = new ArrayList<>();

		if (!headerUtils.validateToken(request.getHeader(CHTOKEN_KEY))) {
			logger.warn(String.format(MESSAGE_TEMPLATE, RESPONSE_MESSAGE, MESSAGE_INVALID_TOKEN));
			errors.add(new ApiError(MESSAGE_INVALID_TOKEN));
			apiResponse.setResponse(HttpStatus.UNAUTHORIZED, null, null, errors, request);
			return apiResponse;
		}

		List<ApiMessage> messages = new ArrayList<>();

		for(String id : projectIds) {
			try {
				String result = projectsDao.deleteProject(id);
				messages.add(new ApiMessage(String.format(MESSAGE_TEMPLATE, result, id)));
				
				if (result.compareToIgnoreCase("NOT_FOUND")== 0) {
					logger.info(String.format(MESSAGE_TEMPLATE, RESPONSE_MESSAGE+HttpStatus.NOT_FOUND.toString(), result+" : "+id));
				} else {
					logger.info(String.format(MESSAGE_TEMPLATE, RESPONSE_MESSAGE+HttpStatus.OK.toString(), result+" : "+id));
				}

			} catch(ElasticsearchStatusException | IOException e) {
				apiUtils.getErrorsFromException(errors, e);
			}
		}

		if (!errors.isEmpty()) {
			apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, errors, request);
		} else {
			apiResponse.setResponse(HttpStatus.OK, projectIds, messages, null, request);
		}

		return apiResponse;
	}

}
