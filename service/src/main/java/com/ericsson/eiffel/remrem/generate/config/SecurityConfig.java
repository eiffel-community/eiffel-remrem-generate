/*
    Copyright 2018 Ericsson AB.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.ldap.LdapBindAuthenticationManagerFactory;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.web.SecurityFilterChain;

import com.ericsson.eiffel.remrem.generate.controller.RemremGenerateController;

/**
 * This class is used to enable the ldap authentication based on property
 * activedirectory.generate.enabled=true in property file.
 *
 */
@Profile("!integration-test")
@Configuration
@ConditionalOnProperty(value = "activedirectory.generate.enabled")
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${activedirectory.ldapUrl}")
    private String ldapUrl;

    @Value("${jasypt.encryptor.jasyptKeyFilePath:{null}}")
    private String jasyptKeyFilePath;

    @Value("${activedirectory.managerPassword}")
    private String managerPassword;

    @Value("${activedirectory.managerDn}")
    private String managerDn;

    @Value("${activedirectory.userSearchFilter}")
    private String userSearchFilter;

    @Value("${activedirectory.rootDn}")
    private String rootDn;

    @Bean
    public DefaultSpringSecurityContextSource contextSource() {
        final String jasyptKey = RemremGenerateController.readJasyptKeyFile(jasyptKeyFilePath);
        if (managerPassword.startsWith("{ENC(") && managerPassword.endsWith("}")) {
            managerPassword = DecryptionUtils.decryptString(managerPassword.substring(1, managerPassword.length() - 1), jasyptKey);
        }
        LOGGER.debug("LDAP server url: " + ldapUrl);
        DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(ldapUrl);
        contextSource.setUserDn(managerDn);
        contextSource.setPassword(managerPassword);
        contextSource.setBase(rootDn);
        return contextSource;
    }

    @Bean
    public AuthenticationManager authenticationManager(BaseLdapPathContextSource contextSource) {
        LdapBindAuthenticationManagerFactory factory = new LdapBindAuthenticationManagerFactory(contextSource);
        factory.setUserSearchFilter(userSearchFilter);
        return factory.createAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        LOGGER.debug("LDAP authentication enabled");
        http.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
            .httpBasic(httpBasic -> {})
            .csrf(csrf -> csrf.disable());
        return http.build();
    }

}
