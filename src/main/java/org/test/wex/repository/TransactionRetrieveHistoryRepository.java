package org.test.wex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.test.wex.domain.TransactionRetrieveHistory;

@Repository
public interface TransactionRetrieveHistoryRepository extends JpaRepository<TransactionRetrieveHistory, Long> {
}