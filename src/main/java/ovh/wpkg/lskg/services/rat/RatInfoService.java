package ovh.wpkg.lskg.services.rat;

import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import ovh.wpkg.lskg.db.entities.RatInfo;
import ovh.wpkg.lskg.db.entities.User;
import ovh.wpkg.lskg.db.repository.RatInfoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor
public class RatInfoService {
    private final RatInfoRepository ratInfoRepository;

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
    public RatInfo shareRat(UUID uuid, User user) {
        var ratInfo = findByUuid(uuid);
        System.out.println(ratInfo);
        if (!ratInfo.orElseThrow().getSharedUsers().contains(user)) {
            ratInfo.orElseThrow().getSharedUsers().add(user);
        }

        return ratInfoRepository.update(ratInfo.orElseThrow());
    }

    @Transactional
    public void deleteById(Long id) {
        ratInfoRepository.deleteById(id);
    }
}
