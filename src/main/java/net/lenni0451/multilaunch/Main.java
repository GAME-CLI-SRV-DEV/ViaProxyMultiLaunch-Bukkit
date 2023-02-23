package net.lenni0451.multilaunch;

import joptsimple.OptionSpec;
import net.lenni0451.lambdaevents.EventHandler;
import net.raphimc.viaproxy.plugins.PluginManager;
import net.raphimc.viaproxy.plugins.ViaProxyPlugin;
import net.raphimc.viaproxy.plugins.events.ConsoleCommandEvent;
import net.raphimc.viaproxy.plugins.events.PostOptionsParseEvent;
import net.raphimc.viaproxy.plugins.events.PreOptionsParseEvent;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Main extends ViaProxyPlugin {

    private OptionSpec<String> serverjarArg;
    private JarLauncher launcher;

    @Override
    public void onEnable() {
        PluginManager.EVENT_MANAGER.register(this);
    }

    @EventHandler
    public void onPreOptionsParse(PreOptionsParseEvent event) {
        this.serverjarArg = event.getParser().accepts("serverjar", "The path to the server jar to launch").withRequiredArg().ofType(String.class).required();
    }

    @EventHandler
    public void onPostOptionsParse(PostOptionsParseEvent event) {
        String serverJar = event.getOptions().valueOf(this.serverjarArg);
        try {
            this.launcher = new JarLauncher(serverJar);
            this.launcher.launch();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (Main.this.launcher.getProcess().isAlive()) Main.this.launcher.stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(-1);
        }
    }

    @EventHandler
    public void onConsoleCommand(ConsoleCommandEvent event) throws IOException {
        event.setCancelled(true);
        OutputStream os = this.launcher.getOutputStream();
        os.write(event.getCommand().getBytes(StandardCharsets.UTF_8));
        if (event.getArgs().length != 0) {
            os.write(' ');
            os.write(String.join(" ", event.getArgs()).getBytes(StandardCharsets.UTF_8));
        }
        os.write('\n');
        os.flush();
    }

}
