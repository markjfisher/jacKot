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

fun main() {
    SLF4JBridgeHandler.removeHandlersForRootLogger()
    SLF4JBridgeHandler.install()

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
    val client = jack.openClient("jacKot client", options, status)
    val app: App = JackConnectionsApp(jack, client)

    // Turn off gfx debug globally
    imgui.DEBUG = false
    Configuration.DEBUG.set(false)

    Engine("jacKot", 1200, 800, true, app).run()
}