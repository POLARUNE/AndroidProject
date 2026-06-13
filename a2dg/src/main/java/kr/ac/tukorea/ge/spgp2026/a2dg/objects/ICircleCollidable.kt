package kr.ac.tukorea.ge.spgp2026.a2dg.objects

import android.graphics.PointF

interface ICircleCollidable {
    val centerX: Float
    val centerY: Float
    val radius: Float
}

// 두 원형 객체가 충돌했는지 판정하는 확장 함수
fun ICircleCollidable.collidesWith(other: ICircleCollidable): Boolean {
    val dx = this.centerX - other.centerX
    val dy = this.centerY - other.centerY
    // 피타고라스 정리를 이용한 중심점 간의 거리 계산 (거리의 제곱 비교로 연산 속도 최적화)
    val distanceSquared = dx * dx + dy * dy
    val radiusSum = this.radius + other.radius

    return distanceSquared < (radiusSum * radiusSum)
}