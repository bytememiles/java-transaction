package com.mamoru.transactionsystem.reconciliation.application;

import com.mamoru.transactionsystem.common.config.AppConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "app.reconciliation.enabled", havingValue = "true", matchIfMissing = true)
public class ReconciliationScheduler {
    
    private final ReconciliationService reconciliationService;
    private final AppConfig appConfig;
    
    /**
     * Scheduled job that runs daily to reconcile merchant accounts.
     * Default schedule: Daily at 2 AM (configured in application.yml)
     */
    @Scheduled(cron = "${app.reconciliation.cron:0 0 2 * * *}")
    public void runDailyReconciliation() {
        log.info("Starting scheduled daily reconciliation job");
        
        try {
            reconciliationService.reconcileAllMerchantsForYesterday();
            log.info("Scheduled daily reconciliation completed successfully");
        } catch (Exception e) {
            log.error("Error during scheduled daily reconciliation", e);
        }
    }
}

