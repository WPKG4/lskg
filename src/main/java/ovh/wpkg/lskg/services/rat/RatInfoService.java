package ovh.wpkg.lskg.services.rat;

import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.db.entities.RatInfo;
import ovh.wpkg.lskg.db.entities.RatShares;
import ovh.wpkg.lskg.db.entities.User;
import ovh.wpkg.lskg.db.repository.RatInfoRepository;
import ovh.wpkg.lskg.db.repository.RatSharesRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class RatInfoService {
    private final RatInfoRepository ratInfoRepository;

    private final RatSharesRepository ratSharesRepository;

    public List<RatInfo> findAll() {
        return ratInfoRepository.findAll();
    }

    public Optional<RatInfo> findById(Long id) {
        return ratInfoRepository.findById(id);
    }

    public Optional<RatInfo> findByUuid(UUID uuid) {
        return ratInfoRepository.findByUuid(uuid);
    }

    @Transactional
    public RatInfo save(RatInfo ratInfo) {
        return ratInfoRepository.save(ratInfo);
    }

    @Transactional
    public RatInfo registerRat(User owner, UUID uuid, String hostname, String username) {
        return ratInfoRepository.save(new RatInfo(owner, uuid, hostname, username));
    }

    @Transactional
    public void shareRat(UUID uuid, User user) {
        var ratInfo = findByUuid(uuid).orElseThrow();

        log.info("Sharing RAT {} owned by {} to user {}", ratInfo.getUuid(), ratInfo.getOwner().getEmail(), user.getEmail());

        RatShares ratShares = new RatShares();
        ratShares.setRatInfo(ratInfo);
        ratShares.setUser(user);

        ratSharesRepository.save(ratShares);
    }

    @Transactional
    public void deleteById(Long id) {
        ratInfoRepository.deleteById(id);
    }
}
