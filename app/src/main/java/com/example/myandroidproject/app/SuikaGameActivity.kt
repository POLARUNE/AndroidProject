package com.example.myandroidproject.app

import com.example.myandroidproject.BuildConfig
import com.example.myandroidproject.main.MainScene
import kr.ac.tukorea.ge.spgp2026.a2dg.activity.BaseGameActivity
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class SuikaGameActivity : BaseGameActivity() {
    override val drawsDebugGrid: Boolean = BuildConfig.DEBUG
    override val drawsDebugInfo: Boolean = BuildConfig.DEBUG
    override val drawsFpsGraph: Boolean = BuildConfig.DEBUG
    override fun createRootScene(gctx: GameContext): Scene {
        gctx.metrics.setSize(900f, 1600f)
        return MainScene(gctx)
    }
}