package gov.dot.its.codehub.adminapi.business;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import gov.dot.its.codehub.adminapi.model.ApiResponse;
import gov.dot.its.codehub.adminapi.model.CHProject;

public interface ProjectsService {

	ApiResponse<List<CHProject>> getProjects(HttpServletRequest request, Map<String, String> params);

	ApiResponse<CHProject> updateProject(HttpServletRequest request, CHProject chProject);

	ApiResponse<String> deleteProject(HttpServletRequest request, String id);

	ApiResponse<CHProject> addProject(HttpServletRequest request, CHProject chProject);

	ApiResponse<List<String>> deleteProjects(HttpServletRequest request, List<String> projectIds);

}
