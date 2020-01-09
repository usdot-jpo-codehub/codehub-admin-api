package gov.dot.its.codehub.adminapi.model;

public class CHSourceData {
	private String name;
	private String repositoryUrl;
	private CHOwner owner;
	
	public CHSourceData() {
		this.owner = new CHOwner();
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public CHOwner getOwner() {
		return owner;
	}
	public void setOwner(CHOwner owner) {
		this.owner = owner;
	}
	public String getRepositoryUrl() {
		return repositoryUrl;
	}
	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

}
