package com.example.myandroidproject.main

import com.example.myandroidproject.R
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class Background(gctx: GameContext) : Sprite(gctx, R.drawable.ingamebg) {
    init {
        setCenterProportionalWidth(450f, 800f, 900f)
    }
}