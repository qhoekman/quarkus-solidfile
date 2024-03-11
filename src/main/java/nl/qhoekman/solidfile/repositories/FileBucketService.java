package nl.qhoekman.solidfile.repositories;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.minio.BucketExistsArgs;
import io.minio.CopySource;
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
import nl.qhoekman.solidfile.domain.MultipartBody;
import nl.qhoekman.solidfile.entities.File;

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

  public byte[] getFile(String name) {
    try (InputStream is = minio.getObject(
        GetObjectArgs.builder()
            .bucket(getBucketName())
            .object(name)
            .build());) {
      return is.readAllBytes();
    } catch (MinioException e) {
      throw new IllegalStateException(e);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public List<File> getFiles() {
    try {
      List<File> files = new ArrayList<File>();
      // get all files from the bucket and subfolders
      var results = minio.listObjects(
          ListObjectsArgs.builder()
              .bucket(getBucketName())
              .build());
      for (var _object : results) {
        // if the object is a directory, list all files in the directory
        if (_object.get().isDir()) {
          var subResults = minio.listObjects(
              ListObjectsArgs.builder()
                  .bucket(getBucketName())
                  .prefix(_object.get().objectName())
                  .recursive(true)
                  .build());
          for (var subObject : subResults) {
            File subFile = new File();
            subFile.setName(subObject.get().objectName());
            subFile.setSize(subObject.get().size());
            subFile.setBucket(getBucketName());
            subFile.setUpdatedAt(subObject.get().lastModified());
            files.add(subFile);
          }
          // if the object is a file, add it to the list
        } else {
          File file = new File();
          file.setName(_object.get().objectName());
          file.setBucket(getBucketName());
          file.setSize(_object.get().size());
          file.setUpdatedAt(_object.get().lastModified());

          files.add(file);
        }

      }

      return files;
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
              .recursive(true)
              .build());
      for (var _object : objects) {
        count++;
      }
      return count;

    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public File uploadFile(MultipartBody input) {
    try {
      String fileName = input.getName();
      byte[] fileBytes = input.getBytes();
      InputStream is = new InputStream() {
        int index = 0;

        @Override
        public int read() {
          if (index == fileBytes.length) {
            return -1;
          }
          return fileBytes[index++];
        }
      };

      // upload the file to the bucket
      minio.putObject(
          PutObjectArgs.builder()
              .bucket(getBucketName())
              .object(fileName)
              .stream(is, -1, 10485760)
              .build());
      File file = new File();
      file.setName(fileName);
      file.setBucket(getBucketName());
      file.setSize((long) fileBytes.length);
      file.setUpdatedAt(java.time.ZonedDateTime.now());

      return file;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public File moveFile(String from, String to) {
    try {
      CopySource source = CopySource.builder()
          .bucket(getBucketName())
          .object(from)
          .build();
      minio.copyObject(
          io.minio.CopyObjectArgs.builder()
              .source(source)
              .bucket(getBucketName())
              .object(to)
              .build());
      minio.removeObject(
          RemoveObjectArgs.builder()
              .bucket(getBucketName())
              .object(from)
              .build());
      File file = new File();
      file.setName(to);
      file.setBucket(getBucketName());
      file.setSize(source.length());
      file.setUpdatedAt(java.time.ZonedDateTime.now());
      return file;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public void deleteFile(String name) {
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
