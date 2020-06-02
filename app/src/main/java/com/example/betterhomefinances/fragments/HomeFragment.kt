package com.example.betterhomefinances.fragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableList
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.betterhomefinances.adapters.MyGroupItemRecyclerViewAdapter
import com.example.betterhomefinances.databinding.FragmentHomeBinding
import com.example.betterhomefinances.handlers.GroupHandler
import com.example.betterhomefinances.handlers.GroupItem
import com.example.betterhomefinances.handlers.UserDetails
import com.example.betterhomefinances.handlers.UserHandler
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.tsuryo.swipeablerv.SwipeLeftRightCallback
import kotlin.math.abs


class HomeFragment : Fragment(),
    OnGroupListFragmentInteractionListener,
    SwipeLeftRightCallback.Listener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var temptext: UserDetails? = null;
    private var listener = this
    private var navController: NavController? = null;
    private lateinit var mOnListChangedCallback: ObservableList.OnListChangedCallback<ObservableList<GroupItem>>

    private var _chart: PieChart? = null
    private val chart get() = _chart!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter =
            MyGroupItemRecyclerViewAdapter(
                GroupHandler,
                listener
            )
        binding.recyclerView.setListener(this)
        navController = findNavController()

        mOnListChangedCallback =
            MyOnGroupListChangedCallback(
                this
            )

        GroupHandler.data.addOnListChangedCallback(mOnListChangedCallback)

        configurePlot()

        return binding.root
    }

    fun configurePlot() {
        //        tvX = findViewById(R.id.tvXMax)
//        tvY = findViewById(R.id.tvYMax)

//        seekBarX = findViewById(R.id.seekBar1)
//        seekBarY = findViewById(R.id.seekBar2)

//        seekBarX.setOnSeekBarChangeListener(this)
//        seekBarY.setOnSeekBarChangeListener(this)

        _chart = binding.piechart

        chart.setUsePercentValues(false)
        chart.description.isEnabled = false
        chart.setExtraOffsets(5.0F, 10.0F, 5.0F, 5.0F)

        chart.dragDecelerationFrictionCoef = 0.95f

//        chart.setCenterTextTypeface(tfLight)
//        chart.setCenterText(generateCenterSpannableText())

        chart.isDrawHoleEnabled = true
        chart.setHoleColor(Color.WHITE)

        chart.setTransparentCircleColor(Color.WHITE)
        chart.setTransparentCircleAlpha(110)

        chart.holeRadius = 58f
        chart.transparentCircleRadius = 61f

        chart.setDrawCenterText(true)


        chart.rotationAngle = 0F
        // enable rotation of the chart by touch
        // enable rotation of the chart by touch
        chart.isRotationEnabled = true
        chart.isHighlightPerTapEnabled = true

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
//        chart.setOnChartValueSelectedListener(this)

//        seekBarX.setProgress(4)
//        seekBarY.setProgress(10)

        chart.animateY(1400, Easing.EaseInOutQuad)
        // chart.spin(2000, 0, 360);

        // chart.spin(2000, 0, 360);
        val l: Legend = chart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f

        // entry label styling

        // entry label styling
        chart.setEntryLabelColor(Color.BLACK)
        chart.setEntryLabelTypeface(Typeface.DEFAULT)
        chart.setEntryLabelTextSize(12f)
        addDataToPlot()
    }


    fun addDataToPlot() {
        val entries: ArrayList<PieEntry> = ArrayList()
        val colors = mutableListOf<Int>()
        for (groupItem in GroupHandler.data) {
            if (groupItem.group.balance.balances.containsKey(UserHandler.currentUserDocumentReference.path) && abs(
                    groupItem.group.balance.balances[UserHandler.currentUserReference]!!
                ) > 0.1
            ) {
                entries.add(
                    PieEntry(
                        abs(groupItem.group.balance.balances[UserHandler.currentUserDocumentReference.path]!!.toFloat()),
                        groupItem.group.name
                    )
                )
                val v = groupItem.group.balance.balances[UserHandler.currentUserReference]!!
                if (v >= 0) {
                    colors.add(Color.parseColor("#52d053"))
                } else {
                    colors.add(Color.parseColor("#d3290f"))
                }

            }
        }


        val dataSet = PieDataSet(entries, "")

        dataSet.setDrawIcons(false)
        dataSet.colors = colors

        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0F, 40F)
        dataSet.selectionShift = 5f
        dataSet.valueTextColor = Color.BLACK

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(chart))
//        data.setValueFormatter(MyValueFormatter(chart))
        data.setValueTextSize(11f)

        data.setValueTextColor(Color.BLACK)
//        data.setValueTypeface(tfLight)
        chart.data = data
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onGroupListFragmentInteraction(v: View, item: String?) {
        val action =
            HomeFragmentDirections.actionNavHomeToNavGroupDetails(
                item!!
            )
        v.findNavController().navigate(action)
    }

    override fun onSwipedRight(position: Int) {
        val action =
            HomeFragmentDirections.actionNavHomeToCreateTransaction(
                GroupHandler.data[position].reference,
                null, 0.0F, null
            )
        navController?.navigate(action)
    }

    override fun onSwipedLeft(position: Int) {
        TODO("Not yet implemented")
    }
}

private class MyOnGroupListChangedCallback(myGroupItemRecyclerViewAdapter: HomeFragment) :
    ObservableList.OnListChangedCallback<ObservableList<GroupItem>>(
    ) {
    var MTEST: HomeFragment = myGroupItemRecyclerViewAdapter
    override fun onChanged(sender: ObservableList<GroupItem>?) {
//        TODO("Not yet implemented")
        MTEST.addDataToPlot()
    }

    override fun onItemRangeRemoved(
        sender: ObservableList<GroupItem>?,
        positionStart: Int,
        itemCount: Int
    ) {
//        TODO("Not yet implemented")
        MTEST.addDataToPlot()

    }

    override fun onItemRangeMoved(
        sender: ObservableList<GroupItem>?,
        fromPosition: Int,
        toPosition: Int,
        itemCount: Int
    ) {
//        TODO("Not yet implemented")
        MTEST.addDataToPlot()

    }

    override fun onItemRangeInserted(
        sender: ObservableList<GroupItem>?,
        positionStart: Int,
        itemCount: Int
    ) {
//        TODO("Not yet implemented")
        MTEST.addDataToPlot()

    }

    override fun onItemRangeChanged(
        sender: ObservableList<GroupItem>?,
        positionStart: Int,
        itemCount: Int
    ) {
//        TODO("Not yet implemented")
        MTEST.addDataToPlot()

    }

}



