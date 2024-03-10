package nl.qhoekman.solidfile.repositories;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import nl.qhoekman.solidfile.entities.File;

@ApplicationScoped
public class FileRepository implements PanacheRepository<File> {

  public List<File> findByFolderId(Long folderId) {
    return find("folder.id", folderId).list();
  }

}
