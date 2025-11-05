package com.mamoru.transactionsystem.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppConfig {
    
    private Reconciliation reconciliation = new Reconciliation();
    private Currency currency = new Currency();
    
    @Getter
    @Setter
    public static class Reconciliation {
        private String cron;
        private boolean enabled;
    }
    
    @Getter
    @Setter
    public static class Currency {
        private String defaultCurrency = "USD";
    }
}

