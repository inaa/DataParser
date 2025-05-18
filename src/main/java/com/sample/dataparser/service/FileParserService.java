package com.sample.dataparser.service;

import com.sample.dataparser.db.MatchDataRepository;
import com.sample.dataparser.db.entity.MatchData;
import java.time.LocalDateTime;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class FileParserService {

  private final MatchDataRepository repository;
  private final JmsTemplate jmsTemplate;

  public FileParserService(MatchDataRepository repository, JmsTemplate jmsTemplate) {
    this.repository = repository;
    this.jmsTemplate = jmsTemplate;
  }

  public void parseAndSave() {
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(getClass().getResourceAsStream("/fo_random.txt")))) {
      reader.lines()
          .filter(line -> !line.isBlank())
          .skip(1)
          .map(this::mapToEntity)
          .forEach(each ->
            {
              jmsTemplate.convertAndSend("match.data.queue", each);
              System.out.println(each);
            });
      jmsTemplate.convertAndSend("match.data.queue_end", "end");
    } catch (Exception e) {
    }
  }

  private MatchData mapToEntity(String line) {
    String[] parts = line.split("\\|");

    if (parts.length < 3) {
      throw new IllegalArgumentException("Invalid line: " + line);
    }

    MatchData data = new MatchData();
    String rawMatchId = parts[0].replace("'", "").trim();
    String[] matchParts = rawMatchId.split(":");
    data.setMatchId(Integer.parseInt(matchParts[2]));
    data.setMarketId(Integer.parseInt(parts[1].replace("'", "").trim()));
    data.setOutcomeId(parts[2].replace("'", "").trim());
    if (parts.length >= 4) {
      data.setSpecifiers(parts[3].replace("'", "").trim());
    } else {
      data.setSpecifiers("");
    }
    data.setDateInsert(LocalDateTime.now());
    return data;
  }
}
