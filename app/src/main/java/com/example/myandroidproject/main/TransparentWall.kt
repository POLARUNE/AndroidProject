package com.example.myandroidproject.main

import android.graphics.Canvas
import android.graphics.RectF
import com.example.myandroidproject.R
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class TransparentWall(gctx: GameContext, x: Float, y: Float, w: Float, h: Float) : Sprite(gctx, R.drawable.redbox), IBoxCollidable {
    init {
        setCenter(x, y)
        setSize(w, h)
    }

    override val collisionRect: RectF
        get() = dstRect

    override fun draw(canvas: Canvas) {
    }
}