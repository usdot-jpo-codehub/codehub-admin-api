package gov.dot.its.codehub.adminapi.dao;

import java.io.IOException;
import java.util.List;

import gov.dot.its.codehub.adminapi.model.CHCategory;
import gov.dot.its.codehub.adminapi.model.CHConfiguration;
import gov.dot.its.codehub.adminapi.model.CHEngagementPopup;

public interface ConfigurationDao {

	CHConfiguration getConfiguration() throws IOException;

	List<CHCategory> getCategories() throws IOException;

	String addCategory(CHCategory chCategory) throws IOException;

	String updateCategory(CHCategory chCategory) throws IOException;

	CHCategory getCategoryById(String id) throws IOException;

	Boolean deleteCategoryById(String id) throws IOException;

	List<String> getCategoryImages() throws IOException;

	List<CHEngagementPopup> getEngagementPopups() throws IOException;

	String updateEngagementPopup(CHEngagementPopup engagementPopup) throws IOException;

	String addEngagementPopup(CHEngagementPopup cdEngagementPopup) throws IOException;

	Boolean removeEngagementPopup(String id) throws IOException;

}
