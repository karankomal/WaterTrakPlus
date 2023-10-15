package com.example.watertrak

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.watertrak.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var itemsRV: RecyclerView
    private lateinit var binding: ActivityMainBinding
    private val items = mutableListOf<DisplayItem>()
    //private var avg : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        itemsRV = findViewById(R.id.items)
        val adapter = ItemAdapter(this, items)
        itemsRV.adapter = adapter
        itemsRV.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            (application as WaterTrakApplication).db.itemDao().getAll().collect { databaseList ->
                databaseList.map { entity ->
                    DisplayItem(
                        entity.date,
                        entity.desc,
                        entity.waterDrank,
                    )
                }.also { mappedList ->
                    items.clear()
                    items.addAll(mappedList)
                    adapter.notifyDataSetChanged()
                    recalculateAvg()
                }
            }
        }

        findViewById<Button>(R.id.addBtn).setOnClickListener {
            val options : ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, androidx.core.util.Pair(findViewById<StrokedTextView>(R.id.watertrakLogo), "logo"),
                             androidx.core.util.Pair(findViewById<Button>(R.id.addBtn), "button"))
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent, options.toBundle())
        }
    }
    fun recalculateAvg() {
        lifecycleScope.launch(Dispatchers.IO) {
            (application as WaterTrakApplication).db.itemDao().average().also{
                findViewById<TextView>(R.id.averageDrank).text = String.format("%.2f", it) + "L"
                var avgDesc = ""
                if (it == 3.7)
                    avgDesc = "You're drinking exactly enough on average!"
                else if (it < 3.7)
                    avgDesc = "You should drink more on average!"
                else
                    avgDesc = "You drink more than you need to on average!"
                findViewById<TextView>(R.id.averageDesc).text = avgDesc
            }
        }
    }
}