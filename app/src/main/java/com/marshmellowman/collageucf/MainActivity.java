package com.marshmellowman.collageucf;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    DynamoDBMapper dynamoDBMapper;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //setTitle("Home");
                    toolbar.setSubtitle("Home");
                    Home fragment = new Home();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.content, fragment, "FragmentName");
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_library:
                    //setTitle("Library");
                    toolbar.setSubtitle("Library");
                    Library fragment2 = new  Library().setDynamoDBMapper(dynamoDBMapper);
                    android.support.v4.app.FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.content, fragment2, "FragmentName");
                    fragmentTransaction2.commit();
                    return true;
                case R.id.navigation_notifications:
                    //setTitle("Notifications");
                    toolbar.setSubtitle("Notifications");
                    Notifications fragment3 = new Notifications();
                    android.support.v4.app.FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction3.replace(R.id.content, fragment3, "FragmentName");
                    fragmentTransaction3.commit();
                    return true;
                case R.id.navigation_upload:
                    //setTitle("Up-Load");
                    toolbar.setSubtitle("Up-Load");
                    UpLoad fragment4 = new UpLoad();
                    android.support.v4.app.FragmentTransaction fragmentTransaction4 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction4.replace(R.id.content, fragment4, "FragmentName");
                    fragmentTransaction4.commit();
                    return true;
                case R.id.navigation_collage:
                    //setTitle("Collage");
                    toolbar.setSubtitle("Collage");
                    Collage fragment5 = new Collage();
                    android.support.v4.app.FragmentTransaction fragmentTransaction5 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction5.replace(R.id.content, fragment5, "FragmentName");
                    fragmentTransaction5.commit();
                    return true;
            }
            return false;
        }
    };

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set main screen to activity_main
        setContentView(R.layout.activity_main);


        // Setting up the top toolbar and defaulting to Home
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Collage UCF");
        toolbar.setSubtitle("Home");
        setSupportActionBar(toolbar);

        // Compatibility checking
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            toolbar.setElevation(10.f);
        }

        // Creating bottom navigation buttons and on click listener
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Sets the main page to Home after login.
        // setTitle("Home");
        Home fragment = new Home();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "FragmentName");
        fragmentTransaction.commit();

        // Initiate AWS
        AWSMobileClient.getInstance().initialize(this).execute();

        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();
    }

    @Override
    // Creates top right drop down menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    // Menu items for top right drop down.
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.profile:
                //Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                toolbar.setSubtitle("Profile");
                Profile fragment = new Profile();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content, fragment, "FragmentName");
                fragmentTransaction.commit();
                return true;
            case R.id.following:
                //Toast.makeText(this, "Following", Toast.LENGTH_SHORT).show();
                toolbar.setSubtitle("Following");
                Following fragment2 = new Following();
                android.support.v4.app.FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                fragmentTransaction2.replace(R.id.content, fragment2, "FragmentName");
                fragmentTransaction2.commit();
                return true;
            case R.id.password:
                //Toast.makeText(this, "Password Reset", Toast.LENGTH_SHORT).show();
                toolbar.setSubtitle("Password Reset");
                Password_Reset fragment3 = new Password_Reset();
                android.support.v4.app.FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                fragmentTransaction3.replace(R.id.content, fragment3, "FragmentName");
                fragmentTransaction3.commit();
                return true;
            case R.id.logout:
                //Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(MainActivity.this , LoginActivity.class);
                MainActivity.this.startActivity(myIntent);
                return true;
        }
        return false;
    }
}
