package io.paraga.moneytrackerdev.utils.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class GeneralActivityResult<T> :
    ActivityResultContract<ActivityResultRequestData<T>, ActivityResultRequestData<T>>() {
    private var tag: T? = null

    override fun createIntent(context: Context, input: ActivityResultRequestData<T>): Intent {
        tag = input.requestIdentifier
        return input.intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): ActivityResultRequestData<T> {
        if (resultCode == Activity.RESULT_OK) {
            intent?.let {
                tag?.let {
                    return ActivityResultRequestData(intent, it)
                }
            }
        }
        return ActivityResultRequestData(Intent(), tag)
    }
}

data class ActivityResultRequestData<T>(
    val intent: Intent,
    val requestIdentifier: T?
)