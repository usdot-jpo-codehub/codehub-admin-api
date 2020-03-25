package gov.dot.its.codehub.adminapi.business;

import javax.servlet.http.HttpServletRequest;

import gov.dot.its.codehub.adminapi.model.ApiResponse;

public interface CloudfrontService {
	
	ApiResponse<String> invalidate(HttpServletRequest request, String path);

}
