package gov.dot.its.codehub.adminapi.model;

import java.sql.Timestamp;

@SuppressWarnings({"squid:S00100","squid:S00116","squid:S00117"})
public class CHRepository {
	private String id;
	private String name;
	private String owner;
	private String repo;
	private String url;
	private String etag;
	private String source;
	private Timestamp last_modified;
	private Timestamp last_ingested;
	private boolean enabled;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getRepo() {
		return repo;
	}
	public void setRepo(String repo) {
		this.repo = repo;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getEtag() {
		return etag;
	}
	public void setEtag(String etag) {
		this.etag = etag;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public Timestamp getLast_modified() {
		return last_modified;
	}
	public void setLast_modified(Timestamp last_modified) {
		this.last_modified = last_modified;
	}
	public Timestamp getLast_ingested() {
		return last_ingested;
	}
	public void setLast_ingested(Timestamp last_ingested) {
		this.last_ingested = last_ingested;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
