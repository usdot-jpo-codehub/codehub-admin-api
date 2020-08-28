package gov.dot.its.codehub.adminapi;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import gov.dot.its.codehub.adminapi.model.CHRepository;

public class MockDataRepository {
	private static final String HASH_ID_1 = "91128507a8ae9c8046c33ee0f31e37f8";

	public List<CHRepository> getFakeListOfRepositories() {
		List<CHRepository> repositories = new ArrayList<>();
		for(int i=1 ; i<=3; i++) {
			CHRepository repository = getFakeCHRepository(
					String.format("[MD5-HASH-%d]",i),
					String.format("Repository-Name-%d", i),
					String.format("Repository-Owner-%d", i),
					String.format("http://www.example.com/owner%d/repository%d", i, i)
					);
			repositories.add(repository);
		}

		return repositories;
	}

	public CHRepository getFakeCHRepository(String id, String name, String owner, String url) {
		CHRepository chRepository = new CHRepository();
		chRepository.setId(id);

		chRepository.getSourceData().setName(name);
		chRepository.getSourceData().getOwner().setName(owner);
		chRepository.getSourceData().setRepositoryUrl(url);

		chRepository.getCodehubData().setIngestionEnabled(true);
		chRepository.getCodehubData().setLastModified(new Timestamp(System.currentTimeMillis()));
		chRepository.getCodehubData().setEtag("c99aa9c9867ddb8693e7740d0ca0c00f");

		for(int i=1; i<=3; i++) {
			chRepository.getCodehubData().getCategories().add(String.valueOf(i));
		}

		return chRepository;
	}

	public CHRepository getFakeCHRepository() {
		return this.getFakeCHRepository(HASH_ID_1, "Repository-Name", "Repository-Owner", "http://www.example.com/owner/repository");
	}
}
