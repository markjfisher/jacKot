package vis

import glm_.vec2.Vec2
import glm_.vec4.Vec4
import imgui.Cond
import imgui.ImGui
import imgui.WindowFlag
import imgui.dsl
import imgui.or
import kotlin.math.abs

object PortConnections {
    var lineColour = Vec4(1.0f, 1.0f, 0.4f, 1.0f)

    operator fun invoke(portWidgets: List<PortWidget>) {
        // create a transparent window with no decorations that lives in the background and covers entire windows. It will hold all the line drawing between boxes.
        // Could get interesting when moving lines...

        val windowFlags = WindowFlag.NoDecoration or WindowFlag.NoMove or WindowFlag.NoFocusOnAppearing or WindowFlag.NoNav or WindowFlag.NoResize or WindowFlag.NoBackground or WindowFlag.NoBringToFrontOnFocus
        // For debugging, show title etc.
        // val windowFlags = WindowFlag.NoMove or WindowFlag.NoFocusOnAppearing or WindowFlag.NoResize or WindowFlag.NoBringToFrontOnFocus

        ImGui.setNextWindowBgAlpha(0.0f)
        ImGui.setNextWindowSize(Vec2(ImGui.io.displaySize.x.toFloat() + 1, ImGui.io.displaySize.y.toFloat() + 1), Cond.Always)
        ImGui.setNextWindowPos(Vec2(0f, 0f), Cond.Always)
        dsl.window("lines", null, windowFlags) {
            // take first 2 and draw bezier between edges - this will have to understand all the Ports eventually
            val p1 = portWidgets[0].cardinal(CardinalPoint.E)
            val p2 = portWidgets[1].cardinal(CardinalPoint.W)
            // There are 2 Control Points, with X at the midpoint of 2 endpoints, and respective Y coordinates from end points. Makes a nice curve from P1 to P2
            val midPointX = p1.x + abs(p1.x - p2.x) / 2f
            val cp1 = Vec2(midPointX, p1.y)
            val cp2 = Vec2(midPointX, p2.y)
            val c = ImGui.getColorU32(lineColour)
            val drawList = ImGui.windowDrawList
            drawList.apply {
                addBezierCubic(p1, cp1, cp2, p2, c, 1.75f, 64)
            }
        }

    }
}
