
package ca.bigdata.voice.contacts;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BDVSimpleContactsModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private static final String TAG = "BDVSimpleContactsModule";


    public BDVSimpleContactsModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "BDVSimpleContacts";
    }

    @ReactMethod
    public void getContacts(final Promise promise) {
        Log.d(TAG, "getContacts");
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        this.getContactList(uri, promise);
    }

    @ReactMethod
    public void getContactsByFilter(final String filter, final Promise promise) {
        Log.d(TAG, "getContactsByFilter");
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, Uri.encode(filter));
        this.getContactList(uri, promise);
    }

    private void getContactList(final Uri uri, final Promise promise) {
        Log.d(TAG, "getContacts");

        Thread thread = new Thread() {
            @Override
            public void run() {
                Activity ca = getCurrentActivity();
                if (ca == null) {
                    promise.reject("1", "Null activity");
                    return;
                }
                ContentResolver cr = ca.getContentResolver();

                JSONArray jsonA = new JSONArray();

                Cursor cursor = cr.query(
                        uri,
                        new String[]{
                                ContactsContract.Contacts.DISPLAY_NAME,
                                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
                                ContactsContract.Contacts._ID
                        },
                        null, null, null);
                try {
                    while (cursor.moveToNext()) {
                        String contactID = cursor.getString(
                                cursor.getColumnIndex(ContactsContract.Contacts._ID)
                        );
                        Cursor cursorPhone = ca.getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                                new String[]{contactID},
                                null
                        );
                        try {
                            if (cursorPhone.moveToFirst()) {
                                JSONObject json = new JSONObject();
                                json.put(
                                        "key",
                                        "contact_" + contactID
                                );
                                json.put(
                                        "name",
                                        cursor.getString(
                                                cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                                        )
                                );
                                json.put(
                                        "avatar",
                                        cursor.getString(
                                                cursor.getColumnIndex(
                                                        ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
                                                )
                                        )
                                );
                                json.put(
                                        "number",
                                        cursorPhone.getString(
                                                cursorPhone.getColumnIndex(
                                                        ContactsContract.CommonDataKinds.Phone.NUMBER
                                                )
                                        )
                                );
                                jsonA.put(json);
                            }
                        } catch (JSONException exc) {
                            Log.e(TAG, exc.toString());
                        } finally {
                            cursorPhone.close();
                        }
                    }
                } finally {
                    cursor.close();
                }
                promise.resolve(jsonA.toString());
            }
        };
        thread.start();
    }

    @ReactMethod
    public void getProfile(Promise promise) {
        ContentResolver cr = getCurrentActivity().getContentResolver();
        Cursor cursor = cr.query(
                ContactsContract.Profile.CONTENT_URI,
                new String[]{
                        ContactsContract.Profile.DISPLAY_NAME,
                        ContactsContract.Profile.PHOTO_THUMBNAIL_URI
                },
                null, null, null
        );
        JSONObject json = new JSONObject();

        if (cursor.moveToFirst()) {
            try {
                json.put(
                        "display",
                        cursor.getString(
                                cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
                        )
                );
                json.put(
                        "avatar",
                        cursor.getString(
                                cursor.getColumnIndex(
                                        ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI
                                )
                        )
                );
            } catch (JSONException exc) {
                Log.e(TAG, exc.toString());
            }
        } else {
            Log.d(TAG, "Error retrieving profile");
        }
        cursor.close();
        promise.resolve(json.toString());
    }

    @ReactMethod
    public void findContactByNumber(String number, Promise promise) {
        ContentResolver cr = getCurrentActivity().getContentResolver();
        Uri uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number)
        );

        JSONObject json = new JSONObject();

        Cursor cursor = cr.query(
                uri,
                new String[]{
                        ContactsContract.PhoneLookup.DISPLAY_NAME,
                        ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI
                },
                null, null, null
        );
        if (cursor.moveToFirst()) {
            try {
                json.put(
                        "display",
                        cursor.getString(
                                cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
                        )
                );
                json.put(
                        "avatar",
                        cursor.getString(
                                cursor.getColumnIndex(
                                        ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI
                                )
                        )
                );
            } catch (JSONException exc) {
                Log.e(TAG, exc.toString());
            }

        } else {
            Log.d(TAG, "Contact: Not found.");
        }
        cursor.close();
        promise.resolve(json.toString());
    }

}