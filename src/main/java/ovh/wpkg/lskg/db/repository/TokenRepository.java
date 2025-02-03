package ovh.wpkg.lskg.db.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import ovh.wpkg.lskg.db.entities.Token;

import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface TokenRepository extends CrudRepository<Token, Long> {
    Optional<Token> findByToken(String token);
}