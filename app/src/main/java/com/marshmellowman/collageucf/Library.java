package com.marshmellowman.collageucf;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.models.nosql.UserDBDO;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Library extends Fragment {

    DynamoDBMapper dynamoDBMapper;
    static int DB_TEXT = 12345;

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

        // Create Handler to connect alt Thread (where DB connections can happen) to the Main Thread (where UI changes can happen).
        // Pass it this view so you can change it below.
        final ViewHandler handler = new ViewHandler(thisView);

        // Create alt Thread to connect to the DB, as it could take forever, who knows?
        // Send a Message with a string in it to the Handler
        new Thread(new Runnable() {
            public void run() {
                // Get a list of users from the database
                List<UserDBDO> list = dynamoDBMapper.scan(UserDBDO.class, new DynamoDBScanExpression());

                // Construct the message to the handler
                // Note that the message stores an Object, so it could be literally anything
                Message msg = handler.obtainMessage();
                msg.what = DB_TEXT;
                msg.obj = "";
                for (UserDBDO u : list)
                    msg.obj = msg.obj + u.getName() + "\n";
                handler.sendMessage(msg);
            }
        }).start();

        return thisView;
    }

    // This is where you'd take the message's text and do stuff to the UI with it
    private static class ViewHandler extends Handler{
        View thisView;

        ViewHandler(View thisView) {
            this.thisView = thisView;
        }

        public void handleMessage(Message msg) {
            if(msg.what==DB_TEXT){
                ((TextView) thisView.findViewById(R.id.library_list)).setText( (String) msg.obj );
            }
            super.handleMessage(msg);
        }
    };

}
