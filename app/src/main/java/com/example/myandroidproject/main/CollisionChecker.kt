package com.example.myandroidproject.main

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.collidesWith
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kotlin.math.abs
import kotlin.math.sqrt

class CollisionChecker(
    private val world: World<MainScene.Layer>
) : IGameObject {

    override fun update(gctx: GameContext) {
        val fruits = world.objectsAt(MainScene.Layer.FRUIT).filterIsInstance<Fruit>()
        val floors = world.objectsAt(MainScene.Layer.FLOOR).filterIsInstance<IBoxCollidable>()

        // 1. 조작 중인 과일을 제외한 모든 과일을 일단 "낙하 상태"로 설정
        fruits.forEach {
            if (it.y > 600f) it.isFalling = true
        }

        // 이번 프레임에서 제거하고 생성할 정보들을 담을 리스트
        val toRemove = mutableSetOf<Fruit>()
        var toSpawn: Triple<Float, Float, Int>? = null // x, y, nextIndex

        // ---------------------------------------------------------------
        // A. 과일(원형)과 과일(원형)의 충돌 처리 (뚫림 방지 가드 강화)
        // ---------------------------------------------------------------
        // 겹침 현상을 완벽히 밀어내기 위해 물리 반복 연산을 3회로 증가시킵니다.
        repeat(3) {
            for (i in fruits.indices) {
                val fruit = fruits[i]
                if (toRemove.contains(fruit)) continue

                for (j in i + 1 until fruits.size) {
                    val other = fruits[j]
                    if (toRemove.contains(other)) continue

                    // 두 과일 중심점 간의 거리 계산
                    val dx = fruit.x - other.x
                    val dy = fruit.y - other.y
                    val distance = sqrt((dx * dx + dy * dy).toDouble()).toFloat()
                    val minDistance = fruit.radius + other.radius

                    // 두 원이 충돌(겹침)한 상태
                    if (distance < minDistance && distance != 0f) {

                        // [1] 같은 종류의 과일이면 합성 (첫 번째 반복에서만 처리)
                        if (fruit.index == other.index) {
                            if (fruit.index < Fruit.MAX_INDEX) {
                                toRemove.add(fruit)
                                toRemove.add(other)
                                val midX = (fruit.x + other.x) / 2
                                val midY = (fruit.y + other.y) / 2
                                toSpawn = Triple(midX, midY, fruit.index + 1)
                                break
                            }
                        }
                        // [2] 다른 종류의 과일이면 밀어내기
                        else {
                            val overlap = minDistance - distance
                            val nx = dx / distance
                            val ny = dy / distance

                            val top = if (fruit.y < other.y) fruit else other
                            val bottom = if (fruit.y < other.y) other else fruit

                            // ⭐️ 핵심 수정: 서로 부딪혀서 밀려나는 순간, 두 과일 모두 물리(낙하) 상태를 강제로 깨웁니다!
                            // 이렇게 해야 바닥에 고정되어 있던 과일도 옆에서 밀 때 스르륵 밀려납니다.
                            fruit.isFalling = true
                            other.isFalling = true

                            // 위치 보정: 겹친 거리를 완벽하게 100% 밀어냅니다.
                            if (bottom.vy == 0f || !bottom.isFalling) {
                                // 아래 과일이 바닥에 닿아있다면 위쪽 과일을 완전히 100% 들어 올립니다.
                                val directionSign = if (top === fruit) 1.0f else -1.0f
                                top.x += nx * overlap * directionSign
                                top.y += ny * overlap * directionSign
                            } else {
                                // 둘 다 공중 유동 상태면 반반씩 밀어냅니다.
                                fruit.x += nx * overlap * 0.5f
                                fruit.y += ny * overlap * 0.5f
                                other.x -= nx * overlap * 0.5f
                                other.y -= ny * overlap * 0.5f
                            }

                            // 경사면 미끄러짐 물리 가속도 부여
                            val slideAcceleration = 3.5f
                            val directionSign = if (top === fruit) 1.0f else -1.0f
                            top.x += nx * slideAcceleration * directionSign
                            top.vy *= 0.95f // 미끄러짐 마찰 저항

                            // 실시간 좌표 즉시 갱신
                            fruit.setPosition(fruit.x, fruit.y)
                            other.setPosition(other.x, other.y)

                            // [3] 안착 상태 계산 (실시간 갱신된 ny 기준)
                            // 수직에 가까운 평지(정수리 부근 ny > 0.85f)이고, 속도가 충분히 느릴 때만 다시 안착시킵니다.
                            if (abs(ny) > 0.85f) {
                                if (top.vy > 0) {
                                    top.vy *= -0.2f
                                    if (abs(top.vy) < 1.5f) {
                                        top.vy = 0f
                                        top.isFalling = false
                                    }
                                } else if (abs(top.vy) < 1.5f) {
                                    top.isFalling = false
                                }
                            } else {
                                // 옆구리를 타고 흐르거나 끼인 상태라면 무조건 물리 엔진을 켜서 탈출시킵니다.
                                top.isFalling = true
                            }
                        }
                    }
                }
            }
        }

        // ---------------------------------------------------------------
        // B. 최종 통합 루프 (바닥 및 벽 차단 보정)
        // ---------------------------------------------------------------
        for (i in fruits.indices) {
            val fruit = fruits[i]
            if (toRemove.contains(fruit)) continue

            // [최종 점검 1] 바닥 충돌 처리
            for (floor in floors) {
                val rect = floor.collisionRect

                if (fruit.x >= rect.left && fruit.x <= rect.right) {
                    if (fruit.y + fruit.radius > rect.top) {
                        fruit.bounceBack(rect.top)

                        if (abs(fruit.vy) < 2.0f) {
                            fruit.isFalling = false
                        }
                    }
                }
            }

            // [최종 점검 2] 좌우 벽 제한 적용 (절대 탈출 불가)
            val fruitHalf = fruit.radius
            fruit.x = fruit.x.coerceIn(100f + fruitHalf, 800f - fruitHalf)

            // 최종 확정된 안전 좌표를 캔버스 사각형에 동기화
            fruit.setPosition(fruit.x, fruit.y)
        }

        // 실제로 월드에서 제거
        toRemove.forEach {
            world.remove(it, MainScene.Layer.FRUIT)
        }

        // 새 과일 생성
        toSpawn?.let { (sx, sy, nextIdx) ->
            val newFruit = Fruit(gctx, nextIdx).apply {
                setPosition(sx, sy)
                isFalling = true
            }
            world.add(newFruit, MainScene.Layer.FRUIT)
        }
    }

    override fun draw(canvas: Canvas) {
    }
}