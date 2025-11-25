package de.malkusch.ha;

import org.apache.commons.cli.*;
import org.apache.commons.cli.help.HelpFormatter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.io.IOException;

@SpringBootApplication
public class Application {

    private record CliArguments(String config) {
    }

    public static void main(String[] args) throws IOException {
        var appBuilder = new SpringApplicationBuilder(Application.class);

        var arguments = parseArguments(args);
        if (arguments.config != null) {
            appBuilder.properties("spring.config.additional-location:" + arguments.config);
        }

        var app = appBuilder.build();
        app.run(args);
    }

    private static CliArguments parseArguments(String[] args) throws IOException {
        var options = new Options();

        {
            var option = new Option("c", "config", true, "configuration file");
            option.setRequired(false);
            options.addOption(option);
        }

        var parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            var formatter = HelpFormatter.builder().get();
            formatter.printHelp("trashday.jar", null, options, null, true);

            System.exit(1);
        }

        return new CliArguments(cmd.getOptionValue("config"));
    }
}
