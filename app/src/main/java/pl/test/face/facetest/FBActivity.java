package pl.test.face.facetest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import pl.test.face.facetest.x.ShareDialogX;

public class FBActivity extends AppCompatActivity {
    private static final String PERMISSION = "publish_actions";
    private static final Location SEATTLE_LOCATION = new Location("Seattle") {
        {
            setLatitude(50.6097);
            setLongitude(19.3331);
        }
    };
    private static final String TAG = FBActivity.class.getSimpleName();

    private final String PENDING_ACTION_BUNDLE_KEY =
            "testo.pl.penauts:PendingAction";

    private Button postStatusUpdateButton;
    private Button postPhotoButton;
    private ProfilePictureView profilePictureView;
    private TextView greeting;
    private PendingAction pendingAction = PendingAction.NONE;
    private boolean canPresentShareDialog;
    private boolean canPresentShareDialogWithPhotos;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private ShareDialogX shareDialog;
    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onCancel() {
            Log.d("HelloFacebook", "Canceled");
        }

        @Override
        public void onError(FacebookException error) {
            Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
            String title = "Error connecting to facebook ;(";
            String alertMessage = error.getMessage();
            showResult(title, alertMessage);
        }

        @Override
        public void onSuccess(Sharer.Result result) {
            Log.d("HelloFacebook", "Success!");
            if (result.getPostId() != null) {
                String title = "Result success";
                String id = result.getPostId();
                String alertMessage = "Successfully posted " + id;
                showResult(title, alertMessage);
            }
        }

        private void showResult(String title, String alertMessage) {
            new android.app.AlertDialog.Builder(FBActivity.this)
                    .setTitle(title)
                    .setMessage(alertMessage)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
    };

    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

//        setContentView(R.layout.activity_facebook);
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handlePendingAction();
                        updateUI();
                    }

                    @Override
                    public void onCancel() {
                        if (pendingAction != PendingAction.NONE) {
                            showAlert();
                            pendingAction = PendingAction.NONE;
                        }
                        updateUI();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        if (pendingAction != PendingAction.NONE
                                && exception instanceof FacebookAuthorizationException) {
                            showAlert();
                            pendingAction = PendingAction.NONE;
                        }
                        updateUI();
                    }

                    private void showAlert() {
                        new android.app.AlertDialog.Builder(FBActivity.this)
                                .setTitle("Cancelled")
                                .setMessage("Permission not granted")
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                    }
                });

        shareDialog = new ShareDialogX(this);
        shareDialog.registerCallback(
                callbackManager,
                shareCallback);

        if (savedInstanceState != null) {
            String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
            pendingAction = PendingAction.valueOf(name);
        }

        setContentView(R.layout.activity_main);

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                updateUI();
                // It's possible that we were waiting for Profile to be populated in order to
                // post a status update.
                handlePendingAction();
            }
        };

        profilePictureView = (ProfilePictureView) findViewById(R.id.profilePicture);
        greeting = (TextView) findViewById(R.id.greeting);

        postStatusUpdateButton = (Button) findViewById(R.id.postStatusUpdateButton);
        postStatusUpdateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickPostStatusUpdate();
            }
        });

        postPhotoButton = (Button) findViewById(R.id.postPhotoButton);
        postPhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickPostPhoto();
            }
        });

        // Can we present the share dialog for regular links?
        canPresentShareDialog = ShareDialog.canShow(
                ShareLinkContent.class);

        // Can we present the share dialog for photos?
        canPresentShareDialogWithPhotos = ShareDialog.canShow(
                SharePhotoContent.class);
//        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        // Check if Internet present
        Log.d(TAG, "onCreate()");
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            Log.i(TAG, "no internet connection");
            // Internet Connection is not present
            buildNoInternetDialog(this).show();
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if (fab != null)
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.i(TAG, "onCreate: get basic info");
                    ReactivePoster.create().rolex();
                }
            });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }


    public AlertDialog.Builder buildNoInternetDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet connection.");
        builder.setMessage("You have no internet connection");

        builder.setPositiveButton("Launch internet", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
            }
        });

        return builder;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//    }

    @Override
    public void onPause() {
        super.onPause();

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }

    private void updateUI() {
        boolean enableButtons = AccessToken.getCurrentAccessToken() != null;

        postStatusUpdateButton.setEnabled(enableButtons || canPresentShareDialog);
        postPhotoButton.setEnabled(enableButtons || canPresentShareDialogWithPhotos);

        Profile profile = Profile.getCurrentProfile();
        if (enableButtons && profile != null) {
            profilePictureView.setProfileId(profile.getId());
            greeting.setText("Hello, " + profile.getFirstName());
        } else {
            profilePictureView.setProfileId(null);
            greeting.setText(null);
        }
    }

    private void handlePendingAction() {
        Log.i(TAG, "handlePendingAction: ");
        if (true) {
            postStatusUpdate();
            return;
        }
        PendingAction previouslyPendingAction = pendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        pendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case NONE:
                break;
            case POST_PHOTO:
                postPhoto();
                break;
            case POST_STATUS_UPDATE:
                postStatusUpdate();
                break;
        }
    }

    private void onClickPostStatusUpdate() {
        performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);
    }

    private Clickerbot clickerbot;

    private void postStatusUpdate() {
        Log.i(TAG, "postStatusUpdate: ");
        clickerbot = new Clickerbot();
        clickerbot.start(this);
        Log.i(TAG, "postStatusUpdate: ");
        Profile profile = Profile.getCurrentProfile();
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle(RandomPostGenerator.createTitle())
                .setContentDescription(
                        RandomPostGenerator.createDescription())
//                .setContentUrl(Uri.parse("http://developers.facebook.com/docs/android"))
                .setContentUrl(Uri.parse("http://www.wykop.pl"))
                .build();


        if (canPresentShareDialog) {
            Log.i(TAG, "canPresentShareDialog: ");
            shareDialog.show(linkContent);
        } else if (profile != null && hasPublishPermission()) {
            Log.i(TAG, "profile is not null and can publish");
            ShareApi.share(linkContent, shareCallback);
        } else {
            Log.i(TAG, "pending action post status update");
            pendingAction = PendingAction.POST_STATUS_UPDATE;
        }
    }

    private void onClickPostPhoto() {
        performPublish(PendingAction.POST_PHOTO, canPresentShareDialogWithPhotos);
    }

    final int SELECT_PHOTO = 100;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {

        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        callbackManager.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + ")");

        Bitmap yourSelectedImage = null;
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri selectedImage = imageReturnedIntent.getData();
                        InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                        yourSelectedImage = BitmapFactory.decodeStream(imageStream);

                    } catch (Exception ex) {
                        Log.e(TAG, "error during decoding image");
                    }
                    if (yourSelectedImage != null) {
                        postPhotoResult(yourSelectedImage);

                    }
                }

        }
    }

    private void postPhotoResult(Bitmap image) {
        Log.d(TAG, "postPhotoResult()");
        SharePhoto sharePhoto = new SharePhoto.Builder().setBitmap(image).build();
        ArrayList<SharePhoto> photos = new ArrayList<>();
        photos.add(sharePhoto);

        SharePhotoContent sharePhotoContent =
                new SharePhotoContent.Builder().setPhotos(photos).build();
        if (canPresentShareDialogWithPhotos) {
            shareDialog.show(sharePhotoContent);
        } else if (hasPublishPermission()) {
            ShareApi.share(sharePhotoContent, shareCallback);
        } else {
            pendingAction = PendingAction.POST_PHOTO;
        }
    }

    private void postPhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    private boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains("publish_actions");
    }

    private void performPublish(PendingAction action, boolean allowNoToken) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            pendingAction = action;
            if (hasPublishPermission()) {
                // We can do the action right away.
                handlePendingAction();
                return;
            } else {
                // We need to get new permissions, then complete the action when we get called back.
                LoginManager.getInstance().logInWithPublishPermissions(
                        this,
                        Arrays.asList(PERMISSION));
                return;
            }
        }

        if (allowNoToken) {
            pendingAction = action;
            handlePendingAction();
        }
    }
}