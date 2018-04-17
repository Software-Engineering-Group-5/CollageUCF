package com.marshmellowman.collageucf;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.AWSAbstractCognitoIdentityProvider;
import com.amazonaws.auth.AWSCognitoIdentityProvider;
import com.amazonaws.mobile.auth.core.IdentityHandler;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.auth.ui.SignInUI;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.tokens.CognitoUserToken;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.models.nosql.PostDBDO;
import com.amazonaws.models.nosql.UserDBDO;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.s3.AmazonS3Client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    DynamoDBMapper dynamoDBMapper;
    TransferUtility transferUtility;
    AmazonS3Client s3;
    final String bucket = "collageucf-userfiles-mobilehub-199851075";

    public List<UserDBDO> usersAll;
    public List<UserDBDO> usersFollowing;
    public String currentUser;


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
                    Library fragment2 = new  Library();
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
        // Initiate AWS managers
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        AppInfo.getInstance().setDynamoDBMapper(DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build());
        dynamoDBMapper = AppInfo.getInstance().getDynamoDBMapper();

        AppInfo.getInstance().setS3(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()));
        s3 = AppInfo.getInstance().getS3();

        AppInfo.getInstance().setTransferUtility(TransferUtility.builder()
                .context(getApplicationContext())
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .s3Client(s3)
                .defaultBucket(bucket)
                .build());
        transferUtility = AppInfo.getInstance().getTransferUtility();

        // Set current user
        AppInfo.getInstance().setCurrentUser(new CognitoUserPool(getApplicationContext(), AWSMobileClient.getInstance().getConfiguration()).getCurrentUser().getUserId());
        currentUser = AppInfo.getInstance().getCurrentUser();

        // Fill the list of all users and the following users
        new Thread(new Runnable() {
            public void run() {
                // Get a list of users from the database
                AppInfo.getInstance().setUsersAll(new ArrayList<>(dynamoDBMapper.scan(UserDBDO.class, new DynamoDBScanExpression())));

                // Get the list of users you are following
                Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
                eav.put(":val1", new AttributeValue().withS(currentUser));
                DynamoDBScanExpression exp = new DynamoDBScanExpression()
                        .withFilterExpression("Follower = :val1")
                        .withExpressionAttributeValues(eav);
                AppInfo.getInstance().setUsersFollowing( new ArrayList<>(dynamoDBMapper.scan(UserDBDO.class, exp)));
            }
        }).start();
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
                //Password_Reset fragment3 = new Password_Reset();
                android.support.v4.app.FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                //fragmentTransaction3.replace(R.id.content, fragment3, "FragmentName");
                fragmentTransaction3.commit();
                return true;
            case R.id.logout:
                // Add a call to initialize AWSMobileClient
                AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
                    @Override
                    public void onComplete(AWSStartupResult awsStartupResult) {
                        // Log out first then launch the sign in page again.
                        IdentityManager.getDefaultIdentityManager().signOut();
                        SignInUI signin = (SignInUI) AWSMobileClient.getInstance().getClient(MainActivity.this, SignInUI.class);
                        signin.login(MainActivity.this, LoginActivity.class).execute();
                    }
                }).execute();
        }
        return false;
    }
}
