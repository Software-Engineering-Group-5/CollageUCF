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

@DynamoDBTable(tableName = "collageucf-mobilehub-199851075-PostDB")

public class PostDBDO {
    private Double _postID;
    private String _imageURL;
    private Double _numberOfLikes;
    private Boolean _publicPrivate;
    private String _timeUploaded;
    private Double _uploader;

    @DynamoDBHashKey(attributeName = "PostID")
    @DynamoDBAttribute(attributeName = "PostID")
    public Double getPostID() {
        return _postID;
    }

    public void setPostID(final Double _postID) {
        this._postID = _postID;
    }
    @DynamoDBAttribute(attributeName = "ImageURL")
    public String getImageURL() {
        return _imageURL;
    }

    public void setImageURL(final String _imageURL) {
        this._imageURL = _imageURL;
    }
    @DynamoDBAttribute(attributeName = "NumberOfLikes")
    public Double getNumberOfLikes() {
        return _numberOfLikes;
    }

    public void setNumberOfLikes(final Double _numberOfLikes) {
        this._numberOfLikes = _numberOfLikes;
    }
    @DynamoDBAttribute(attributeName = "Public/Private")
    public Boolean getPublicPrivate() {
        return _publicPrivate;
    }

    public void setPublicPrivate(final Boolean _publicPrivate) {
        this._publicPrivate = _publicPrivate;
    }
    @DynamoDBAttribute(attributeName = "TimeUploaded")
    public String getTimeUploaded() {
        return _timeUploaded;
    }

    public void setTimeUploaded(final String _timeUploaded) {
        this._timeUploaded = _timeUploaded;
    }
    @DynamoDBIndexHashKey(attributeName = "Uploader", globalSecondaryIndexName = "Uploader")
    public Double getUploader() {
        return _uploader;
    }

    public void setUploader(final Double _uploader) {
        this._uploader = _uploader;
    }

}
