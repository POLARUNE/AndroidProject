package com.example.myandroidproject.main


import android.graphics.Rect
import android.graphics.RectF
import com.example.myandroidproject.R
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class Fruit(gctx: GameContext, val index: Int) : Sprite(gctx, R.drawable.fruits), IBoxCollidable {

    // IBoxCollidable 구현: Sprite의 dstRect를 충돌 영역으로 사용
    override val collisionRect: RectF get() = dstRect

    var vy = 0f
    val gravity = 1.0f
    var isFalling = false

    companion object {
        const val COLS = 5 // 열 개수
        const val ROWS = 3 // 행 개수
    }

    init {
        // 1. 개별 과일 프레임의 크기 계산
        val frameWidth = bitmapWidth / COLS
        val frameHeight = bitmapHeight / ROWS

        // 2. index를 이용해 행(row)과 열(col) 번호 계산 (0부터 시작)
        val col = index % COLS
        val row = index / COLS

        // 3. 비트맵에서 자를 영역(srcRect) 설정
        val left = col * frameWidth
        val top = row * frameHeight
        srcRect = Rect(left, top, left + frameWidth, top + frameHeight)

        // 4. 화면에 그릴 기본 크기 설정 (예: 100x100)
        // 나중에 과일 종류(index)에 따라 크기를 다르게 설정할 수 있습니다.
        setSize(100f, 100f)
    }

    // 과일이 떨어지는 로직을 위해 x, y 좌표를 외부에서 쉽게 설정할 수 있도록 public으로 열어둡니다.
    fun setPosition(targetX: Float, targetY: Float) {
        setCenter(targetX, targetY)
    }

    override fun update(gctx: GameContext) {
        if (!isFalling) return

        vy += gravity
        y += vy

        // 좌표 변경 후 dstRect 갱신
        syncDstRect()
    }
}