package com.example.myandroidproject.main

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.collidesWith
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class CollisionChecker(
    private val world: World<MainScene.Layer>
) : IGameObject {

    override fun update(gctx: GameContext) {
        // 1. 검사 대상들 가져오기
        val fruits = world.objectsAt(MainScene.Layer.FRUIT).filterIsInstance<Fruit>()
        val walls = world.objectsAt(MainScene.Layer.FLOOR).filterIsInstance<IBoxCollidable>()

        // 2. 모든 과일에 대해 루프
        for (fruit in fruits) {
            // A. 벽(바닥)과의 충돌 체크
            for (wall in walls) {
                if (fruit.collidesWith(wall)) {
                    // 과일이 아래로 떨어지다 바닥에 닿은 경우
                    if (fruit.vy > 0 && fruit.y < wall.collisionRect.top) {
                        fruit.bounceBack(wall.collisionRect.top)
                    }
                }
            }

            // B. 다른 과일과의 충돌 체크
            for (other in fruits) {
                if (fruit === other) continue // 나 자신은 제외

                if (fruit.collidesWith(other)) {
                    // 과일끼리 부딪혔을 때의 로직
                    if (fruit.vy > 0 && fruit.y < other.y) {
                        fruit.bounceBack(other.collisionRect.top)
                    }
                }
            }
        }
    }

    override fun draw(canvas: Canvas) {
    }
}