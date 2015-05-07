package androidpath.ll.leshare.Helper;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Le on 2015/5/7.
 */
public class LeShareApplication extends Application {
    private static final String APPLICATION_ID = "8b2mbbgT3jBpV3s66bFlrWeKGAbTlrVYK5RrrPmY";
    private static final String CLIENT_KEY = "KrYJYXjpdFczLyzmDIRmWTwaCT2A0JwqerA4ZqKz";



    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
//  //    Test connection
//        ParseObject testObject = new ParseObject("TestObject");
//        testObject.put("foo", "bar");
//        testObject.saveInBackground();

    }
}
