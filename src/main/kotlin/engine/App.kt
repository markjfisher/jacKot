package engine

interface App {
    fun init(window: Window)
    fun input(window: Window, mouseInput: MouseInput)
    fun update(interval: Float, window: Window, mouseInput: MouseInput)
    fun render(window: Window)
    fun cleanup()
}