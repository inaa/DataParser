package com.sample.dataparser.db;

import com.sample.dataparser.db.entity.MatchData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchDataRepository extends JpaRepository<MatchData, Long> {
}
