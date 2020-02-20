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
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.dot.its.codehub.adminapi.model.CHRepository;
import gov.dot.its.codehub.adminapi.utils.ApiUtils;

@Repository
public class RepositoryDaoImpl implements RepositoryDao {

	@Value("${codehub.admin.api.es.repos.index}")
	private String reposIndex;
	@Value("${codehub.admin.api.es.repos.fields}")
	private String[] includedFields;
	@Value("${codehub.admin.api.es.sort.by}")
	private String sortBy;
	@Value("${codehub.admin.api.es.sort.order}")
	private String sortOrder;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ApiUtils apiUtils;

	private RestHighLevelClient restHighLevelClient;

	public RepositoryDaoImpl(RestHighLevelClient restHighLevelClient) {
		this.restHighLevelClient = restHighLevelClient;
	}

	@Override
	public List<CHRepository> getAll(int limit) throws IOException {
		SearchRequest searchRequest = new SearchRequest(reposIndex);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchAllQuery());

		searchSourceBuilder.size(limit);
		searchSourceBuilder.fetchSource(includedFields, new String[] {});
		SortOrder so = sortOrder.equalsIgnoreCase("desc") ? SortOrder.DESC : SortOrder.ASC;
		searchSourceBuilder.sort(sortBy, so);
		searchRequest.source(searchSourceBuilder);

		SearchResponse searchResponse = null;

		searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		SearchHits hits = searchResponse.getHits();

		SearchHit[] searchHits = hits.getHits();

		List<CHRepository> result = new ArrayList<>();
		for (SearchHit hit : searchHits) {
			Map<String, Object> sourceAsMap = hit.getSourceAsMap();

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			CHRepository chRepository = mapper.convertValue(sourceAsMap, CHRepository.class);
			chRepository.setId(hit.getId());
			result.add(chRepository);
		}

		return result;
	}

	@Override
	public String addRepository(CHRepository chRepository) throws IOException {
		chRepository.getCodehubData().setLastModified(apiUtils.getCurrentUtc());
		@SuppressWarnings("unchecked")
		Map<String, Object> map = objectMapper.convertValue(chRepository, Map.class);

		IndexRequest indexRequest = new IndexRequest(reposIndex, "_doc", chRepository.getId());
		indexRequest.source(map, XContentType.JSON);

		IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);

		return indexResponse.getResult().name();
	}

	@Override
	public String updateRepository(CHRepository chRepository) throws IOException {
		chRepository.getCodehubData().setLastModified(apiUtils.getCurrentUtc());
		@SuppressWarnings("unchecked")
		Map<String, Object> map = objectMapper.convertValue(chRepository, Map.class);

		UpdateRequest updateRequest = new UpdateRequest(reposIndex, "_doc", chRepository.getId());
		updateRequest.doc(map, XContentType.JSON);

		UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

		return updateResponse.getResult().name();
	}

	@Override
	public String deleteRepository(String id) throws IOException {
		DeleteRequest deleteRequest = new DeleteRequest(reposIndex, "_doc", id);
		DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
		return deleteResponse.getResult().name();
	}

	@Override
	public String resetCache(String id) throws IOException {

		String jsonObj = "{\"codehubData\":{\"etag\":\"N/A\",\"lastModified\":\""+apiUtils.getTimestampFormat(apiUtils.getCurrentUtc(),"yyyy-MM-dd'T'HH:mm:ss'Z'")+"\"}}";

		UpdateRequest updateRequest = new UpdateRequest(reposIndex, "_doc", id);
		updateRequest.doc(jsonObj, XContentType.JSON);

		UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

		return updateResponse.getResult().name();
	}

	@Override
	public boolean docExists(String id) throws IOException {
		GetRequest getRequest = new GetRequest(reposIndex);
		getRequest.id(id);
		getRequest.fetchSourceContext(new FetchSourceContext(false));
		getRequest.storedFields("_none_");

		return restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
	}

	@Override
	public String removeCategory(String categoryId) throws IOException {
		List<CHRepository> repositories = this.getAll(1000);
		StringBuilder stringBuilder = new StringBuilder();
		for(CHRepository repository: repositories) {
			 if (repository.getCodehubData().getCategories().isEmpty() || !repository.getCodehubData().getCategories().contains(categoryId)) {
				 continue;
			 }
			 repository.getCodehubData().getCategories().remove(categoryId);
			 stringBuilder.append(String.format("Category [%s] removed from [%s]%n", categoryId, repository.getSourceData().getName()));
			 this.updateRepository(repository);
		 }
		return stringBuilder.toString();
	}
}
