package com.marshmellowman.collageucf;


import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.models.nosql.PostDBDO;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpLoad extends Fragment {

    DynamoDBMapper dynamoDBMapper;
    AmazonS3Client s3;
    TransferUtility transferUtility;
    UploadHandler handler;

    private static final int GET_FILE_REQUEST_CODE = 42;
    private static final int FILE_PARSE_ERROR = 10;
    private static final int FILE_UPLOAD_BEGIN = 11;
    private static final int FILE_UPLOAD_SUCCESS = 12;
    private static final int FILE_UPLOAD_FAILURE = 13;
    private static final String bucket = "collageucf-userfiles-mobilehub-199851075";


    public UpLoad() {
        // Required empty public constructor
    }

    public UpLoad setDynamoDBMapper(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
        return this;
    }

    public UpLoad setS3Client(AmazonS3Client s3) {
        this.s3 = s3;
        return this;
    }

    public UpLoad setTransferUtility(TransferUtility transferUtility) {
        this.transferUtility = transferUtility;
        return this;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisView = inflater.inflate(R.layout.fragment_up_load, container, false);

        handler = new UploadHandler(thisView);

        Button btn1 = (Button) thisView.findViewById(R.id.button);
        Button btn2 = (Button) thisView.findViewById(R.id.button_upload);

        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditPhoto.class);
                getActivity().startActivity(intent);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent()
                        .setAction(Intent.ACTION_OPEN_DOCUMENT) // This action is to choose a document
                        .addCategory(Intent.CATEGORY_OPENABLE)  // We only want to choose from files
                        .setType("image/*");                    // filter to show only images via MIME data type

                startActivityForResult(intent, GET_FILE_REQUEST_CODE);
            }
        });
        return thisView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        // Request Code indicates this is the request for reading
        if (requestCode == GET_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Result Data contains a URI to the chosen file
            if (resultData != null) {
                beginUpload(resultData.getData());
            }
        }
    }

    // Begins the file upload in a new thread
    private void beginUpload(final Uri uri) {
        new Thread(new Runnable() {
            public void run() {

                Message msg = handler.obtainMessage();
                msg.what = FILE_UPLOAD_BEGIN;
                handler.sendMessage(msg);

                // To read the file, we have to stream it into a temp file we create and have control
                // over, as we don't know the situation of the original file
                final File file = new File(getContext().getFilesDir(), "tempImage.tmp");
                try {
                    FileInputStream in = (FileInputStream) getContext().getContentResolver().openInputStream(uri);
                    byte buf[] = new byte[in.available()];
                    in.read(buf);
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(buf);

                } catch (Exception e) {

                    Message msg2 = handler.obtainMessage();
                    msg2.what = FILE_PARSE_ERROR;
                    msg2.obj = "Something happened with the file reading.";
                    handler.sendMessage(msg2);

                    e.printStackTrace();
                    return;
                }

                // This file hash will be used for names
                final int hash = new Random().nextInt();

                // File name format: "CollageUCF/somehashcode.type"
                final String filename = "CollageUCF/" + hash + uri.getPath().substring( (uri.getPath()).lastIndexOf('.'));

                // Starts an upload. The TransferListener gets run in the main thread, so we're back
                // into thread networking issues again
                transferUtility.upload(bucket, filename, file).setTransferListener(new TransferListener() {

                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            // Now add new entry to Post Database
                            newPostDB((double) hash, filename, 0.0, true, System.currentTimeMillis(), 0.0);
                            file.delete();
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        Log.d("MainActivity", "   ID:" + id + "   bytesCurrent: " + bytesCurrent + "   bytesTotal: " + bytesTotal + " " + (float) (((float) bytesCurrent / (float) bytesTotal) * 100) + "%");
                    }

                    @Override
                    public void onError(int id, Exception ex) {

                        Message msg = handler.obtainMessage();
                        msg.what = FILE_UPLOAD_FAILURE;
                        msg.obj = "Something went wrong...\n" + ex.getMessage();
                        handler.sendMessage(msg);

                        ex.printStackTrace();
                    }

                });
            }
        }).start();
    }

    // Uploads a new entry to the posts database
    private void newPostDB(
            final double postID,
            final String imageURL,
            final double numberOfLikes,
            final boolean publicPrivate,
            final double timeUploaded,
            final double uploader) {

        new Thread(new Runnable() {
            public void run() {
                PostDBDO post = new PostDBDO();

                post.setPostID(postID);
                post.setImageURL(imageURL);
                post.setNumberOfLikes(numberOfLikes);
                post.setPublicPrivate(publicPrivate);
                post.setTimeUploaded(timeUploaded);
                post.setUploader(uploader);

                dynamoDBMapper.save(post);

                Message msg = handler.obtainMessage();
                msg.what = FILE_UPLOAD_SUCCESS;
                msg.obj = "File Upload Complete!";
                handler.sendMessage(msg);
            }
        }).start();
    }


    /**
     *  This is where the Main Thread reacts to Messages
     */
    private static class UploadHandler extends Handler{
        View thisView;
        UploadHandler(View thisView) {
            this.thisView = thisView;
        }

        public void handleMessage(Message msg) {
            if(msg.what==FILE_PARSE_ERROR){
                ((TextView) thisView.findViewById(R.id.upload_status)).setText( (String) msg.obj );
            }
            else if(msg.what==FILE_UPLOAD_BEGIN){
                ((TextView)thisView.findViewById(R.id.upload_status)).setText( "" );
            }
            else if(msg.what==FILE_UPLOAD_SUCCESS){
                ((TextView) thisView.findViewById(R.id.upload_status)).setText( (String) msg.obj );
            }
            else if(msg.what==FILE_UPLOAD_FAILURE){
                ((TextView) thisView.findViewById(R.id.upload_status)).setText( (String) msg.obj );
            }
            super.handleMessage(msg);
        }
    };

}
