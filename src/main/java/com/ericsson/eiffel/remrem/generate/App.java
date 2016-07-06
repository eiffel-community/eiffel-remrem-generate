package com.ericsson.eiffel.remrem.generate;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.ericsson.eiffel.remrem.generate.cli.CLI;

import java.util.Arrays;

@SpringBootApplication
@ComponentScan("com.ericsson.eiffel.remrem")
public class App extends SpringBootServletInitializer {

    public static void main(String[] args) {
        
        // CLI class checks if arguments are passed to application
        // and if so we do not start the service but act based on 
        // passed arguments. If no arguments are passed the server
        // will be started
        CLI cli = new CLI();
        boolean needsStartService = cli.parse(args);

        if (needsStartService) {
            startService(args);
        }
    }
    
    private static void startService(String[] args) {
        ApplicationContext ctx = SpringApplication.run(App.class, args);

        System.out.println("Let's inspect active profiles:");
        for (String envNames : ctx.getEnvironment().getActiveProfiles()) {
            System.out.println(envNames);
        }

        System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
    }
}