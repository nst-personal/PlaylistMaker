package com.example.playlistmaker.presentation.ui.media_player

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.example.playlistmaker.R

interface OnPlaybackButtonViewClickListener {
    fun onTouch(isPlaying: Boolean)
}

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var isPlaying = false

    private val imagePlayBitmap: Bitmap?
    private val imagePauseBitmap: Bitmap?
    private val playDrawable: Drawable?
    private val pauseDrawable: Drawable?
    private var imageRect = RectF(0f, 0f, 0f, 0f)
    private var listener: OnPlaybackButtonViewClickListener? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                playDrawable = getDrawable(R.styleable.PlaybackButtonView_playDrawable)
                pauseDrawable = getDrawable(R.styleable.PlaybackButtonView_pauseDrawable)
                playDrawable?.setBounds(
                    0, 0,
                    playDrawable.intrinsicWidth, playDrawable.intrinsicHeight
                )
                pauseDrawable?.setBounds(
                    0, 0,
                    pauseDrawable.intrinsicWidth, pauseDrawable.intrinsicHeight
                )
                imagePlayBitmap = playDrawable?.toBitmap()
                imagePauseBitmap = pauseDrawable?.toBitmap()
            } finally {
                recycle()
            }
        }
    }

    fun setOnPlayPauseButtonClickListener(listener: OnPlaybackButtonViewClickListener) {
        this.listener = listener
    }

    fun updateState() {
        isPlaying = !isPlaying
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }

            MotionEvent.ACTION_UP -> {
                listener?.onTouch(isPlaying)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        handleImage(canvas)
    }

    private fun handleImage(canvas: Canvas) {
        val image = if (!isPlaying) imagePlayBitmap else imagePauseBitmap
        image?.let {
            canvas.drawBitmap(image, null, imageRect, null)
        }
    }

}