package cz.larpovadatabaze.common.services.s3;

import org.apache.log4j.Logger;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.time.Time;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Representation of S3Bucket and associated operations. If the constructor succeeds the bucket will exist. Either
 * it already exists or it is created.
 */
public class S3Bucket implements Serializable {
    private final static Logger logger = Logger.getLogger(S3Bucket.class);
    private LoadableDetachableModel<S3Client> client;
    private String bucketName;

    public S3Bucket(String bucketName) {
        this.client = new LoadableDetachableModel<>() {
            @Override
            protected S3Client load() {
                return S3Client.builder()
                        .region(Region.EU_CENTRAL_1)
                        .build();
            }
        };
        this.bucketName = bucketName;

        if (!exists()) {
            try {
                create();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Verifies whether bucket with given name exists in given S3Client.
     *
     * @return True if exists, false otherwise.
     */
    private boolean exists() {
        List<Bucket> availableBuckets = client.getObject().listBuckets().buckets()
                .stream()
                .filter(bucket -> bucket.name().equals(bucketName))
                .collect(Collectors.toList());
        return availableBuckets.size() > 0;
    }

    /**
     * It creates a Bucket with given name. Used as a part of construction.
     */
    private void create() throws IOException {
        CreateBucketResponse response = client.getObject().createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
        if (!response.sdkHttpResponse().isSuccessful()) {
            throw new IOException("Can't create the Bucket with name: " + bucketName);
        }
    }

    /**
     * Delete Bucket with everything present.
     */
    public void delete() throws IOException {
        ListObjectsResponse allObjectsResponse = client.getObject().listObjects(ListObjectsRequest.builder().bucket(bucketName).build());
        allObjectsResponse.contents()
                .forEach(object -> removeObject(object.key()));
        DeleteBucketResponse response = client.getObject().deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build());
        if (!response.sdkHttpResponse().isSuccessful()) {
            throw new IOException("Can't delete bucket with name: " + bucketName);
        }
    }

    /**
     * Upload a file under specific key to this S3 Bucket.
     *
     * @param key      Key for storage and retrieval of the file
     * @param toUpload InputStream representing the file to upload.
     * @throws IOException Either there is problem with the InputStream
     */
    public void upload(String key, InputStream toUpload) throws IOException {
        PutObjectResponse response = client.getObject().putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build(), RequestBody.fromInputStream(toUpload, toUpload.available()));
        if (!response.sdkHttpResponse().isSuccessful()) {
            throw new IOException("Wasn't possible to upload file " + key);
        }
    }

    /**
     * Remove object from this bucket.
     *
     * @param key Key of the file to remove
     */
    public void removeObject(String key) {
        DeleteObjectResponse response = client.getObject().deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
        if (!response.sdkHttpResponse().isSuccessful()) {
            throw new RuntimeException("Wasn't possible to remove file " + key);
        }
    }

    /**
     * Returns whether object exists in this bucket.
     *
     * @param key Key of the file to check for.
     * @return True if the object exists
     */
    public boolean existsObject(String key) {
        List<S3Object> foundObjectsWithPath = client.getObject().listObjects(ListObjectsRequest.builder().bucket(bucketName).build())
                .contents()
                .stream()
                .filter(s3Object -> {
                    logger.info("Key of the object in bucket: " + s3Object.key() + " Key: " + key);
                    return s3Object.key().equals(key);
                })
                .collect(Collectors.toList());
        return foundObjectsWithPath.size() > 0;
    }

    /**
     * @param key Key of the object for which to load the item.
     * @return Time of update in miliseconds
     */
    public Time getLastUpdatedForObject(String key) {
        List<S3Object> foundObjectsForKey = client.getObject().listObjects(ListObjectsRequest.builder().bucket(bucketName).build())
                .contents()
                .stream()
                .filter(s3Object -> s3Object.key().equals(key))
                .collect(Collectors.toList());
        if (foundObjectsForKey.size() == 0) {
            throw new RuntimeException("File with given key doesn't exist");
        }

        return Time.millis(foundObjectsForKey.get(0).lastModified().toEpochMilli());
    }

    /**
     * Prepares the InputStream for download of the object.
     *
     * @param key Key of the object in the bucket
     * @return InputStream for download of the object.
     */
    public InputStream download(String key) {
        return client.getObject().getObject(GetObjectRequest.builder().bucket(bucketName).key(key).build());
    }

    @Override
    public String toString() {
        return "S3Bucket{" +
                "bucketName='" + bucketName + '\'' +
                '}';
    }
}
