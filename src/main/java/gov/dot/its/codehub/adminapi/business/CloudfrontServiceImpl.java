package gov.dot.its.codehub.adminapi.business;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cloudfront.AmazonCloudFront;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClientBuilder;
import com.amazonaws.services.cloudfront.model.CreateInvalidationRequest;
import com.amazonaws.services.cloudfront.model.CreateInvalidationResult;
import com.amazonaws.services.cloudfront.model.InvalidationBatch;
import com.amazonaws.services.cloudfront.model.Paths;

import gov.dot.its.codehub.adminapi.model.ApiError;
import gov.dot.its.codehub.adminapi.model.ApiResponse;
import gov.dot.its.codehub.adminapi.model.CFInvalidation;
import gov.dot.its.codehub.adminapi.utils.ApiUtils;
import gov.dot.its.codehub.adminapi.utils.HeaderUtils;

@Service
public class CloudfrontServiceImpl implements CloudfrontService {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryServiceImpl.class);

	private static final String CHTOKEN_KEY = "CHTOKEN";
	private static final String MESSAGE_INVALID_TOKEN = "Invalid token";
	private static final String MESSAGE_TEMPLATE = "{} : {}";

	@Value("${codehub.admin.api.debug}")
	private boolean debug;

	@Value("${codehub.admin.api.cloudfront.distributionid}")
	private String distributionId;

	@Autowired
	private ApiUtils apiUtils;

	@Autowired
	private HeaderUtils headerUtils;

	@Override
	public ApiResponse<String> invalidate(HttpServletRequest request, CFInvalidation cfInvalidation) {
		
		AmazonCloudFront cfclient = AmazonCloudFrontClientBuilder.defaultClient();
		
		String path = cfInvalidation.getPath();

		logger.info(MESSAGE_TEMPLATE, "Request: CloudFront invalidation for path", cfInvalidation.getPath());
		final String RESPONSE_INVALIDATE = String.format(MESSAGE_TEMPLATE, "Response: CloudFront invalidation for path",
				path);

		ApiResponse<String> apiResponse = new ApiResponse<>();
		List<ApiError> errors = new ArrayList<>();

		if (!headerUtils.validateToken(request.getHeader(CHTOKEN_KEY))) {
			logger.warn(MESSAGE_TEMPLATE, RESPONSE_INVALIDATE, MESSAGE_INVALID_TOKEN);
			errors.add(new ApiError(MESSAGE_INVALID_TOKEN));
			apiResponse.setResponse(HttpStatus.UNAUTHORIZED, null, null, errors, request);
			return apiResponse;
		}

		Paths paths = new Paths().withItems(path);
		String callerReference = Long.toString(Instant.now().getEpochSecond());

		InvalidationBatch invalidationBatch = new InvalidationBatch().withPaths(paths)
				.withCallerReference(callerReference);
		CreateInvalidationRequest invalidationRequest = new CreateInvalidationRequest(distributionId,
				invalidationBatch);

		try {
			CreateInvalidationResult result = cfclient.createInvalidation(invalidationRequest);
			apiResponse.setResponse(HttpStatus.OK, result.getSdkResponseMetadata().getRequestId(), null, null, request);
		} catch (AmazonServiceException e) {
			logger.error("Failed to invalidate CloudFront path", e);
			apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null,
					apiUtils.getErrorsFromException(errors, e), request);
		}

		return apiResponse;
	}

}
