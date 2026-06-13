package com.example.myandroidproject.main

import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class MainScene(gctx: GameContext) : Scene(gctx) {

    // 현재 조작 중인(대기 중인) 과일
    private var currentFruit: Fruit? = null
    private val score = Score(gctx)
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


    fun addScore(amount: Int) {
        score.value += amount
    }

    fun getScore(): Int {
        return score.value
    }
}


