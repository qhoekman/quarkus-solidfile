package nl.qhoekman.solidfile.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import nl.qhoekman.solidfile.repositories.FileBucketService;
import nl.qhoekman.solidfile.repositories.UserRepository;

@Path("/api/greeting")
public class GreetingResource {
    @Inject
    UserRepository userRepository;
    @Inject
    FileBucketService fileBucketService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        long nunberOfUsers = userRepository.count();
        long numberOfFiles = fileBucketService.getNumberOfFiles();

        return "Congratulations, you have " + nunberOfUsers + " users" + " and " + numberOfFiles + " files!";
    }
}
