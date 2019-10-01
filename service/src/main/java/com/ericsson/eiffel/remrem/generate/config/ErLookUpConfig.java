/*
    Copyright 2019 Ericsson AB.
    For a full list of individual contributors, please see the commit history.
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/
package com.ericsson.eiffel.remrem.generate.config;

import javax.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ch.qos.logback.classic.Logger;

/**
* This class is used to check whether Event-Repository lookUp is enabled or not based on property
* event-repository.enabled in property file.
*
*/

@Profile("!integration-test")
@Configuration
@Component("event-repository")
public class ErLookUpConfig {

    Logger log = (Logger) LoggerFactory.getLogger(ErLookUpConfig.class);

    @Value("${event-repository.url}")
    private String erURL;
    @Value("${event-repository.enabled}")
    private String eventRepositoryEnabled;

    private boolean eventRepositoryCheck;

    public String getErURL() {
        return erURL;
    }

    public void setErURL(String erURL) {
        this.erURL = erURL;
    }

    public String getEventRepositoryEnabled() {
        return eventRepositoryEnabled;
    }

    public void setEventRepositoryEnabled(String eventRepositoryEnabled) {
        this.eventRepositoryEnabled = eventRepositoryEnabled;
    }


    /**
    * This method is used to check whether to enable Event-Repository lookup .
    * If event-repository.enabled is false, it didn't perform lookup functionality while fetching events.
    * If event-repository.enabled is true, it sets ER URL for lookup and
    * if event-repository.enabled is not provided or mis-spelt or if event-repository.enabled = true and 
    * event-repository.url not provided , then the service gets terminated.
    *
    */
    @PostConstruct
    public void checkAndLoadEventRepositoryConfiguration() throws InterruptedException {
        if (eventRepositoryEnabled.equalsIgnoreCase("true") || eventRepositoryEnabled.equalsIgnoreCase("false")) {
            eventRepositoryCheck = Boolean.parseBoolean(eventRepositoryEnabled);
            log.info("Checking whether Event Repository configurations for lookup are enabled or not");
            if (eventRepositoryCheck) {
                if (!erURL.isEmpty()) {
                    log.info("Event Repository configurations for lookup are enabled");
                    setErURL(erURL);
                    log.info("Configured Event Repository URL for lookup : " + getErURL());
                } else {
                    log.error("Enabled Event Repository configurations for lookUp but not provided Event Repository URL");
                    throw new InterruptedException("Event Repository URL not configured");

                }
            } else {
                log.info("Event Repository configurations for lookup are not enabled");
            }
        } else {
            log.error("Please check and provide proper value for event-repository.enabled field in configuration file");
            log.info("Allowed values are either true or false");
            throw new InterruptedException("Provided incorrect values for lookup configurations");
        }
    }
}
