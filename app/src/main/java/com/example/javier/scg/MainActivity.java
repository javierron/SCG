package com.example.javier.scg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.estimote.coresdk.common.requirements.DefaultRequirementsCheckerCallback;
import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.R.id.list;


public class MainActivity extends AppCompatActivity {

    private BeaconManager beaconManager;
    private BeaconRegion region;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Button button = (Button)findViewById(R.id.connect);

        button.setEnabled(false);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("arg", "on");

                String URL = "https://api.particle.io/v1/devices/320033000d51353532343635/led?access_token=8d7dfb5db6e116b75f1ff1dff2ee4d5212b59c8e";

                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method
                        .POST, URL, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                TextView t = (TextView) findViewById(R.id.req_response);
                                t.setText(response.toString());  //Log
                            }
                        },
                        new Response.ErrorListener()

                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                //Log

                                TextView t = (TextView) findViewById(R.id.req_response);
                                t.setText("ERROR: " + Integer.toString(error.networkResponse.statusCode));

                            }

                        }


                )
                {
                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }



                }
                ;
                TextView t = (TextView) findViewById(R.id.req_response);

                String str = new String(jsObjRequest.getBody(), StandardCharsets.UTF_8);
                t.setText(str);


                requestQueue.add(jsObjRequest);
            }
        });

        beaconManager = new BeaconManager(this);

        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
            @Override
            public void onBeaconsDiscovered(BeaconRegion region, List<Beacon> list) {

                Button b;
                b = (Button) findViewById(R.id.connect);
                b.setEnabled(!list.isEmpty());
            }
        });


        region = new BeaconRegion("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);






    }

    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);

        super.onPause();
    }


}
