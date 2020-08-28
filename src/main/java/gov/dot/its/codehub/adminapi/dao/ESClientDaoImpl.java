package gov.dot.its.codehub.adminapi.dao;

import java.io.IOException;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Repository;

@Repository
public class ESClientDaoImpl implements ESClientDao {

	private RestHighLevelClient restHighLevelClient;

	public ESClientDaoImpl(RestHighLevelClient restHighLevelClient) {
		this.restHighLevelClient = restHighLevelClient;
	}

	@Override
	public GetResponse get(GetRequest getRequest, RequestOptions requestOptions) throws IOException {
		return restHighLevelClient.get(getRequest, requestOptions);
	}

	@Override
	public SearchResponse search(SearchRequest searchRequest, RequestOptions requestOptions) throws IOException {
		return restHighLevelClient.search(searchRequest, requestOptions);
	}

	@Override
	public IndexResponse index(IndexRequest indexRequest, RequestOptions requestOptions) throws IOException {
		return restHighLevelClient.index(indexRequest, requestOptions);
	}

	@Override
	public UpdateResponse update(UpdateRequest updateRequest, RequestOptions requestOptions) throws IOException {
		return restHighLevelClient.update(updateRequest, requestOptions);
	}

	@Override
	public DeleteResponse delete(DeleteRequest deleteRequest, RequestOptions requestOptions) throws IOException {
		return restHighLevelClient.delete(deleteRequest, requestOptions);
	}

	@Override
	public boolean exists(GetRequest getRequest, RequestOptions requestOptions) throws IOException {
		return restHighLevelClient.exists(getRequest, requestOptions);
	}

}
