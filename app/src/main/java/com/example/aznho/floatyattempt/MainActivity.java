package com.example.aznho.floatyattempt;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bezyapps.floatieslibrary.Floaty;
import com.bezyapps.floatieslibrary.FloatyOrientationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Floaty pokeballFloaty;
    Button button_start, button_stop;
    LatLng latLng = new LatLng(37,-121);
    private static final int NOTIFICATION_ID = 1500;
    public static final int PERMISSION_REQUEST_CODE = 16;
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = MainActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_start = (Button) findViewById(R.id.button_start);
        button_stop = (Button) findViewById(R.id.button_stop);
        Intent intent = new Intent(this, MainActivity.class);

        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        Log.e(TAG, "PermissionGranted for Fine?  "+ Boolean.toString(permissionGranted));
        if(permissionGranted) {
            // {Some Code}
            Log.e(TAG, "PermissionGranted for Fine dont ask");
        } else {

            Log.e(TAG, "PermissionGranted for Fine try to ask");

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = Floaty.createNotification(this, "Floaty Demo", "Service Running", R.drawable.pokeball, resultPendingIntent);

        // Inflate the Views that are to be used as HEAD and BODY of The Window
        View head = LayoutInflater.from(this).inflate(R.layout.float_head, null);
        // You should not add click listeners to head as it will be overridden, but the purpose of not making head just
        // an ImageView is so you can add multiple views in it, and show and hide the relevant views to notify user etc.
        View body = LayoutInflater.from(this).inflate(R.layout.float_body, null);

        // Access child views of body
        final ListView listView = (ListView) body.findViewById(R.id.listViewTasks);
//        final EditText editText = (EditText) body.findViewById(R.id.editText_task);
        Button button = (Button) body.findViewById(R.id.button_save);

        String pokemon_names = new String(getResources().getString(R.string.pokemon_names_list));
        String[] pokemon_names_list = pokemon_names.split(",");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, pokemon_names_list);
        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) body.findViewById(R.id.editText_task);
        autoCompleteTextView.setAdapter(adapter);

        ArrayList<String> strings = new ArrayList<>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strings);

        listView.setAdapter(arrayAdapter);

        // Get the instance of the Floaty
        pokeballFloaty = Floaty.createInstance(this, head, body, NOTIFICATION_ID, notification, new FloatyOrientationListener() {
            @Override
            public void beforeOrientationChange(Floaty floaty) {
                Toast.makeText(MainActivity.this, "Orientation Change Start", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterOrientationChange(Floaty floaty) {
                Toast.makeText(MainActivity.this, "Orientation Change End", Toast.LENGTH_SHORT).show();
            }
        });


        // Set your domain specific logic to body's children
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = autoCompleteTextView.getText().toString().trim();
                if (!item.isEmpty()) {
                    autoCompleteTextView.setText("");
                    arrayAdapter.add(item);
                    arrayAdapter.notifyDataSetChanged();
                }

                RequestParams params = new RequestParams("payload", "{\"text\":\"" +item+" at  https://maps.google.com/maps?q=loc:"+Double.toString(latLng.latitude)+","+Double.toString(latLng.longitude)+"\"}");

                SlackWebhookRestClient.post("",params,new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.e(TAG, "On Success: "+Integer.toString(statusCode));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        Log.e(TAG, "On failure: "+Integer.toString(statusCode) + " response: " + res);
                    }
                });
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                arrayAdapter.remove(arrayAdapter.getItem(position));
                arrayAdapter.notifyDataSetChanged();
                return false;
            }
        });


        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    startFloatyForAboveAndroidL();
                } else {
                    pokeballFloaty.startService();
                }
            }
        });

        button_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pokeballFloaty.stopService();
            }
        });

        if (null == mGoogleApiClient){
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void startFloatyForAboveAndroidL() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, PERMISSION_REQUEST_CODE);
        } else {
            pokeballFloaty.startService();
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                pokeballFloaty.startService();
            } else {
                Spanned message = Html.fromHtml("Please allow this permission, so <b>Floaties</b> could be drawn.");
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 200: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // {Some Code}
                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle){
        Log.i(TAG, "Location services connected. "
                + " (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION): " + (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                + " (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION): " + (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED));
        if ((checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                || (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Log.e(TAG,location == null? "no location" : location.toString());
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
            else {
                handleNewLocation(location);
            };
        }
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, "handleNewLocation"+location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        latLng = new LatLng(currentLatitude, currentLongitude);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onResume(){
        super.onResume();
//        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location services changed.");
        handleNewLocation(location);
    }
}
