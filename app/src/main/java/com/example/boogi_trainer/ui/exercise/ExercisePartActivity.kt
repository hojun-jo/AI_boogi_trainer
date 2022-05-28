package com.example.boogi_trainer.ui.exercise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.example.boogi_trainer.R
import com.example.boogi_trainer.databinding.ActivityExercisePartBinding

class ExercisePartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExercisePartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_part)

        binding = ActivityExercisePartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val exercisePart = intent.getIntExtra("partName", 0)

        val recyclerView : RecyclerView = binding.recycleView
        val list = ArrayList<exerciseData>()

        binding.back.setOnClickListener {
            finish()
        }

        when(exercisePart){
            0 -> {
                list.add(exerciseData(R.drawable.pushup_btn, "푸쉬업"))
                list.add(exerciseData(R.drawable.flank_btn,"플랭크"))
                list.add(exerciseData(R.drawable.pullup_btn,"풀업"))

                binding.partName.text = "전신 운동"
            }
            1 -> {
                list.add(exerciseData(R.drawable.situp_btn,"싯업"))
                list.add(exerciseData(R.drawable.pullup_btn,"풀업"))
                list.add(exerciseData(R.drawable.pushup_btn,"푸쉬업"))
                list.add(exerciseData(R.drawable.babellrow_btn,"바벨로우"))
                binding.partName.text = "상체 운동"
            }

            2 -> {
                list.add(exerciseData(R.drawable.deadlift_btn,"데드리프트"))
                list.add(exerciseData(R.drawable.squat_btn,"스쿼트"))
                binding.partName.text = "하체 운동"
            }

            3 -> {
                list.add(exerciseData(R.drawable.dumbelcurl_btn,"덤벨컬"))
                list.add(exerciseData(R.drawable.babellcurl_btn,"바벨컬"))
                list.add(exerciseData(R.drawable.pushup_btn,"푸쉬업"))
                binding.partName.text = "팔 운동"
            }
        }



        val adapter = itemAdapter(this, list)
        recyclerView.adapter = adapter
    }

}