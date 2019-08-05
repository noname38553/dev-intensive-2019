package ru.skillbranch.devintensive.ui.profile

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextPaint
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_profile.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.hideKeyboard
import ru.skillbranch.devintensive.models.Bender
import ru.skillbranch.devintensive.models.Profile
import ru.skillbranch.devintensive.utils.Utils
import ru.skillbranch.devintensive.utils.Utils.convertSpToPx
import ru.skillbranch.devintensive.utils.Utils.toInitials
import ru.skillbranch.devintensive.viewmodels.ProfileViewModel

class ProfileActivity : AppCompatActivity() {
    companion object {
        const val IS_EDIT_MODE = "IS_EDIT_MODE"
    }
    private var currentInitials:String? = null
    private lateinit var viewModel: ProfileViewModel
    var isEditMode = false
    lateinit var viewFields: Map<String, TextView>
    //lateinit var avatarIV: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initViews(savedInstanceState)
        initViewModel()
        Log.d("M_ProfileActivity", "onCreate")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean(IS_EDIT_MODE, isEditMode)

    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModel.getProfileData().observe(this, Observer { updateUI(it) })
        viewModel.getTheme().observe(this, Observer { updateTheme(it) })
        viewModel.getRepositoryError().observe(this, Observer { updateRepoError(it) })
        viewModel.getIsRepoError().observe(this, Observer { updateRepository(it) })
    }

    private fun updateTheme(mode: Int) {
        Log.d("M_ProfileActivity", "updateTheme")
        delegate.setLocalNightMode(mode)
    }

    private fun updateUI(profile: Profile) {

        profile.toMap().also {
            for ((k, v) in viewFields) {
                v.text = it[k].toString()
            }
        }
      //  if (((profile.firstName == "") or (profile.firstName == null)) and ((profile.lastName == "") or (profile.lastName == null))) {
      //  } else {
            updateAvatar(profile)
        //val initials = Utils.toInitials(profile.firstName, profile.lastName)
      //  updateAvatar1(initials)

      //  }
    }
   // private fun updateAvatar1(initials: String?) {
  //      if(initials != currentInitials) {
  //          currentInitials = initials
  //          iv_avatar.setImageDrawable(generateNewAvatar())
   //     }
  //  }

    private fun generateNewAvatar(): Drawable? {
        if(currentInitials.isNullOrEmpty()) return null

        val width = 400
        val height = 400

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap!!)

        val color = TypedValue()
        theme.resolveAttribute(R.attr.colorAccent, color, true)
        canvas.drawColor(color.data)

        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        textPaint.textSize = 50F * resources.displayMetrics.scaledDensity
        textPaint.color = Color.WHITE
        val textWidth = textPaint.measureText(currentInitials) * 0.5F
        val textBaseLineHeight = textPaint.fontMetrics.ascent * -0.35F
        canvas.drawText(currentInitials!!, width/2 - textWidth, height/2 + textBaseLineHeight, textPaint)

        return bitmap.toDrawable(resources)
    }
    private fun initViews(savedInstanceState: Bundle?) {
        viewFields = mapOf(
            "nickName" to tv_nick_name,
            "rank" to tv_rank,
            "firstName" to et_first_name,
            "lastName" to et_last_name,
            "about" to et_about,
            "repository" to et_repository,
            "rating" to tv_rating,
            "respect" to tv_respect
        )
        //avatarIV = iv_avatar
        isEditMode = savedInstanceState?.getBoolean(IS_EDIT_MODE, false) ?: false
        showCurrentMode(isEditMode)

        btn_edit.setOnClickListener {
            viewModel.onRepoEditCompleted(wr_repository.isErrorEnabled)
            if (isEditMode) saveProfileInfo()
            isEditMode = !isEditMode
            showCurrentMode(isEditMode)
        }

        btn_switch_theme.setOnClickListener {
            viewModel.switchTheme()
        }

        et_repository.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onRepositoryChanged(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun showCurrentMode(isEdit: Boolean) {
        val info = viewFields.filter { setOf("firstName", "lastName", "about", "repository").contains(it.key) }
        for ((_, v) in info) {
            v as EditText
            v.isFocusable = isEdit
            v.isFocusableInTouchMode = isEdit
            v.isEnabled = isEdit
            v.background.alpha = if (isEdit) 255 else 0
        }

        ic_eye.visibility = if (isEdit) View.GONE else View.VISIBLE
        wr_about.isCounterEnabled = isEdit

        with(btn_edit) {
            val filter: ColorFilter? = if (isEdit) {
                PorterDuffColorFilter(
                    resources.getColor(R.color.color_accent, theme),
                    PorterDuff.Mode.SRC_IN
                )
            } else {
                null
            }
            val icon = if (isEdit) {
                resources.getDrawable(R.drawable.ic_save_black_24dp, theme)
            } else {
                resources.getDrawable(R.drawable.ic_edit_black_24dp, theme)
            }

            background.colorFilter = filter
            setImageDrawable(icon)
        }

    }

    private fun saveProfileInfo() {
        Profile(
            firstName = et_first_name.text.toString(),
            lastName = et_last_name.text.toString(),
            about = et_about.text.toString(),
            repository = et_repository.text.toString()
        ).apply {
            viewModel.saveProfileData(this)
           // updateAvatar(this)
        }
    }

    private fun getThemeAccentColor(): Int {
        val value = TypedValue()
        theme.resolveAttribute(R.attr.colorAccent, value, true)
        return value.data
    }

    private fun updateAvatar(profile: Profile) {
       // val initials = toInitials(profile.firstName, profile.lastName)
      //  if (((profile.firstName == "") or (profile.firstName == null)) and ((profile.lastName == "") or (profile.lastName == null))) {
      //      //iv_avatar.generateAvatar(null, convertSpToPx(this, 0), theme)
     //       iv_avatar.generateAvatar("", "", convertSpToPx(this, 48),theme)
       //     //iv_avatar.setImageDrawable(resources.getDrawable(R.drawable.avatar_default, theme))
         //       //resources.getDrawable(R.drawable.avatar_default, theme)

        //} else {
            //iv_avatar.generateAvatar(initials, convertSpToPx(this, 48), theme)
            iv_avatar.generateAvatar(profile.firstName, profile.lastName, convertSpToPx(this, 48),theme)
        //}
    }

    private fun updateRepository(isError: Boolean) {
        if (isError) et_repository.text.clear()
    }

    private fun updateRepoError(isError: Boolean) {
        wr_repository.isErrorEnabled = isError
        wr_repository.error = if (isError) "Невалидный адрес репозитория" else ""
        //scroll.scrollBy(0, 200)
    }

}
