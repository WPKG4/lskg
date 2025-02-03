package ovh.wpkg.lskg.db.entities;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import lombok.*;

import java.time.Instant;

@MappedEntity("users")
public @Data class Token {
    @Id
    @GeneratedValue(GeneratedValue.Type.AUTO)
    @EqualsAndHashCode.Exclude
    private Long id;

    private String token;
    private String username;
    private boolean revoked;

    @DateCreated
    private Instant dateCreated;
}
