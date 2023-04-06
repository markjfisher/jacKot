package engine

class Engine(
    windowTitle: String,
    width: Int,
    height: Int,
    vSync: Boolean,
    private val app: LogicUpdater,
    private val targetUPS: Int = 60
) : Runnable {
    private val window: Window = Window(windowTitle, width, height, vSync)
    private val timer: Timer = Timer()
    private val mouseInput: MouseInput = MouseInput()

    companion object {
        const val TARGET_FPS = 60
    }

    override fun run() {
        try {
            init()
            mainLoop()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cleanup()
        }
    }

    private fun init() {
        initUI()
        app.init(window)
    }

    private fun initUI() {
        window.initImgui()
        timer.set()
        mouseInput.init(window)
    }

    private fun mainLoop() {
        var elapsedTime: Float
        var accumulator = 0f
        val interval = 1f / targetUPS

        val running = true
        while (running && !window.windowShouldClose()) {
            elapsedTime = timer.elapsedTime
            accumulator += elapsedTime
            input()
            while (accumulator >= interval) {
                update(interval)
                accumulator -= interval
            }
            render()
            if (window.isVSync()) {
                sync()
            }
        }

    }

    private fun input() {
        mouseInput.input(window)
        app.input(window, mouseInput)
    }

    private fun update(interval: Float) {
        app.update(interval, mouseInput, window)
    }

    private fun render() {
        app.render(window)
        window.update()
    }

    private fun sync() {
        val loopSlot = 1f / TARGET_FPS
        val endTime = timer.lastLoopTime + loopSlot
        while (timer.time < endTime) {
            try { Thread.sleep(1) } catch (_: InterruptedException) {}
        }
    }

    private fun cleanup() {
        app.cleanup()
    }

}
