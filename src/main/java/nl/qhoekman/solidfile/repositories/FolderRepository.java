package nl.qhoekman.solidfile.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import nl.qhoekman.solidfile.entities.Folder;

@ApplicationScoped
public class FolderRepository implements PanacheRepository<Folder> {

  public Folder findByUserId(Long userId) {
    return find("user.id", userId).firstResult();
  }
}
