package com.medicine.reminder.data.model

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.medicine.reminder.R
import com.medicine.reminder.view.activity.MainActivity

object GlobalFunctions {

    fun getNavControllerFragmentProperty(activity: FragmentActivity): NavController {
        val navHostFragment =
            activity.supportFragmentManager.findFragmentById(R.id.fragmentContainerViewMedicineReminder) as NavHostFragment
        return navHostFragment.navController
    }

    fun getResult(activity: FragmentActivity, res: Long) {
        if (res > 0) {
            activity.finish()
            val intentProperty = Intent(activity, MainActivity::class.java)
            intentProperty.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intentProperty, activityFadeAnimation(activity as Context))
        } else {
            Toast.makeText(activity, activity.getString(R.string.unsuccessful), Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun activityFadeAnimation(context: Context) = ActivityOptionsCompat.makeCustomAnimation(
        context,
        android.R.anim.fade_in,
        android.R.anim.fade_out
    ).toBundle()
}