package com.example.messaginghub;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.IBinder;
import android.view.OnReceiveContentListener;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.squareup.picasso.Picasso;

public class ListenOrder extends Service {
    NotificationCompat.Builder builder;
    String CHANNEL_ID="Messages_Channel";
    String CHANNEL_NAME="Messages";
    BitmapDrawable Bitmapdrawable;
    Intent intent2;
    DBhelper db=DBhelper.getInstance(ListenOrder.this);
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        //pending intent
        intent2 = new Intent(this,ChatActivity.class);
        String Currentimgid=db.fetch_data().getImgid();
        //
        intent2.putExtra("Name",intent.getStringExtra("Name"));
        intent2.putExtra("Receiverimgid",intent.getStringExtra("Receiverimgid"));
        intent2.setAction(intent.getStringExtra("Receiverimgid"));
        intent2.putExtra("Receiverphoneno", intent.getStringExtra("Receiverphoneno"));
        intent2.setAction(intent.getStringExtra("Receiverphoneno"));
        intent2.putExtra("Roomid",intent.getStringExtra("Roomid"));
        intent2.setAction(intent.getStringExtra("Roomid"));
        intent2.putExtra("Senderimgid",Currentimgid);
        intent2.setAction(Currentimgid);
        intent2.putExtra("FirstMessage", true);
        intent2.setAction("true");
        //
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent2, PendingIntent.FLAG_IMMUTABLE);
        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.msgicon)
                .setContentTitle(intent.getStringExtra("Name"))
                .setContentText(intent.getStringExtra("Message"))
                .setChannelId(CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                 .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify("Message",1,builder.build());
        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }


}
