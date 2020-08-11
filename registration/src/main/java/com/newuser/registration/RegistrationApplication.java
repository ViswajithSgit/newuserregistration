package com.newuser.registration;

import com.newuser.registration.config.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ FileStorageProperties.class })
public class RegistrationApplication {

	public static void main(String[] args) {

		SpringApplication.run(RegistrationApplication.class, args);
		System.out.println("Welcome to New User Registration");
	}

}
