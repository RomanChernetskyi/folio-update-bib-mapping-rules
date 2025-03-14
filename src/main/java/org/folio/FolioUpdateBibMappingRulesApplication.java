package org.folio;

import lombok.extern.slf4j.Slf4j;
import org.folio.exception.MarcRulesNotFoundException;
import org.folio.service.UpdateMappingRulesService;
import org.folio.util.FileWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@Slf4j
@SpringBootApplication
public class FolioUpdateBibMappingRulesApplication implements CommandLineRunner {

	@Autowired
	private UpdateMappingRulesService service;

	public static void main(String[] args) {
		SpringApplication.run(FolioUpdateBibMappingRulesApplication.class, args);
	}

	public static void exitWithError(String errorMessage) {
		log.error(errorMessage);
		System.exit(1);
	}

	public static void exitWithMessage(String message) {
		log.info(message);
		System.exit(0);
	}

	@Override
	public void run(String... args) throws MarcRulesNotFoundException {
		if (args.length != 1) {
			exitWithError("Please specify all parameters: configuration.json file path");
		}

		FileWorker.configurationFile = new File(args[0]);
		service.start();
	}
}
