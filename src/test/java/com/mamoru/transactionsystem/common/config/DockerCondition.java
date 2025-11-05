package com.mamoru.transactionsystem.common.config;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.DockerClientFactory;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility class to check if Docker is available for Testcontainers.
 */
@Slf4j
public class DockerCondition {
    
    /**
     * Checks if Docker is available for Testcontainers.
     * 
     * @return true if Docker is available, false otherwise
     */
    public static boolean isDockerAvailable() {
        try {
            // Check if Docker socket exists and is accessible
            String dockerSocket = "/var/run/docker.sock";
            if (!Files.exists(Paths.get(dockerSocket))) {
                log.debug("Docker socket not found at: {}", dockerSocket);
                return false;
            }
            
            // Try to get Docker client - this will throw an exception if Docker is not available
            DockerClientFactory.instance().client();
            return true;
        } catch (Exception e) {
            // Check if it's a permission issue
            if (e.getMessage() != null && e.getMessage().contains("permission denied")) {
                log.warn("Docker permission denied. Make sure you're in the 'docker' group and have logged out/in after being added.");
            }
            return false;
        }
    }
}

