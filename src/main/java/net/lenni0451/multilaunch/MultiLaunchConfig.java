package net.lenni0451.multilaunch;

import net.lenni0451.optconfig.ConfigLoader;
import net.lenni0451.optconfig.annotations.Description;
import net.lenni0451.optconfig.annotations.OptConfig;
import net.lenni0451.optconfig.annotations.Option;
import net.lenni0451.optconfig.provider.ConfigProvider;
import net.raphimc.viaproxy.util.logging.Logger;

import java.io.File;
import java.util.List;

@OptConfig(header = {
        "Configuration for the MultiLaunch ViaProxy plugin.",
        "Used to launch a server jar alongside ViaProxy.",
        "",
        "Made by Lenni0451",
        "Source: https://github.com/ViaVersionAddons/ViaProxyMultiLaunch"
})
public class MultiLaunchConfig {

    @Option("ServerJar")
    @Description({
            "The path to the server jar to launch",
            "Make sure the server is in another folder than ViaProxy itself to avoid file conflicts!"
    })
    public static String serverJar = "other/server.jar";

    @Option("JvmArguments")
    @Description("The JVM arguments to use when launching the server")
    public static List<String> jvmArguments = List.of("-DIReallyKnowWhatIAmDoingISwear");

    @Option("ServerArguments")
    @Description("The arguments to use when launching the server")
    public static List<String> serverArguments = List.of("nogui");

    @Option("ForwardConsole")
    @Description({
            "If the console input should be forwarded to the server",
            "This also means that ViaProxy is not able to read the console input!",
            "If the server process is not running, the input will be handled by ViaProxy"
    })
    public static boolean forwardConsole = true;

    @Option("ShutdownTimeout")
    @Description({
            "The time in seconds to wait for the server to shutdown before forcing it",
            "A force shutdown may cause data loss!"
    })
    public static int shutdownTimeout = 60;

    @Option("StopCommand")
    @Description("The command to send to the server when stopping it")
    public static String stopCommand = "stop";

    @Option("StopViaProxyOnServerStop")
    @Description("If ViaProxy should be stopped when the server stops")
    public static boolean stopViaProxyOnServerStop = true;

    public static void load() {
        try {
            ConfigLoader<MultiLaunchConfig> configLoader = new ConfigLoader<>(MultiLaunchConfig.class);
            configLoader.getConfigOptions().setResetInvalidOptions(true); //Reset invalid options to their default value
            configLoader.loadStatic(ConfigProvider.file(new File("multilaunch.yml")));
        } catch (Throwable t) {
            Logger.LOGGER.error("Failed to load the MultiLaunch configuration!", t);
            System.exit(-1);
        }
    }

}
