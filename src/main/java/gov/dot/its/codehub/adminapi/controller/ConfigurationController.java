package gov.dot.its.codehub.adminapi.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import gov.dot.its.codehub.adminapi.business.ConfigurationService;
import gov.dot.its.codehub.adminapi.model.ApiResponse;
import gov.dot.its.codehub.adminapi.model.CHCategory;
import gov.dot.its.codehub.adminapi.model.CHConfiguration;

@CrossOrigin(maxAge = 3600)
@RestController
public class ConfigurationController {

	@Autowired
	private ConfigurationService configurationService;

	@GetMapping(value="/v1/configurations", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<CHConfiguration>> configurations(HttpServletRequest request) {

		ApiResponse<CHConfiguration> apiResponse = configurationService.configurations(request);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@GetMapping(value="/v1/configurations/categories", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<List<CHCategory>>> categories(HttpServletRequest request) {

		ApiResponse<List<CHCategory>> apiResponse = configurationService.categories(request);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@GetMapping(value="/v1/configurations/categories/{id}", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<CHCategory>> category(HttpServletRequest request, @PathVariable(name = "id") String id) {

		ApiResponse<CHCategory> apiResponse = configurationService.category(request, id);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@PostMapping(value="/v1/configurations/categories", headers = "Accept=application/json", produces = "application/json")
	public ResponseEntity<ApiResponse<CHCategory>> addCategory(HttpServletRequest request, @RequestBody CHCategory chCategory) {

		ApiResponse<CHCategory> apiResponse = configurationService.addCategory(request, chCategory);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@PutMapping(value="/v1/configurations/categories", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<CHCategory>> updateCategory(HttpServletRequest request, @RequestBody CHCategory chCategory) {

		ApiResponse<CHCategory> apiResponse = configurationService.updateCategory(request, chCategory);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@DeleteMapping(value="/v1/configurations/categories/{id}", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<CHCategory>> deleteCategory(HttpServletRequest request, @PathVariable(name = "id") String id) {

		ApiResponse<CHCategory> apiResponse = configurationService.deleteCategory(request, id);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@GetMapping(value="/v1/images/categories", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<List<String>>> categoryImages(HttpServletRequest request) {

		ApiResponse<List<String>> apiResponse = configurationService.categoryImages(request);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}
}
