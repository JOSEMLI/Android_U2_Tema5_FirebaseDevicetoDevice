package com.example.android_u2_tema5_firebasedevicetodevice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
  EditText edtTitle;
  EditText edtMessage;
  final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
  final private String serverKey = "key=" + "AAAAf-DMsDk:APA91bHyncC13ojsBuAWA4IL_DGQevDCXQuh9FdOjXXXpYWHPVPkaVUqtDaaoeo3KNmMtaA9ENuWdNhZ9Xxxz6T1_MJJomAUlyHAGWc3P0ODF4jvCe0WUj8arNzLzfoKhumETtxo4t7G";
  final private String contentType = "application/json";
  final String TAG = "NOTIFICATION TAG";
  String NOTIFICATION_TITLE;
  String NOTIFICATION_MESSAGE;
  String TOPIC = "/topics/userABC";
  String SUBSCRIBE_TO = "userABC";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    edtTitle = findViewById(R.id.edtTitle);
    edtMessage = findViewById(R.id.edtMessage);
    Button btnSend = findViewById(R.id.btnSend);
    btnSend.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        TOPIC = "/topics/userABC"; //topic has to match what the receiver subscribed to
        NOTIFICATION_TITLE = edtTitle.getText().toString();
        NOTIFICATION_MESSAGE = edtMessage.getText().toString();
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
          notifcationBody.put("title", NOTIFICATION_TITLE);
          notifcationBody.put("message", NOTIFICATION_MESSAGE);
          notification.put("to", TOPIC);
          notification.put("data", notifcationBody);
        } catch (JSONException e) {
          Log.e(TAG, "onCreate: " + e.getMessage());
        }
        sendNotification(notification);
      }
    });
    FirebaseInstanceId.getInstance().getInstanceId()
        .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
          @Override
          public void onSuccess(InstanceIdResult instanceIdResult) {
            Log.d("token_Id", instanceIdResult.getToken());
            FirebaseMessaging.getInstance().subscribeToTopic(SUBSCRIBE_TO);
          }
        });
  }

  private void sendNotification(JSONObject notification) {
    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
        new Response.Listener<JSONObject>() {
          @Override
          public void onResponse(JSONObject response) {
            Log.i(TAG, "onResponse: " + response.toString());
            edtTitle.setText("");
            edtMessage.setText("");
          }
        },
        new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
            Toast.makeText(MainActivity.this, "Request error", Toast.LENGTH_LONG).show();
            Log.i(TAG, "onErrorResponse: Didn't work");
          }
        }) {
      @Override
      public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> params = new HashMap<>();
        params.put("Authorization", serverKey);
        params.put("Content-Type", contentType);
        return params;
      }
    };
    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
  }
}
