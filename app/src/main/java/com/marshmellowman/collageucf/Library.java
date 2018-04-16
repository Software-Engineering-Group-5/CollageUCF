package com.marshmellowman.collageucf;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.models.nosql.PostDBDO;
import com.amazonaws.models.nosql.UserDBDO;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Library extends Fragment {

    DynamoDBMapper dynamoDBMapper;
    static int DB_TEXT = 12345;
    static int S3_IMAGE = 23456;
    static int DB_ERROR = 345;
    static String uri = "https://s3.amazonaws.com/collageucf-userfiles-mobilehub-199851075/";

    public Library() {
        // Required empty public constructor
    }

    public Library setDynamoDBMapper(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisView = inflater.inflate(R.layout.fragment_library, container, false);

        // Attempt at a grid of pictures
        ArrayList<Bitmap> array = new ArrayList<>();
        ArrayAdapter<Bitmap> adapter = new ArrayAdapter<Bitmap>(thisView.getContext(), R.layout.thumbnail_list_item, array);
        GridView listView = (GridView) thisView.findViewById(R.id.library_grid);
        listView.setAdapter(adapter);

        // Create Handler to connect alt Thread (where DB connections can happen) to the Main Thread (where UI changes can happen).
        // Pass it this view so you can change it below.
        final ViewHandler handler = new ViewHandler(thisView, array, adapter);

        // Create alt Thread to connect to the DB, as it could take forever, who knows?
        // Send a Message with a string in it to the Handler
        new Thread(new Runnable() {
            public void run() {
                // Get a list of users from the database
                List<PostDBDO> list = dynamoDBMapper.scan(PostDBDO.class, new DynamoDBScanExpression());

                // Construct the message to the handler
                // Note that the message stores an Object, so it could be literally anything
                Message msg = handler.obtainMessage();
                msg.what = DB_TEXT;
                msg.obj = new String();
                for (PostDBDO u : list)
                    msg.obj = msg.obj + u.getImageURL() + "\n";
                handler.sendMessage(msg);
            }
        }).start();

        // Create another thread to download an image
        // Send a Message with a bitmap in it to the Handler
        Thread t2 = new Thread(new Runnable() {
            public void run() {
                // Get a list of posts from the database
                List<PostDBDO> list = dynamoDBMapper.scan(PostDBDO.class, new DynamoDBScanExpression());

                // Construct the message to the handler
                // Note the message now contains a bitmap image
                Message msg = handler.obtainMessage();
                msg.what = S3_IMAGE;
                try {
                    msg.obj = BitmapFactory.decodeStream(new java.net.URL(uri + list.get(0).getImageURL()).openStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.sendMessage(msg);
            }
        });
        t2.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t2, Throwable ex) {
                Message msg = handler.obtainMessage();
                msg.what = DB_ERROR;
                msg.obj = ex;
                handler.sendMessage(msg);            }
        });
        t2.start();

        return thisView;
    }

    /**
     *  This is where the Main Thread reacts to Messages
     */
    private static class ViewHandler extends Handler{
        View thisView;
        ArrayList array;
        ArrayAdapter adapter;

        ViewHandler(View thisView, ArrayList array, ArrayAdapter adapter) {
            this.thisView = thisView;
            this.array = array;
            this.adapter = adapter;
        }

        public void handleMessage(Message msg) {
            if(msg.what==DB_TEXT){
                ((TextView) thisView.findViewById(R.id.library_list)).setText( (String) msg.obj );
            }
            else if (msg.what == S3_IMAGE) {
                ((ImageView) thisView.findViewById(R.id.library_image)).setImageBitmap( (Bitmap) msg.obj);
            }
            else if (msg.what == DB_ERROR) {
                ((TextView) thisView.findViewById(R.id.library_list)).setText( "Problems!" + ((Throwable) msg.obj).getLocalizedMessage() );
            }
            super.handleMessage(msg);
        }
    };
}
