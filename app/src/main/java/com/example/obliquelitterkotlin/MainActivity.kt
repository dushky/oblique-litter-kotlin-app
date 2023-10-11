package com.example.obliquelitterkotlin

import TrajectoryAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    private val trajectoryPoints = mutableListOf<TrajectoryPoint>()
    private val trajectoryAdapter = TrajectoryAdapter(trajectoryPoints)
    private var animationJob: Job? = null
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
            animationJob?.cancel()

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

            val animatedOval = findViewById<AnimatedOvalView>(R.id.animatedOval)

            animationJob = CoroutineScope(Dispatchers.Main).launch {
                trajectoryPoints.forEach { point ->
                    // Convert trajectory point to screen coordinates as needed
                    // This is a placeholder. You need to implement the conversion based on your requirements.
                    val screenX = convertToScreenCoordinateX(point.x)
                    val screenY = convertToScreenCoordinateY(point.y)

                    animatedOval.moveToPoint(screenX.toFloat(), screenY.toFloat())

                    delay(500) // Delay for the animation effect, you can adjust this value as needed
                }
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

    private fun convertToScreenCoordinateX(x: Double): Int {
        return x.toInt()  // Simple conversion, replace with actual logic
    }

    private fun convertToScreenCoordinateY(y: Double): Int {
        val viewHeight = findViewById<AnimatedOvalView>(R.id.animatedOval).height

        return (viewHeight - y).toInt()
    }

    override fun onDestroy() {
        super.onDestroy()
        animationJob?.cancel()  // Cancel the animation coroutine if it's still running
    }
}
