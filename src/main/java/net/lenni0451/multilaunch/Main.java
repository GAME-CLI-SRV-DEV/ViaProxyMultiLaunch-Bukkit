package net.lenni0451.multilaunch;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Main extends JavaPlugin implements Listener {

    private JarLauncher launcher;

    @Override
    public void onEnable() {
        MultiLaunchConfig.load(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        this.launch();
    }

    private void launch() {
        try {
            this.launcher = new JarLauncher(getLogger());
            this.launcher.launch();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (Main.this.launcher.getProcess().isAlive()) Main.this.launcher.stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
        } catch (Throwable t) {
            getLogger().severe("Failed to launch the server jar!");
            t.printStackTrace();
            System.exit(-1);
        }
    }

    @EventHandler
    public void onConsoleCommand(ServerCommandEvent event) throws IOException {
        if (!MultiLaunchConfig.forwardConsole) return;

        try {
            OutputStream os = this.launcher.getOutputStream();
            os.write(event.getCommand().getBytes(StandardCharsets.UTF_8));
            if (event.getCommand().split(" ").length > 1) {
                os.write(' ');
                os.write(event.getCommand().substring(event.getCommand().indexOf(' ') + 1).getBytes(StandardCharsets.UTF_8));
            }
            os.write('\n');
            os.flush();
            event.setCancelled(true);
        } catch (Throwable ignored) {
            // If the server is not running, the input will be handled by Bukkit
        }
    }
}
