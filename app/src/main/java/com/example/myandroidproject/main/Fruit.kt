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
    private val bounceFactor = 0.5f

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

    fun setPosition(targetX: Float, targetY: Float) {
        setCenter(targetX, targetY)
    }

    fun bounceBack(surfaceTop: Float) {
        // 1. 위치 보정: 벽 안으로 파고들지 않게 표면 위로 딱 붙임
        y = surfaceTop - (height / 2f)
        syncDstRect()

        // 2. 속도 반전 및 감쇠: 위쪽 방향(-)으로 속도를 바꿈
        vy = -vy * bounceFactor

        // 3. 정지 조건: 튕기는 속도가 아주 작아지면 아예 멈추고 떨어지는 상태 해제
        if (Math.abs(vy) < 2.0f) {
            vy = 0f
            isFalling = false
        }
    }

    override fun update(gctx: GameContext) {
        if (!isFalling) return

        vy += gravity
        y += vy

        // 2. 좌우 벽 제한 (X축 범위 제한)
        val halfWidth = width / 2f
        val minX = 100f + halfWidth
        val maxX = 800f - halfWidth

        if (x < minX) {
            x = minX
        } else if (x > maxX) {
            x = maxX
        }

        // 좌표 변경 후 dstRect 갱신
        syncDstRect()
    }
}