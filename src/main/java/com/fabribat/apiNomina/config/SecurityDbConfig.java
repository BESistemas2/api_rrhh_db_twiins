package com.fabribat.apiNomina.config;

import java.util.HashMap;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        entityManagerFactoryRef = "securityEntityManagerFactory",
        transactionManagerRef = "securityTransactionManager",
        basePackages = {"com.fabribat.apiNomina.repositories.security"}
)
public class SecurityDbConfig {

    @Primary
    @Bean(name = "securityDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.seguridad")
    public DataSource securityDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "securityEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean securityEntityManagerFactory(
            @Qualifier("securityDataSource") DataSource securityDataSource,
            Environment env) { // <-- IMPORTANTE: Inyectamos Environment
        
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(securityDataSource);
        em.setPackagesToScan("com.fabribat.apiNomina.entities.security");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        
        HashMap<String, Object> properties = new HashMap<>();
        
        // IMPORTANTE: Leemos los valores de las propiedades dinámicamente. 
        // Si no existen (ej. en prod), usarán "none" y "MySQLDialect" por defecto.
        // En tests, usarán "create-drop" y "H2Dialect".
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto", "none"));
        properties.put("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.MySQLDialect"));
        
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Primary
    @Bean(name = "securityTransactionManager")
    public PlatformTransactionManager securityTransactionManager(
            @Qualifier("securityEntityManagerFactory") EntityManagerFactory securityEntityManagerFactory) {
        return new JpaTransactionManager(securityEntityManagerFactory);
    }
}