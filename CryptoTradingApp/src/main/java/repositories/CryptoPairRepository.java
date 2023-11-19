package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import entities.CryptoPair;

public interface CryptoPairRepository extends JpaRepository<CryptoPair, Long> {
	java.util.List<CryptoPair> findAll();
}