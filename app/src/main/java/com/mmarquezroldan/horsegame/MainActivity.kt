package com.mmarquezroldan.horsegame

import android.graphics.Point
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private var cellSelectedX = 0
    private var cellSelectedY = 0

    private var movesRequired = 4
    private var moves = 64
    private var options: Int = 0

    private var nameColorBlack = "black_cell"
    private var nameColorWhite = "white_cell"

    private lateinit var board: Array<IntArray>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initScreenGame()
        resetBoard()
        setFirstPosition()
    }

    fun checkCellClicked(view: View) {
        val name = view.tag.toString()
        val x = name.subSequence(1, 2).toString().toInt()
        val y = name.subSequence(2, 3).toString().toInt()

        checkCell(x, y)
    }

    private fun checkCell(x: Int, y: Int) {
        val diffX = x - cellSelectedX
        val diffY = y - cellSelectedY

        // TODO: improve L move logic
        var checkTrue = false
        if (diffX == 1 && diffY == 2) checkTrue = true
        if (diffX == 1 && diffY == -2) checkTrue = true
        if (diffX == 2 && diffY == 1) checkTrue = true
        if (diffX == 2 && diffY == -1) checkTrue = true
        if (diffX == -1 && diffY == 2) checkTrue = true
        if (diffX == -1 && diffY == -2) checkTrue = true
        if (diffX == -2 && diffY == 1) checkTrue = true
        if (diffX == -2 && diffY == -1) checkTrue = true

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

    private fun setFirstPosition() {

        val x: Int = (0..7).random()
        val y: Int = (0..7).random()

        cellSelectedX = x
        cellSelectedY = y

        selectCell(x, y)
    }

    private fun selectCell(x: Int, y: Int) {

        moves--
        var tvMovesData = findViewById<TextView>(R.id.tvMovesData)
        tvMovesData.text = moves.toString()

        board[x][y] = 1
        paintHorseCell(cellSelectedX, cellSelectedY, "previous_cell")

        cellSelectedX = x
        cellSelectedY = y

        clearOptions()

        paintHorseCell(x, y, "selected_cell")
        checkOptions(x, y)

        if (moves > 0) {
            checkNewBonus()
            //checkGameOver()
        } else {
            //checkSucessfulEnd()
        }
    }

    private fun checkNewBonus() {
        if (moves % movesRequired == 0) {
            var bonusCellX = 0
            var bonusCellY = 0

            var bonusCell = false
            while (bonusCell == false) {
                bonusCellX = (0..7).random()
                bonusCellY = (0..7).random()

                if (board[bonusCellX][bonusCellY] == 0) bonusCell = true
            }
            board[bonusCellX][bonusCellY] = 2
            paintBonusCell(bonusCellX, bonusCellY)
        }
    }

    private fun paintBonusCell(x: Int, y: Int) {
        var iv: ImageView = findViewById(resources.getIdentifier("c$x$y", "id", packageName))
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
                paintOptions(optionX, optionY)

                board[optionX][optionY] = 9
            }
        }
    }

    private fun paintOptions(x: Int, y: Int) {
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
        val heightCell = widthCell

        for (i in 0..7) {
            for (j in 0..7) {
                iv = findViewById(resources.getIdentifier("c$i$j", "id", packageName))

                val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightCell, getResources().displayMetrics).toInt()
                val width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthCell, getResources().displayMetrics).toInt()

                iv.layoutParams = TableRow.LayoutParams(width, height)

            }
        }
    }

    private fun hideMessage() {
        val lyMessage = findViewById<LinearLayout>(R.id.lyMessage)
        lyMessage.visibility = View.INVISIBLE
    }
}