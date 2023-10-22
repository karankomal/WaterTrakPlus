package com.example.watertrak

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yangp.ypwaveview.YPWaveView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class WaterListFragment : Fragment() {
    private lateinit var itemsRV: RecyclerView
    private lateinit var adapter: ItemAdapter
    private val items = mutableListOf<DisplayItem>()
    private lateinit var waveview: YPWaveView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_water_list, container, false)

        itemsRV = view.findViewById<RecyclerView>(R.id.items)

        itemsRV.layoutManager = LinearLayoutManager(view.context)
        adapter = ItemAdapter(view.context, items)
        itemsRV.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Call the new method within onViewCreated
        fetchList()
    }
    companion object {
        fun newInstance(): WaterListFragment {
            return WaterListFragment()
        }
    }
    private fun fetchList() {
        CoroutineScope(Dispatchers.Main + Job()).launch {
            (activity!!.application as WaterTrakApplication).db.itemDao().getAll().collect { databaseList ->
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
                }
            }
        }
    }
}