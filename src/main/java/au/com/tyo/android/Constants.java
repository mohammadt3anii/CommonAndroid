package au.com.tyo.android;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 18/4/17.
 */

public interface Constants {

    String ACTIVITY_EXTRA = "ACTIVITY_EXTRA";

    int MESSAGE_NETWORK_READY = 88888;

    int MESSAGE_NETWORK_DISCONNECTED = 99999;

    int MESSAGE_SERVICE_PERMISSION_GRANTED = 1235;

    int MESSAGE_SERVICE_REGISTER_CLIENT = 1234;
    
    int MESSAGE_SERVICE_UNREGISTER_CLIENT = 4321;

    /**
     * Activity Communication
     */
    String DATA = "TYODROID_DATA";
    String BUNDLE = "TYODROID_BUNDLE";
    String RESULT = "TYODROID_RESULT";

    int REQUEST_CODE_PICK_DOCUMENT = 6666;
    int REQUEST_CODE_PICK_IMAGE = 6667;

}
