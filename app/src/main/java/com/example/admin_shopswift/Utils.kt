package com.example.admin_shopswift

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.admin_shopswift.databinding.ProgessBarBinding
import com.google.firebase.auth.FirebaseAuth

object Utils {

    private var firebaseAuthInstance: FirebaseAuth? = null

    fun getAuthInstance(): FirebaseAuth {
        if (firebaseAuthInstance == null) {
            firebaseAuthInstance = FirebaseAuth.getInstance()
        }
        return firebaseAuthInstance!!
    }

    private var dialog: AlertDialog? = null

    fun showDialog(context: Context, message: String) {
        val progress = ProgessBarBinding.inflate(LayoutInflater.from(context))
        progress.messageBar.text = message
        dialog = AlertDialog.Builder(context)
            .setView(progress.root)
            .setCancelable(false)
            .create()
        dialog!!.show()
    }

    fun hideDialog() {
        dialog?.dismiss()
        dialog = null
    }

    fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
    fun getRandomId(): String {
        return (1..25)
            .map {
                (('A'..'Z') + ('a'..'z') + ('0'..'9')).random()
            }
            .joinToString("")
    }

}

