package gov.dot.its.codehub.adminapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CHBadges {
	private boolean isFeatured;
	private String status;

	@JsonProperty("isFeatured")
	public boolean isFeatured() {
		return isFeatured;
	}
	public void setFeatured(boolean isFeatured) {
		this.isFeatured = isFeatured;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
