package ovh.wpkg.lskg.db.entities;

import io.micronaut.data.annotation.*;
import io.micronaut.data.annotation.sql.JoinColumn;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@MappedEntity("ratclients")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RatInfo {
    @Id
    @GeneratedValue(GeneratedValue.Type.AUTO)
    @EqualsAndHashCode.Exclude
    private Long id;

    @Relation(value = Relation.Kind.MANY_TO_ONE)
    @JoinColumn(name = "owner_id")
    private User owner;

    @NonNull
    private UUID uuid;

    @NonNull
    private String hostname;

    @NonNull
    private String username;

    private boolean active;

    @DateCreated
    private Instant dateCreated;

    public RatInfo(@NonNull User owner, @NonNull UUID uuid, @NonNull String hostname, @NonNull String username) {
        this.owner = owner;
        this.uuid = uuid;
        this.hostname = hostname;
        this.username = username;
        this.active = true;
    }
}