package edu.washington.ronney.awty

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.telephony.SmsManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.support.v4.content.ContextCompat.startActivity
import java.io.File
import android.os.StrictMode
import android.provider.Telephony


class MainActivity : AppCompatActivity() {

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         val builder = StrictMode.VmPolicy.Builder()
         StrictMode.setVmPolicy(builder.build())

        val message = findViewById<EditText>(R.id.message) as EditText
        val phone = findViewById<EditText>(R.id.phone) as EditText
        val nagInterval = findViewById<EditText>(R.id.interval) as EditText

         val button = findViewById<Button>(R.id.button)
         val audioButton = findViewById<Button>(R.id.button2)
         val videoButton = findViewById<Button>(R.id.button3)

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
                    intent.putExtra("message", message.text.toString())
                    intent.putExtra("phone", phone.text.toString())


                    alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                            nagInterval.text.toString().toInt().toLong() * 1000 * 60,
                            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))

                    Log.i("alarm", "set repeating succesful")
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

         audioButton.setOnClickListener{
             /* Attach Url is local (!) URL to file which should be sent */
             val audioAttachURL = "file:///sdcard/audio.mp3"

             Toast.makeText(this, audioAttachURL, Toast.LENGTH_LONG).show()

             val audioIntent = Intent(Intent.ACTION_SEND)
             audioIntent.setPackage(Telephony.Sms.getDefaultSmsPackage(this))
             audioIntent.putExtra("address", "5555215554")
             audioIntent.putExtra("sms_body", "SENDING AUDIO FILE")

             audioIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(audioAttachURL))
             audioIntent.type = "audio/mp3"
             audioIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
             startActivity(this, audioIntent, null)
         }

         videoButton.setOnClickListener{
             val videoAttachURL = "file:///sdcard/video.mp4"

             Toast.makeText(this, videoAttachURL, Toast.LENGTH_LONG).show()

             val videoIntent = Intent(Intent.ACTION_SEND)
             videoIntent.setPackage(Telephony.Sms.getDefaultSmsPackage(this))
             videoIntent.putExtra("subject", "video")
             videoIntent.putExtra("address", "5555215554")
             videoIntent.putExtra("sms_body", "SENDING VIDEO FILE")

             videoIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(videoAttachURL))
             videoIntent.type = "video/mp4"
             videoIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
             startActivity(this, videoIntent, null)
         }

    }
}

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val toast= intent.getStringExtra("toast")
        val message= intent.getStringExtra("message")
        val phone= intent.getStringExtra("phone")
        Toast.makeText(context, toast, Toast.LENGTH_LONG).show()

        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phone, null, message, null, null)

        Log.i("check", "message sent")


/* Attach Url is local (!) URL to file which should be sent */
      /*  val strAttachUrl = "file:///sdcard/sound.mp3"

/* Attach Type is a content type of file which should be sent */
        val strAttachType = "audio/*"

        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.setClassName("com.android.mms", "com.android.mms.ui.ComposeMessageActivity")
        sendIntent.putExtra("address", "5555215554")
        sendIntent.putExtra("sms_body", "Audio File")

/* Adding The Attach */
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(strAttachUrl))
        sendIntent.type = strAttachType
        sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(context, sendIntent, null)*/ */

    }
}