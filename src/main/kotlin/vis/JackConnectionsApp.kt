package vis

import engine.App
import engine.MouseInput
import engine.Window
import engine.WindowRefreshCallbackHandler
import glm_.vec2.Vec2
import imgui.ImGui
import java.util.EnumSet
import org.jaudiolibs.jnajack.Jack
import org.jaudiolibs.jnajack.JackBufferSizeCallback
import org.jaudiolibs.jnajack.JackClient
import org.jaudiolibs.jnajack.JackPortConnectCallback
import org.jaudiolibs.jnajack.JackPortFlags
import org.jaudiolibs.jnajack.JackPortRegistrationCallback
import org.jaudiolibs.jnajack.JackSampleRateCallback
import org.jaudiolibs.jnajack.JackShutdownCallback
import org.jaudiolibs.jnajack.JackXrunCallback
import org.lwjgl.glfw.GLFW

class JackConnectionsApp(
    private val jack: Jack,
    private val client: JackClient
) : App, WindowRefreshCallbackHandler {
    private val bufferSizeCallback = BufferSizeCallback()
    private val sampleRateCallback = SampeRateCallback()
    private val xRunCallback = XRunCallback()
    private val portRegistrationCallback = PortRegistrationCallback()
    private val portConnectCallback = PortConnectCallback()
    private val onShutdownCallback = OnShutdownCallback()

    override fun init(window: Window) {
        window.windowRefreshCallback = this

        initCallbacks()
        initPorts()
        client.activate()
    }

    override fun input(window: Window, mouseInput: MouseInput) {
    }

    override fun update(interval: Float, window: Window, mouseInput: MouseInput) {
    }

    // The main render for the window of all connections.
    override fun render(window: Window) {
        window.implGl3.newFrame()
        window.implGlfw.newFrame()

        val ports = mapOf(
            "music 1" to Vec2(10f, 10f),
            "output 1" to Vec2(500f, 150f)
        )

        // Build all the connections from the captured data structure
        ImGui.run {
            newFrame()
            pushFont(window.ubuntuFont)

            val portWidgets = ports.map { PortWidget(it.key, it.value).let { p -> p.invoke(); p } }
            PortConnections(portWidgets)

            popFont()
        }

        window.clear()
        ImGui.render()
        window.implGl3.renderDrawData(ImGui.drawData!!)
    }

    override fun cleanup() {
        client.deactivate()
        client.close()
    }

    private fun initCallbacks() {
        client.setBuffersizeCallback(bufferSizeCallback)
        client.setSampleRateCallback(sampleRateCallback)
        client.setXrunCallback(xRunCallback)
        client.setPortRegistrationCallback(portRegistrationCallback)
        client.setPortConnectCallback(portConnectCallback)
        client.onShutdown(onShutdownCallback)
    }

    private fun initPorts() {
        val ports = jack.getPorts(client, "", null, EnumSet.noneOf(JackPortFlags::class.java))
        for (port: String in ports) {
            println("output port: $port")
            val conns = jack.getAllConnections(client, port)
            for (conn: String in conns) {
                println(" --> $conn")
            }
        }
    }

    // This is an additional call to render the page during (e.g) window resizing. The magic is doing the swap buffer to get it to show as well
    override fun refresh(window: Window) {
        render(window)
        // This causes the view to be redrawn during a window resize, without which will not paint correctly until the mouse is released.
        GLFW.glfwSwapBuffers(window.windowHandle)
    }
}

class BufferSizeCallback : JackBufferSizeCallback {
    override fun buffersizeChanged(client: JackClient?, buffersize: Int) {
        println("new buffersize: $buffersize")
    }
}

class SampeRateCallback : JackSampleRateCallback {
    override fun sampleRateChanged(client: JackClient?, sampleRate: Int) {
        println("sample rate changed to: $sampleRate")
    }
}

class XRunCallback : JackXrunCallback {
    override fun xrunOccured(client: JackClient?) {
        println("xrun!")
    }
}

class PortRegistrationCallback : JackPortRegistrationCallback {
    override fun portRegistered(client: JackClient?, portFullName: String?) {
        println("port registered: $portFullName")
    }

    override fun portUnregistered(client: JackClient?, portFullName: String?) {
        println("port unregistered: $portFullName")
    }
}

class PortConnectCallback : JackPortConnectCallback {
    override fun portsConnected(client: JackClient?, portName1: String?, portName2: String?) {
        println("port connected: $portName1, $portName2")
    }

    override fun portsDisconnected(client: JackClient?, portName1: String?, portName2: String?) {
        println("port disconnected: $portName1, $portName2")
    }
}

class OnShutdownCallback : JackShutdownCallback {
    override fun clientShutdown(client: JackClient?) {
        println("client shutdown")
    }

}
