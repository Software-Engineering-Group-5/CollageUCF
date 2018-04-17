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
    private Double _timeUploaded;
    private String _imageURL;
    private Double _numberOfLikes;
    private Boolean _publicPrivate;
    private String _uploader;

    @DynamoDBHashKey(attributeName = "PostID")
    @DynamoDBAttribute(attributeName = "PostID")
    public Double getPostID() {
        return _postID;
    }

    public void setPostID(final Double _postID) {
        this._postID = _postID;
    }
    @DynamoDBRangeKey(attributeName = "TimeUploaded")
    @DynamoDBAttribute(attributeName = "TimeUploaded")
    public Double getTimeUploaded() {
        return _timeUploaded;
    }

    public void setTimeUploaded(final Double _timeUploaded) {
        this._timeUploaded = _timeUploaded;
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
    @DynamoDBAttribute(attributeName = "Uploader")
    public String getUploader() {
        return _uploader;
    }

    public void setUploader(final String _uploader) {
        this._uploader = _uploader;
    }

}
