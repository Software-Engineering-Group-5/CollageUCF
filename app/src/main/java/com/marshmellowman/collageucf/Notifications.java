package com.marshmellowman.collageucf;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.models.nosql.PostDBDO;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Notifications extends Fragment {

    DynamoDBMapper dynamoDBMapper;
    static int DB_TEXT = 12345;
    static int NOTIFICATION_STRING = 23456;
    static int DB_ERROR = 345;
    static String uri = "https://s3.amazonaws.com/collageucf-userfiles-mobilehub-199851075/";

    public Notifications() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisView = inflater.inflate(R.layout.fragment_notifications, container, false);

        dynamoDBMapper = AppInfo.getInstance().getDynamoDBMapper();

        // Attempt at a list of notifications
        ArrayList<ListItem> array = new ArrayList<>();
        ListAdapter adapter = new ListAdapter(thisView.getContext(), R.layout.notification_item, array);
        ListView listView = (ListView) thisView.findViewById(R.id.notifications_list);
        listView.setAdapter(adapter);

        // Create Handler to connect alt Thread (where DB connections can happen) to the Main Thread (where UI changes can happen).
        // Pass it this view so you can change it below.
        final ViewHandler handler = new ViewHandler(thisView, array, adapter);

        // Create another thread to get the image entries
        // Send a Message with a bitmap in it to the Handler
        Thread t2 = new Thread(new Runnable() {
            public void run() {
                // Get a list of posts from the database

                Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
                eav.put(":val1", new AttributeValue().withN( (System.currentTimeMillis() - ( 1L * 24L * 60L * 60L * 1000L ) ) + "" ));

                DynamoDBScanExpression exp = new DynamoDBScanExpression()
                        .withFilterExpression("TimeUploaded > :val1")
                        .withExpressionAttributeValues(eav);

                List<PostDBDO> list = new ArrayList<>(dynamoDBMapper.scan(PostDBDO.class, exp));

                // Sort the list by Time Uploaded
                Collections.sort(list, new Comparator<PostDBDO>(){
                    public int compare(PostDBDO o1, PostDBDO o2){
                        if(o1.getTimeUploaded() == o2.getTimeUploaded())
                            return 0;
                        return o1.getTimeUploaded() > o2.getTimeUploaded() ? -1 : 1;
                    }
                });

                // Construct the message to the handler
                // Note the message now contains a bitmap image
                Message msg;
                try {
                    for (PostDBDO p : list) {
                        msg = handler.obtainMessage();
                        msg.what = NOTIFICATION_STRING;
                        msg.obj = new ListItem(BitmapFactory.decodeStream(new java.net.URL(uri + p.getImageURL()).openStream())
                                , p.getUploader() + " uploaded a photo!");
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                msg = handler.obtainMessage();
                msg.what = DB_TEXT;
                msg.obj = "Loaded!";
                handler.sendMessage(msg);
            }
        });
        t2.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t2, Throwable ex) {
                Message msg = handler.obtainMessage();
                msg.what = DB_ERROR;
                msg.obj = ex;
                handler.sendMessage(msg);
                ex.printStackTrace();
            }
        });
        t2.start();

        return thisView;
    }



    /**
     *  This is where the Main Thread reacts to Messages
     */
    private static class ViewHandler extends Handler {
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
                ((TextView) thisView.findViewById(R.id.notifications_text)).setText( (String) msg.obj );
            }
            else if (msg.what == NOTIFICATION_STRING) {
                array.add((ListItem) msg.obj);
                adapter.notifyDataSetChanged();
            }
            else if (msg.what == DB_ERROR) {
                ((TextView) thisView.findViewById(R.id.notifications_text)).setText( "Problems!" + ((Throwable) msg.obj).getLocalizedMessage() );
            }
            super.handleMessage(msg);
        }
    };

    /**
     *  Grid Adapter: how the array of bitmaps gets put into a fancy grid
     */
    private class ListAdapter extends ArrayAdapter {
        private Context context;
        private int resource;
        private List list;

        public ListAdapter(Context context, int resource, List objects) {
            super(context, resource, objects);
            this.context = context;
            this.resource = resource;
            this.list = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View currentView;

            if (convertView == null) {
                currentView = inflater.inflate(resource, null);
            } else {
                currentView = (View) convertView;
            }

            ( (ImageView) currentView.findViewById(R.id.thumbnail)).setImageBitmap( (Bitmap) ( (ListItem) list.get(position)).bitmap );
            ( (TextView) currentView.findViewById(R.id.text)).setText( (String) ( (ListItem) list.get(position)).string );
            return currentView;
        }
    }

    private class ListItem {
        public Bitmap bitmap;
        public String string;

        public ListItem(Bitmap bitmap, String string) {
            this.bitmap = bitmap;
            this.string = string;
        }
    }

}
