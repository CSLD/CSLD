package cz.larpovadatabaze.common.services.s3;

import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.time.Time;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Representation of S3Bucket and associated operations. If the constructor succeeds the bucket will exist. Either
 * it already exists or it is created.
 */
public class S3Bucket implements Serializable {
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
     * TODO: Won't work with buckets over 1000 items.
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
                .acl(ObjectCannedACL.PUBLIC_READ)
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
        return objectsForKey(key).size() > 0;
    }

    /**
     * @param key Key of the object for which to load the item.
     * @return Time of update in miliseconds
     */
    public Instant getLastUpdatedForObject(String key) {
        List<S3Object> foundObjectsForKey = objectsForKey(key);
        if (foundObjectsForKey.size() == 0) {
            throw new RuntimeException("File with given key doesn't exist");
        }

        return Instant.ofEpochMilli(foundObjectsForKey.get(0).lastModified().toEpochMilli());
    }

    /**
     * Get all objects with given key.
     *
     * @param key Key to filter on.
     * @return Array of the S3Objects with given key.
     */
    private List<S3Object> objectsForKey(String key) {
        return client.getObject().listObjects(
                ListObjectsRequest.builder().bucket(bucketName).prefix(key).build()
        )
                .contents()
                .stream()
                .filter(s3Object -> s3Object.key().equals(key))
                .collect(Collectors.toList());
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

    /**
     * Return public URL usable to work with file.
     *
     * @param key Key of the file to work with
     */
    public String getPublicUrl(String key) {
        return "https://" + bucketName + ".s3.eu-central-1.amazonaws.com/" + key;
    }
}
