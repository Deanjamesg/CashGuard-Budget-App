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







