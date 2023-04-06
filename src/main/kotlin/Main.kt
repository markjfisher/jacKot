import engine.App
import java.util.EnumSet
import java.util.ServiceLoader
import org.jaudiolibs.audioservers.AudioServerProvider
import org.jaudiolibs.jnajack.Jack
import org.jaudiolibs.jnajack.JackOptions
import org.jaudiolibs.jnajack.JackStatus
import org.slf4j.bridge.SLF4JBridgeHandler
import vis.JackConnectionsApp
import engine.Engine
import org.lwjgl.system.Configuration

fun main(args: Array<String>) {
    SLF4JBridgeHandler.removeHandlersForRootLogger()
    SLF4JBridgeHandler.install()

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.

    val lib = "JACK"

    var provider: AudioServerProvider? = null
    for (p in ServiceLoader.load(AudioServerProvider::class.java)) {
        if (lib == p.libraryName) {
            provider = p
            break
        }
    }
    if (provider == null) {
        throw Exception("Could not find provider for $lib")
    }

    val jack = Jack.getInstance()
    val options = EnumSet.of(JackOptions.JackNoStartServer)
    val status = EnumSet.noneOf(JackStatus::class.java)
    val client = jack.openClient("jacKot test 2", options, status)
    val app: App = JackConnectionsApp(jack, client)

    imgui.DEBUG = false
    Configuration.DEBUG.set(false)
    Engine("jacKot", 1200, 800, true, app = app).run()
}