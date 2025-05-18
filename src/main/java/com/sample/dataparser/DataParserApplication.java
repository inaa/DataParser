package com.sample.dataparser;

import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJms
@EnableScheduling
public class DataParserApplication {

  public static void main(String[] args) {
    SpringApplication.run(DataParserApplication.class, args);
  }

  @Bean
  public EmbeddedActiveMQ embeddedActiveMQ() throws Exception {
    Configuration config = new ConfigurationImpl()
        .setPersistenceEnabled(false)
        .setSecurityEnabled(false)
        .addAcceptorConfiguration("invm", "vm://0");

    EmbeddedActiveMQ broker = new EmbeddedActiveMQ();
    broker.setConfiguration(config);
    broker.start();
    return broker;
  }
}
