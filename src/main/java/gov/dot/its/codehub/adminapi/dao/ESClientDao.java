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

public interface ESClientDao {
	GetResponse get(GetRequest getRequest, RequestOptions requestOptions) throws IOException;
	SearchResponse search(SearchRequest searchRequest, RequestOptions requestOptions) throws IOException;
	IndexResponse index(IndexRequest indexRequest, RequestOptions requestOptions) throws IOException;
	UpdateResponse update(UpdateRequest updateRequest, RequestOptions requestOptions) throws IOException;
	DeleteResponse delete(DeleteRequest deleteRequest, RequestOptions requestOptions) throws IOException;
	boolean exists(GetRequest getRequest, RequestOptions requestOptions) throws IOException;
}
