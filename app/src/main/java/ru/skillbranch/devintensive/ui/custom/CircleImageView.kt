package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.Bitmap.Config
import android.graphics.PorterDuff.Mode
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import ru.skillbranch.devintensive.App
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.utils.Utils
import ru.skillbranch.devintensive.utils.Utils.convertDpToPx
import ru.skillbranch.devintensive.utils.Utils.convertPxToDp
import ru.skillbranch.devintensive.utils.Utils.convertSpToPx
import kotlin.math.min

class CircleImageView @JvmOverloads constructor (
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ImageView(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_BORDER_COLOR: Int = Color.WHITE
    }
    /*
        var cvborderColor = Color.WHITE
        var cvborderWidth = convertDpToPx(context, 2)

        init {

            val attributes = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0)

            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)

            val BorderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_civ_border_width, cvborderWidth)
            val BorderColor = a.getColor(R.styleable.CircleImageView_civ_border_color, cvborderColor)
    //        val BorderOverlay = a.getBoolean(R.styleable.CircleImageView_civ_border_overlay, DEFAULT_BORDER_OVERLAY)
    //        val mCircleBackgroundColor =
    //            a.getColor(R.styleable.CircleImageView_civ_circle_background_color, DEFAULT_CIRCLE_BACKGROUND_COLOR)

            a.recycle()
        }
        fun getBorderWidth():Int = cvborderWidth

        fun setBorderWidth(@Dimension dp:Int){
            cvborderWidth = convertDpToPx(context, dp)
            this.invalidate()
        }

        fun getBorderColor():Int = cvborderColor

        fun setBorderColor(hex:String){

        }

        fun setBorderColor(@ColorRes colorId: Int){

        }
    */
    private var borderColor = DEFAULT_BORDER_COLOR
    private var borderWidth = convertDpToPx(context, 2)
    private var text: String? = null
    private var bitmap: Bitmap? = null

    init {
        if (attrs != null) {
            val attrVal = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderColor = attrVal.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            borderWidth = attrVal.getDimensionPixelSize(R.styleable.CircleImageView_cv_borderWidth, borderWidth)
            attrVal.recycle()
        }
    }

    fun getBorderWidth(): Int = convertPxToDp(context, borderWidth)

    fun setBorderWidth(dp: Int) {
        borderWidth = convertDpToPx(context, dp)
        this.invalidate()
    }

    fun getBorderColor(): Int = borderColor

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)
        this.invalidate()
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        borderColor = ContextCompat.getColor(App.applicationContext(), colorId)
        this.invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        var bitmap = getBitmapFromDrawable() ?: return
        if (width == 0 || height == 0) return

        bitmap = getScaledBitmap(bitmap, width)
        bitmap = getCenterCroppedBitmap(bitmap, width)
        bitmap = getCircleBitmap(bitmap)

        if (borderWidth > 0)
            bitmap = getStrokedBitmap(bitmap, borderWidth, borderColor)

        canvas.drawBitmap(bitmap, 0F, 0F, null)
    }

 //   fun generateAvatar(text: String?, sizeSp: Int, theme: Resources.Theme){
        /* don't render if initials haven't changed */
        /*if (bitmap == null || text != this.text){
            val image =
                if (text == null) {
                    getDefaultAvatar(theme)
                }
                else getInitials(text, sizeSp, theme)

            this.text = text
            bitmap = image
            //setImageBitmap(bitmap)
            invalidate()
        }*/

    fun generateAvatar(firstName: String, lastName: String, sizeSp: Int, theme: Resources.Theme) {
        val initials = Utils.toInitials(firstName, lastName)
        Log.d("M_CircleImageView","$firstName , $lastName , $sizeSp , $theme , $initials")
        //if (firstName.isEmpty() && lastName.isEmpty()) {
        val image = if (((firstName == "") or (firstName == null)) and ((lastName == "") or (lastName == null))) {
            //setImageDrawable(resources.getDrawable(R.drawable.avatar_default, context.theme))
            //getInitials("", 0, theme)
            getDefaultAvatar(theme)
          //  Log.d("M_CircleImageView","Yes")
        } else {
            getInitials(initials.toString(), sizeSp, theme)
            //setImageDrawable(resources.getDrawable(R.drawable.avatar_default, context.theme))
         //   Log.d("M_CircleImageView","No")
        }
        this.text = text
        bitmap = image
        setImageBitmap(bitmap)
        invalidate()
    }

    private fun getDefaultAvatar(theme: Resources.Theme): Bitmap {
        val image = Bitmap.createBitmap(layoutParams.height, layoutParams.height, Bitmap.Config.ARGB_8888)
        //val image = Bitmap.createBitmap(layoutParams.height, layoutParams.height, R.drawable.avatar_default)
        //val color = TypedValue()
        //theme.resolveAttribute(R.attr.colorAccent, color, true)

        val canvas = Canvas(image)

        //canvas.drawColor(color.data)
        //canvas.drawARGB(0,0,0,0)
        return image
    }

    private fun getInitials(text: String, sizeSp: Int, theme: Resources.Theme): Bitmap {
        val image = getDefaultAvatar(theme)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = sizeSp.toFloat()
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER

        val textBounds = Rect()
        paint.getTextBounds(text, 0, text.length, textBounds)

        val backgroundBounds = RectF()
        backgroundBounds.set(0f, 0f, layoutParams.height.toFloat(), layoutParams.height.toFloat())

        val textBottom = backgroundBounds.centerY() - textBounds.exactCenterY()
        val canvas = Canvas(image)
        val color = TypedValue()
        theme.resolveAttribute(R.attr.colorAccent, color, true)
        canvas.drawColor(color.data)

        canvas.drawText(text, backgroundBounds.centerX(), textBottom, paint)


        return image
    }


    private fun getStrokedBitmap(squareBmp: Bitmap, strokeWidth: Int, color: Int): Bitmap {
        val inCircle = RectF()
        val strokeStart = strokeWidth / 2F
        val strokeEnd = squareBmp.width - strokeWidth / 2F

        inCircle.set(strokeStart , strokeStart, strokeEnd, strokeEnd)

        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        strokePaint.color = color
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = strokeWidth.toFloat()

        val canvas = Canvas(squareBmp)
        canvas.drawOval(inCircle, strokePaint)

        return squareBmp
    }

    private fun getCenterCroppedBitmap(bitmap: Bitmap, size: Int): Bitmap {
        val cropStartX = (bitmap.width - size) / 2
        val cropStartY = (bitmap.height - size) / 2

        return Bitmap.createBitmap(bitmap, cropStartX, cropStartY, size, size)
    }

    private fun getScaledBitmap(bitmap: Bitmap, minSide: Int) : Bitmap {
        return if (bitmap.width != minSide || bitmap.height != minSide) {
            val smallest = min(bitmap.width, bitmap.height).toFloat()
            val factor = smallest / minSide
            Bitmap.createScaledBitmap(bitmap, (bitmap.width / factor).toInt(), (bitmap.height / factor).toInt(), false)
        } else bitmap
    }

    private fun getBitmapFromDrawable(): Bitmap? {
        if (bitmap != null)
            return bitmap

        if (drawable == null)
            return null

        if (drawable is BitmapDrawable)
            return (drawable as BitmapDrawable).bitmap

        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    private fun getCircleBitmap(bitmap: Bitmap): Bitmap {
        val smallest = min(bitmap.width, bitmap.height)
        val outputBmp = Bitmap.createBitmap(smallest, smallest, Config.ARGB_8888)
        val canvas = Canvas(outputBmp)

        val paint = Paint()
        val rect = Rect(0, 0, smallest, smallest)

        paint.isAntiAlias = true
        paint.isFilterBitmap = true
        paint.isDither = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(smallest / 2F, smallest / 2F, smallest / 2F, paint)

        paint.xfermode = PorterDuffXfermode(Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return outputBmp
    }


}

