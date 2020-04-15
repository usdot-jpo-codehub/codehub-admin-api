package gov.dot.its.codehub.adminapi.business;

import javax.servlet.http.HttpServletRequest;

import gov.dot.its.codehub.adminapi.model.ApiResponse;
import gov.dot.its.codehub.adminapi.model.CFInvalidation;

public interface CloudfrontService {

	ApiResponse<String> invalidate(HttpServletRequest request, CFInvalidation cfInvalidation);

}
