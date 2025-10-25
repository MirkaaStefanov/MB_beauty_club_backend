package com.example.MB_beauty_club_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MbBeautyClubBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MbBeautyClubBackendApplication.class, args);
	}

}
