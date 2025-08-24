package com.example.loyalty.common.database;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Base repository interface with common operations Provides standard database operations with audit
 * fields
 */
@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {

  /** Find entities created between date range */
  List<T> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

  /** Find entities updated after specified date */
  List<T> findByUpdatedAtAfter(LocalDateTime date);

  /** Find active entities (if entity supports soft delete) */
  default List<T> findActive() {
    return findAll();
  }

  /** Soft delete (if entity supports it) */
  default void softDelete(ID id) {
    deleteById(id);
  }
}
