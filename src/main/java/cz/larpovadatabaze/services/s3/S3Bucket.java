package cz.larpovadatabaze.services.s3;

import org.apache.wicket.util.time.Time;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Representation of S3Bucket and associated operations. If the constructor succeeds the bucket will exist. Either
 * it already exists or it is created.
 */
public class S3Bucket {
    private S3Client client;
    private String bucketName;

    public S3Bucket(S3Client client, String bucketName) {
        this.client = client;
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
        List<Bucket> availableBuckets = client.listBuckets().buckets()
                .stream()
                .filter(bucket -> bucket.name().equals(bucketName))
                .collect(Collectors.toList());
        return availableBuckets.size() > 0;
    }

    /**
     * It creates a Bucket with given name. Used as a part of construction.
     */
    private void create() throws IOException {
        CreateBucketResponse response = client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
        if (!response.sdkHttpResponse().isSuccessful()) {
            throw new IOException("Can't create the Bucket with name: " + bucketName);
        }
    }

    /**
     * Delete Bucket with everything present.
     */
    public void delete() throws IOException {
        ListObjectsResponse allObjectsResponse = client.listObjects(ListObjectsRequest.builder().bucket(bucketName).build());
        allObjectsResponse.contents()
                .forEach(object -> removeObject(object.key()));
        DeleteBucketResponse response = client.deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build());
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
        PutObjectResponse response = client.putObject(PutObjectRequest.builder()
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
        DeleteObjectResponse response = client.deleteObject(DeleteObjectRequest.builder()
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
        List<S3Object> foundObjectsWithPath = client.listObjects(ListObjectsRequest.builder().bucket(bucketName).build())
                .contents()
                .stream()
                .filter(s3Object -> s3Object.key().equals(key))
                .collect(Collectors.toList());
        return foundObjectsWithPath.size() > 0;
    }

    /**
     * @param key Key of the object for which to load the item.
     * @return Time of update in miliseconds
     */
    public Time getLastUpdatedForObject(String key) {
        List<S3Object> foundObjectsForKey = client.listObjects(ListObjectsRequest.builder().bucket(bucketName).build())
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
        return client.getObject(GetObjectRequest.builder().bucket(bucketName).key(key).build());
    }
}