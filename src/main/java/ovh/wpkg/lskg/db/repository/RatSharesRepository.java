package ovh.wpkg.lskg.db.repository;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import ovh.wpkg.lskg.db.entities.RatShares;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
@Join("user")
@Join("ratInfo")
@Join("ratInfo.owner")
public interface RatSharesRepository extends CrudRepository<RatShares, Long> {
    List<RatShares> findByRatInfoUuid(UUID uuid);
}
