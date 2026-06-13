package com.example.myandroidproject.main

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import com.example.myandroidproject.app.GameOverScene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kotlin.math.sin

class MainScene(gctx: GameContext) : Scene(gctx) {

    // 현재 조작 중인(대기 중인) 과일
    private var currentFruit: Fruit? = null
    private val score = Score(gctx)
    private var overLineTime = 0f
    private val GAME_OVER_LIMIT = 4.0f

    private val DEAD_LINE_Y = 668f
    //private val DEAD_LINE_Y = 1300f // 디버깅용

    enum class Layer {
        BG, FLOOR, FRUIT, CONTROLLER, UI
    }

    override val clipsRect = true
    override val world = World(Layer.entries.toTypedArray()).apply {
        add(Background(gctx), Layer.BG)

        add(Floor(gctx, 450f, 1405f, 700f, 10f), Layer.FLOOR)

        //자기 자신(this)을 주입하여 CollisionChecker가 MainScene의 함수들을 부를 수 있게 함
        add(CollisionChecker(this@MainScene, this), Layer.CONTROLLER)

        add(score, Layer.UI)
    }

    init {
        gctx.res.loadSound(com.example.myandroidproject.R.raw.pop)
        spawnNextFruit()
    }

    private fun spawnNextFruit() {
        // 0~4번 사이의 작은 과일 중 하나를 랜덤하게 생성
        val randomIndex = (0..3).random()
        val fruit = Fruit(gctx, randomIndex).apply {
            // 초기 위치: 상단 중앙, 아직 떨어지지 않음
            setPosition(450f, 600f)
            isFalling = false
        }
        currentFruit = fruit
        world.add(fruit, Layer.FRUIT)
    }

    private val linePaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
        style = Paint.Style.STROKE
        alpha = 180
        isAntiAlias = true
    }

    override fun update(gctx: GameContext) {
        // 상위 Scene 클래스의 기본 update(world 업데이트)를 정상 수행
        super.update(gctx)

        // 데드라인 감시 및 카운트다운
        val fruits = world.objectsAt(Layer.FRUIT).filterIsInstance<Fruit>()
        val isAnyFruitOverLine = fruits.any { it !== currentFruit && it.y - it.radius < DEAD_LINE_Y }

        if (isAnyFruitOverLine) {
            overLineTime += gctx.frameTime

            // 4초 제한 시간에 가까워질수록 선을 더 불투명하고 붉게 만듦
            val blinkFactor = (sin(System.currentTimeMillis().toDouble() * 0.01) + 1) / 2
            linePaint.alpha = (100 + (blinkFactor * 155)).toInt() // 100~255 사이로 깜빡임
            linePaint.strokeWidth = 8f // 선을 더 두껍게 변경

            if (overLineTime >= GAME_OVER_LIMIT) {
                // 4초 완료 시점: 깔끔하게 GameOverScene을 스택에 푸시!
                overLineTime = 0f // 타이머 초기화
                GameOverScene(gctx, this@MainScene, score.value).push()
            }
        } else {
            overLineTime = 0f
            linePaint.alpha = 180 // 평소 상태로 복귀
            linePaint.strokeWidth = 5f
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        val tx = event.x - 50f

        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                // 1. 손가락을 대고 있거나 움직일 때 과일이 좌우로 따라옴
                currentFruit?.let { fruit ->
                    val halfWidth = fruit.width / 2f
                    // 벽 범위(100~800) 안에서만 움직이도록 제한
                    val clampedX = tx.coerceIn(100f + halfWidth, 800f - halfWidth)
                    fruit.setPosition(clampedX, 600f)
                }
            }
            MotionEvent.ACTION_UP -> {
                // 2. 손가락을 떼면 과일 투하
                currentFruit?.let { fruit ->
                    fruit.isFalling = true
                    currentFruit = null // 이제 제어권을 잃음

                    // 1초 뒤 다음 과일 생성
                    Handler(Looper.getMainLooper()).postDelayed({
                        spawnNextFruit()
                    }, 1000)
                }
            }
        }
        return true
    }

    override fun draw(canvas: Canvas) {
        // 1. 배경, 바닥, 과일, UI 등 월드의 모든 요소를 먼저 그립니다.
        super.draw(canvas)

        // 2. 좌우 벽(100f ~ 800f) 사이에 지정한 DEAD_LINE_Y(600f) 높이로 선을 긋습니다.
        // 화면 전체를 가로지르게 하고 싶다면 100f 대신 0f, 800f 대신 canvas.width.toFloat()를 넣으셔도 됩니다.
        canvas.drawLine(
            100f,           // 시작 X (좌측 벽)
            DEAD_LINE_Y,    // 시작 Y
            800f,           // 끝 X (우측 벽)
            DEAD_LINE_Y,    // 끝 Y
            linePaint       // 지정한 페인트 스타일
        )
    }

    override fun onEnter() {
        super.onEnter()
        gctx.res.playBgm(com.example.myandroidproject.R.raw.bgm)
    }

    override fun onExit() {
        super.onExit()
        gctx.res.stopBgm()
    }

    fun addScore(amount: Int) {
        score.value += amount
    }

    fun getScore(): Int {
        return score.value
    }
}


