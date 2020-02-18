package gov.dot.its.codehub.adminapi.dao;

import java.io.IOException;
import java.util.List;

import gov.dot.its.codehub.adminapi.model.CHCategory;
import gov.dot.its.codehub.adminapi.model.CHConfiguration;

public interface ConfigurationDao {

	CHConfiguration getConfiguration() throws IOException;

	List<CHCategory> getCategories() throws IOException;

	String addCategory(CHCategory chCategory) throws IOException;

	String updateCategory(CHCategory chCategory) throws IOException;

	CHCategory getCategoryById(String id) throws IOException;

	Boolean deleteCategoryById(String id) throws IOException;

}
