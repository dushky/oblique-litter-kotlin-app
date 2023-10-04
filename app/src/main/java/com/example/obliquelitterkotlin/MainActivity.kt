package com.example.obliquelitterkotlin

import TrajectoryAdapter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.example.obliquelitterkotlin.databinding.ActivityMainBinding
import com.example.obliquelitterkotlin.TrajectoryPoint
import androidx.recyclerview.widget.LinearLayoutManager
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    private val trajectoryPoints = mutableListOf<TrajectoryPoint>()
    private val trajectoryAdapter = TrajectoryAdapter(trajectoryPoints)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            var timeInterval = 0.1
            var time = 0.0


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

            trajectoryAdapter.notifyDataSetChanged()
        }
    }
}
