package ovh.wpkg.lskg.db.entities;

import io.micronaut.data.annotation.*;
import io.micronaut.data.annotation.sql.JoinColumn;
import lombok.*;

@MappedEntity("rat_shares")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RatShares {
    @Id
    @GeneratedValue(GeneratedValue.Type.AUTO)
    @EqualsAndHashCode.Exclude
    private Long id;

    @Relation(Relation.Kind.MANY_TO_ONE)
    @JoinColumn(name = "user_id")
    private User user;

    @Relation(Relation.Kind.MANY_TO_ONE)
    @JoinColumn(name = "rat_info")
    private RatInfo ratInfo;
}
