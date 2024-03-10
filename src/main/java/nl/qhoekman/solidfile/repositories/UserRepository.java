package nl.qhoekman.solidfile.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import nl.qhoekman.solidfile.entities.User;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

  public User findByExternalUserId(String externalUserId) {
    return find("externalUserId", externalUserId).firstResult();
  }
}
