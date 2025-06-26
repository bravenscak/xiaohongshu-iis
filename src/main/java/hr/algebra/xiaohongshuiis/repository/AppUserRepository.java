
package hr.algebra.xiaohongshuiis.repository;

import hr.algebra.xiaohongshuiis.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByName(String name);
}