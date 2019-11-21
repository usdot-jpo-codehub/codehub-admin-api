package gov.dot.its.codehub.adminapi.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HeaderUtils {

	@Value("${codehub.admin.api.chtoken}")
	private String chToken;

	public boolean validateToken(String value) {
		return value != null && chToken.compareTo(value) == 0;
	}
}
