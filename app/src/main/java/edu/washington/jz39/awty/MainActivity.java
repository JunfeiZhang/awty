package edu.washington.jz39.awty;

import android.app.Activity;
import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.content.ContextCompat;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {
    private boolean started;
    Button start;
    EditText message;
    EditText phoneNumber;
    EditText interval;
    private final static int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    PendingIntent alarm = null;
    BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            String number = intent.getStringExtra("number");
            //Toast.makeText(MainActivity.this, number + ": " + message, Toast.LENGTH_SHORT).show();

            try{
                SmsManager sm = SmsManager.getDefault();
                sm.sendTextMessage(number, null, message, null, null);
                Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Send failed", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
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

        //current = this;
        started = false;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.SEND_SMS)) {
                } else {
                    ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.SEND_SMS},MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
        }

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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "Send SMS permission is required for this app.", Toast.LENGTH_SHORT).show();
                }
            }
        }
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
