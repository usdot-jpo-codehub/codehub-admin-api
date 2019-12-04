package gov.dot.its.codehub.adminapi.controller;

import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.dot.its.codehub.adminapi.business.ProjectsService;
import gov.dot.its.codehub.adminapi.model.ApiResponse;
import gov.dot.its.codehub.adminapi.model.CHProject;

@CrossOrigin(maxAge = 3600)
@RestController
public class ProjectsController {

	@Autowired
	private ProjectsService projectsService;

	@GetMapping(value = "/v1/projects", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<List<CHProject>>> projects(HttpServletRequest request, @RequestParam Map<String, String> params) {

		ApiResponse<List<CHProject>> apiResponse = projectsService.getProjects(request, params);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@PostMapping(value = "/v1/projects", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<CHProject>> addProject(HttpServletRequest request, @RequestBody CHProject chProject ) {

		ApiResponse<CHProject> apiResponse = projectsService.addProject(request, chProject);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@PutMapping(value = "/v1/projects", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<CHProject>> updateProject(HttpServletRequest request, @RequestBody CHProject chProject ) {

		ApiResponse<CHProject> apiResponse = projectsService.updateProject(request, chProject);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@DeleteMapping(value = "/v1/projects/{id}", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<String>> deleteProject(HttpServletRequest request, @PathVariable(name="id") String id ) {

		ApiResponse<String> apiResponse = projectsService.deleteProject(request, id);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/v1/projects", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<List<String>>> deleteProjects(HttpServletRequest request, @RequestBody List<String> projectIds ) {

		ApiResponse<List<String>> apiResponse = projectsService.deleteProjects(request, projectIds);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}
}
