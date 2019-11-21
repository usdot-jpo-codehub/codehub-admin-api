package gov.dot.its.codehub.adminapi.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import gov.dot.its.codehub.adminapi.model.ApiError;


@Component
public class ApiUtils {

	private static final Logger logger = LoggerFactory.getLogger(ApiUtils.class);
	
	@Value("${codehub.admin.api.debug}")
	private boolean debug;
	
	private static final String MESSAGE_TEMPLATE = "%s : %s ";
	private static final String ERROR_LABEL = "Error";

	public String getQueryParamString(Map<String, String> params,String name, String defaultValue) {
		for(Map.Entry<String, String> entry : params.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(name)) {
				return entry.getValue();
			}
		}
		return defaultValue;
	}

	public int getQueryParamInteger(Map<String, String> params, String name, int defaultValue) {
		String val = this.getQueryParamString(params, name, null);
		if (val == null) {
			return defaultValue;
		}

		int result = -1;
		try {
			result = Integer.parseInt(val);
		} catch(NumberFormatException e) {
			result = defaultValue;
		}

		return result;
	}

	public String getMd5(String source) {
		if (source == null) {
			return null;
		}

		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5"); //NOSONAR
			messageDigest.update(source.trim().getBytes());
			byte[] bytes = messageDigest.digest();
			return DatatypeConverter.printHexBinary(bytes).toLowerCase();
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
			return null;
		} 
	}

	public Timestamp getCurrentUtc() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		String timstampStr = df.format(timestamp);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Date parsedDate;
		try {
			parsedDate = dateFormat.parse(timstampStr);
			return new Timestamp(parsedDate.getTime());
		} catch (ParseException e) {
			return timestamp;
		}
	}

	public String getTimestampFormat(Timestamp timestamp, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(timestamp);
	}

	public List<ApiError> getErrorsFromException(List<ApiError> errors, Exception e) {
		errors.add(new ApiError(String.format(MESSAGE_TEMPLATE, ERROR_LABEL, e.getMessage())));
		logger.error(String.format(MESSAGE_TEMPLATE, ERROR_LABEL, e.getMessage()));
		if (debug) {
			logger.error(String.format(MESSAGE_TEMPLATE, ERROR_LABEL, e.toString()));
		}
		if (e.getSuppressed().length > 0) {
			for (Throwable x : e.getSuppressed()) {
				errors.add(new ApiError(String.format(MESSAGE_TEMPLATE, ERROR_LABEL, x.toString())));
				logger.error(String.format(MESSAGE_TEMPLATE, ERROR_LABEL, x.toString()));
			}
		}
		return errors;
	}
}