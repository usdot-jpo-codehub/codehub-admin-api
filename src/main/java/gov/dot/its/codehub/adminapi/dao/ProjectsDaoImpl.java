package gov.dot.its.codehub.adminapi.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.dot.its.codehub.adminapi.model.CHProject;
import gov.dot.its.codehub.adminapi.utils.ApiUtils;

@Repository
public class ProjectsDaoImpl implements ProjectsDao {

	@Value("${codehub.admin.api.es.projects.index}")
	private String projectsIndex;

	@Value("${codehub.admin.api.es.projects.fields}")
	private String[] includedFields;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ApiUtils apiUtils;

	private RestHighLevelClient restHighLevelClient;

	public ProjectsDaoImpl(RestHighLevelClient restHighLevelClient) {
		this.restHighLevelClient = restHighLevelClient;
	}

	@Override
	public List<CHProject> getProjects(int limit) throws IOException {
		SearchRequest searchRequest = new SearchRequest(projectsIndex);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.fetchSource(includedFields, new String[] {});
		searchSourceBuilder.size(limit);
		searchSourceBuilder.query(QueryBuilders.matchAllQuery());
		searchRequest.source(searchSourceBuilder);

		SearchResponse searchResponse = null;

		searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		SearchHits hits = searchResponse.getHits();

		SearchHit[] searchHits = hits.getHits();

		List<CHProject> result = new ArrayList<>();
		for (SearchHit hit : searchHits) {
			Map<String, Object> sourceAsMap = hit.getSourceAsMap();
			ObjectMapper mapper = new ObjectMapper();
			CHProject chProject = mapper.convertValue(sourceAsMap, CHProject.class);
			chProject.setId(hit.getId());

			result.add(chProject);
		}
		return result;
	}

	@Override
	public String updateProject(CHProject chProject) throws IOException {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = objectMapper.convertValue(chProject, Map.class);
		map.remove("id");

		UpdateRequest updateRequest = new UpdateRequest(projectsIndex, "_doc", chProject.getId());
		updateRequest.doc(map, XContentType.JSON);

		UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

		return updateResponse.getResult().name();
	}

	@Override
	public String deleteProject(String id) throws IOException {
		DeleteRequest deleteRequest = new DeleteRequest(projectsIndex, "_doc", id);
		DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
		return deleteResponse.getResult().name();
	}

	@Override
	public String addProject(CHProject chProject) throws IOException {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = objectMapper.convertValue(chProject, Map.class);
		map.remove("id");

		String md5Id = apiUtils.getMd5(chProject.getRepository_url());

		IndexRequest indexRequest = new IndexRequest(projectsIndex, "_doc", md5Id);
		indexRequest.source(map, XContentType.JSON);

		IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);

		return indexResponse.getResult().name();
	}

	@Override
	public boolean docExits(String id) throws IOException {
		GetRequest getRequest = new GetRequest(projectsIndex);
		getRequest.id(id);
		getRequest.fetchSourceContext(new FetchSourceContext(false));
		getRequest.storedFields("_none_");

		return restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
	}

}
