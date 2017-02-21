package edu.washington.jz39.awty;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private boolean started;
    Button start;
    EditText message;
    EditText phoneNumber;
    EditText interval;

    PendingIntent alarm = null;
    BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            String number = intent.getStringExtra("number");
            Toast.makeText(MainActivity.this, number + ": " + message, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = (Button) findViewById(R.id.startbtn);
        message = (EditText) findViewById(R.id.message);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        interval = (EditText) findViewById(R.id.interval);
        
        started = false;

        start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String intervalS = interval.getText().toString();
                String messageS = message.getText().toString();
                String numberS = phoneNumber.getText().toString();
                Log.i("onClick()", messageS + " to " + numberS + " every " + intervalS + " mins");

                registerReceiver(alarmReceiver, new IntentFilter("Sound"));
                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                if(started){
                    started = false;
                    start.setText("Start");
                    start.setBackgroundColor(Color.rgb(49,81,181));
                    am.cancel(alarm);
                    alarm.cancel();
                }else if(intervalS.matches("^[1-9]+[0-9]*$") && !messageS.isEmpty() && !numberS.isEmpty()){
                    start.setText("Stop");
                    start.setBackgroundColor(Color.rgb(255,0,0));
                    started = true;
                    int milliSeconds = Integer.parseInt(intervalS) * 1000 * 60;

                    Intent i = new Intent();
                    i.putExtra("number", numberS);
                    i.putExtra("message", messageS);
                    i.setAction("Sound");

                    alarm = PendingIntent.getBroadcast(MainActivity.this, 0, i, 0);
                    am.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + milliSeconds, milliSeconds, alarm);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
