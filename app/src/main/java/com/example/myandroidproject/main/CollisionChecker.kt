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
        val fruits = world.objectsAt(MainScene.Layer.FRUIT).filterIsInstance<Fruit>()
        val floors = world.objectsAt(MainScene.Layer.FLOOR).filterIsInstance<IBoxCollidable>()

        // 1. 조작 중인 과일을 제외한 모든 과일을 일단 "낙하 상태"로 설정
        // (단, MainScene에서 currentFruit인 객체는 건드리지 않도록 주의)
        fruits.forEach {
            // y가 생성 지점(예: 150f)보다 충분히 아래에 있을 때만 중력 체크 시작
            if (it.y > 600f) it.isFalling = true
        }

        // 이번 프레임에서 제거하고 생성할 정보들을 담을 리스트
        val toRemove = mutableSetOf<Fruit>()
        var toSpawn: Triple<Float, Float, Int>? = null // x, y, nextIndex

        for (i in fruits.indices) {
            val fruit = fruits[i]
            if (toRemove.contains(fruit)) continue

            // A. 바닥과의 충돌
            for (floor in floors) {
                if (fruit.collidesWith(floor)) {
                    if (fruit.vy > 0 && fruit.y < floor.collisionRect.top) {
                        fruit.bounceBack(floor.collisionRect.top)
                    }
                }
            }

            // B. 과일 간의 충돌
            for (j in i + 1 until fruits.size) {
                val other = fruits[j]
                if (toRemove.contains(other)) continue

                if (fruit.collidesWith(other)) {
                    if (fruit.index == other.index) {
                        // 합성 로직
                        if (fruit.index < Fruit.MAX_INDEX) {
                            toRemove.add(fruit)
                            toRemove.add(other)
                            val midX = (fruit.x + other.x) / 2
                            val midY = (fruit.y + other.y) / 2
                            toSpawn = Triple(midX, midY, fruit.index + 1)
                            break
                        }
                    } else {
                        // [물리 충돌 및 안착 로직]
                        val top = if (fruit.y < other.y) fruit else other
                        val bottom = if (fruit.y < other.y) other else fruit

                        // 아래 과일이 받침대 역할을 함
                        top.bounceBack(bottom.collisionRect.top)

                        // 아래 과일이 고정되어 있다면 위 과일도 고정 가능성 있음
                        if (!bottom.isFalling && Math.abs(top.vy) < 2.0f) {
                            top.isFalling = false
                        }
                    }
                }
            }
        }

        // 실제로 월드에서 제거
        toRemove.forEach {
            world.remove(it, MainScene.Layer.FRUIT)
        }

        // 새 과일 생성
        toSpawn?.let { (sx, sy, nextIdx) ->
            val newFruit = Fruit(gctx, nextIdx).apply {
                setPosition(sx, sy)
                isFalling = true // 생성되자마자 물리 적용
            }
            world.add(newFruit, MainScene.Layer.FRUIT)
        }
    }

    override fun draw(canvas: Canvas) {
    }
}