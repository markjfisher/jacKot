package vis

import engine.LogicUpdater
import engine.MouseInput
import engine.Window
import glm_.vec4.Vec4
import gln.glClearColor
import gln.glViewport
import imgui.ImGui
import imgui.font.Font
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
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11C

class JackUpdater(
    private val jack: Jack,
    private val client: JackClient
): LogicUpdater {
    private val bufferSizeCallback = BufferSizeCallback()
    private val sampleRateCallback = SampeRateCallback()
    private val xRunCallback = XRunCallback()
    private val portRegistrationCallback = PortRegistrationCallback()
    private val portConnectCallback = PortConnectCallback()
    private val onShutdownCallback = OnShutdownCallback()

    lateinit var sysDefault: Font
    lateinit var ubuntuFont: Font
    private val clearColor = Vec4(0.45f, 0.55f, 0.6f, 1f)

    var counter: Int = 0

    override fun init(window: Window) {
        with(ImGui) {
            sysDefault = io.fonts.addFontDefault()
            ubuntuFont = io.fonts.addFontFromFileTTF("fonts/UbuntuMono-R.ttf", 18.0f) ?: sysDefault
        }

//        GLFW.glfwSetWindowRefreshCallback(window.windowHandle) {
//            println("window refresh cb with wh: $it, windowHandle: ${window.windowHandle}")
//            GL11C.glViewport(0, 0, window.width, window.height)
//            render(window)
//        }
//
//        GLFW.glfwSetWindowSizeCallback(window.windowHandle) { wh, w, h ->
//            println("window size cb: $w, $h")
//            window.width = w
//            window.height = h
//            window.isResized = true
//            GL11C.glViewport(0, 0, window.width, window.height)
//            render(window)
//        }
//
//        GLFW.glfwSetFramebufferSizeCallback(window.windowHandle) { wh, w, h ->
//            println("window fb size cb: $w, $h")
//            window.width = w
//            window.height = h
//            window.isResized = true
//            GL11C.glViewport(0, 0, window.width, window.height)
//            render(window)
//        }
//
//        GLFW.glfwSetWindowPosCallback(window.windowHandle) { wh, xp, yp ->
//            println("window pos cb: $xp, $yp")
//        }

        initCallbacks()
        initPorts()
        client.activate()
    }

    override fun input(window: Window, mouseInput: MouseInput) {
    }

    override fun update(interval: Float, mouseInput: MouseInput, window: Window) {
    }

    override fun render(window: Window) {
        window.implGl3.newFrame()
        window.implGlfw.newFrame()
        ImGui.run {
            newFrame()
            pushFont(ubuntuFont)
            begin("A UI!!")
            text("Hello, fish %d", counter++)
            button("Push me if you dare")
            end()
        }
        ImGui.render()

        clear(window)
        window.implGl3.renderDrawData(ImGui.drawData!!)

//        GLFW.glfwSwapBuffers(window.windowHandle)
    }

    private fun clear(window: Window) {
        glClearColor(clearColor)
        GL11C.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT or GL11C.GL_STENCIL_BUFFER_BIT)
        if (window.isResized) {
            GL11C.glViewport(0, 0, window.width, window.height)
            window.isResized = false
        }
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
        val ports = jack.getPorts(client, "", JackPortType.AUDIO, EnumSet.of(JackPortFlags.JackPortIsOutput))
        for (port: String in ports) {
            println("output port: $port")
            val conns = jack.getAllConnections(client, port)
            for (conn: String in conns) {
                println(" --> $conn")
            }
        }
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
