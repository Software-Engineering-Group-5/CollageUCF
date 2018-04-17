package com.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "collageucf-mobilehub-199851075-FollowDB")

public class FollowDBDO {
    private String _follower;
    private String _following;

    @DynamoDBHashKey(attributeName = "Follower")
    @DynamoDBAttribute(attributeName = "Follower")
    public String getFollower() {
        return _follower;
    }

    public void setFollower(final String _follower) {
        this._follower = _follower;
    }
    @DynamoDBRangeKey(attributeName = "Following")
    @DynamoDBAttribute(attributeName = "Following")
    public String getFollowing() {
        return _following;
    }

    public void setFollowing(final String _following) {
        this._following = _following;
    }

}
