package ovh.wpkg.lskg.db.entities;

import io.micronaut.data.annotation.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@ToString
@MappedEntity("users")
public class User {
    @Id
    @GeneratedValue(GeneratedValue.Type.AUTO)
    @EqualsAndHashCode.Exclude
    private Long id;

    private String email;
    private String passwordHash;

    @DateCreated
    private Instant dateCreated;

    @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "owner")
    private List<RatInfo> ownedRats;

    public User(@NonNull String email, @NonNull String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }
}
