package gov.dot.its.codehub.adminapi.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.StringUtils;

import gov.dot.its.codehub.adminapi.MockDataRepository;
import gov.dot.its.codehub.adminapi.MockESUtils;
import gov.dot.its.codehub.adminapi.model.CHRepository;
import gov.dot.its.codehub.adminapi.utils.ApiUtils;

@RunWith(SpringRunner.class)
public class RepositoryDaoTest {

	private static final String TEST_ID = "1";
	private static final int TEST_LIMIT = 10;

	private MockESUtils mockESUtils;
	private MockDataRepository mockData;

	@InjectMocks
	RepositoryDaoImpl repositoryDao;

	@Mock
	ESClientDao esClientDao;

	@Mock
	ObjectMapper objectMapper;

	@Mock
	ApiUtils apiUtils;

	@Before
	public void setUp() {
		ReflectionTestUtils.setField(repositoryDao, "reposIndex", "unitTextIndex");
		ReflectionTestUtils.setField(repositoryDao, "includedFields", new String[]{"*"});
		ReflectionTestUtils.setField(repositoryDao, "sortBy", "codehubData.lastModified");
		ReflectionTestUtils.setField(repositoryDao, "sortOrder", "desc");

		mockESUtils = new MockESUtils();
		mockData = new MockDataRepository();
	}

	@Test
	public void testGetAllData() throws IOException {
		CHRepository fakeRepositories = mockData.getFakeCHRepository();
		SearchResponse searchResponse = mockESUtils.generateFakeSearchResponse(fakeRepositories);
		when(esClientDao.search(any(SearchRequest.class), any(RequestOptions.class))).thenReturn(searchResponse);

		List<CHRepository> repositories =  repositoryDao.getAll(TEST_LIMIT);
		assertFalse(repositories.isEmpty());
	}

	@Test
	public void testGetAllNoData() throws IOException {
		SearchResponse searchResponse = mockESUtils.generateFakeSearchResponse(null);
		when(esClientDao.search(any(SearchRequest.class), any(RequestOptions.class))).thenReturn(searchResponse);

		List<CHRepository> repositories =  repositoryDao.getAll(TEST_LIMIT);
		assertTrue(repositories.isEmpty());
	}

	@Test
	public void testAddRepository() throws IOException {
		CHRepository repository = mockData.getFakeCHRepository();
		IndexResponse indexResponse = mockESUtils.generateFakeIndexResponse(repository);
		when(apiUtils.getCurrentUtc()).thenReturn(new Date());
		when(esClientDao.index(any(IndexRequest.class), any(RequestOptions.class))).thenReturn(indexResponse);

		String result =  repositoryDao.addRepository(repository);
		assertEquals(Result.CREATED.toString(), result);
	}

	@Test
	public void testUpdateRepository() throws IOException {
		CHRepository repository = mockData.getFakeCHRepository();
		UpdateResponse updateResponse = mockESUtils.generateFakeUpdateResponse(repository);
		when(apiUtils.getCurrentUtc()).thenReturn(new Date());
		when(esClientDao.update(any(UpdateRequest.class), any(RequestOptions.class))).thenReturn(updateResponse);

		String result =  repositoryDao.updateRepository(repository);
		assertEquals(Result.UPDATED.toString(), result);
	}

	@Test
	public void testDeleteRepository() throws IOException {
		DeleteResponse deleteResponse = mockESUtils.generateFakeDeleteResponse(TEST_ID);
		when(esClientDao.delete(any(DeleteRequest.class), any(RequestOptions.class))).thenReturn(deleteResponse);

		String result =  repositoryDao.deleteRepository(TEST_ID);
		assertEquals(Result.DELETED.toString(), result);
	}

	@Test
	public void testResetCache() throws IOException {
		CHRepository repository = mockData.getFakeCHRepository();
		UpdateResponse updateResponse = mockESUtils.generateFakeUpdateResponse(repository);
		when(apiUtils.getCurrentUtc()).thenReturn(new Date());
		when(esClientDao.update(any(UpdateRequest.class), any(RequestOptions.class))).thenReturn(updateResponse);

		String result =  repositoryDao.resetCache(repository.getId());
		assertEquals(Result.UPDATED.toString(), result);
	}

	@Test
	public void testDocExists() throws IOException {
		when(esClientDao.exists(any(GetRequest.class), any(RequestOptions.class))).thenReturn(true);
		boolean result = repositoryDao.docExists(TEST_ID);
		assertTrue(result);
	}

	@Test
	public void testRemoveCategory() throws IOException {
		CHRepository repository = mockData.getFakeCHRepository();
		SearchResponse searchResponse = mockESUtils.generateFakeSearchResponse(repository);
		when(esClientDao.search(any(SearchRequest.class), any(RequestOptions.class))).thenReturn(searchResponse);
		UpdateResponse updateResponse = mockESUtils.generateFakeUpdateResponse(repository);
		when(esClientDao.update(any(UpdateRequest.class), any(RequestOptions.class))).thenReturn(updateResponse);
		String result = repositoryDao.removeCategory(TEST_ID);
		assertFalse(StringUtils.isEmpty(result));
	}
}
