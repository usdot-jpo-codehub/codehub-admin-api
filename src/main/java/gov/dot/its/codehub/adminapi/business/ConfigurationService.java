package gov.dot.its.codehub.adminapi.business;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import gov.dot.its.codehub.adminapi.model.ApiResponse;
import gov.dot.its.codehub.adminapi.model.CHCategory;
import gov.dot.its.codehub.adminapi.model.CHConfiguration;
import gov.dot.its.codehub.adminapi.model.CHEngagementPopup;

public interface ConfigurationService {

	ApiResponse<CHConfiguration> configurations(HttpServletRequest request);

	ApiResponse<List<CHCategory>> categories(HttpServletRequest request);

	ApiResponse<CHCategory> addCategory(HttpServletRequest request, CHCategory chCategory);

	ApiResponse<CHCategory> updateCategory(HttpServletRequest request, CHCategory chCategory);

	ApiResponse<CHCategory> category(HttpServletRequest request, String id);

	ApiResponse<CHCategory> deleteCategory(HttpServletRequest request, String id);

	ApiResponse<List<String>> categoryImages(HttpServletRequest request);

	ApiResponse<List<CHEngagementPopup>> engagementpopups(HttpServletRequest request);

	ApiResponse<CHEngagementPopup> updateEngagementPopup(HttpServletRequest request, CHEngagementPopup engagementPopup);

	ApiResponse<CHEngagementPopup> addEngagementPopup(HttpServletRequest request, CHEngagementPopup cdEngagementPopup);

	ApiResponse<CHEngagementPopup> deleteEngagementPopup(HttpServletRequest request, String id);

}
