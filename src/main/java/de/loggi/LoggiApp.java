package de.loggi;

import de.loggi.model.Parameter;
import de.loggi.service.ConfigurationService;
import de.loggi.service.HelpService;
import de.loggi.service.WriteService;
import de.loggi.service.impl.ReadServiceImpl;
import org.apache.commons.cli.*;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.io.IOException;

/**
 * Main application starter
 */
public class LoggiApp {

    static final Logger logger = LoggerFactory.getLogger(LoggiApp.class);
    CommandLine commandLine;
    Options options;
    ApplicationContext context;
    private HelpService help;

    public static void main(String[] args) {
        LoggiApp app = new LoggiApp();
        app.initializeContext();

        try {
            app.initializeOptions(args);
            app.start();
        } catch (ParseException e) {
            logger.error("Error initializing options", e);
            app.printUsage();
        }
    }

    private void printUsage() {
        help.printUsage(options);
    }

    /*
     * All the magic goes here
     */
    private void start() {
        // TODO implement cli option to test/validate template file

        try {
            if (commandLine.hasOption(Parameter.HELP.getShortName())) {
                help.printHelp(options);
                System.exit(0);
            }

            if (!commandLine.hasOption(Parameter.SOURCE.getShortName())) {
                help.printUsage(options);
                System.exit(0);
            }

            String templateFileName = commandLine.hasOption(Parameter.TEMPLATE.getShortName()) ? commandLine.getOptionValue(Parameter.TEMPLATE.getShortName()) : "template.json";

            WriteService writeService = (WriteService) context.getBean("writeService");
            ConfigurationService configurationService = (ConfigurationService) context.getBean("configurationService");
            configurationService.setCommandLine(commandLine);
            configurationService.initialize(templateFileName);
            configurationService.setSource(commandLine.getOptionValue(Parameter.SOURCE.getShortName()));
            writeService.initialize();

            logger.debug("Using configuration template: \n {}", configurationService.currentConfigToString());

            ReadServiceImpl readService = (ReadServiceImpl) context.getBean("readService");
            readService.process();

            pauseUntilKeypressed();
            writeService.finalizeAndShutdown();

        } catch (Exception ex) {
            logger.error("Exception initializing Configuration Service", ex);
        }

    }

    private void pauseUntilKeypressed() {
        try {
            System.out.println("Go to http://localhost:8082 or press Enter to quit..."); // TODO remove this crappy hardcode later
            System.in.read();
        } catch (IOException e) {
            // oops, nothing here
        }
    }

    /**
     * Initialize logger and Spring context
     */
    public void initializeContext() {
        DOMConfigurator.configure(LoggiApp.class.getClassLoader().getResource("log4j.xml"));
        context = new GenericXmlApplicationContext("classpath:sp-loggi.xml");
        help = (HelpService) context.getBean("helpService");
    }

    public void initializeOptions(String[] args) throws ParseException {
        CommandLineParser parser = new GnuParser();

        this.options = new Options();

        for (Parameter param : Parameter.values()) {
            OptionBuilder builder = OptionBuilder
                    .withDescription(param.getDescription())
                    .isRequired(param.isRequired());

            if (param.isHasArgument()) {
                builder.hasArg();
                builder.withArgName(param.getLongName() + "Arg");
            }
            Option option = builder.create(param.getShortName());
            if(!param.getLongName().isEmpty()){
            option.setLongOpt(param.getLongName());
            }
            options.addOption(option);
        }

        this.commandLine = parser.parse(options, args);
    }


}