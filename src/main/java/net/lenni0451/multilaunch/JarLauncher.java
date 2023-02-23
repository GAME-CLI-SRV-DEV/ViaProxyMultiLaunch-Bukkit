package net.lenni0451.multilaunch;

import net.raphimc.viaproxy.util.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public class JarLauncher {

    private final String jarPath;
    private Thread watchDogThread;
    private Process process;
    private OutputStream outputStream;

    public JarLauncher(final String jarPath) {
        this.jarPath = jarPath;
    }

    public void launch() throws IOException {
        File serverJarFile = new File(this.jarPath);

        ProcessBuilder pb = new ProcessBuilder(System.getProperty("java.home") + "/bin/java", "-jar", serverJarFile.getAbsolutePath());
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
            System.exit(0);
        }, "ServerWatchDog");
        this.watchDogThread.setDaemon(true);
        this.watchDogThread.start();
    }

    public void stop() throws IOException {
        this.watchDogThread.interrupt();
        Logger.LOGGER.info("Stopping server (force shutdown after 1 minute)...");
        this.outputStream.write("stop\n".getBytes());
        this.outputStream.flush();
        try {
            this.process.waitFor(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Logger.LOGGER.warn("Server stop was interrupted after 1 minute!");
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
