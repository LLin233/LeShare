package androidpath.ll.leshare.Utils;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import androidpath.ll.leshare.Model.ParseConstants;

/**
 * Created by Le on 2015/5/7.
 */
public class LeShareApplication extends Application {
    private static final String APPLICATION_ID = "8b2mbbgT3jBpV3s66bFlrWeKGAbTlrVYK5RrrPmY";
    private static final String CLIENT_KEY = "KrYJYXjpdFczLyzmDIRmWTwaCT2A0JwqerA4ZqKz";


    @Override
    public void onCreate() {
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();

    }


    public static void updateParseInstallation(ParseUser user) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
    }
}
