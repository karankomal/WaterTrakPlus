package com.example.watertrak

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddActivity : AppCompatActivity() {
    lateinit var myCalendar: Calendar
    lateinit var dateET: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        dateET = findViewById(R.id.entryDateET)
        myCalendar = Calendar.getInstance()
        val date =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                updateLabel()
            }
        dateET!!.setOnClickListener {
            val dpd = DatePickerDialog(
                this,
                date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            dpd.datePicker.minDate = System.currentTimeMillis() - 1000 - (1000*60*60*24*7)
            dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
            dpd.show()
        }

        findViewById<Button>(R.id.addEntryBtn).setOnClickListener {
            lateinit var description : String
            val waterDrank = findViewById<EditText>(R.id.waterDrankET).getText().toString()
            if (waterDrank.toDouble() > 3.7)
                description = "You drank more water than needed!"
            else if (waterDrank.toDouble() == 3.7)
                description = "You drank enough water!"
            else
                description = "You did not drink enough water!"

            lifecycleScope.launch(Dispatchers.IO) {
                (application as WaterTrakApplication).db.itemDao().insert(
                    ItemEntity(
                        date = dateET.getText().toString(),
                        desc = description,
                        waterDrank = waterDrank,
                    )
                )
            }
            this.finish()
        }
    }

    private fun updateLabel() {
        val suffix = suffix(SimpleDateFormat("dd", Locale.US).format(myCalendar.getTime()))
        val myFormat = "MMMM dd'" + suffix + ", " + "'yyyy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        dateET.setText(dateFormat.format(myCalendar.getTime()))
    }

    fun suffix(day: String) : String {
        return if (day.toInt() >= 11 && day.toInt() <= 13) {
            "th"
        } else when (day.toInt() % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }
}