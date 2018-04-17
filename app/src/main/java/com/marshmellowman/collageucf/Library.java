package com.marshmellowman.collageucf;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.models.nosql.PostDBDO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View thisView = inflater.inflate(R.layout.fragment_library, container, false);
        final ImageView mainImage = thisView.findViewById(R.id.library_image);
        final LinearLayout holder = thisView.findViewById(R.id.library_image_holder);

        dynamoDBMapper = AppInfo.getInstance().getDynamoDBMapper();

        holder.setVisibility(View.GONE);
        mainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.setVisibility(View.GONE);
                ((Button) thisView.findViewById(R.id.library_like)).setText("Like");
            }
        });
        ((Button) thisView.findViewById(R.id.library_like)).setOnClickListener(
            new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   ((Button) v).setText("Liked");
               }
           }

        );

        // Attempt at a grid of pictures
        ArrayList<ListItem> array = new ArrayList<>();
        GridAdapter adapter = new GridAdapter(thisView.getContext(), R.layout.library_item, array);
        GridView listView = (GridView) thisView.findViewById(R.id.library_grid);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mainImage.setImageBitmap(((ListItem) parent.getItemAtPosition(position)).bitmap);
                holder.setVisibility(View.VISIBLE);
                ((TextView) thisView.findViewById(R.id.library_user)).setText(((ListItem) parent.getItemAtPosition(position)).string);
            }
        });

        // Create Handler to connect alt Thread (where DB connections can happen) to the Main Thread (where UI changes can happen).
        // Pass it this view so you can change it below.
        final ViewHandler handler = new ViewHandler(thisView, array, adapter);

        // Create another thread to download an image
        // Send a Message with a bitmap in it to the Handler
        Thread t2 = new Thread(new Runnable() {
            public void run() {
                // Get a list of posts from the database
                List<PostDBDO> list = new ArrayList<>(dynamoDBMapper.scan(PostDBDO.class, new DynamoDBScanExpression()));

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
                        msg.what = S3_IMAGE;
                        msg.obj = new ListItem(BitmapFactory.decodeStream(new java.net.URL(uri + p.getImageURL()).openStream())
                                , p.getUploader());
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
    private static class ViewHandler extends Handler{
        View thisView;
        ArrayList array;
        GridAdapter adapter;

        ViewHandler(View thisView, ArrayList array, GridAdapter adapter) {
            this.thisView = thisView;
            this.array = array;
            this.adapter = adapter;
        }

        public void handleMessage(Message msg) {
            if(msg.what==DB_TEXT){
                Toast.makeText(thisView.getContext(), (String) msg.obj, Toast.LENGTH_SHORT).show();
            }
            else if (msg.what == S3_IMAGE) {
                array.add(msg.obj);
                adapter.notifyDataSetChanged();
            }
            else if (msg.what == DB_ERROR) {
                Toast.makeText(thisView.getContext(), "Problems!" + ((Throwable) msg.obj).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };

    /**
     *  Grid Adapter: how the array of bitmaps gets put into a fancy grid
     */
    private class GridAdapter extends ArrayAdapter {
        private Context context;
        private int resource;
        private List list;

        public GridAdapter(Context context, int resource, List objects) {
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

            ( (ImageView) currentView.findViewById(R.id.thumbnail)).setImageBitmap( (Bitmap) ((ListItem)list.get(position)).bitmap );
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
