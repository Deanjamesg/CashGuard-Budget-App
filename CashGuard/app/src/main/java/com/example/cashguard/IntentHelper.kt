package com.example.cashguard


import android.app.Activity
import android.content.Context
import android.content.Intent

    fun loginIntent(context: Context, activityToOpen: Class<*>) {
        // Declare intent with context and class to pass the value to
        val intent = Intent(context, activityToOpen)
        // If context is not an Activity, add FLAG_ACTIVITY_NEW_TASK
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        // Start the activity
        context.startActivity(intent)
    }

    fun registerIntent(context: Context, activityToOpen: Class<*>){
        // Declare intent with context and class to pass the value to
        val intent = Intent(context, activityToOpen)
        // If context is not an Activity, add FLAG_ACTIVITY_NEW_TASK
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        // Start the activity
        context.startActivity(intent)
    }

    fun overViewIntent(context: Context, activityToOpen: Class<*>, firstName: String, userId: Int) {
        val intent = Intent(context, activityToOpen).apply {
            putExtra("FIRST_NAME", firstName)
            putExtra("USER_ID", userId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

//    fun navigateToOverview(context: Context, firstName: String, userId: Int) {
//        val intent = Intent(context, OverviewActivity::class.java).apply {
//            putExtra("FIRST_NAME", firstName)
//            putExtra("USER_ID", userId)
//        }
//        context.startActivity(intent)
//    }
//
//    fun navigateToBudgetOverview(context: Context, firstName: String, userId: Int) {
//        val intent = Intent(context, BudgetOverviewActivity::class.java).apply {
//            putExtra("FIRST_NAME", firstName)
//            putExtra("USER_ID", userId)
//        }
//        context.startActivity(intent)
//    }







