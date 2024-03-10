package nl.qhoekman.solidfile.repositories;

import java.io.InputStream;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import io.quarkus.logging.Log;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class FileBucketService {

  @Inject
  MinioClient minio;

  @Inject
  SecurityIdentity identity;

  @PostConstruct
  public void init() {
    if (identity.getPrincipal() == null) {
      Log.error("No security principal found. Bucket creation is skipped.");
      return;
    }
    String principalName = "qhoekman";
    String bucketName = getBucketName();

    try {
      // Check if the bucket already exists
      boolean isExist = minio.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
      if (!isExist) {
        // Make a new bucket
        minio.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        Log.info("Bucket created successfully for user: " + principalName);
      } else {
        Log.info("Bucket already exists for user: " + principalName);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Error creating bucket for user " + principalName + ": " + e.getMessage());
    }
  }

  public String getObject(String name) {
    try (InputStream is = minio.getObject(
        GetObjectArgs.builder()
            .bucket(getBucketName())
            .object(name)
            .build());) {
      return new String(is.readAllBytes());
    } catch (MinioException e) {
      throw new IllegalStateException(e);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public int getNumberOfFiles() {
    try {
      int count = 0;
      var objects = minio.listObjects(
          ListObjectsArgs.builder()
              .bucket(getBucketName())
              .build());
      for (var _object : objects) {
        count++;
      }
      return count;

    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public void putObject(String name, InputStream content) {
    try {
      minio.putObject(
          PutObjectArgs.builder()
              .bucket(getBucketName())
              .object(name)
              .stream(content, -1, content.available())
              .build());
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public void deleteObject(String name) {
    try {
      minio.removeObject(
          RemoveObjectArgs.builder()
              .bucket(getBucketName())
              .object(name)
              .build());
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  private String getBucketName() {
    if (identity.getPrincipal() == null) {
      throw new IllegalStateException("No security principal found");
    }
    String principalName = identity.getPrincipal().getName();
    // if identity is anonymous, use a default name
    if (identity.isAnonymous()) {
      return "public";
    }

    return "user-" + principalName;
  }
}
