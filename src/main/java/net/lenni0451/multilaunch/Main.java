package net.lenni0451.multilaunch;

import net.lenni0451.lambdaevents.EventHandler;
import net.raphimc.viaproxy.ViaProxy;
import net.raphimc.viaproxy.plugins.ViaProxyPlugin;
import net.raphimc.viaproxy.plugins.events.ConsoleCommandEvent;
import net.raphimc.viaproxy.util.logging.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Main extends ViaProxyPlugin {

    private JarLauncher launcher;

    @Override
    public void onEnable() {
        MultiLaunchConfig.load();
        ViaProxy.EVENT_MANAGER.register(this);
        this.launch();
    }

    private void launch() {
        try {
            this.launcher = new JarLauncher();
            this.launcher.launch();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (Main.this.launcher.getProcess().isAlive()) Main.this.launcher.stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
        } catch (Throwable t) {
            Logger.LOGGER.error("Failed to launch the server jar!", t);
            System.exit(-1);
        }
    }

    @EventHandler
    public void onConsoleCommand(ConsoleCommandEvent event) throws IOException {
        if (!MultiLaunchConfig.forwardConsole) return;

        try {
            OutputStream os = this.launcher.getOutputStream();
            os.write(event.getCommand().getBytes(StandardCharsets.UTF_8));
            if (event.getArgs().length != 0) {
                os.write(' ');
                os.write(String.join(" ", event.getArgs()).getBytes(StandardCharsets.UTF_8));
            }
            os.write('\n');
            os.flush();
            event.setCancelled(true);
        } catch (Throwable ignored) {
            //If the server is not running, the input will be handled by ViaProxy
        }
    }

}
