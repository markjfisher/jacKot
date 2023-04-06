package engine

import glm_.vec2.Vec2i
import glm_.vec4.Vec4
import imgui.ImGui
import imgui.classes.Context
import imgui.font.Font
import imgui.impl.gl.ImplGL3
import imgui.impl.glfw.ImplGlfw
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.glfwGetKey
import org.lwjgl.glfw.GLFW.glfwGetVideoMode
import org.lwjgl.glfw.GLFW.glfwPollEvents
import org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback
import org.lwjgl.glfw.GLFW.glfwSetWindowPosCallback
import org.lwjgl.glfw.GLFW.glfwSetWindowRefreshCallback
import org.lwjgl.glfw.GLFW.glfwSwapBuffers
import org.lwjgl.glfw.GLFW.glfwWindowShouldClose
import org.lwjgl.opengl.GL.createCapabilities
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11C
import org.lwjgl.opengl.GL11C.GL_BLEND
import org.lwjgl.opengl.GL11C.GL_DEPTH_TEST
import org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA
import org.lwjgl.opengl.GL11C.GL_SRC_ALPHA
import org.lwjgl.opengl.GL11C.GL_STENCIL_TEST
import org.lwjgl.opengl.GL11C.glBlendFunc
import org.lwjgl.opengl.GL11C.glClearColor
import org.lwjgl.opengl.GL11C.glEnable
import uno.glfw.GlfwWindow
import uno.glfw.VSync
import uno.glfw.glfw
import uno.glfw.windowHint.Profile.core

data class Window(
    val title: String,
    var width: Int,
    var height: Int,
    private var vSync: Boolean,
    var backgroundColour: Vec4 = Vec4(0.45f, 0.55f, 0.6f, 1f)
) {
    var windowHandle: Long = 0

    var isResized = false

    private lateinit var window: GlfwWindow
    lateinit var ctx: Context
    lateinit var implGlfw: ImplGlfw
    lateinit var implGl3: ImplGL3

    var windowRefreshCallback: WindowRefreshCallbackHandler = EmptyWindowRefreshCallbackHandler()

    lateinit var sysDefault: Font
    lateinit var ubuntuFont: Font

    // generic background colour
    val clearColor = Vec4(0.45f, 0.55f, 0.6f, 1f)

    fun initImgui() {
        glfw {
            errorCallback = defaultErrorCallback
            init()
            windowHint {
                visible = false
                resizable = true
                decorated = true
                transparentFramebuffer = false
                context.version = "4.6"
                samples = 4
                maximized = false
                profile = core
                forwardComp = true
            }

        }
        window = GlfwWindow(width, height, title)
        windowHandle = window.handle.value

        glfwSetFramebufferSizeCallback(windowHandle) { wh, w, h ->
            width = w
            height = h
            isResized = true
        }

        // Window resizing:
        glfwSetWindowRefreshCallback(windowHandle) { windowRefreshCallback.refresh(this) }
        // Window moving:
        glfwSetWindowPosCallback(windowHandle) { _, _, _ -> windowRefreshCallback.refresh(this) }

        // can also consider:
        // glfwSetWindowSizeCallback(windowHandle) { _, _, _ -> windowRefreshCallback.refresh(this) }
        // glfwSetFramebufferSizeCallback(windowHandle) {  _, _, _ -> windowRefreshCallback.refresh(this) }

        // Center window on primary monitor
        val vidmode = glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()) ?: throw Exception("Cannot get primary monitor information")
        window.pos = Vec2i((vidmode.width() - width) / 2, (vidmode.height() - height) / 2)

        window.makeContextCurrent()
        if (vSync) {
            glfw.swapInterval = VSync.ON
        }

        window.show()
        createCapabilities()
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        restoreState()

        ctx = Context()
        ImGui.styleColorsDark()
        implGlfw = ImplGlfw(window, true)
        implGl3 = ImplGL3()

        with(ImGui) {
            sysDefault = io.fonts.addFontDefault()
            ubuntuFont = io.fonts.addFontFromFileTTF("fonts/UbuntuMono-R.ttf", 18.0f) ?: sysDefault
        }

    }

    fun isKeyPressed(keyCode: Int): Boolean {
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS
    }

    fun windowShouldClose(): Boolean {
        return glfwWindowShouldClose(windowHandle)
    }

    fun isVSync(): Boolean {
        return vSync
    }

    fun update() {
        glfwSwapBuffers(windowHandle)
        glfwPollEvents()
    }

    fun clear() {
        gln.glClearColor(clearColor)
        GL11C.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT or GL11C.GL_STENCIL_BUFFER_BIT)
        if (isResized) {
            GL11C.glViewport(0, 0, width, height)
            isResized = false
        }
    }

    private fun restoreState() {
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_STENCIL_TEST)
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    }
}

interface WindowRefreshCallbackHandler {
    fun refresh(window: Window)
}

class EmptyWindowRefreshCallbackHandler: WindowRefreshCallbackHandler {
    override fun refresh(window: Window) {}
}