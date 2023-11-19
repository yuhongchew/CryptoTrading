package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import entities.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
	
	Wallet findByUserId(Long userId);
}