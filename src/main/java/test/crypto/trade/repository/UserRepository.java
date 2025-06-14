package test.crypto.trade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import test.crypto.trade.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
