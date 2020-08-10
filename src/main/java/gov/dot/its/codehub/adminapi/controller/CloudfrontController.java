package gov.dot.its.codehub.adminapi.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import gov.dot.its.codehub.adminapi.business.CloudfrontService;
import gov.dot.its.codehub.adminapi.model.ApiResponse;
import gov.dot.its.codehub.adminapi.model.CFInvalidation;

@RestController
public class CloudfrontController {
	
	@Autowired
	private CloudfrontService cloudfrontService;
	
	@PostMapping(value = "/v1/invalidate", headers = "Accept=application/json", produces = "application/json")
	public ResponseEntity<ApiResponse<String>> invalidate(HttpServletRequest request, @RequestBody CFInvalidation cfInvalidation) {

		ApiResponse<String> apiResponse = cloudfrontService.invalidate(request, cfInvalidation);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

}
