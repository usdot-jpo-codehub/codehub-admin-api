package gov.dot.its.codehub.adminapi;

import java.io.PrintStream;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class CodehubAdminApiApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(CodehubAdminApiApplication.class);
		app.setLogStartupInfo(false);

		Banner banner = new Banner() {
			static final String MESSAGE_TEMPLATE = "%s = %s";
			static final String ES_TEMPLATE = "%s = %s://%s:%s";
			@Override
			public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
				out.println(new String(new char[80]).replace("\0", "_"));
				out.println("   ******                **         **      **         **     ");
				out.println("  **    **               **         **      **         **     ");
				out.println(" **         ******       **  *****  **      ** **   ** **     ");
				out.println(" **        **    **  ****** **   ** ********** **   ** ****** ");
				out.println(" **        **    ** **   ** ******* **      ** **   ** **   **");
				out.println("  **    ** **    ** **   ** **      **      ** **   ** **   **");
				out.println("  ******   ******   ******  ****** **      **  ****** ******  ");
				out.println();
				out.println("CodeHub Admin API");
				out.println();
				out.println(String.format(MESSAGE_TEMPLATE, "Port", environment.getProperty("server.port")));
				out.println(String.format(MESSAGE_TEMPLATE, "Origins", environment.getProperty("codehub.admin.api.origins")));
				out.println(String.format(MESSAGE_TEMPLATE, "CHTOKEN", environment.getProperty("codehub.admin.api.chtoken")));
				out.println(String.format(
						ES_TEMPLATE, "Elasticsearch",
						environment.getProperty("codehub.admin.api.es.scheme"),
						environment.getProperty("codehub.admin.api.es.host"),
						environment.getProperty("codehub.admin.api.es.port")
						));
				out.println(String.format(MESSAGE_TEMPLATE, "Debug", environment.getProperty("codehub.admin.api.debug")));

				out.println(new String(new char[80]).replace("\0", "_"));
			}

		};
		app.setBanner(banner);
		app.run();
	}

}
