package net.lenni0451.multilaunch;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class MultiLaunchConfig {

    public static String serverJar = "other/server.jar";
    public static List<String> jvmArguments = List.of("-DIReallyKnowWhatIAmDoingISwear");
    public static List<String> serverArguments = List.of("nogui");
    public static boolean forwardConsole = true;
    public static int shutdownTimeout = 60;
    public static String stopCommand = "stop";
    public static boolean stopViaProxyOnServerStop = true;

    public static void load(JavaPlugin plugin) {
        Logger logger = plugin.getLogger();
        File configFile = new File(plugin.getDataFolder(), "multilaunch.yml");
        if (!configFile.exists()) {
            plugin.saveResource("multilaunch.yml", false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        serverJar = config.getString("ServerJar", serverJar);
        jvmArguments = config.getStringList("JvmArguments");
        serverArguments = config.getStringList("ServerArguments");
        forwardConsole = config.getBoolean("ForwardConsole", forwardConsole);
        shutdownTimeout = config.getInt("ShutdownTimeout", shutdownTimeout);
        stopCommand = config.getString("StopCommand", stopCommand);
        stopViaProxyOnServerStop = config.getBoolean("StopViaProxyOnServerStop", stopViaProxyOnServerStop);

        logger.info("MultiLaunch configuration loaded successfully.");
    }
}
