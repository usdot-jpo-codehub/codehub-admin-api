package gov.dot.its.codehub.adminapi.dao;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.dot.its.codehub.adminapi.model.CHCategory;
import gov.dot.its.codehub.adminapi.model.CHConfiguration;

@Repository
public class ConfigurationDaoImpl implements ConfigurationDao {

	@Value("${codehub.admin.api.configurations.index}")
	private String configurationsIndex;

	@Value("${codehub.admin.api.configurations.default}")
	private String configurationId;

	@Value("${codehub.admin.api.configurations.images.list}")
	private String imagesList;

	@Value("${codehub.admin.api.configurations.images.path}")
	private String imagesPath;

	@Autowired
	private ObjectMapper objectMapper;

	private RestHighLevelClient restHighLevelClient;

	public ConfigurationDaoImpl(RestHighLevelClient restHighLevelClient) {
		this.restHighLevelClient = restHighLevelClient;
	}

	@Override
	public CHConfiguration getConfiguration() throws IOException {
		GetRequest getRequest = new GetRequest(configurationsIndex, "_doc", configurationId);
		GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
		if (!getResponse.isExists()) {
			return null;
		}

		Map<String, Object> sourceMap = getResponse.getSourceAsMap();

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		return mapper.convertValue(sourceMap, CHConfiguration.class);
	}

	@Override
	public List<CHCategory> getCategories() throws IOException {
		GetRequest getRequest = new GetRequest(configurationsIndex, "_doc", configurationId);
		GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
		if (!getResponse.isExists()) {
			return new ArrayList<>();
		}

		Map<String, Object> sourceMap = getResponse.getSourceAsMap();

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		CHConfiguration configuration = mapper.convertValue(sourceMap, CHConfiguration.class);

		return configuration.getCategories();

	}

	@Override
	public String addCategory(CHCategory chCategory) throws IOException {

		CHConfiguration configuration = getConfiguration();
		if (configuration == null) {
			return null;
		}

		configuration.getCategories().add(chCategory);

		@SuppressWarnings("unchecked")
		Map<String, Object> map = objectMapper.convertValue(configuration, Map.class);

		UpdateRequest updateRequest = new UpdateRequest(configurationsIndex, "_doc", configurationId);
		updateRequest.doc(map, XContentType.JSON);

		UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

		return updateResponse.getResult().name();

	}

	@Override
	public String updateCategory(CHCategory chCategory) throws IOException {
		CHConfiguration configuration = getConfiguration();
		if (configuration == null) {
			return null;
		}
		boolean found = false;
		List<CHCategory> categories = configuration.getCategories();
		for(int i=0; i<categories.size(); i++) {
			if (categories.get(i).getId().equalsIgnoreCase(chCategory.getId())) {
				categories.set(i, chCategory);
				found = true;
			}
		}

		if (!found) {
			return null;
		}

		@SuppressWarnings("unchecked")
		Map<String, Object> map = objectMapper.convertValue(configuration, Map.class);

		UpdateRequest updateRequest = new UpdateRequest(configurationsIndex, "_doc", configurationId);
		updateRequest.doc(map, XContentType.JSON);

		UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

		return updateResponse.getResult().name();
	}

	@Override
	public CHCategory getCategoryById(String id) throws IOException {
		CHConfiguration configuration = getConfiguration();
		if (configuration == null) {
			return null;
		}

		for(CHCategory category: configuration.getCategories()) {
			if (category.getId().equalsIgnoreCase(id)) {
				return category;
			}
		}

		return null;
	}

	@Override
	public Boolean deleteCategoryById(String id) throws IOException {
		CHConfiguration configuration = getConfiguration();
		if (configuration == null) {
			return false;
		}
		int removeIndex = -1;
		List<CHCategory> categories = configuration.getCategories();
		for(int i=0; i<categories.size(); i++) {
			if (categories.get(i).getId().equalsIgnoreCase(id)) {
				removeIndex = i;
				break;
			}
		}

		if (removeIndex < 0) {
			return false;
		}

		categories.remove(removeIndex);

		@SuppressWarnings("unchecked")
		Map<String, Object> map = objectMapper.convertValue(configuration, Map.class);

		UpdateRequest updateRequest = new UpdateRequest(configurationsIndex, "_doc", configurationId);
		updateRequest.doc(map, XContentType.JSON);

		restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

		return true;
	}

	@Override
	public List<String> getCategoryImages() throws IOException {
		ObjectMapper mapper = new ObjectMapper();

		TypeReference<List<String>> typeRef= new TypeReference<List<String>>() {};
		List<String> images = mapper.readValue(new URL(this.imagesList), typeRef);
		for(int i=0; i<images.size(); i++) {
			images.set(i, String.format("%s/%s", this.imagesPath, images.get(i)));
		}
		return images;
	}

}
