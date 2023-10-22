package com.example.watertrak

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.addBtn).setOnClickListener {
            val options : ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, androidx.core.util.Pair(findViewById<StrokedTextView>(R.id.watertrakLogo), "logo"),
                             androidx.core.util.Pair(findViewById<Button>(R.id.addBtn), "button"))
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent, options.toBundle())
        }

        val waterListFragment: Fragment = WaterListFragment()
        val graphFragment: Fragment = GraphFragment()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // handle navigation selection
        bottomNavigationView.setOnItemSelectedListener { item ->
            lateinit var fragment: Fragment
            when (item.itemId) {
                R.id.nav_entries -> fragment = waterListFragment
                R.id.nav_graph -> fragment = graphFragment
            }
            replaceFragment(fragment)
            true
        }

        // Set default selection
        bottomNavigationView.selectedItemId = R.id.nav_entries

        sendDailyNotification(this)
    }
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.list_frame_layout, fragment)
        fragmentTransaction.commit()
    }
    fun sendDailyNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel("daily_notification", "Daily Notification", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // Create a notification
        val notificationBuilder = NotificationCompat.Builder(context, "daily_notification")
            .setContentTitle("Daily Reminder")
            .setContentText("Remember to input your water consumption!")
            .setSmallIcon(R.drawable.droplet)
            .setAutoCancel(true)

        // Set the notification time
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 21)
        calendar.set(Calendar.MINUTE, 53)
        calendar.set(Calendar.SECOND, 0)

        // Set the notification time
        notificationBuilder.setWhen(calendar.getTimeInMillis())

        // Send the notification
        notificationManager.notify(0, notificationBuilder.build())
    }
}