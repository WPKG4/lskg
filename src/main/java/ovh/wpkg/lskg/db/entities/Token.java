package ovh.wpkg.lskg.db.entities;

import io.micronaut.data.annotation.*;
import lombok.*;

import java.time.Instant;

@MappedEntity("tokens")
public @Data class Token {
    @Id
    @GeneratedValue(GeneratedValue.Type.AUTO)
    @EqualsAndHashCode.Exclude
    private Long id;

    @Relation(value = Relation.Kind.MANY_TO_ONE)
    private User user;

    private String token;
    private String username;
    private boolean revoked;

    @DateCreated
    private Instant dateCreated;
}
