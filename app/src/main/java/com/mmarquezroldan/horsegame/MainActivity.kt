package com.mmarquezroldan.horsegame

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private var bitmap: Bitmap? = null

    private var mHandler: Handler? = null
    private var timeInSeconds = 0L
    private var gaming = true

    private var widthBonus = 0

    private var cellSelectedX = 0
    private var cellSelectedY = 0

    private var levelMoves = 64
    private var movesRequired = 4
    private var moves = 64
    private var options: Int = 0
    private var bonus = 0

    private var checkMovement = true

    private var nameColorBlack = "black_cell"
    private var nameColorWhite = "white_cell"

    private lateinit var board: Array<IntArray>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initScreenGame()
        startGame()
    }

    fun checkCellClicked(view: View) {
        val name = view.tag.toString()
        val x = name.subSequence(1, 2).toString().toInt()
        val y = name.subSequence(2, 3).toString().toInt()

        checkCell(x, y)
    }

    private fun checkCell(x: Int, y: Int) {

        var checkTrue = true

        if (checkMovement) {
            val diffX = x - cellSelectedX
            val diffY = y - cellSelectedY

            checkTrue = false

            // TODO: improve L move logic
            if (diffX == 1 && diffY == 2) checkTrue = true
            if (diffX == 1 && diffY == -2) checkTrue = true
            if (diffX == 2 && diffY == 1) checkTrue = true
            if (diffX == 2 && diffY == -1) checkTrue = true
            if (diffX == -1 && diffY == 2) checkTrue = true
            if (diffX == -1 && diffY == -2) checkTrue = true
            if (diffX == -2 && diffY == 1) checkTrue = true
            if (diffX == -2 && diffY == -1) checkTrue = true
        } else {
            if (board[x][y] != 1) {
                bonus--
                val tvBonusData = findViewById<TextView>(R.id.tvBonusData)
                tvBonusData.text = " + $bonus"
                if (bonus == 0) {
                    tvBonusData.text = ""
                }
            }
        }


        if (board[x][y] == 1) checkTrue = false

        if (checkTrue) selectCell(x, y)
    }

    private fun resetBoard() {

        // 0 empty
        // 1 not empty
        // 2 bonus
        // 9 possible move

        board = arrayOf(
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0)
        )
    }

    private fun clearBoard() {
        var iv: ImageView

        var colorBlack = ContextCompat.getColor(this,
            resources.getIdentifier(nameColorBlack, "color", packageName))

        var colorWhite = ContextCompat.getColor(this,
            resources.getIdentifier(nameColorWhite, "color", packageName))

        for (i in 0..7) {
            for (j in 0..7) {
                iv = findViewById(resources.getIdentifier("c$i$j", "id", packageName))
                iv.setImageResource(0)

                if (checkColorCell(i, j)) iv.setBackgroundColor(colorBlack)
                else iv.setBackgroundColor(colorWhite)
            }
        }
    }

    private fun setFirstPosition() {

        val x: Int = (0..7).random()
        val y: Int = (0..7).random()

        cellSelectedX = x
        cellSelectedY = y

        selectCell(x, y)
    }

    private fun growProgressBonus() {

        val movesDone = levelMoves - moves
        val bonusDone = movesDone / movesRequired
        val movesRest = movesRequired * bonusDone
        val bonusGrow = movesDone - movesRest

        val widthBonus = ((widthBonus / movesRequired) * bonusGrow).toFloat()

        val v = findViewById<View>(R.id.vNewBonus)
        val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics()).toInt()
        val width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthBonus, getResources().getDisplayMetrics()).toInt()
        v.setLayoutParams(TableRow.LayoutParams(width, height))
    }

    private fun selectCell(x: Int, y: Int) {

        moves--
        val tvMovesData = findViewById<TextView>(R.id.tvMovesData)
        tvMovesData.text = moves.toString()

        growProgressBonus()

        if (board[x][y] == 2) {
            bonus++
            var tvBonusData = findViewById<TextView>(R.id.tvBonusData)
            tvBonusData.setText(" + $bonus")
        }

        board[x][y] = 1
        paintHorseCell(cellSelectedX, cellSelectedY, "previous_cell")

        cellSelectedX = x
        cellSelectedY = y

        clearOptions()

        paintHorseCell(x, y, "selected_cell")
        checkMovement = true
        checkOptions(x, y)

        if (moves > 0) {
            checkNewBonus()
            checkGameOver()
        } else {
            showMessage("You Win!", "Next Level", false)
        }
    }

    private fun checkGameOver() {
        if (options == 0) {
            if (bonus > 0) {
                checkMovement = false
                paintAllOptions()
            } else {
                showMessage("Game Over", "Try Again", true)
            }
        }
    }

    private fun paintAllOptions() {
        for (i in 0..7) {
            for (j in 0..7) {
                if (board[i][j] != 1) paintOption(i, j)
                if (board[i][j] == 0) board[i][j] = 9
            }
        }
    }

    private fun showMessage(title: String, action: String, gameOver: Boolean) {

        gaming = false
        val lyMessage = findViewById<LinearLayout>(R.id.lyMessage)
        lyMessage.visibility = View.VISIBLE

        val tvTitleMessage = findViewById<TextView>(R.id.tvTitleMessage)
        tvTitleMessage.text = title

        val tvTimeData = findViewById<TextView>(R.id.tvTimeData)

        val score: String = if (gameOver) {
            "Score: " + (levelMoves - moves) + "/" + levelMoves
        } else {
            tvTimeData.text.toString()
        }

        val tvScoreMessage = findViewById<TextView>(R.id.tvScoreMessage)
        tvScoreMessage.text = score

        val tvAction = findViewById<TextView>(R.id.tvAction)
        tvAction.text = action

    }

    private fun checkNewBonus() {
        if (moves % movesRequired == 0) {
            var bonusCellX = 0
            var bonusCellY = 0

            var bonusCell = false
            while (!bonusCell) {
                bonusCellX = (0..7).random()
                bonusCellY = (0..7).random()

                if (board[bonusCellX][bonusCellY] == 0) bonusCell = true
            }
            board[bonusCellX][bonusCellY] = 2
            paintBonusCell(bonusCellX, bonusCellY)
        }
    }

    private fun paintBonusCell(x: Int, y: Int) {
        val iv: ImageView = findViewById(resources.getIdentifier("c$x$y", "id", packageName))
        iv.setImageResource(R.drawable.bonus)
    }

    private fun clearOptions() {
        for (i in 0..7) {
            for (j in 0..7) {
                if (board[i][j] == 9 || board[i][j] == 2) {
                    if (board[i][j] == 9) board[i][j] = 0
                    clearOption(i, j)
                }
            }
        }
    }

    private fun clearOption(x: Int, y: Int) {
        val iv: ImageView = findViewById(resources.getIdentifier("c$x$y", "id", packageName))

        if (checkColorCell(x, y)) {
            iv.setBackgroundColor(ContextCompat.getColor(this,
                resources.getIdentifier(nameColorBlack, "color", packageName)))
        } else {
            iv.setBackgroundColor(ContextCompat.getColor(this,
                resources.getIdentifier(nameColorWhite, "color", packageName)))
        }

        if (board[x][y] == 1) {
            iv.setBackgroundColor(ContextCompat.getColor(this,
                resources.getIdentifier("previous_cell", "color", packageName)))
        }
    }

    private fun checkOptions(x: Int, y: Int) {
        options = 0

        checkMove(x, y, 1, 2)
        checkMove(x, y, 2, 1)
        checkMove(x, y, 1, -2)
        checkMove(x, y, 2, -1)
        checkMove(x, y, -1, 2)
        checkMove(x, y, -2, 1)
        checkMove(x, y, -1, -2)
        checkMove(x, y, -2, -1)

        val tvOptionData = findViewById<TextView>(R.id.tvOptionsData)
        tvOptionData.text = options.toString()
    }

    private fun checkMove(x: Int, y: Int, moveX: Int, moveY: Int) {
        val optionX = x + moveX
        val optionY = y + moveY

        if (optionX < 8 && optionY < 8 && optionX >= 0 && optionY >= 0) {
            if (board[optionX][optionY] == 0 || board[optionX][optionY] == 2 ) {
                options++
                paintOption(optionX, optionY)

                if (board[optionX][optionY] == 0) board[optionX][optionY] = 9
            }
        }
    }

    private fun paintOption(x: Int, y: Int) {
        val iv: ImageView = findViewById(resources.getIdentifier("c$x$y", "id", packageName))

        if (checkColorCell(x, y)) iv.setBackgroundResource(R.drawable.option_black)
        else iv.setBackgroundResource(R.drawable.option_white)
    }

    private fun checkColorCell(x: Int, y: Int): Boolean {
        val blackColumnX = arrayOf(0, 2, 4, 6)
        val blackRowX = arrayOf(1, 3, 5, 7)

        return (blackColumnX.contains(x) && blackColumnX.contains(y)
                || blackRowX.contains(x) && blackRowX.contains(y))
    }

    private fun paintHorseCell(x: Int, y: Int, color: String) {
        val iv: ImageView = findViewById(resources.getIdentifier("c$x$y", "id", packageName))
        iv.setBackgroundColor(ContextCompat.getColor(this, resources.getIdentifier(color, "color", packageName)))
        iv.setImageResource(R.drawable.chess_horse)
    }

    private fun initScreenGame() {
        setSizeBoard()
        hideMessage()
    }

    private fun setSizeBoard() {
        var iv: ImageView

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x

        val widthDP = (width / getResources().displayMetrics.density)
        val widthCell = widthDP / 8

        widthBonus = 2 * widthCell.toInt()

        for (i in 0..7) {
            for (j in 0..7) {
                iv = findViewById(resources.getIdentifier("c$i$j", "id", packageName))

                val height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    widthCell,
                    getResources().displayMetrics
                ).toInt()
                val width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthCell, getResources().displayMetrics).toInt()

                iv.layoutParams = TableRow.LayoutParams(width, height)
            }
        }
    }
    // TODO: implement levels
    // TODO: screenshot not working (should use MediaProjection)
    fun launchShareGame(view: View) {
        shareGame()
    }

    private fun shareGame() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)

    }

    private fun hideMessage() {
        val lyMessage = findViewById<LinearLayout>(R.id.lyMessage)
        lyMessage.visibility = View.INVISIBLE
    }

    private fun startTime() {
        mHandler = Handler(Looper.getMainLooper())
        chronometer.run()
    }

    private fun resetTime() {
        mHandler?.removeCallbacks(chronometer)
        timeInSeconds = 0

        val tvTimeData = findViewById<TextView>(R.id.tvTimeData)
        tvTimeData.text = "00:00"
    }

    private var chronometer: Runnable = object: Runnable {
        override fun run() {
            try {
                if (gaming) {
                    timeInSeconds++
                    updateStopWatchView(timeInSeconds)
                }
            } finally {
                mHandler!!.postDelayed(this, 1000)
            }
        }
    }

    private fun updateStopWatchView(timeInSeconds: Long) {
        val formatedTime = getFormattedStopWatch((timeInSeconds * 1000))
        val tvTimeData = findViewById<TextView>(R.id.tvTimeData)
        tvTimeData.text = formatedTime
    }

    private fun getFormattedStopWatch(ms: Long): String {
        var milliseconds = ms
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

        return "${if (minutes < 10) "0" else ""}$minutes" +
                ":${if (seconds < 10) "0" else ""}$seconds"
    }

    private fun startGame() {

        gaming = true
        resetBoard()
        clearBoard()
        setFirstPosition()
        resetTime()
        startTime()
    }

    // TODO: create an AD
}