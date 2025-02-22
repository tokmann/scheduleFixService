package app.repository;

import app.model.BadSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BadSpaceRepository extends JpaRepository<BadSpace, Long> {
    List<BadSpace> findByVictim(String victim);
}
