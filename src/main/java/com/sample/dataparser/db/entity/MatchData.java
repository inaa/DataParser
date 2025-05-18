package com.sample.dataparser.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Data
public class MatchData implements java.io.Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private int matchId;
  private int marketId;
  private String outcomeId;
  @Column(length = 1024)
  private String specifiers;
  private LocalDateTime dateInsert;

}
