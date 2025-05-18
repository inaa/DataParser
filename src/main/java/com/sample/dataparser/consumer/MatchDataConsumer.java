package com.sample.dataparser.consumer;


import com.sample.dataparser.db.MatchDataRepository;
import com.sample.dataparser.db.entity.MatchData;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MatchDataConsumer {

  private final MatchDataRepository repository;
  private final Map<Integer, PriorityQueue<MatchData>> matchBuffers = new ConcurrentHashMap<>();
  private final Map<Integer, Instant> lastUpdateTime = new ConcurrentHashMap<>();


  public MatchDataConsumer(MatchDataRepository repository) {
    this.repository = repository;
  }

  @JmsListener(destination = "match.data.queue_end")
  public void receiveEnd(String text) {
    if (text.equals("end")) {
      for (Integer matchId : new ArrayList<>(matchBuffers.keySet())) {
        flush(matchId);
      }
    }
  }

  @JmsListener(destination = "match.data.queue")
  public void receive(MatchData data) {
    matchBuffers
        .computeIfAbsent(data.getMatchId(), k -> new PriorityQueue<>(
                Comparator.comparing(MatchData::getMarketId)
                    .thenComparing(MatchData::getOutcomeId, Comparator.nullsFirst(Comparator.naturalOrder()))
                    .thenComparing(MatchData::getSpecifiers, Comparator.nullsFirst(Comparator.naturalOrder()))
            )
        )
        .add(data);
    lastUpdateTime.put(data.getMatchId(), Instant.now());

    if (matchBuffers.get(data.getMatchId()).size() >= 10000) {
      flush(data.getMatchId());
    }
  }

  @Scheduled(fixedDelay = 10000)
  public void flushIdleBuffers() {
    Instant now = Instant.now();

    for (Integer matchId : new ArrayList<>(matchBuffers.keySet())) {
      Instant lastUpdate = lastUpdateTime.get(matchId);
      if (lastUpdate != null && Duration.between(lastUpdate, now).toMillis() > 120000) {
        flush(matchId);
      }
    }
  }

  private void flush(Integer matchId) {
    List<MatchData> sorted = new ArrayList<>();
    PriorityQueue<MatchData> queue = matchBuffers.get(matchId);

    while (queue != null && !queue.isEmpty()) {
      MatchData next = queue.poll();
      next.setDateInsert(LocalDateTime.now());
      sorted.add(next);
    }

    repository.saveAll(sorted);
    matchBuffers.remove(matchId);
  }

}
