package nl.qhoekman.solidfile.resources;

import java.util.List;

import org.jboss.resteasy.reactive.MultipartForm;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nl.qhoekman.solidfile.domain.APIResponse;
import nl.qhoekman.solidfile.domain.MultipartBody;
import nl.qhoekman.solidfile.entities.File;
import nl.qhoekman.solidfile.repositories.FileBucketService;
import nl.qhoekman.solidfile.repositories.FileRepository;

@Path("/api/files")
@RolesAllowed("user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FileResource {
  @Inject
  FileBucketService fileBucketService;

  @Inject
  FileRepository fileRepository;

  @GET
  public Response getFiles() {
    return Response.ok(new APIResponse<List<File>>(fileBucketService.getFiles())).build();
  }

  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response uploadFile(@MultipartForm MultipartBody form) {
    File file = fileBucketService.uploadFile(form);
    fileRepository.persist(file);

    return Response.status(Response.Status.CREATED).entity(
        new APIResponse<File>(file)).build();
  }

  @GET
  @Path("/{fileId}")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response getFile(@RestPath("fileId") String fileId) {
    var file = fileBucketService.getFile(fileId);
    if (file == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.ok(file).build();
  }

  @PUT
  @Path("/{fileId}")
  public Response moveFile(@RestPath("fileId") String from, @RestQuery("to") String to) {
    File file = fileBucketService.moveFile(from, to);
    return Response.ok(new APIResponse<File>(file)).build();
  }

  @DELETE
  @Path("/{fileId}")
  public Response deleteFile(@RestPath("fileId") String fileId) {
    fileBucketService.deleteFile(fileId);
    return Response.noContent().build();
  }
}
