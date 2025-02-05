package ovh.wpkg.lskg.db.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import ovh.wpkg.lskg.db.entities.RatInfo;
import ovh.wpkg.lskg.db.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatInfoRepository extends CrudRepository<RatInfo, Long> {
    Optional<RatInfo> findByUuid(UUID uuid);

    List<RatInfo> findByUser(User user);
}