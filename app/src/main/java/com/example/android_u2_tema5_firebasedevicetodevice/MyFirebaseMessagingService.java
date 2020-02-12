package com.example.android_u2_tema5_firebasedevicetodevice;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
  private final String ADMIN_CHANNEL_ID = "admin_channel";
  String SUBSCRIBE_TO = "userABC";

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    final Intent intent = new Intent(this, MainActivity.class);
    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    int notificationID = new Random().nextInt(3000);
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      setupChannels(notificationManager);
    }
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
        PendingIntent.FLAG_ONE_SHOT);
    Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
        R.drawable.notify_icon);
    Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
        .setSmallIcon(R.drawable.notify_icon)
        .setLargeIcon(largeIcon)
        .setContentTitle(remoteMessage.getData().get("title"))
        .setContentText(remoteMessage.getData().get("message"))
        .setAutoCancel(true)
        .setSound(notificationSoundUri)
        .setContentIntent(pendingIntent);
    notificationManager.notify(notificationID, notificationBuilder.build());
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  private void setupChannels(NotificationManager notificationManager){
    CharSequence adminChannelName = "New notification";
    String adminChannelDescription = "Device to devie notification";
    NotificationChannel adminChannel;
    adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
    adminChannel.setDescription(adminChannelDescription);
    adminChannel.enableLights(true);
    adminChannel.setLightColor(Color.RED);
    adminChannel.enableVibration(true);
    if (notificationManager != null) {
      notificationManager.createNotificationChannel(adminChannel);
    }
  }
  @Override
  public void onNewToken(String token) {
    super.onNewToken(token);
    sendRegistrationToServer(token);
  }
  private void sendRegistrationToServer(String token) {
    Log.d("newToken_ID", token);
    FirebaseMessaging.getInstance().subscribeToTopic(SUBSCRIBE_TO);
  }
}


