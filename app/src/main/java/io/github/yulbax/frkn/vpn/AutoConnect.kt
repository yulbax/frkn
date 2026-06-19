package io.github.yulbax.frkn.vpn

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager

object AutoConnect {
    private const val WORK_NAME = "frkn_auto_connect"

    fun enqueue(context: Context) {
        val request = OneTimeWorkRequestBuilder<ConnectWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, request)
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}
