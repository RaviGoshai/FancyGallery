package com.vanravi.fancygallery.alertDialog

interface DialogListener {
    fun onYesClicked(obj: Any?)
    fun onNoClicked(error: String?)
}