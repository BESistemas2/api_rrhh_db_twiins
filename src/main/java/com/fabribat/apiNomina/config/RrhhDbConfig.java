package com.fabribat.apiNomina.config;

import java.util.HashMap;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment; // <-- IMPORTANTE: Nuevo import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "rrhhEntityManagerFactory",
        transactionManagerRef = "rrhhTransactionManager",
        basePackages = {"com.fabribat.apiNomina.repositories.rrhh"}
)
public class RrhhDbConfig {

    @Bean(name = "rrhhDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.rrhh")
    public DataSource rrhhDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "rrhhEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean rrhhEntityManagerFactory(
            @Qualifier("rrhhDataSource") DataSource rrhhDataSource,
            Environment env) { // <-- IMPORTANTE: Inyectamos Environment
        
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(rrhhDataSource);
        em.setPackagesToScan("com.fabribat.apiNomina.entities.rrhh");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        
        HashMap<String, Object> properties = new HashMap<>();
        
        // Leemos las propiedades dinámicamente desde el application.properties o application-test.properties
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto", "none"));
        properties.put("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.MySQLDialect"));
        
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean(name = "rrhhTransactionManager")
    public PlatformTransactionManager rrhhTransactionManager(
            @Qualifier("rrhhEntityManagerFactory") EntityManagerFactory rrhhEntityManagerFactory) {
        return new JpaTransactionManager(rrhhEntityManagerFactory);
    }
}