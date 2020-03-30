package gov.dot.its.codehub.adminapi.dao;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.dot.its.codehub.adminapi.model.CHCategory;
import gov.dot.its.codehub.adminapi.model.CHConfiguration;
import gov.dot.its.codehub.adminapi.model.CHEngagementPopup;

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

		@SuppressWarnings("unchecked")
		Map<String, Object> jsonData = objectMapper.convertValue(chCategory, Map.class);
		Map<String, Object> param = new HashMap<>();
		param.put("cate", jsonData);

		String scriptCode = ""
				+ "int found_index = -1;"
				+ "for(int i=0;i<ctx._source.categories.length;i++) {"
				+ "  if(ctx._source.categories[i].id == params.cate.id){"
				+ "    found_index = i;"
				+ "    break;"
				+ "  }"
				+ "}"
				+ "if (found_index < 0) {"
				+ "  ctx._source.categories.add(params.cate);"
				+ "}";

		Script inline = new Script(ScriptType.INLINE, "painless",scriptCode, param);

		UpdateRequest updateRequest = new UpdateRequest(configurationsIndex, "_doc", configurationId);
		updateRequest.script(inline);

		UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

		return updateResponse.getResult().name();

	}

	@Override
	public String updateCategory(CHCategory chCategory) throws IOException {
		@SuppressWarnings("unchecked")
		Map<String, Object> jsonData = objectMapper.convertValue(chCategory, Map.class);
		Map<String, Object> param = new HashMap<>();
		param.put("cate", jsonData);

		String scriptCode = ""
				+ "for(int i=0;i<ctx._source.categories.length;i++) {"
				+ "  if(ctx._source.categories[i].id == params.cate.id){"
				+ "    ctx._source.categories[i] = params.cate"
				+ "  }"
				+ "}";

		Script inline = new Script(ScriptType.INLINE, "painless",scriptCode, param);

		UpdateRequest updateRequest = new UpdateRequest(configurationsIndex, "_doc", configurationId);
		updateRequest.script(inline);

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
		Map<String, Object> param = new HashMap<>();
		param.put("cateId", id);

		String scriptCode = ""
				+ "int remove_index = -1;"
				+ "for(int i=0;i<ctx._source.categories.length;i++) {"
				+ "  if(ctx._source.categories[i].id == params.cateId){"
				+ "    remove_index = i;"
				+ "    break;"
				+ "  }"
				+ "}"
				+ "if (remove_index >= 0) {"
				+ "  ctx._source.categories.remove(remove_index);"
				+ "}";

		Script inline = new Script(ScriptType.INLINE, "painless",scriptCode, param);

		UpdateRequest updateRequest = new UpdateRequest(configurationsIndex, "_doc", configurationId);
		updateRequest.script(inline);

		UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

		return updateResponse.getResult().name() != null;
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

	@Override
	public List<CHEngagementPopup> getEngagementPopups() throws IOException {
		GetRequest getRequest = new GetRequest(configurationsIndex, "_doc", configurationId);
		GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
		if (!getResponse.isExists()) {
			return new ArrayList<>();
		}

		Map<String, Object> sourceMap = getResponse.getSourceAsMap();

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		CHConfiguration configuration = mapper.convertValue(sourceMap, CHConfiguration.class);

		return configuration.getEngagementPopups();
	}

	@Override
	public String updateEngagementPopup(CHEngagementPopup engagementPopup) throws IOException {
		@SuppressWarnings("unchecked")
		Map<String, Object> jsonData = objectMapper.convertValue(engagementPopup, Map.class);
		Map<String, Object> param = new HashMap<>();
		param.put("enpo", jsonData);

		String scriptCode = ""
				+ "for(int i=0;i<ctx._source.engagementPopups.length;i++) {"
				+ "  if (params.enpo.isActive == true) {"
				+ "    if(ctx._source.engagementPopups[i].id != params.enpo.id) {"
				+ "       if(ctx._source.engagementPopups[i].isActive == true) {"
				+ "         ctx._source.engagementPopups[i].isActive = false;"
				+ "       }"
				+ "    }"
				+ "  }"
				+ "  if(ctx._source.engagementPopups[i].id == params.enpo.id){"
				+ "    ctx._source.engagementPopups[i] = params.enpo"
				+ "  }"
				+ "}";

		Script inline = new Script(ScriptType.INLINE, "painless",scriptCode, param);

		UpdateRequest updateRequest = new UpdateRequest(configurationsIndex, "_doc", configurationId);
		updateRequest.script(inline);

		UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

		return updateResponse.getResult().name();

	}

	@Override
	public String addEngagementPopup(CHEngagementPopup cdEngagementPopup) throws IOException {
		@SuppressWarnings("unchecked")
		Map<String, Object> jsonData = objectMapper.convertValue(cdEngagementPopup, Map.class);
		Map<String, Object> param = new HashMap<>();
		param.put("enpo", jsonData);

		String scriptCode = ""
				+ "int found_index = -1;"
				+ "for(int i=0;i<ctx._source.engagementPopups.length;i++) {"
				+ "  if(ctx._source.engagementPopups[i].id == params.enpo.id){"
				+ "    found_index = i;"
				+ "    break;"
				+ "  }"
				+ "}"
				+ "if (found_index < 0) {"
				+ "  ctx._source.engagementPopups.add(params.enpo);"
				+ "}";

		Script inline = new Script(ScriptType.INLINE, "painless",scriptCode, param);

		UpdateRequest updateRequest = new UpdateRequest(configurationsIndex, "_doc", configurationId);
		updateRequest.script(inline);

		UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

		return updateResponse.getResult().name();
	}

	@Override
	public Boolean removeEngagementPopup(String id) throws IOException {
		Map<String, Object> param = new HashMap<>();
		param.put("enpoId", id);

		String scriptCode = ""
				+ "int remove_index = -1;"
				+ "for(int i=0;i<ctx._source.engagementPopups.length;i++) {"
				+ "  if(ctx._source.engagementPopups[i].id == params.enpoId){"
				+ "    remove_index = i;"
				+ "    break;"
				+ "  }"
				+ "}"
				+ "if (remove_index >= 0) {"
				+ "  ctx._source.engagementPopups.remove(remove_index);"
				+ "}";

		Script inline = new Script(ScriptType.INLINE, "painless",scriptCode, param);

		UpdateRequest updateRequest = new UpdateRequest(configurationsIndex, "_doc", configurationId);
		updateRequest.script(inline);

		UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

		return updateResponse.getResult().name() != null;
	}

}
