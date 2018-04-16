package com.marshmellowman.collageucf;


import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.Button;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpLoad extends Fragment {

    AmazonS3Client s3;

    private static final int READ_REQUEST_CODE = 42;
    static String bucket = "collageucf-userfiles-mobilehub-199851075";


    public UpLoad() {
        // Required empty public constructor
    }

    public UpLoad setS3Client(AmazonS3Client s3) {
        this.s3 = s3;
        return this;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisView = inflater.inflate(R.layout.fragment_up_load, container, false);
        Button btn1 = (Button) thisView.findViewById(R.id.button);
        Button btn2 = (Button) thisView.findViewById(R.id.button_upload);

        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                
                Intent intent = new Intent(getActivity(), EditPhoto.class);
                getActivity().startActivity(intent);

            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                // Filter to only show results that can be "opened", such as a
                // file (as opposed to a list of contacts or timezones)
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                // Filter to show only images, using the image MIME data type.
                // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
                // To search for all documents available via installed storage providers,
                // it would be "*/*".
                intent.setType("image/*");

                startActivityForResult(intent, READ_REQUEST_CODE);
            }

        });

        return thisView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (resultData != null) {
                beginUpload(resultData.getData());
            }
        }
    }

    private void beginUpload(final Uri uri) {
        new Thread(new Runnable() {
            public void run() {
                // Initializes TransferUtility
                TransferUtility transferUtility = TransferUtility.builder()
                        .s3Client(s3)
                        .context(getContext())
                        .build();


                File file = new File(getContext().getFilesDir(), "tempImage.tmp");
                try {
                    FileInputStream in = (FileInputStream) getContext().getContentResolver().openInputStream(uri);
                    byte buf[] = new byte[in.available()];
                    in.read(buf);
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(buf);
                }catch (Exception e) {
                    e.printStackTrace();
                }

                // Starts an upload
                TransferObserver observer = transferUtility.upload(bucket, "CollageUCF/" + file.getName(), file);

                observer.setTransferListener(new TransferListener() {

                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (TransferState.COMPLETED == state) {
                            // Handle a completed upload.
                            Log.d("Test", "Upload complete!");
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                        int percentDone = (int) percentDonef;

                        Log.d("MainActivity", "   ID:" + id + "   bytesCurrent: " + bytesCurrent + "   bytesTotal: " + bytesTotal + " " + percentDone + "%");
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        // Handle errors
                        Log.d("Test", "Problems");
                    }

                });
                Log.d("Test", "Upload begun!");
            }
        }).start();
    }
}
