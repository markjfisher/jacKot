package vis

import engine.App
import engine.WindowRefreshCallbackHandler
import engine.MouseInput
import engine.Window
import imgui.ImGui
import java.util.EnumSet
import org.jaudiolibs.jnajack.Jack
import org.jaudiolibs.jnajack.JackBufferSizeCallback
import org.jaudiolibs.jnajack.JackClient
import org.jaudiolibs.jnajack.JackPortConnectCallback
import org.jaudiolibs.jnajack.JackPortFlags
import org.jaudiolibs.jnajack.JackPortRegistrationCallback
import org.jaudiolibs.jnajack.JackPortType
import org.jaudiolibs.jnajack.JackSampleRateCallback
import org.jaudiolibs.jnajack.JackShutdownCallback
import org.jaudiolibs.jnajack.JackXrunCallback
import org.lwjgl.glfw.GLFW

class JackConnectionsApp(
    private val jack: Jack,
    private val client: JackClient
): App, WindowRefreshCallbackHandler {
    private val bufferSizeCallback = BufferSizeCallback()
    private val sampleRateCallback = SampeRateCallback()
    private val xRunCallback = XRunCallback()
    private val portRegistrationCallback = PortRegistrationCallback()
    private val portConnectCallback = PortConnectCallback()
    private val onShutdownCallback = OnShutdownCallback()

    var counter: Int = 0

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

        // Build all the connections from the captured data structure
        ImGui.run {
            newFrame()
            pushFont(window.ubuntuFont)
            begin("A UI!!")
            text("Hello, fish %d", counter++)
            button("Push me if you dare")
            end()
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
        // TODO: build the port structure here.
        val ports = jack.getPorts(client, "", JackPortType.AUDIO, EnumSet.of(JackPortFlags.JackPortIsOutput))
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
