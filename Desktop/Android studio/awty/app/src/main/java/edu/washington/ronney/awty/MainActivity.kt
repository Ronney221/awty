package edu.washington.ronney.awty

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val message = findViewById<EditText>(R.id.message) as EditText
        val phone = findViewById<EditText>(R.id.phone) as EditText
        val nagInterval = findViewById<EditText>(R.id.interval) as EditText

        val button = findViewById<Button>(R.id.button)

        val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)

        button.setOnClickListener {
            if (button.text == "START") {
                //see if all 3 are filled out
                //an EditText for the message I want to send,
                //an EditText for the phone number to which to send it (which we will not do anything with for this part),
                //an EditText for how many minutes between each nag (no zeros, no negatives, must be an integer),

                if (!message.text.isEmpty() &&
                     phone.text.length == 10 &&
                        (!nagInterval.text.isEmpty() && nagInterval.text.toString().toInt() > 0)) {
                    button.text = "STOP"


                    //should be Toast messages using the format: "(425) 555-1212: Are we there yet?".
                    //substring is non inclusive
                    val toast = ("(" + phone.text.toString().substring(0, 3) + ") " + phone.text.toString().substring(3, 6)
                    + "-" + phone.text.toString().substring(6) + ": " + message.text.toString())

                    intent.putExtra("toast", toast)

                    alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                            nagInterval.text.toString().toInt().toLong() * 1000 * 60,
                            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                } else {
                    //could have more than one thing wrong
                    if (message.text.isEmpty()) {
                        Toast.makeText(this, "CANNOT START, message is empty", Toast.LENGTH_SHORT).show()
                    }

                    if (phone.text.isEmpty()) {
                        Toast.makeText(this, "CANNOT START, invalid PHONE NUMBER. Enter 10 digits", Toast.LENGTH_SHORT).show()
                    } else if (phone.text.length != 10){
                        Toast.makeText(this, "CANNOT START, invalid PHONE NUMBER. Enter 10 digits", Toast.LENGTH_SHORT).show()
                    }

                    if (nagInterval.text.isEmpty()) {
                        Toast.makeText(this, "CANNOT START, empty NAG INTERVAL. Enter an integer greater than 0", Toast.LENGTH_SHORT).show()
                    } else if (nagInterval.text.toString().toInt() <= 0){
                        Toast.makeText(this, "CANNOT START, invalid NAG INTERVAL. Enter an integer greater than 0", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                button.text = "START"
                alarm.cancel(PendingIntent.getBroadcast(this, 0, intent, 0))
            }
        }

    }
}

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val toast= intent.getStringExtra("toast")
        Toast.makeText(context, toast, Toast.LENGTH_LONG).show()
    }
}
