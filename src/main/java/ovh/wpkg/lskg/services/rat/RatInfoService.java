package ovh.wpkg.lskg.services.rat;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import ovh.wpkg.lskg.db.entities.RatInfo;
import ovh.wpkg.lskg.db.entities.User;
import ovh.wpkg.lskg.db.repository.RatInfoRepository;
import ovh.wpkg.lskg.dto.response.RatDto;
import ovh.wpkg.lskg.server.services.ConnectedRatService;

import java.util.List;
import java.util.UUID;

@Singleton
@AllArgsConstructor
public class RatInfoService {

    private final RatInfoRepository ratInfoRepository;
    private final ConnectedRatService connectedRatService;

    @Transactional
    public void registerRat(User owner, UUID uuid, String hostname, String username) {
        var ratInfo = ratInfoRepository.findByUuid(uuid);

        if (ratInfo.isEmpty()) {
            ratInfoRepository.save(new RatInfo(null, uuid, hostname, username, true, owner, null));
        }
    }

    public List<RatDto> getRatForUser(User user) {
        var rats = ratInfoRepository.findByUser(user);
        var connectedRats = connectedRatService.getRatsMap();

        return rats.stream()
                .map(it -> {
                    var connectedRat = connectedRats.get(it.getUuid());

                    return RatDto.builder()
                            .uuid(it.getUuid())
                            .username(it.getUsername())
                            .hostname(it.getHostname())
                            .connected(connectedRat != null)
                            .connectedAmount(connectedRat != null ? connectedRat.getSockets().size() : 0)
                            .os("Windows")
                            .arch("x64")
                            .coreVersion("0.0.0")
                            .build();
                }).toList();
    }

}
