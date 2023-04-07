package vis

import glm_.vec2.Vec2
import imgui.Cond
import imgui.ImGui
import imgui.WindowFlag
import imgui.dsl
import imgui.or
import vis.CardinalPoint.E
import vis.CardinalPoint.N
import vis.CardinalPoint.S
import vis.CardinalPoint.W

enum class CardinalPoint {
    N, S, E, W;
}

class PortWidget(private val name: String, private val startingPosition: Vec2) {
    var currentPos = Vec2()
    var currentSize = Vec2()

    fun cardinal(pt: CardinalPoint): Vec2 {
        return when (pt) {
            N -> currentPos + Vec2(currentSize.x / 2, 0)
            S -> currentPos + Vec2(currentSize.x / 2, currentSize.y)
            E -> currentPos + Vec2(currentSize.x, currentSize.y / 2)
            W -> currentPos + Vec2(0, currentSize.y / 2)
        }
    }

    operator fun invoke() {
        val windowFlags = WindowFlag.NoDecoration or WindowFlag.AlwaysAutoResize or WindowFlag.NoFocusOnAppearing or WindowFlag.NoNav
        // val windowFlags = WindowFlag.NoFocusOnAppearing or WindowFlag.None
        with(ImGui) {
            setNextWindowPos(startingPosition, Cond.Once)
            setNextWindowBgAlpha(0.35f)
            // each "window" needs a unique name
            dsl.window(name = "PortWidget-$name", flags = windowFlags) {
                // some space at the top, and set a min width
                dummy(Vec2(160f, 3f))
                val textSize = calcTextSize(name)
                cursorPosX = (windowSize.x - textSize.x) / 2f
                text(name)
                // some space at the bottom
                dummy(Vec2(1f, 3f))
                currentPos = windowPos
                currentSize = windowSize
            }

        }
    }
}