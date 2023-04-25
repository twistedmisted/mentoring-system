package ua.kpi.mishchenko.mentoringsystem.service.impl;

import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.kpi.mishchenko.mentoringsystem.client.S3Client;
import ua.kpi.mishchenko.mentoringsystem.domain.dto.MediaDTO;
import ua.kpi.mishchenko.mentoringsystem.service.S3Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private static final String BUCKET_NAME = "mentoring-system";
    public static final String PROFILE_PHOTO = "profile-photo";
    private static final String USERS_FOLDER = "users";
    private static final String SLASH = "/";

    private final S3Client s3Client;

    @PostConstruct
    public void createBucket() {
        log.debug("Check if bucket with name [{}] exists", BUCKET_NAME);
        if (!s3Client.getAmazonS3().doesBucketExistV2(BUCKET_NAME)) {
            log.debug("Creating bucket with name [{}]", BUCKET_NAME);
            s3Client.getAmazonS3().createBucket(BUCKET_NAME);
            log.debug("The bucket created successfully");
        }
    }

    @Override
    public void uploadUserPhoto(Long userId, MediaDTO userPhoto) {
        log.debug("Uploading file to S3");
        s3Client.getAmazonS3().putObject(
                BUCKET_NAME,
                USERS_FOLDER + SLASH + userId + SLASH + userPhoto.getFilename(),
                userPhoto.getInputStream(),
                new ObjectMetadata()
        );
        log.debug("The file was successfully uploaded");
    }

    @Override
    public String getUserPhoto(Long userId) {
        List<String> keys = findAllKeysByPrefix(USERS_FOLDER + SLASH + userId + SLASH);
        if (keys.isEmpty()) {
            log.info("Cannot find user photo for user with id = [{}]", userId);
            return null;
        }
        String key = keys.get(0);
        return s3Client.getAmazonS3().getUrl(BUCKET_NAME, key).toString();
    }

    private List<String> findAllKeysByPrefix(String prefix) {
        return s3Client.getAmazonS3().listObjects(BUCKET_NAME, prefix).getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .toList();
    }

    @Override
    public void removeUserPhoto(Long userId) {
        log.debug("Removing user photo from S3 by userId = [{}]", userId);
        List<String> keysToDelete = findAllKeysByPrefix(USERS_FOLDER + SLASH + userId + SLASH);
        if (keysToDelete.isEmpty()) {
            log.info("Cannot find user photo for user with id = [{}]", userId);
            return;
        }
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(BUCKET_NAME)
                .withKeys(keysToDelete.toArray(new String[0]));
        s3Client.getAmazonS3().deleteObjects(deleteObjectsRequest);
        log.debug("The user photo were removed successfully");
    }
}
