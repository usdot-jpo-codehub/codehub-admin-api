package gov.dot.its.codehub.adminapi;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchResponseSections;
import org.elasticsearch.action.search.ShardSearchFailure;
import org.elasticsearch.action.support.replication.ReplicationResponse.ShardInfo;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.index.shard.ShardId;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

public class MockESUtils {
	private ObjectMapper mapper;

	public MockESUtils() {
		this.mapper = new ObjectMapper();
	}

	public SearchResponse generateFakeSearchResponse(Object sourceObject) throws JsonProcessingException {
		String jsonObject = sourceObject != null ? mapper.writeValueAsString(sourceObject) : "";
		BytesReference source = new BytesArray(jsonObject);
		SearchHit hit = new SearchHit(sourceObject != null ? 1 : 0);
		hit.sourceRef( source );
		SearchHit[] searchHitArray = sourceObject != null ? new SearchHit[] { hit } : new SearchHit[] {};
		SearchHits hits = new SearchHits( searchHitArray, null, 1 );
		SearchResponseSections searchResponseSections = new SearchResponseSections( hits, null, null, false, null, null, 5 );
		SearchResponse searchResponse = new SearchResponse(searchResponseSections, null, 8, 8, 0, 8, new ShardSearchFailure[] {}, null);

		return searchResponse;
	}

	public GetResponse generateFakeGetResponse(Object sourceObject) throws JsonProcessingException {
		String index = "index";
		String type = "type";
		String id = "id";
		long seqNo = sourceObject != null ? 1L : -2;
		long primaryTerm = sourceObject != null ? 1L : 0;
		long version = 1;
		boolean exists = sourceObject != null;

		String jsonObject = sourceObject != null ? mapper.writeValueAsString(sourceObject) : "";
		BytesReference source = new BytesArray(jsonObject);

		Map<String, DocumentField> fields = sourceObject != null ? new HashMap<>() : null;
		Map<String, DocumentField> metaFields = sourceObject != null ? new HashMap<>() : null;
		GetResult getResult = new GetResult(index, type, id, seqNo, primaryTerm, version, exists, source, fields, metaFields);
		GetResponse getResponse = new GetResponse(getResult);
		return getResponse;
	}

	public UpdateResponse generateFakeUpdateResponse(Object sourceObject) throws JsonProcessingException {
		int total = sourceObject != null ? 1 : 0;
		int successful = total;
		String index = "index";
		String indexUUID = "UUID";
		String type = "type";
		String id = "id";
		int shartIdInt = 1;
		long seqNo = sourceObject != null ? 1L : -2;
		long primaryTerm = sourceObject != null ? 1L : 0;
		long version = 1;
		Result result = sourceObject != null ? Result.UPDATED : Result.NOOP;
		ShardId shardId = new ShardId(index, indexUUID, shartIdInt);
		ShardInfo shardInfo = new ShardInfo(total, successful, null);
		UpdateResponse updateResponse = new UpdateResponse(shardInfo, shardId, type, id, seqNo, primaryTerm, version, result);
		return updateResponse;
	}

	public IndexResponse generateFakeIndexResponse(Object sourceObject) throws JsonProcessingException {
		String index = "index";
		String indexUUID = "UUID";
		String type = "type";
		String id = "id";
		int shartIdInt = 1;
		long seqNo = sourceObject != null ? 1L : -2;
		long primaryTerm = sourceObject != null ? 1L : 0;
		long version = 1;
		boolean created = sourceObject != null;
		ShardId shardId = new ShardId(index, indexUUID, shartIdInt);
		IndexResponse indexResponse = new IndexResponse(shardId, type, id, seqNo, primaryTerm, version, created);
		return indexResponse;
	}

	public DeleteResponse generateFakeDeleteResponse(String objectId) {
		String index = "index";
		String indexUUID = "UUID";
		String type = "type";
		String id = "id";
		int shartIdInt = 1;
		long seqNo = objectId != null ? 1L : -2;
		long primaryTerm = objectId != null ? 1L : 0;
		long version = 1;
		boolean found = objectId != null;
		ShardId shardId = new ShardId(index, indexUUID, shartIdInt);
		DeleteResponse deleteResponse = new DeleteResponse(shardId, type, id, seqNo, primaryTerm, version, found);
		return deleteResponse;
	}
}
