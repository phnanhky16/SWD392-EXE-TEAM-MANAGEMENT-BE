package com.swd.exe.teammanagement;

import com.swd.exe.teammanagement.config.DotenvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class ExeTeamManagementApplication {

	public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        DotenvLoader.loadEnv();
        SpringApplication.run(ExeTeamManagementApplication.class, args);
	}
}
