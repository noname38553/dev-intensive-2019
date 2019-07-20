package ru.skillbranch.devintensive.extensions

import android.app.Activity
import android.view.inputmethod.InputMethodManager

fun Activity.hideKeyboard() {
    val inputMethodManager: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    if (this.currentFocus!= null){
        inputMethodManager.hideSoftInputFromWindow(this.currentFocus!!.getWindowToken(), 0)
   }
}
