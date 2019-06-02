
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Signing;

@Repository
public interface SigningRepository extends JpaRepository<Signing, Integer> {

	@Query("select s from Signing s where s.president.id=?1 and s.player.id=?2 and s.status = 'ACCEPTED'")
	Signing findSigningOfPresidentAndPlayer(int presidentId, int playerId);

	@Query("Select s from Signing s where s.player.id = ?1 and s.status = 'PENDING' and s.player.team = null")
	Collection<Signing> findByPlayerId(int id);

	@Query("Select s from Signing s where s.player.id = ?1")
	Collection<Signing> findAllByPlayerId(int id);

	@Query("Select s from Signing s join s.player p where p.team.id = ?1 and s.status = 'PENDING'")
	Collection<Signing> findByTeamId(int id);

	@Query("Select s from Signing s where s.president.id = ?1 group by s.player")
	Collection<Signing> findAllByTeamId(int id);

}
