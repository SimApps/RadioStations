package com.amirami.simapp.radiobroadcastpro.pairalarm.ui.dialog

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.amirami.simapp.radiobroadcastpro.R
import com.amirami.simapp.radiobroadcastpro.pairalarm.model.AlarmMode

object SimpleDialog {
    fun showSimpleDialog(
        context: Context,
        title: String,
        message: String? = null,
        messageItems: Array<String>? = null,
        clickedItemPosition: Int = 0,
        icon: Int? = null,
        positive: ((dialogInterface: DialogInterface) -> Unit),
        negative: (() -> Unit)? = null
    ) {
        val dialogBuilder = AlertDialog.Builder(context)

        dialogBuilder.setTitle(title)
        if (messageItems.isNullOrEmpty()) {
            dialogBuilder.setMessage(message)
        } else {
            dialogBuilder.setSingleChoiceItems(
                messageItems,
                clickedItemPosition,
                null
            )
        }

        if (icon != null) {
            dialogBuilder.setIcon(icon)
        }

        dialogBuilder.setNegativeButton(context.getString(R.string.no)) { _: DialogInterface, _: Int ->
            negative?.invoke()
        }

        dialogBuilder.setNeutralButton(context.getString(R.string.yes)) { dialogInterface: DialogInterface, _: Int ->
            positive.invoke(dialogInterface)
        }

        dialogBuilder.setOnCancelListener {
            negative?.invoke()
        }

        dialogBuilder.show()
    }

    fun showAlarmModeDialog(
        context: Context,
        clickedItemPosition: Int,
        positive: ((dialogInterface: DialogInterface) -> Unit)
    ) {
        showSimpleDialog(
            context,
            context.getString(R.string.alarmSet_selectBellDialogTitle),
            messageItems = AlarmMode.values().map { it.mode }.toTypedArray(),
            clickedItemPosition = clickedItemPosition,
            positive = positive
        )
    }
}
