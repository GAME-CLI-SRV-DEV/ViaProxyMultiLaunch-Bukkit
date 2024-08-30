package net.lenni0451.multilaunch;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class JarLauncher {

    private final Logger logger;
    private Thread watchDogThread;
    private Process process;
    private OutputStream outputStream;

    public JarLauncher(Logger logger) {
        this.logger = logger;
    }


    public void launch() throws IOException {
        File serverJarFile = new File(MultiLaunchConfig.serverJar);
        if (!serverJarFile.exists()) {
            logger.info("---------- MultiLaunch ----------");
            logger.severe("Server jar file does not exist!");
            logger.info("Please make sure you have set the correct path in the configuration file!");
            logger.info("If this is your first time using the plugin, please edit the configuration file and restart the server!");
            Bukkit.getPluginManager().disablePlugin(JavaPlugin.getProvidingPlugin(this.getClass()));
            return;
        }

        List<String> arguments = new ArrayList<>();
        arguments.add(System.getProperty("java.home") + "/bin/java");
        arguments.addAll(MultiLaunchConfig.jvmArguments);
        arguments.add("-jar");
        arguments.add(serverJarFile.getAbsolutePath());
        arguments.addAll(MultiLaunchConfig.serverArguments);

        ProcessBuilder pb = new ProcessBuilder(arguments.toArray(new String[0]));
        pb.directory(serverJarFile.getParentFile());
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        this.process = pb.start();
        this.outputStream = this.process.getOutputStream();

        this.watchDogThread = new Thread(() -> {
            try {
                this.process.waitFor();
            } catch (InterruptedException e) {
                return;
            }
            logger.info("Server process stopped!");
            if (MultiLaunchConfig.stopViaProxyOnServerStop) {
                logger.info("Stopping ViaProxy...");
                Bukkit.getPluginManager().disablePlugin(JavaPlugin.getProvidingPlugin(this.getClass()));
            }
        }, "ServerWatchDog");
        this.watchDogThread.setDaemon(true);
        this.watchDogThread.start();
    }

    public void stop() throws IOException {
        this.watchDogThread.interrupt();
        logger.info("Stopping server (force shutdown after " + MultiLaunchConfig.shutdownTimeout + " seconds)...");
        this.outputStream.write(MultiLaunchConfig.stopCommand.getBytes());
        this.outputStream.write('\n');
        this.outputStream.flush();
        try {
            if (!this.process.waitFor(MultiLaunchConfig.shutdownTimeout, TimeUnit.SECONDS)) {
                throw new InterruptedException("Server did not stop in time!");
            }
        } catch (InterruptedException e) {
            logger.warning("Server stop was interrupted after " + MultiLaunchConfig.shutdownTimeout + " seconds!");
            this.process.destroy();
        }
    }

    public Process getProcess() {
        return this.process;
    }

    public OutputStream getOutputStream() {
        return this.outputStream;
    }
}

