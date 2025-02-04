package ovh.wpkg.lskg.db.entities;

import io.micronaut.data.annotation.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@ToString
@MappedEntity("tokens")
public class Token {
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
