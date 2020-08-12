package gov.dot.its.codehub.adminapi.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.dot.its.codehub.adminapi.business.RepositoryService;
import gov.dot.its.codehub.adminapi.model.ApiResponse;
import gov.dot.its.codehub.adminapi.model.CHRepository;

@RestController
public class RepositoryController {

	@Autowired
	private RepositoryService repositoryService;
	
	@GetMapping(value = "/v1/repositories", headers = "Accept=application/json", produces = "application/json")
	public ResponseEntity<ApiResponse<List<CHRepository>>> repositories(HttpServletRequest request, @RequestParam Map<String, String> params) {

		ApiResponse<List<CHRepository>> apiResponse = repositoryService.getAll(request, params);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@PostMapping(value = "/v1/repositories", headers = "Accept=application/json", produces = "application/json")
	public ResponseEntity<ApiResponse<CHRepository>> addRepository(HttpServletRequest request, @RequestBody CHRepository chRepository) {

		ApiResponse<CHRepository> apiResponse = repositoryService.addRepository(request, chRepository);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@PutMapping(value = "/v1/repositories", headers = "Accept=application/json", produces = "application/json")
	public ResponseEntity<ApiResponse<CHRepository>> updateRepository(HttpServletRequest request, @RequestBody CHRepository chRepository) {

		ApiResponse<CHRepository> apiResponse = repositoryService.updateRepository(request, chRepository);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@DeleteMapping(value = "/v1/repositories/{id}", headers = "Accept=application/json", produces = "application/json")
	public ResponseEntity<ApiResponse<String>> deleteRepository(HttpServletRequest request, @PathVariable(name = "id") String id ) {

		ApiResponse<String> apiResponse = repositoryService.deleteRepository(request, id);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@DeleteMapping(value = "/v1/repositories", headers = "Accept=application/json", produces = "application/json")
	public ResponseEntity<ApiResponse<List<String>>> deleteRepositories(HttpServletRequest request, @RequestBody List<String> repositoryIds) {

		ApiResponse<List<String>> apiResponse = repositoryService.deleteRepositories(request, repositoryIds);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@PatchMapping(value = "/v1/repositories/resetcache", headers = "Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<List<String>>> resetCache(HttpServletRequest request, @RequestBody List<String> repositoryIds) {

		ApiResponse<List<String>> apiResponse = repositoryService.resetCache(request, repositoryIds);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}
}
