package com.example.myandroidproject.main

import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class MainScene(gctx: GameContext) : Scene(gctx) {
    enum class Layer {
        BG, BUCKET, FRUIT
    }

    override val clipsRect = true
    override val world = World(Layer.entries.toTypedArray()).apply {
        add(Background(gctx), Layer.BG)

        add(TransparentWall(gctx, 450f, 1405f, 700f, 10f), Layer.BUCKET)
        //add(TransparentWall(gctx, 100f, 1045f, 10f, 750f), Layer.BUCKET)
        //add(TransparentWall(gctx, 800f, 1045f, 10f, 750f), Layer.BUCKET)

        // 0번 과일(첫 번째 행 첫 번째 열)을 화면 중앙에 추가해보기 (이미지 조정 필요)
        val testFruit = Fruit(gctx, 0).apply {
            setPosition(450f, 800f)
            isFalling = true
        }
        add(testFruit, Layer.FRUIT)
    }
}


