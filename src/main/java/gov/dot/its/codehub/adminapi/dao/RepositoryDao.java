package gov.dot.its.codehub.adminapi.dao;

import java.io.IOException;
import java.util.List;

import gov.dot.its.codehub.adminapi.model.CHRepository;

public interface RepositoryDao {

	List<CHRepository> getAll(int limit) throws IOException;

	String addRepository(CHRepository chRepository) throws IOException;

	String updateRepository(CHRepository chRepository) throws IOException;

	String deleteRepository(String id) throws IOException;

	String resetCache(String id) throws IOException;

	boolean docExists(String id) throws IOException;

}
