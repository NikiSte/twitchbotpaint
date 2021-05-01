package de.brainsizzle.twitchbotpaint

import de.brainsizzle.twitchbotpaint.bot.BotRunner
import de.brainsizzle.twitchbotpaint.bot.MessageCallback
import de.brainsizzle.twitchbotpaint.command.initCommands
import de.brainsizzle.twitchbotpaint.command.parseCommands
import de.brainsizzle.twitchbotpaint.paint.Display
import de.brainsizzle.twitchbotpaint.paint.Shape
import de.brainsizzle.twitchbotpaint.paint.animateAll
import java.util.*

fun main() {
    val gameLoop = GameLoop()
    gameLoop.start()
}

class GameLoop : MessageCallback, ShapeLookup {

    private val display = Display(this)
    private val userDisplayData = mutableMapOf<String, MutableList<Shape>>()

    private var shapes = listOf<Shape>()

    fun start() {
        initCommands()

        // to init canvas with any shape
        // updateDisplayData(userDisplayData,"dumm1", parseCommands("circle 80 north 20"))
        updateShapes()

        display.canvas.repaint()

        val botRunner = BotRunner(this)
        botRunner.init()

        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                animateAll(shapes)
                display.canvas.repaint()
            }
        }, 30, 20)
    }

    override fun calcShapes() : List<Shape> {
        return shapes
    }

    override fun handlePaintMessage(userName: String, messagePayload: String) {
        val parsedCommands = parseCommands(messagePayload)
        if (parsedCommands.isNotEmpty()) {
            updateDisplayData(userDisplayData, userName, parsedCommands)
            updateShapes()
            display.canvas.repaint()
        }
    }

    private fun updateShapes() {
        shapes = transformToShapes(userDisplayData)
    }

    private fun transformToShapes(userDisplayData: MutableMap<String, MutableList<Shape>>): MutableList<Shape> {
        val result = mutableListOf<Shape>()
        for (shapes in userDisplayData.values) {
            result.addAll(shapes)
        }
        return result
    }
}

