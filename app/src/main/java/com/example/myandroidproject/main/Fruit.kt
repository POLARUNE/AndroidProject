package com.example.myandroidproject.main


import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.example.myandroidproject.R
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.ICircleCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameView

class Fruit(gctx: GameContext, val index: Int) : Sprite(gctx, R.drawable.fruits),
    ICircleCollidable {

    // ICircleCollidable 구현
    override val centerX: Float get() = x
    override val centerY: Float get() = y
    override val radius: Float get() = width / 2f // 과일 너비의 절반을 반지름으로 사용

    var vy = 0f
    val gravity = 1.0f
    var isFalling = false
    private val bounceFactor = 0.3f

    companion object {
        const val COLS = 5 // 열 개수
        const val ROWS = 2 // 행 개수
        const val MAX_INDEX = 10 // 과일 개수
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

        // 4. 화면에 그릴 기본 크기 설정
        // 0번 과일은 반지름 40(크기 80), 갈수록 커짐
        val calculatedSize = 80f + (index * 15f)
        setSize(calculatedSize, calculatedSize)
    }

    // 디버그용 Paint 객체를 동적 생성을 피하기 위해 클래스 멤버로 선언
    private val debugPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    // draw 함수 재정의
    override fun draw(canvas: Canvas) {
        val visualScale = 1.4f

        val halfW = (width / 2f) * visualScale
        val halfH = (height / 2f) * visualScale

        // 중앙 좌표(x, y) 기준으로 비주얼 스케일이 적용된 새로운 출력 영역 계산
        dstRect.set(
            x - halfW,
            y - halfH,
            x + halfW,
            y + halfH
        )

        // 부모(Sprite)의 draw를 호출하여 실제 과일 이미지를 먼저 그립니다.
        super.draw(canvas)

        // 프레임워크의 디버그 정보 표시 플래그가 true일 때만 빨간 원을 얹어서 그립니다.
        if (GameView.drawsDebugInfo) {
            canvas.drawCircle(
                centerX, // x와 동일
                centerY, // y와 동일
                radius,  // width / 2f와 동일
                debugPaint
            )
        }
    }

    fun setPosition(targetX: Float, targetY: Float) {
        setCenter(targetX, targetY)
    }

    fun bounceBack(surfaceTop: Float) {
        y = surfaceTop - radius // height / 2f 대신 radius 사용
        syncDstRect()

        vy = -vy * bounceFactor
        if (Math.abs(vy) < 2.0f) {
            vy = 0f
        }
    }

    override fun update(gctx: GameContext) {
        // 중력과 Y축 이동은 낙하 중일 때만 처리
        if (isFalling) {
            vy += gravity
            y += vy
        }

        // 벽 제한은 낙하 중(isFalling)이 아니더라도 '항상' 감시합니다.
        // 바닥에 누워있을 때 위에서 밀어도 절대 벽을 못 넘어가게 만듭니다.
        val minX = 100f + radius
        val maxX = 800f - radius

        if (x < minX) {
            x = minX
            // 벽에 부딪혔을 때 속도가 남아있다면 지워줍니다.
        } else if (x > maxX) {
            x = maxX
        }

        // 좌표 변경 후 dstRect 갱신
        syncDstRect()
    }


}