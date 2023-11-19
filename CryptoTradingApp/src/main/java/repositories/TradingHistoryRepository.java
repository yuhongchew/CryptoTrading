package repositories;

import java.math.BigDecimal;

import org.hibernate.mapping.List;
import org.springframework.data.jpa.repository.JpaRepository;
import entities.TradingHistory;

public interface TradingHistoryRepository extends JpaRepository<TradingHistory, Long> {

	List findByUserId(Long userId);

	BigDecimal getTotalQuantityForUserAndPair(Long userId, Long pairId);
}