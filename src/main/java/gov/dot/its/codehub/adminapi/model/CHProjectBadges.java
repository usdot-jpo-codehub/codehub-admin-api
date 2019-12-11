package gov.dot.its.codehub.adminapi.model;

public class CHProjectBadges {
	private boolean featured;
	private String status;

	public boolean isFeatured() {
		return featured;
	}

	public void setFeatured(boolean featured) {
		this.featured = featured;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
