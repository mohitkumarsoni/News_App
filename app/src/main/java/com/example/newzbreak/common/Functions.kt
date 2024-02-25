package com.example.newzbreak.common

import android.widget.ProgressBar
import androidx.core.view.isVisible

fun hideProgressBar(progressBar: ProgressBar){
    progressBar.isVisible = false
}

fun showProgressBar(progressBar: ProgressBar){
    progressBar.isVisible = true
}
