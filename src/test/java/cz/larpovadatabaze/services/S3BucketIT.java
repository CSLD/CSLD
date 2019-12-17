package cz.larpovadatabaze.services;

import cz.larpovadatabaze.services.s3.S3Bucket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class S3BucketIT {
    S3Client client;

    private S3Bucket bucket;
    private String bucketName = "larpdb-images";

    @Before
    public void setUp() {
        client = S3Client.builder()
                .region(Region.EU_CENTRAL_1)
                .build();
        CreateBucketResponse response = client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
        if (response.sdkHttpResponse().isSuccessful()) {
            bucket = new S3Bucket(client, bucketName);
        } else {
            throw new RuntimeException("Problem with creating bucket.");
        }
    }

    private boolean existsObject(String key) {
        List<S3Object> foundObjectsWithPath = client.listObjects(ListObjectsRequest.builder().bucket(bucketName).build())
                .contents()
                .stream()
                .filter(s3Object -> s3Object.key().equals(key))
                .collect(Collectors.toList());
        return foundObjectsWithPath.size() > 0;
    }

    @Test
    public void uploadNewFile() throws IOException {
        String objectKey = "test.jpeg";
        String objectPath = "src/test/java/cz/larpovadatabaze/services/t0Dh33USkSOzdLwc.jpeg";
        File toUpload = new File(objectPath);
        bucket.upload(objectKey, new FileInputStream(toUpload));

        assertThat(existsObject(objectKey), is(true));
    }

    @Test
    public void testNonExistence() {
        assertThat(bucket.existsObject("nonexistent-file"), is(false));
    }

    @Test
    public void testExistence() {
        String key = "existing-file.jpeg";
        PutObjectResponse response = client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build(),
                RequestBody.fromFile(new File("src/test/java/cz/larpovadatabaze/services/t0Dh33USkSOzdLwc.jpeg")));
        if (!response.sdkHttpResponse().isSuccessful()) {
            throw new RuntimeException("Not possible to upload file.");
        }

        assertThat(bucket.existsObject(key), is(true));
    }

    @Test
    public void removeFileFromBucket() throws IOException {
        String key = "toRemove.jpeg";
        PutObjectResponse response = client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build(),
                RequestBody.fromFile(new File("src/test/java/cz/larpovadatabaze/services/t0Dh33USkSOzdLwc.jpeg")));
        if (!response.sdkHttpResponse().isSuccessful()) {
            throw new RuntimeException("Not possible to upload file.");
        }
        assertThat(existsObject(key), is(true));

        bucket.removeObject(key);
        assertThat(existsObject(key), is(false));
    }

    @Test
    public void deleteBucket() throws IOException {
        // Create new bucket
        String bucketToDelete = "larpdb-to-delete";
        CreateBucketResponse response = client.createBucket(CreateBucketRequest.builder().bucket(bucketToDelete).build());
        if (!response.sdkHttpResponse().isSuccessful()) {
            throw new IOException("Can't create the Bucket with name: " + bucketToDelete);
        }

        // Delete the bucket
        S3Bucket toDelete = new S3Bucket(client, bucketToDelete);
        toDelete.delete();
    }

    @After
    public void tearDown() {
        emptyBucket(bucketName);
        DeleteBucketResponse response = client.deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build());
        if (!response.sdkHttpResponse().isSuccessful()) {
            throw new RuntimeException("Problem with removing bucket.");
        }
    }

    private void emptyBucket(final String name) {
        ListObjectsResponse response = client.listObjects(ListObjectsRequest.builder().bucket(name).build());
        List<S3Object> objects = response.contents();
        objects
                .forEach(object -> client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(name)
                        .key(object.key())
                        .build()));
    }
}
