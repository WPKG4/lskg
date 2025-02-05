package ovh.wpkg.lskg.db.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rat_clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RatInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Column(nullable = false)
    private String hostname;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "rat_shares",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "rat_info_id"))
    private List<User> sharedToUsers;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant dateCreated;
}
