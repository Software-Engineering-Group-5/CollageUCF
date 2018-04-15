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

@DynamoDBTable(tableName = "collageucf-mobilehub-199851075-UserDB")

public class UserDBDO {
    private Double _userID;
    private String _email;
    private String _name;
    private Double _storageUsed;

    @DynamoDBHashKey(attributeName = "UserID")
    @DynamoDBAttribute(attributeName = "UserID")
    public Double getUserID() {
        return _userID;
    }

    public void setUserID(final Double _userID) {
        this._userID = _userID;
    }
    @DynamoDBIndexHashKey(attributeName = "Email", globalSecondaryIndexName = "GetByEmail")
    public String getEmail() {
        return _email;
    }

    public void setEmail(final String _email) {
        this._email = _email;
    }
    @DynamoDBAttribute(attributeName = "Name")
    public String getName() {
        return _name;
    }

    public void setName(final String _name) {
        this._name = _name;
    }
    @DynamoDBAttribute(attributeName = "StorageUsed")
    public Double getStorageUsed() {
        return _storageUsed;
    }

    public void setStorageUsed(final Double _storageUsed) {
        this._storageUsed = _storageUsed;
    }

}
