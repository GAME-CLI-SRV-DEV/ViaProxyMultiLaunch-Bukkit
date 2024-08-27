package net.lenni0451.multilaunch;

import net.raphimc.viaproxy.util.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JarLauncher {

    private Thread watchDogThread;
    private Process process;
    private OutputStream outputStream;

    public void launch() throws IOException {
        File serverJarFile = new File(MultiLaunchConfig.serverJar);
        if (!serverJarFile.exists()) {
            Logger.LOGGER.info("---------- MultiLaunch ----------");
            Logger.LOGGER.error("Server jar file does not exist!");
            Logger.LOGGER.info("Please make sure you have set the correct path in the configuration file!");
            Logger.LOGGER.info("If this is your first time using the plugin, please edit the configuration file and restart the server!");
            System.exit(0);
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
            Logger.LOGGER.info("Server process stopped!");
            if (MultiLaunchConfig.stopViaProxyOnServerStop) {
                Logger.LOGGER.info("Stopping ViaProxy...");
                System.exit(0);
            }
        }, "ServerWatchDog");
        this.watchDogThread.setDaemon(true);
        this.watchDogThread.start();
    }

    public void stop() throws IOException {
        this.watchDogThread.interrupt();
        Logger.LOGGER.info("Stopping server (force shutdown after {} seconds)...", MultiLaunchConfig.shutdownTimeout);
        this.outputStream.write(MultiLaunchConfig.stopCommand.getBytes());
        this.outputStream.write('\n');
        this.outputStream.flush();
        try {
            if (!this.process.waitFor(MultiLaunchConfig.shutdownTimeout, TimeUnit.SECONDS)) {
                throw new InterruptedException("Server did not stop in time!");
            }
        } catch (InterruptedException e) {
            Logger.LOGGER.warn("Server stop was interrupted after {} seconds!", MultiLaunchConfig.shutdownTimeout);
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
