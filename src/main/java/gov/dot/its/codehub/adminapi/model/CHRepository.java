package gov.dot.its.codehub.adminapi.model;

@SuppressWarnings({"squid:S00100","squid:S00116","squid:S00117"})
public class CHRepository {
	private String id;
	private CHSourceData sourceData;
	private CHGeneratedData generatedData;
	private CHCodehubData codehubData;

	public CHRepository() {
		this.sourceData = new CHSourceData();
		this.generatedData = new CHGeneratedData();
		this.codehubData = new CHCodehubData();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public CHSourceData getSourceData() {
		return sourceData;
	}

	public void setSourceData(CHSourceData sourceData) {
		this.sourceData = sourceData;
	}

	public CHGeneratedData getGeneratedData() {
		return generatedData;
	}

	public void setGeneratedData(CHGeneratedData generatedData) {
		this.generatedData = generatedData;
	}

	public CHCodehubData getCodehubData() {
		return codehubData;
	}

	public void setCodehubData(CHCodehubData codehubData) {
		this.codehubData = codehubData;
	}

}
