package de.malkusch.ha;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class Application {

    private static record CliArguments(String config) {
    };

    public static void main(String[] args) {
        var appBuilder = new SpringApplicationBuilder(Application.class);

        var arguments = parseArguments(args);
        if (arguments.config != null) {
            appBuilder.properties("spring.config.additional-location:" + arguments.config);
        }

        var app = appBuilder.build();
        app.run(args);
    }

    private static CliArguments parseArguments(String[] args) {
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
            var formatter = new HelpFormatter();
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

        return new CliArguments(cmd.getOptionValue("config"));
    }
}
