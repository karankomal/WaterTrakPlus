package com.example.watertrak

import android.graphics.Typeface
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.math.roundToInt


class GraphFragment : Fragment() {
    private lateinit var chart: BarChart
    private lateinit var avgDrank: TextView
    private lateinit var avgDescTV: TextView
    private lateinit var minDrank: TextView
    private lateinit var maxDrank: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_graph, container, false)

        chart = view.findViewById(R.id.chart) as BarChart
        avgDrank = view.findViewById(R.id.averageDrank)
        avgDescTV = view.findViewById(R.id.averageDesc)
        minDrank = view.findViewById(R.id.minDrank)
        maxDrank = view.findViewById(R.id.maxDrank)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDetails()
        createGraph()
    }

    companion object {
        fun newInstance() : GraphFragment {
            return GraphFragment()
        }
    }

    private fun getDetails() {
        lifecycleScope.launch(Dispatchers.IO) {
            (activity!!.application as WaterTrakApplication).db.itemDao().average().also{
                avgDrank.text = String.format("%.2f", it) + "L"
                var avgDesc = ""
                if (it == 3.7)
                    avgDesc = "You're drinking exactly enough on average!"
                else if (it < 3.7)
                    avgDesc = "You should drink more on average!"
                else
                    avgDesc = "You drink more than you need to on average!"
                avgDescTV.text = avgDesc
            }
            (activity!!.application as WaterTrakApplication).db.itemDao().min().also{
                minDrank.text = String.format("%.2f", it) + "L"
            }
            (activity!!.application as WaterTrakApplication).db.itemDao().max().also{
                maxDrank.text = String.format("%.2f", it) + "L"
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createGraph() {
        val entries = mutableListOf<BarEntry>()
        val dates = mutableListOf<String>()
        CoroutineScope(Dispatchers.IO + Job()).launch {
            (activity!!.application as WaterTrakApplication).db.itemDao().getData().also{ databaseList ->
                var count = -1.0f
                databaseList.map { entity ->
                    dates.add(entity.date!!)
                    count += 1.0f
                    BarEntry(
                        count,
                        entity.waterDrank!!.toFloat(),
                    )
                }.also { mappedList ->
                    entries.clear()
                    entries.addAll(mappedList)

                    val dataset = BarDataSet(entries, "Water Drank")
                    val data = BarData(dataset)

                    chart.data = data

                    val formatterX: ValueFormatter = object : ValueFormatter() {
                        override fun getAxisLabel(value: Float, axis: AxisBase): String {
                            val month = dates.get(value.toInt()).split(" ")[0]
                            val day = dates.get(value.toInt()).split(" ")[1].substring(0,2)
                            val year = dates.get(value.toInt()).split(" ")[2].substring(2,)
                            return convertMonthToNumber(month).toString() + "/" + day + "/" + year
                        }
                    }
                    chart.xAxis.granularity = 1f
                    chart.xAxis.valueFormatter = formatterX
                    chart.xAxis.setDrawAxisLine(false)
                    chart.xAxis.setDrawGridLines(false)
                    chart.xAxis.position = XAxis.XAxisPosition.BOTTOM

                    chart.axisRight.isEnabled = false
                    val formatterY: ValueFormatter = object : ValueFormatter() {
                        override fun getAxisLabel(value: Float, axis: AxisBase): String {
                            return value.toString() + "L"
                        }
                    }
                    chart.axisLeft.valueFormatter = formatterY

                    chart.description.isEnabled = true
                    chart.description.setPosition(675f, 47f)
                    chart.description.text = "Water Drank"

                    val abstergo = resources.getFont(R.font.abstergo)
                    chart.data.setValueTypeface(abstergo)
                    chart.data.setValueTextSize(12f)
                    chart.axisLeft.typeface = abstergo
                    chart.axisLeft.textSize = 12f
                    chart.xAxis.typeface = abstergo
                    chart.xAxis.textSize = 12f
                    chart.description.typeface = abstergo
                    chart.description.textSize = 20f

                    chart.setDrawBorders(false)
                    chart.setDrawGridBackground(false)
                    chart.legend.isEnabled = false

                    chart.invalidate()
                }
            }
        }
    }
    private fun convertMonthToNumber(month: String): Int {
        val months = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        return months.indexOf(month) + 1
    }
}