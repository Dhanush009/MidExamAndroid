package com.ds.bot;

import androidx.appcompat.app.AppCompatActivity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Collections;
import java.util.Objects;
import com.ds.bot.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String userName = "username";
    private NotificationManager notificationManager;
    private NotificationDecorator notificationDecorator;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationDecorator = new NotificationDecorator(this, notificationManager);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.ds.bot");
        registerReceiver(myBroadcastReceiver, intentFilter);

        list = (ListView)findViewById(R.id.listView);


        binding.generateMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.nameEditText.getText().toString();
                if (name != null) {
                    userName = name;
                } else {
                    userName = "username";
                }
                Bundle data = new Bundle();
                data.putInt(BotService.CMD, BotService.GENERATE_MSG);
                data.putString(BotService.KEY_USER_NAME, userName);
                Intent intent = new Intent(MainActivity.this, BotService.class);
                intent.putExtras(data);
                startService(intent);
            }
        });

        binding.stopServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putInt(BotService.CMD, BotService.STOP_SERVICE);
                Intent intent = new Intent(MainActivity.this, BotService.class);
                intent.putExtras(data);
                startService(intent);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }

    private final BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle data = intent.getExtras();
            if (Objects.equals(action, "com.ds.bot")) {
                if(data.getString("stopping") == "no"){
                    String[] msgs = data.getStringArray("messages");
                    binding.listView.setDivider(null);
                    ArrayAdapter<String> messages = new ArrayAdapter<String>(MainActivity.this, R.layout.activity_list, R.id.messageTextView, msgs);
                    binding.listView.setAdapter(messages);
                }else{
                    String[] msgs = data.getStringArray("messages");
                    ArrayAdapter<String> messages = new ArrayAdapter<String>(MainActivity.this, R.layout.activity_list, R.id.messageTextView, msgs);
                    binding.listView.setAdapter(messages);
                    binding.nameEditText.setText("");
                    Toast.makeText(getApplicationContext(),"ChatBot Service Stopped",Toast.LENGTH_SHORT).show();
                }

            }
        }
    };
}
