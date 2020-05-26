package com.example.betterhomefinances

import android.R
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.betterhomefinances.databinding.FragmentHomeBinding
import com.example.betterhomefinances.handlers.GroupHandler
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


class HomeFragment : Fragment(), OnGroupListFragmentInteractionListener,
    SwipeLeftRightCallback.Listener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var temptext: UserDetails? = null;
    private var listener = this
    private var navController: NavController? = null;

    private var _chart: PieChart? = null
    private val chart get() = _chart!!

    private var seekBarX: SeekBar? = null
    private var seekBarY: SeekBar? = null
    private var tvX: TextView? = null
    private var tvY: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        UserHandler.userDetails(
            fun(u: UserDetails) {
                temptext = u
//                binding.textHome.text = temptext?.settings?.tempstuff!!
            },
            fun() {
                UserHandler.initiateUserDetails()
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = MyGroupItemRecyclerViewAdapter(GroupHandler, listener)
        binding.recyclerView.setListener(this)
        navController = findNavController()


//        tvX = findViewById(R.id.tvXMax)
//        tvY = findViewById(R.id.tvYMax)

//        seekBarX = findViewById(R.id.seekBar1)
//        seekBarY = findViewById(R.id.seekBar2)

//        seekBarX.setOnSeekBarChangeListener(this)
//        seekBarY.setOnSeekBarChangeListener(this)

        _chart = binding.piechart

        chart.setUsePercentValues(true)
        chart.getDescription().setEnabled(false)
        chart.setExtraOffsets(5.0F, 10.0F, 5.0F, 5.0F)

        chart.setDragDecelerationFrictionCoef(0.95f)

//        chart.setCenterTextTypeface(tfLight)
//        chart.setCenterText(generateCenterSpannableText())

        chart.setDrawHoleEnabled(true)
        chart.setHoleColor(Color.WHITE)

        chart.setTransparentCircleColor(Color.WHITE)
        chart.setTransparentCircleAlpha(110)

        chart.setHoleRadius(58f)
        chart.setTransparentCircleRadius(61f)

        chart.setDrawCenterText(true)

        chart.setRotationAngle(0F)
        // enable rotation of the chart by touch
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true)
        chart.setHighlightPerTapEnabled(true)

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
        val l: Legend = chart.getLegend()
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP)
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT)
        l.setOrientation(Legend.LegendOrientation.VERTICAL)
        l.setDrawInside(false)
        l.setXEntrySpace(7f)
        l.setYEntrySpace(0f)
        l.setYOffset(0f)

        // entry label styling

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE)
//        chart.setEntryLabelTypeface(tfRegular)
        chart.setEntryLabelTextSize(12f)
        val entries: ArrayList<PieEntry> = ArrayList()

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        var count = 10
        var range = 2.0
        for (i in 0 until count) {
            entries.add(
                PieEntry(
                    (Math.random() * range + range / 5).toFloat(),
                    "yeet",
                    resources.getDrawable(R.drawable.divider_horizontal_dark)
                )
            )
        }

        val dataSet = PieDataSet(entries, "Election Results")

        dataSet.setDrawIcons(false)

        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0F, 40F)
        dataSet.selectionShift = 5f

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(chart))
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
//        data.setValueTypeface(tfLight)
        chart.data = data

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onGroupListFragmentInteraction(v: View, item: String?) {
        val action = HomeFragmentDirections.actionNavHomeToNavGroupDetails(item!!)
        v.findNavController().navigate(action)
    }

    override fun onSwipedRight(position: Int) {
        val action = HomeFragmentDirections.actionNavHomeToCreateTransaction(
            GroupHandler.data[position].reference,
            null
        )
        navController?.navigate(action)
    }

    override fun onSwipedLeft(position: Int) {
        TODO("Not yet implemented")
    }
}

