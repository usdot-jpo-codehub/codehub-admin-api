package gov.dot.its.codehub.adminapi.model;

@SuppressWarnings({"squid:S00100","squid:S00116","squid:S00117"})
public class CHProject {
	private String id;
	private String repository_url;
	private CHProjectBadges badges;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRepository_url() {
		return repository_url;
	}
	public void setRepository_url(String repository_url) {
		this.repository_url = repository_url;
	}
	public CHProjectBadges getBadges() {
		return badges;
	}
	public void setBadges(CHProjectBadges badges) {
		this.badges = badges;
	}

}
