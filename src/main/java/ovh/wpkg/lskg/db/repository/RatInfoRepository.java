package ovh.wpkg.lskg.db.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import ovh.wpkg.lskg.db.entities.RatInfo;
import ovh.wpkg.lskg.db.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface RatInfoRepository extends CrudRepository<RatInfo, Long> {
    Optional<RatInfo> findByUuid(UUID uuid);
    List<RatInfo> findByOwner(User owner);
    List<RatInfo> findBySharedUsers(User user);
}