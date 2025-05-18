package com.sample.dataparser.startup;

import com.sample.dataparser.service.FileParserService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class FileParserStartupRunner {

  private final FileParserService fileParserService;

  public FileParserStartupRunner(FileParserService fileParserService) {
    this.fileParserService = fileParserService;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    fileParserService.parseAndSave();
  }
}