package com.marshmellowman.collageucf;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.models.nosql.UserDBDO;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3Client;

import java.util.List;

/**
 * Created by Matthew on 4/17/2018.
 */

public class AppInfo {

    private static AppInfo singleton;
    private final String bucket = "collageucf-userfiles-mobilehub-199851075";

    private DynamoDBMapper dynamoDBMapper;
    private TransferUtility transferUtility;
    private AmazonS3Client s3;

    private List<UserDBDO> usersAll;
    private List<UserDBDO> usersFollowing;
    private String currentUser;


    // Getters
    public DynamoDBMapper getDynamoDBMapper() {
        return dynamoDBMapper;
    }

    public TransferUtility getTransferUtility() {
        return transferUtility;
    }

    public AmazonS3Client getS3() {
        return s3;
    }

    public String getBucket() {
        return bucket;
    }

    public List<UserDBDO> getUsersAll() {
        return usersAll;
    }

    public List<UserDBDO> getUsersFollowing() {
        return usersFollowing;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    // Setters
    public void setDynamoDBMapper(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public void setTransferUtility(TransferUtility transferUtility) {
        this.transferUtility = transferUtility;
    }

    public void setS3(AmazonS3Client s3) {
        this.s3 = s3;
    }

    public void setUsersAll(List<UserDBDO> usersAll) {
        this.usersAll = usersAll;
    }

    public void setUsersFollowing(List<UserDBDO> usersFollowing) {
        this.usersFollowing = usersFollowing;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public static synchronized AppInfo getInstance() {
        if (singleton == null) {
            singleton = new AppInfo();
        }
        return singleton;
    }
}
