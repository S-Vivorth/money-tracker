package io.paraga.moneytrackerdev.utils.helper

import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.text.InputType
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.views.adapterDialog.DialogAdapter
import io.paraga.moneytrackerdev.views.importFile.adapter.ImportVerifyAdapter


class DialogHelper {

    companion object {

        fun showPrimaryDialog(context: Context, dialogType: Int, onOkayPressed: (AlertDialog) -> Unit) {
            val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog).create()
            val view = LayoutInflater.from(context).inflate(R.layout.delete_trans_dialog,null)
            val title = view.findViewById<TextView>(R.id.dialogTitle)
            val confirmText = view.findViewById<TextView>(R.id.confirmText)
            val closeBtn = view.findViewById<ImageView>(R.id.closeBtn)
            val cancelBtn = view.findViewById<TextView>(R.id.cancelBtn)
            val okayBtn = view.findViewById<RelativeLayout>(R.id.okayBtn)
            val okayText = view.findViewById<TextView>(R.id.okText)
            val imm: InputMethodManager? =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
//            imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            when (dialogType) {
                Enums.DialogType.TRANSACTION.value -> {
                    title.text = context.getString(R.string.delete_transaction)
                    confirmText.text = context.getString(R.string.do_u_want_to_delete_trans)
                }
                Enums.DialogType.CATEGORY.value -> {
                    title.text = context.getString(R.string.delete_category)
                    confirmText.text = context.getString(R.string.do_u_want_to_delete_category)
                }
                Enums.DialogType.RESET_CATEGORY.value -> {
                    title.text = context.getString(R.string.reset_category)
                    confirmText.text = context.getString(R.string.do_u_want_to_reset_category)
                    okayText.setText(context.getString(R.string.reset))
                }
                Enums.DialogType.WALLET.value -> {
                    title.text = context.getString(R.string.delete_wallet)
                    confirmText.text = context.getString(R.string.your_transaction_data)
                }
                Enums.DialogType.ACCOUNT.value -> {
                    title.text = context.getString(R.string.delete_acc)
                    confirmText.text = context.getString(R.string.your_transaction_data)
                    okayBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.default_og))
                    okayText.setText(R.string.next)
                }
                Enums.DialogType.BUDGET.value -> {
                    title.text = context.getString(R.string.delete_budget)
                    confirmText.text = context.getString(R.string.delete_confirmation_text)
                }
                Enums.DialogType.RETURN_PREVIOUS.value -> {
                    title.text = context.getString(R.string._return)
                    confirmText.text = context.getString(R.string.return_screen)
                    okayBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.default_og))
                    okayText.setText(R.string.okay)
                }
                Enums.DialogType.Notification.value -> {
                    title.text = context.getString(R.string.delete_all_notification)
                    confirmText.text = context.getString(R.string.delete_confirmation_text)
                }
                Enums.DialogType.REMOVE_USER.value -> {
                    title.text = context.getString(R.string.remove_user)
                    confirmText.text = context.getString(R.string.are_you_sure_you_want_to_remove_this_user)
                }
                Enums.DialogType.IMPORT.value -> {
                    title.text = context.getString(R.string.import_file)
                    confirmText.text = context.getString(R.string.are_you_sure_you_want_to_import)
                    okayText.setText(R.string.okay)
                    okayBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.default_og))
                }
                else -> {
                    title.text = context.getString(R.string.confirm_logout)
                    confirmText.text = context.getString(R.string.are_u_sure_you_want_to_logout)
                    okayBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.default_og))
                    okayText.setText(R.string.logout)
                    val nightModeFlags = context.resources?.configuration?.uiMode?.and(
                        Configuration.UI_MODE_NIGHT_MASK)
                    if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                        okayText.setTextColor(Color.BLACK)
                    }
                    else {
                        okayText.setTextColor(Color.WHITE)
                    }
                }
            }

            closeBtn.setOnClickListener {
                builder.dismiss()
            }

            cancelBtn.setOnClickListener {
                builder.dismiss()
            }

            okayBtn.setOnClickListener {
                builder.dismiss()
                onOkayPressed(builder)
            }
            val back = ColorDrawable(Color.TRANSPARENT)
            val inset = InsetDrawable(back, 20)
            builder.window?.setBackgroundDrawable(inset)
            builder.setView(view)
            builder.show()
        }

        fun showTextConfirmDialog(context: Context, dialogType: Int,onOkayPressed: (AlertDialog, String) -> Unit) {
            val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog).create()
            val view = LayoutInflater.from(context).inflate(R.layout.delete_account_dialog,null)
            val title = view.findViewById<TextView>(R.id.dialogTitle)
            val okayText = view.findViewById<TextView>(R.id.okText)
            val confirmEdittext = view.findViewById<EditText>(R.id.confirmEdittext)
            val closeBtn = view.findViewById<ImageView>(R.id.closeBtn)
            val cancelBtn = view.findViewById<TextView>(R.id.cancelBtn)
            val okayBtn = view.findViewById<RelativeLayout>(R.id.okayBtn)

            when(dialogType) {
                Enums.DialogType.PASSWORD.value -> {
                    confirmEdittext.hint = context.getString(R.string.enter_yr_pw)
                    confirmEdittext.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    okayText.text = context.getString(R.string.login)
                }
            }

            okayBtn.apply {
                isEnabled = false
                setBackgroundColor(ContextCompat.getColor(context,R.color.bgButtonDisabled))
            }
            okayText.setTextColor(ContextCompat.getColor(context,R.color.black))
            okayText.alpha = 0.38F




            confirmEdittext.addTextChangedListener {
                if (dialogType == Enums.DialogType.DELETE.value) {
                    if (confirmEdittext.text.toString() == Enums.General.DELETE.value) {
                        okayBtn.apply {
                            isEnabled = true
                            setBackgroundColor(ContextCompat.getColor(context,R.color.redBgColor))
                        }
                        okayText.setTextColor(ContextCompat.getColor(context,R.color.white))
                        okayText.alpha = 1F
                    }
                    else {
                        okayBtn.apply {
                            isEnabled = false
                            setBackgroundColor(ContextCompat.getColor(context,R.color.bgButtonDisabled))
                        }
                        okayText.setTextColor(ContextCompat.getColor(context,R.color.black))
                        okayText.alpha = 0.38F
                    }
                }
                else {
                    if (confirmEdittext.text.toString().isNotEmpty()) {
                        okayBtn.apply {
                            isEnabled = true
                            setBackgroundColor(ContextCompat.getColor(context,R.color.default_og))
                        }
                        okayText.setTextColor(ContextCompat.getColor(context,R.color.white))
                        okayText.alpha = 1F
                    }
                    else {
                        okayBtn.apply {
                            isEnabled = false
                            setBackgroundColor(ContextCompat.getColor(context,R.color.bgButtonDisabled))
                        }
                        okayText.setTextColor(ContextCompat.getColor(context,R.color.black))
                        okayText.alpha = 0.38F
                    }
                }

            }

            //request keyboard
            confirmEdittext.requestFocus()
            val imm: InputMethodManager? =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            closeBtn.setOnClickListener {
                imm?.hideSoftInputFromWindow(confirmEdittext.windowToken, 0);
                builder.dismiss()
            }

            cancelBtn.setOnClickListener {
                imm?.hideSoftInputFromWindow(confirmEdittext.windowToken, 0);
                builder.dismiss()
            }

            okayBtn.setOnClickListener {
                if (dialogType == Enums.DialogType.PASSWORD.value) {
                    onOkayPressed(builder, confirmEdittext.text.toString())
                }
                else {
                    onOkayPressed(builder, "")
                    imm?.hideSoftInputFromWindow(confirmEdittext.windowToken, 0)
                }

            }

            builder.setView(view)
            builder.show()
        }

        fun showSecondaryDialog(context: Context, _titleText: String,
            _confirmText: String, _okayText: String, onOkayPressed: (AlertDialog) -> Unit) {
            val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog).create()
            val view = LayoutInflater.from(context).inflate(R.layout.delete_trans_dialog,null)
            val title = view.findViewById<TextView>(R.id.dialogTitle)
            val confirmText = view.findViewById<TextView>(R.id.confirmText)
            val closeBtn = view.findViewById<ImageView>(R.id.closeBtn)
            val cancelBtn = view.findViewById<TextView>(R.id.cancelBtn)
            val okayBtn = view.findViewById<RelativeLayout>(R.id.okayBtn)
            val okayText = view.findViewById<TextView>(R.id.okText)

            title.text = _titleText
            confirmText.text = _confirmText
            okayText.text = _okayText

            closeBtn.setOnClickListener {
                builder.dismiss()
            }

            cancelBtn.setOnClickListener {
                builder.dismiss()
            }

            okayBtn.setOnClickListener {
                builder.dismiss()
                onOkayPressed(builder)
            }
            val back = ColorDrawable(Color.TRANSPARENT)
            val inset = InsetDrawable(back, 20)
            builder.window?.setBackgroundDrawable(inset)
            builder.setView(view)
            builder.show()
        }

        fun showWhatNewDialog(context: Context,mList: ArrayList<String>) {
            val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog).create()
            val view = LayoutInflater.from(context).inflate(R.layout.what_new_dialog,null)
            val closeBtn = view.findViewById<ImageView>(R.id.closeBtn)
            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_what_new)
            closeBtn.setOnClickListener {
                builder.dismiss()
            }
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                LinearLayoutManager(context).orientation = RecyclerView.VERTICAL
                adapter = DialogAdapter(mList)
                setHasFixedSize(true)
                isNestedScrollingEnabled = false
            }
            val back = ColorDrawable(Color.TRANSPARENT)
            val inset = InsetDrawable(back, 20)
            builder.window?.setBackgroundDrawable(inset)
            builder.setView(view)
            builder.setCancelable(false)
            builder.show()
        }

        fun showSubscriptionExpiredDialog(context: Context, onOkayPressed: (AlertDialog) -> Unit) {
            val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog).create()
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_subscription_expired,null)
            val cancelBtn = view.findViewById<TextView>(R.id.cancelBtn)
            val okayBtn = view.findViewById<RelativeLayout>(R.id.okayBtn)
            cancelBtn.setOnClickListener {
                builder.dismiss()
            }

            okayBtn.setOnClickListener {
                builder.dismiss()
                onOkayPressed(builder)
            }
            val back = ColorDrawable(Color.TRANSPARENT)
            val inset = InsetDrawable(back, 20)
            builder.window?.setBackgroundDrawable(inset)
            builder.setView(view)
            builder.show()
        }

    }

}