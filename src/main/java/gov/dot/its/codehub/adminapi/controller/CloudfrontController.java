package gov.dot.its.codehub.adminapi.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.dot.its.codehub.adminapi.business.CloudfrontService;
import gov.dot.its.codehub.adminapi.model.ApiResponse;

@CrossOrigin(maxAge = 3600)
@RestController
public class CloudfrontController {
	
	@Autowired
	private CloudfrontService cloudfrontService;
	
	@GetMapping(value = "/v1/invalidate", headers = "Accept=application/json", produces = "application/json")
	public ResponseEntity<ApiResponse<String>> invalidate(HttpServletRequest request, @RequestParam String path) {

		ApiResponse<String> apiResponse = cloudfrontService.invalidate(request, path);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

}
