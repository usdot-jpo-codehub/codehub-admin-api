package gov.dot.its.codehub.adminapi.business;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import gov.dot.its.codehub.adminapi.model.ApiResponse;
import gov.dot.its.codehub.adminapi.model.CHRepository;

public interface RepositoryService {

	ApiResponse<List<CHRepository>> getAll(HttpServletRequest request, Map<String, String> params);

	ApiResponse<CHRepository> addRepository(HttpServletRequest request, CHRepository chRepository);

	ApiResponse<CHRepository> updateRepository(HttpServletRequest request, CHRepository chRepository);

	ApiResponse<String> deleteRepository(HttpServletRequest request, String id);

	ApiResponse<List<String>> deleteRepositories(HttpServletRequest request, List<String> repositoryIds);

	ApiResponse<List<String>> resetCache(HttpServletRequest request, List<String> repositoryIds);

}
