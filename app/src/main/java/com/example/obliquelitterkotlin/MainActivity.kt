package com.example.obliquelitterkotlin

import TrajectoryAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Switch
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.obliquelitterkotlin.R.*
import com.example.obliquelitterkotlin.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.PointsGraphSeries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    private val trajectoryPoints = mutableListOf<TrajectoryPoint>()
    private val trajectoryAdapter = TrajectoryAdapter(trajectoryPoints)
    private var animationJob: Job? = null
    private lateinit var calculationSwitch: Switch

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        calculationSwitch = findViewById(R.id.calculationSwitch)

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

            if (calculationSwitch.isChecked) {
                Log.d("MainActivity", "SWITCH IS ON!")

                //TODO: Need to get trajectoryPoints from 127.0.0.1:5005/ . query params are speed, angle and interval and request type is GET
                fetchDataFromApi(angleValue, speedValue, timeInterval)
            }else{
                while (true) {
                    val x = speedValue * cos(angleValue) * time
                    val y = speedValue * sin(angleValue) * time - 0.5 * 9.81 * time * time

                    if (y < 0) {
                        trajectoryPoints.add(TrajectoryPoint(time, x, 0.0))

                        break
                    }

                    trajectoryPoints.add(TrajectoryPoint(time, x, y))
                    time += timeInterval
                }
            }

            startAnimation()

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
        return x.toInt()
    }

    private fun convertToScreenCoordinateY(y: Double): Int {
        val viewHeight = findViewById<AnimatedOvalView>(R.id.animatedOval).height

        return (viewHeight - y).toInt()
    }

    override fun onDestroy() {
        super.onDestroy()
        animationJob?.cancel()
    }

    private fun fetchDataFromApi(angle: Double, speed: Double, interval: Double) {
        val client = OkHttpClient()

        val httpUrl = HttpUrl.Builder()
            .scheme("http")
            .host("10.0.2.2")
            .port(5005)
            .addQueryParameter("angle", Math.toDegrees(angle).toString())
            .addQueryParameter("speed", speed.toString())
            .addQueryParameter("interval", interval.toString())
            .build()

        val request = Request.Builder().url(httpUrl).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        throw IOException("Unexpected code $response")
                    }

                    val responseData = response.body?.string()

                    val trajectoryData = parseTrajectoryData(responseData)

                    runOnUiThread {
                        updateUIWithTrajectoryData(trajectoryData)
                    }
                }
            }
        })
    }

    private fun parseTrajectoryData(jsonData: String?): List<TrajectoryPoint> {
        val gson = Gson()
        val type = object : TypeToken<List<TrajectoryPoint>>() {}.type
        return gson.fromJson(jsonData, type)
    }

    private fun updateUIWithTrajectoryData(data: List<TrajectoryPoint>) {
        trajectoryPoints.clear()
        trajectoryPoints.addAll(data)
        trajectoryAdapter.notifyDataSetChanged()

        updateGraphView(data)

        startAnimation()

    }

    private fun updateGraphView(trajectoryPoints: List<TrajectoryPoint>) {
        val series = PointsGraphSeries<DataPoint>().apply {
            shape = PointsGraphSeries.Shape.POINT
            size = 12f
            color = getColor(this@MainActivity, R.color.purple_200)
        }
        trajectoryPoints.forEach { point ->
            series.appendData(DataPoint(point.time, point.y), true, trajectoryPoints.size)
        }

        val graphView: GraphView = findViewById(R.id.idGraphView)

        graphView.removeAllSeries()
        graphView.addSeries(series)

        graphView.viewport.apply {
            isXAxisBoundsManual = true
            setMinX(series.lowestValueX)
            setMaxX(series.highestValueX)

            isYAxisBoundsManual = true
            setMinY(series.lowestValueY)
            setMaxY(series.highestValueY)
        }

        graphView.viewport.isScalable = true
        graphView.viewport.setScalableY(true)

        graphView.onDataChanged(false, false)
    }

    private fun startAnimation() {
        val animatedOval = findViewById<AnimatedOvalView>(R.id.animatedOval)
        animationJob = CoroutineScope(Dispatchers.Main).launch {
            trajectoryPoints.forEach { point ->
                val screenX = convertToScreenCoordinateX(point.x)
                var screenY = convertToScreenCoordinateY(point.y)

                if (point.y < 20.0 && point.x > 0.0) {
                    screenY = convertToScreenCoordinateY(25.0)
                }
                animatedOval.moveToPoint(screenX.toFloat(), screenY.toFloat())

                delay(50)
            }
        }
    }
}
