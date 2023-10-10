package com.example.obliquelitterkotlin

import TrajectoryAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.*
import com.example.obliquelitterkotlin.databinding.ActivityMainBinding
import com.example.obliquelitterkotlin.TrajectoryPoint
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.obliquelitterkotlin.R.*
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.PointsGraphSeries
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    private val trajectoryPoints = mutableListOf<TrajectoryPoint>()
    private val trajectoryAdapter = TrajectoryAdapter(trajectoryPoints)

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val graphView: GraphView = findViewById(R.id.idGraphView)

        binding.angleText.text = "Angle: ${binding.angleSeekbar.progress}°"
        binding.speedText.text = "speed: ${binding.speedSeekbar.progress} m/s"


        binding.angleSeekbar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                binding.angleText.text = "Angle: $progress°"
            }



            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })

        binding.speedSeekbar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                binding.speedText.text = "speed: $progress m/s"
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })

        binding.dataPointsList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = trajectoryAdapter
        }


        binding.startButton.setOnClickListener {

            trajectoryPoints.clear()
            val angleValue = Math.toRadians(binding.angleSeekbar.progress.toDouble())
            val speedValue = binding.speedSeekbar.progress.toDouble()
            var timeInterval = 0.5
            var time = 0.0

            graphView.removeAllSeries()
            trajectoryPoints.clear()


            while (true) {
                val x = speedValue * cos(angleValue) * time
                val y = speedValue * sin(angleValue) * time - 0.5 * 9.81 * time * time

                if (y < 0) {
                    trajectoryPoints.add(TrajectoryPoint(time, x, 0.00))

                    break
                }

                trajectoryPoints.add(TrajectoryPoint(time, x, y))
                time += timeInterval
            }
            val dataPoints: Array<DataPoint> = trajectoryPoints.map {
                DataPoint(it.time, it.y)
            }.toTypedArray()

            val series = PointsGraphSeries<DataPoint>(dataPoints)


            graphView.addSeries(series)
            graphView.viewport.isScrollable = true
            graphView.viewport.isScalable = true
            graphView.viewport.setScalableY(true)
            graphView.viewport.setScrollableY(true)
            series.shape = PointsGraphSeries.Shape.POINT
            series.size = 12f
            series.color = getColor(this, color.purple_200)



            trajectoryAdapter.notifyDataSetChanged()
        }
    }
}
