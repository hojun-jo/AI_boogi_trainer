package com.example.boogi_trainer.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.round


class APIManager {
    companion object{
        lateinit var user:User
        lateinit var userLog:UserLog
        lateinit var todayLog:DateLog
        lateinit var foods:ArrayList<Food>
        var todayInfo:TodayInfo = TodayInfo()

        private val caller = RetrofitClient.restAPI

        @RequiresApi(Build.VERSION_CODES.O)
        val now: LocalDate = LocalDate.now()
        @RequiresApi(Build.VERSION_CODES.O)
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")!!
        @RequiresApi(Build.VERSION_CODES.O)
        val today: String = now.format(formatter)

        private fun setUser(uid:String) {
            user= caller.getUser(uid).execute().body()?.user!!
        }

        fun setFoods() {
            foods = caller.getFoods().execute().body()?.foods!!
        }

        private fun setUserLog(uid:String){
            userLog = caller.getUserLog(uid).execute().body()?.userLog!!
            setTodayLog()
            setTodayInfo()
        }
        fun getUser(uid: String): User {
            todayInfo = TodayInfo()
            setUser(uid)
            setUserLog(uid)
            setFoods()
            return user
        }

        fun getFood(foodName:String) : Food {
            var mfood:Food = Food()
            for(food in foods){
                if(food.name == foodName)
                    mfood = food
            }
            return mfood!!
        }

        fun getFoodName(foodName:String) : String {
            var mfood:Food = Food()
            for(food in foods){
                if(food.name == foodName)
                    mfood = food
            }
            return mfood.name!!
        }

        fun getFoodKcal(foodName:String, gram: Int) : Double {
            var mfood:Food = Food()
            for(food in foods){
                if(food.name == foodName)
                    mfood = food
            }
            return round(mfood.kcal!! * gram)
        }


        private fun setTodayLog() { // ?????? ????????? ?????????
            var i = 0
            if(userLog.dates?.size == 0){ // ?????? ????????? ????????? ?????? ??????
                todayLog = DateLog(date = today, exercises = null, meals = null, dietInfo = DietInfo(), breakfastImage = null, lunchImage = null, dinnerImage = null)
            }
            for(date in userLog.dates!!){
                if(date.date == today) {
                    todayLog = date
                    if(todayLog.meals?.size==0){ // ?????? ????????? ?????? ??????
                        todayLog.meals!!.add(Meal())
                    }
                    if(todayLog.exercises?.size==0){ // ?????? ???????????? ?????? ??????
                        todayLog.exercises!!.add(Exercise())
                    }
                    if(todayLog.dietInfo == null){
                        todayLog.dietInfo = DietInfo();
                    }
                    break
                } else{ // ?????? ????????? ????????? ?????? ??????
                    todayLog = DateLog(date = today, exercises = null, meals = null, dietInfo = DietInfo(),breakfastImage = null, lunchImage = null, dinnerImage = null)
                }
                i+=1
            }
        }

        private fun setTodayInfo(){
            if(todayLog.meals!=null){
                for(meal in todayLog.meals!!){
                    when (meal.kind) {
                        "breakfast" -> {
                            todayInfo.breakfastKcal += round(meal.kcal!!)
                            todayInfo.breakfastCarbohydrate += round(meal.carbs!!)
                            todayInfo.breakfastProtein += round(meal.protein!!)
                            todayInfo.breakfastFat += round(meal.fat!!)
                        }
                        "lunch" -> {
                            todayInfo.lunchKcal += round(meal.kcal!!)
                            todayInfo.lunchCarbohydrate += round(meal.carbs!!)
                            todayInfo.lunchProtein += round(meal.protein!!)
                            todayInfo.lunchFat += round(meal.fat!!)
                        }
                        "dinner" -> {
                            todayInfo.dinnerKcal += round(meal.kcal!!)
                            todayInfo.dinnerCarbohydrate += round(meal.carbs!!)
                            todayInfo.dinnerProtein += round(meal.protein!!)
                            todayInfo.dinnerFat += round(meal.fat!!)
                        }
                        "" ->{}
                    }
                }
            }
        }
        //POST
        fun postMeal(food:String, gram:Int, mealType: MealType, date:String= today){
            var kind = ""
            kind = when(mealType){
                MealType.BREAKFAST -> "breakfast"
                MealType.LUNCH -> "lunch"
                MealType.DINNER -> "dinner"
            }
            var payload = PostMeal(food, gram, kind)
            if(caller.postMeal(user.uid!!,date,payload).execute().isSuccessful)
                getUser(user.uid!!)
        }
        fun postExercise(exerciseType:ExerciseType, reps:Int, date:String= today){
            var exercise = ""
            exercise = when(exerciseType){
                ExerciseType.PUSH_UP -> "?????????"
                ExerciseType.SQUAT -> "?????????"
                ExerciseType.PULL_UP -> "??????"
                ExerciseType.SIT_UP -> "??????????????????"
                ExerciseType.DEAD_LIFT -> "???????????????"
                ExerciseType.BARBELL_ROW -> "????????????"
                ExerciseType.BARBELL_CURL -> "?????????"
                ExerciseType.DUMBBELL_CURL -> "?????????"
                ExerciseType.PLANK -> "?????????"
            }
            var payload = Exercise(exercise, reps)
            if(caller.postExercise(user.uid!!,date,payload).execute().isSuccessful)
                getUser(user.uid!!)
        }
        fun postFood(food:Food){
            var payload = food
            caller.postFood(food).execute()
        }
        fun postCardioExercise(cardioExerciseType: CardioExerciseType, time:Int, date:String= today){
            println("API+ "+ "??????")
            var exercise = when(cardioExerciseType){
                CardioExerciseType.RUNNING_MACHINE -> "????????????"
                CardioExerciseType.JOGGING -> "??????"
                CardioExerciseType.STAIR_CLIMBING -> "???????????????"
                CardioExerciseType.PLANK -> "?????????"
            }
            var payload = Exercise(exercise, 0, time)
            println("API+ "+ payload)
            if(caller.postExercise(user.uid!!,date,payload).execute().isSuccessful){
                getUser(user.uid!!)
            }
        }

        private fun bitmapToString(bitmap: Bitmap): String {
            var width = bitmap.width
            var height = bitmap.height * 1000/width
            var tmpBitmap = bitmap
            if(width>1000){
                tmpBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
            }
            val byteArrayOutputStream = ByteArrayOutputStream()
            tmpBitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
        fun stringToBitmap(encodedString: String): Bitmap {
            val encodeByte = Base64.decode(encodedString, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        }
        fun postImage(image:Bitmap, mealType: MealType, date:String= today){
            val image = bitmapToString(image)
            var kind = ""
            kind = when(mealType){
                MealType.BREAKFAST -> "breakfast"
                MealType.LUNCH -> "lunch"
                MealType.DINNER -> "dinner"
            }
            var payload = PostImage(image, kind)
            if(caller.postImage(user.uid!!,date,payload).execute().isSuccessful)
                getUser(user.uid!!)
        }

        fun getLogImage(mealType: MealType): Bitmap {
            var matrix = Matrix()
            matrix.setRotate(90F); //90??? ??????
            var srcBitmap : Bitmap = when (mealType) {
                MealType.BREAKFAST -> stringToBitmap(todayLog.breakfastImage!!)
                MealType.LUNCH -> stringToBitmap(todayLog.lunchImage!!)
                MealType.DINNER -> stringToBitmap(todayLog.dinnerImage!!)
            }
            var dscBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
            srcBitmap.recycle(); //bitmap??? ????????? ?????? ???????????? ??????????????? free?????????.

            return dscBitmap
        }
    }
}