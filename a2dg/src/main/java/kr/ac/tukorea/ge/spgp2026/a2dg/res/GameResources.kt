package kr.ac.tukorea.ge.spgp2026.a2dg.res

import android.content.res.Resources
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import java.util.HashMap

class GameResources(
    private val resources: Resources,
) {
    private val bitmapPool = BitmapPool(resources)
    private val soundPool: SoundPool
    private val soundMap = HashMap<Int, Int>()

    // ⭐️ [BGM 관리를 위한 MediaPlayer 변수 추가]
    private var bgmPlayer: MediaPlayer? = null
    private var currentBgmResId: Int = 0

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()
    }

    fun getBitmap(resId: Int): Bitmap = bitmapPool.get(resId)

    fun loadSound(resId: Int) {
        if (!soundMap.containsKey(resId)) {
            val assetFileDescriptor = resources.openRawResourceFd(resId)
            val soundId = soundPool.load(assetFileDescriptor, 1)
            soundMap[resId] = soundId
        }
    }

    fun playSound(resId: Int) {
        val soundId = soundMap[resId]
        if (soundId != null) {
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
        } else {
            loadSound(resId)
        }
    }

    // [배경음악(BGM) 시작 함수]
    fun playBgm(resId: Int) {
        // 이미 같은 음악이 나오고 있다면 중복 재생하지 않고 무시합니다.
        if (currentBgmResId == resId && bgmPlayer?.isPlaying == true) return

        // 다른 음악이 재생 중이었다면 안전하게 멈추고 정리합니다.
        stopBgm()

        currentBgmResId = resId
        // resources.openRawResourceFd를 이용해 안전하게 매개체를 넘겨 줍니다.
        val afd = resources.openRawResourceFd(resId)
        bgmPlayer = MediaPlayer().apply {
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            isLooping = true // 무한 반복 재생 활성화!
            prepare()
            start()
        }
    }

    // ️ [배경음악(BGM) 정지 함수]
    fun stopBgm() {
        bgmPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        bgmPlayer = null
        currentBgmResId = 0
    }

    // 전체 리소스 해제 시 BGM도 깔끔하게 종료
    fun release() {
        soundPool.release()
        stopBgm()
    }
}