package io.hypertrack.sendeta.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;

import io.hypertrack.sendeta.model.AppDeepLink;
import io.hypertrack.sendeta.store.UserStore;
import io.hypertrack.sendeta.util.Constants;
import io.hypertrack.sendeta.util.DeepLinkUtil;
import io.hypertrack.sendeta.util.Utils;

/**
 * Created by piyush on 23/07/16.
 */
public class SplashScreen extends BaseActivity {

    private static final String TAG = SplashScreen.class.getSimpleName();

    private AppDeepLink appDeepLink;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        boolean isUserOnboard = UserStore.isUserLoggedIn();

        handleDeepLink();

        if (!isUserOnboard) {
            Intent registerIntent = new Intent(this, Register.class);
            registerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(registerIntent);
            finish();
        } else {
            UserStore.sharedStore.initializeUser();
            Utils.setCrashlyticsKeys(this);
            proceedToNextScreen(appDeepLink);
        }
    }

    // Method to handle DeepLink Params
    private void handleDeepLink() {
        appDeepLink = new AppDeepLink(DeepLinkUtil.DEFAULT);
        Intent intent = getIntent();

        // if started through deep link
        if (intent != null && !TextUtils.isEmpty(intent.getDataString())) {
            Log.d(TAG, "deeplink " + intent.getDataString());
            appDeepLink = DeepLinkUtil.prepareAppDeepLink(SplashScreen.this, intent.getDataString());
        }
    }

    // Method to proceed to next screen with deepLink params
    private void proceedToNextScreen(final AppDeepLink appDeepLink) {
        switch (appDeepLink.mId) {
            case DeepLinkUtil.MEMBERSHIP:
                TaskStackBuilder.create(this)
                        .addNextIntentWithParentStack(new Intent(this, BusinessProfile.class)
                                .putExtra(BusinessProfile.KEY_MEMBERSHIP_INVITE, true)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        .startActivities();
                finish();
                break;

            case DeepLinkUtil.RECEIVE_ETA_FOR_DESTINATION:
                TaskStackBuilder.create(this)
                        .addNextIntentWithParentStack(new Intent(this, Home.class)
                                .putExtra(Constants.KEY_PUSH_DESTINATION, true)
                                .putExtra(Constants.KEY_ACCOUNT_ID, appDeepLink.id)
                                .putExtra(Constants.KEY_PUSH_DESTINATION_LAT, appDeepLink.lat)
                                .putExtra(Constants.KEY_PUSH_DESTINATION_LNG, appDeepLink.lng)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        .startActivities();
                finish();
                break;

            case DeepLinkUtil.TRACK:
                TaskStackBuilder.create(this)
                        .addNextIntentWithParentStack(new Intent(this, Track.class)
                                .putExtra(Track.KEY_SHORT_CODE, appDeepLink.shortCode)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        .startActivities();
                break;

            case DeepLinkUtil.DEFAULT:
            default:
                TaskStackBuilder.create(this)
                        .addNextIntentWithParentStack(new Intent(this, Home.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        .startActivities();
                finish();
                break;
        }
    }
}
