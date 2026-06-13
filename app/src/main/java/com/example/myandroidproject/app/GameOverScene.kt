package com.example.myandroidproject.app

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import androidx.core.graphics.toColorInt
import com.example.myandroidproject.main.MainScene

class GameOverScene(
    gctx: GameContext,
    private val mainScene: MainScene,
    private val finalScore: Int
) : Scene(gctx) {

    // 1. 배경을 어둡게 할 반투명 페인트
    private val bgPaint = Paint().apply {
        color = Color.BLACK
        alpha = 180
    }

    // 2. 텍스트 페인트 (900x1600 가상 좌표계 기준 크기)
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 70f
        textAlign = Paint.Align.CENTER
        android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        isAntiAlias = true
    }

    // 3. 버튼 영역 정의 (900x1600 가상 좌표계 기준의 고정 위치)
    private val btnRestartRect = RectF(150f, 900f, 430f, 1020f)
    private val btnRankingRect = RectF(470f, 900f, 750f, 1020f)

    private val btnPaint = Paint().apply {
        color = "#FFEB3B".toColorInt() // 노란색
        isAntiAlias = true
    }

    private val btnRankPaint = Paint().apply {
        color = "#2196F3".toColorInt() // 파란색
        isAntiAlias = true
    }

    private val btnTextPaint = Paint().apply {
        color = Color.BLACK
        textSize = 35f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    // 가상 좌표계 매핑 및 클리핑 일치를 위해 true 유지
    override val clipsRect = true

    override fun update(gctx: GameContext) {}

    override fun draw(canvas: Canvas) {
        // [A] 아래에 깔려있던 오리지널 게임 화면을 먼저 강제 렌더링
        mainScene.draw(canvas)

        // [B] 그 위에 가상 해상도 고정 크기(900x1600)만큼 어두운 필터 씌우기
        canvas.drawRect(0f, 0f, 900f, 1600f, bgPaint)

        // [C] GAME OVER 텍스트 및 점수 출력
        canvas.drawText("GAME OVER", 450f, 500f, textPaint)
        canvas.drawText("최종 점수: $finalScore", 450f, 650f, textPaint)

        // [D] 시작화면 (재시작) 버튼 그리기
        canvas.drawRoundRect(btnRestartRect, 20f, 20f, btnPaint)
        canvas.drawText("시작 화면", btnRestartRect.centerX(), btnRestartRect.centerY() + 12f, btnTextPaint)

        // [E] 랭킹보기 버튼 그리기
        canvas.drawRoundRect(btnRankingRect, 20f, 20f, btnRankPaint)
        canvas.drawText("랭킹 보기", btnRankingRect.centerX(), btnRankingRect.centerY() + 12f, btnTextPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            // ⭐️ [핵심 수정] 휴대폰 기기 화면의 픽셀 좌표를
            // 프레임워크의 GameMetrics를 사용하여 900x1600 가상 좌표로 완벽하게 역전환(Inverse)합니다.
            val virtualPoint = gctx.metrics.fromScreen(event.x, event.y)
            val tx = virtualPoint.x
            val ty = virtualPoint.y

            // 디버깅 로그: 변환된 가상 좌표계의 위치를 찍어줍니다.
            android.util.Log.d("GameOverScene", "Converted Virtual Touch: X=$tx, Y=$ty")

            // 이제 변환된 가상 좌표(tx, ty)와 가상 영역(RectF)을 대조하므로 오차가 0%가 됩니다!
            if (btnRestartRect.contains(tx, ty)) {
                android.util.Log.d("GameOverScene", "시작 화면 버튼 터치 매핑 성공!")
                pop()
                pop()
                return true
            }
            if (btnRankingRect.contains(tx, ty)) {
                android.util.Log.d("GameOverScene", "랭킹 보기 버튼 터치 매핑 성공!")
                return true
            }
        }
        return true
    }
}