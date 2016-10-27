package com.ericsson.eiffel.remrem.generate;

import java.util.List;

import org.springframework.boot.Banner;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.ericsson.eiffel.remrem.generate.cli.CLIOptions;
import com.ericsson.eiffel.remrem.generate.config.SpringLoggingInitializer;

@SpringBootApplication
@ComponentScan("com.ericsson.eiffel.remrem")
public class App extends SpringBootServletInitializer {

    public static void main(String[] args) {
        startService(args);
    }

    private static void startService(String[] args) {
        handleArgs(args);
        SpringApplication application = new SpringApplication(App.class);
        application.addInitializers(new SpringLoggingInitializer());
        application.setBannerMode(Banner.Mode.OFF);
        application.setLogStartupInfo(false);
        // We do not start web service if any arguments are passed
        // if nopnOptions argument exists parse them
        if (CLIOptions.hasParsedOptions())
            application.setWebEnvironment(false);
        ApplicationContext ctx = application.run(args);
    }

    private static void handleArgs(String[] args) {
        // We sort out Spring specific arguments and send the others to CLI
        // parser
        DefaultApplicationArguments springARgs = new DefaultApplicationArguments(args);
        List<String> nonOptions = springARgs.getNonOptionArgs();
        // We need to parse the cLI options before Spring starts since we need
        // the available when
        // Spring instantiate autowired components needing argument values
        if (!nonOptions.isEmpty())
            CLIOptions.parse(nonOptions.toArray(new String[0]));

    }
}