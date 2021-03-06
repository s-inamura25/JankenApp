package com.example.shota_inamura.jankenapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    val gu = 0
    val choki = 1
    val pa = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val id = intent.getIntExtra("YOUR_HAND", 0)

        val yourHand: Int
        when(id) {
            R.id.guBtn -> {
                yourHandText.text = "グー"
                yourHand = gu
            }
            R.id.chokiBtn -> {
                yourHandText.text = "チョキ"
                yourHand = choki
            }
            R.id.paBtn -> {
                yourHandText.text = "パー"
                yourHand = pa
            }
            else -> yourHand = gu
        }

        val comHand = getHand()
        when(comHand) {
            gu -> comHandText.text = "グー"
            choki -> comHandText.text = "チョキ"
            pa -> comHandText.text = "パー"
        }

        val result = (comHand - yourHand + 3) % 3
        when(result) {
            0 -> resultText.text = "引き分け"
            1 -> resultText.text = "あなたの勝ち"
            2 -> resultText.text = "あなたの負け"
        }

        printScore(result)

        backBtn.setOnClickListener { finish() }

        saveData(yourHand, result)
    }

    private fun saveData(yourHand: Int, result: Int) {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val gameCount = pref.getInt("GAME_COUNT", 0)
        val yourScore = pref.getInt("YOUR_SCORE", 0)

        val editor = pref.edit()
        editor.putInt("GAME_COUNT", gameCount + 1)
                .putInt("YOUR_SCORE",
                        if (result == 1) yourScore + 1
                        else yourScore)
                .putInt("LAST_YOUR_HAND", yourHand)
                .putInt("LAST_RESULT", result)
                .apply()
    }

    private fun getHand(): Int {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val lastResult = pref.getInt("LAST_RESULT", -1)
        val lastYourHand = pref.getInt("LAST_YOUR_HAND", 0)

//        基本の手はランダム
        var hand = (Math.random() * 3).toInt()

//        以下、日本じゃんけん協会「勝利の法則」より
//        3.あいこには負ける手を
//        4.チョキのあいこにはチョキを
//        7.勝ち手は続く
        if (lastResult == 0) {
            hand = when(lastYourHand) {
                gu, choki -> choki
                pa -> gu
                else -> gu
            }
        } else if (lastResult == 1) {
            hand = when(lastYourHand) {
                gu -> pa
                choki, pa -> lastYourHand - 1
                else -> gu
            }
        }
        return hand
    }

    private fun printScore(result: Int) {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val gameCount = pref.getInt("GAME_COUNT", 0) + 1
        var yourScore = pref.getInt("YOUR_SCORE", 0)

        if (result == 1) yourScore ++

        yourScoreText.text = "あなたのスコア: $yourScore / $gameCount"
    }
}
