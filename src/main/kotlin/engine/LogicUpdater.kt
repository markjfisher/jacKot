package engine

interface LogicUpdater {
    fun init(window: Window)
    fun input(window: Window, mouseInput: MouseInput)
    fun update(interval: Float, mouseInput: MouseInput, window: Window)
    fun render(window: Window)
    fun cleanup()
}