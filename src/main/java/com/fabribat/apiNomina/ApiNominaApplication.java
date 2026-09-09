package com.fabribat.apiNomina;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling //Activa el motor de Cron Jobs
public class ApiNominaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiNominaApplication.class, args);
    }
}