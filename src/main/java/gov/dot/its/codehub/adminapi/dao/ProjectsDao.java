package gov.dot.its.codehub.adminapi.dao;

import java.io.IOException;
import java.util.List;

import gov.dot.its.codehub.adminapi.model.CHProject;

public interface ProjectsDao {

	List<CHProject> getProjects(int limit) throws IOException;

	String updateProject(CHProject chProject) throws IOException;

	String deleteProject(String id) throws IOException;

	String addProject(CHProject chProject) throws IOException;

	boolean docExits(String id) throws IOException;

}
