package ovh.wpkg.lskg.db.entities;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import lombok.*;

import java.time.Instant;

@MappedEntity("users")
public @Data class User {
    @Id
    @GeneratedValue(GeneratedValue.Type.AUTO)
    @EqualsAndHashCode.Exclude
    private Long id;

    private String email;
    private String passwordHash;

    @DateCreated
    private Instant dateCreated;

    public User(@NonNull String email, @NonNull String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }
}
