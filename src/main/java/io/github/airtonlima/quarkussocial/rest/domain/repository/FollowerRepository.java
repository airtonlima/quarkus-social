package io.github.airtonlima.quarkussocial.rest.domain.repository;

import io.github.airtonlima.quarkussocial.rest.domain.model.Follower;
import io.github.airtonlima.quarkussocial.rest.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {
    public boolean follows(User follower, User user) {
        Map<String, Object> params = Parameters.with("follower", follower).and("user", user).map();
        PanacheQuery<Follower> query = find("follower = :follower and user = :user", params);
        Optional<Follower> result = query.firstResultOptional();
        return result.isPresent();
    }
    public List<Follower> findByUser(Long userId) {
        PanacheQuery<Follower> query = find("user.id", userId);
        return query.list();
    }

    public void deleteByFollowerAndUser(Long followerId, Long userId) {
        Map<String, Object> params = Parameters
                .with("userId", userId)
                .and("followerId", followerId)
                .map();

        System.out.println(params);

        delete("user.id =: userId and follower.id =: followerId", params);
    }
}
// Fazem a mesma coisa:
// 1)  Map<String, Object> params = Parameters.with("follower", follower).and("user", user).map();
// 2)     Map<String, Object> params = new HashMap<>();
//        params.put("follower", follower);
//        params.put("user", user);