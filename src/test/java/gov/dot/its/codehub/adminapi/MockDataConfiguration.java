package gov.dot.its.codehub.adminapi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gov.dot.its.codehub.adminapi.model.CHCategory;
import gov.dot.its.codehub.adminapi.model.CHConfiguration;
import gov.dot.its.codehub.adminapi.model.CHEngagementPopup;

public class MockDataConfiguration {

	public CHConfiguration getFakeConfiguration() {
		CHConfiguration configuration = new CHConfiguration();
		configuration.setId("default-configuration");
		configuration.setName(configuration.getId());

		List<CHCategory> categories = new ArrayList<>();
		for (int i=1; i<=3; i++) {
			CHCategory category = this.getFakeCategory(String.valueOf(i));
			categories.add(category);
		}

		configuration.setCategories(categories);

		List<CHEngagementPopup> engagementPopups = new ArrayList<>();
		for(int i=1; i<=2; i++) {
			CHEngagementPopup engagementPopup = this.getFakeEngagementPopup(String.valueOf(i));
			engagementPopups.add(engagementPopup);
		}

		configuration.setEngagementPopups(engagementPopups);

		return configuration;
	}

	public CHCategory getFakeCategory(String id) {
		CHCategory category = new CHCategory();
		category.setDescription(String.format("Description-%s", id));
		category.setEnabled(true);
		category.setId(id);
		category.setLastModified(new Date());
		category.setName(String.format("Category-%s", id));
		category.setImageFileName("http://path.to.the.image/image1.png");
		category.setOrderPopular(1L);
		category.setPopular(true);

		return category;
	}

	public CHEngagementPopup getFakeEngagementPopup(String id) {
		CHEngagementPopup engagementPopup = new CHEngagementPopup();
		engagementPopup.setId(id);
		engagementPopup.setActive(false);
		engagementPopup.setContent(String.format("<h1>This is fake %s</h1>",id));
		engagementPopup.setDescription(String.format("Description %s", id));
		engagementPopup.setLastModified(new Date());
		engagementPopup.setName(String.format("EngagementPopup-%s", id));
		return engagementPopup;
	}

	public List<CHCategory> getFakeCategories() {
		List<CHCategory> categories = new ArrayList<>();
		for(int i=1; i<=3; i++) {
			CHCategory category = this.getFakeCategory(String.valueOf(i));
			categories.add(category);
		}
		return categories;
	}

	public List<CHEngagementPopup> getFakeEngagementPopups() {
		List<CHEngagementPopup> result = new ArrayList<>();
		for(int i=1; i<=3; i++) {
			CHEngagementPopup popup = this.getFakeEngagementPopup(String.valueOf(i));
			result.add(popup);
		}
		return result;
	}

	public List<String> getFakeListOfImages() {
		List<String> images = new ArrayList<>();
		for(int i=1; i<=3; i++) {
			images.add(String.format("http://url.to.images/image%s.jpeg",i));
		}
		return images;
	}
}
