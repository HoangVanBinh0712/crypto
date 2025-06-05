package test.crypto.trade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CryptoTrandingProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoTrandingProjectApplication.class, args);
	}

}
