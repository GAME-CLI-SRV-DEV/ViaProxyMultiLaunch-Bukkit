# ViaProxyMultiLaunch
This is a Port Version of the ViaProxyMultiLaunch Plugin.

Original Plugin is located in https://github.com/ViaVersionAddon/ViaProxyMultiLaunch, by [Lenni0451](https://github.com/Lenni0451)

## Usage
### Installation
Download the latest release from the [releases page](https://github.com/ViaVersionAddons/ViaProxyMultiLaunch/releases) and place it in the `plugins` folder of the ViaProxy server.

### Configuration
When starting ViaProxy with the plugin installed for the first time, the config file (`multilaunch.yml`) will be generated in the server folder.\
It contains all the necessary settings to configure the plugin.

<details>

<summary>Default configuration:</summary>

```yaml
#The path to the server jar to launch
#Make sure the server is in another folder than ViaProxy itself to avoid file conflicts!
ServerJar: other/server.jar

#The JVM arguments to use when launching the server
JvmArguments:
- -DIReallyKnowWhatIAmDoingISwear

#The arguments to use when launching the server
ServerArguments:
- nogui

#If the console input should be forwarded to the server
#This also means that ViaProxy is not able to read the console input!
#If the server process is not running, the input will be handled by ViaProxy
ForwardConsole: true

#The time in seconds to wait for the server to shutdown before forcing it
#A force shutdown may cause data loss!
ShutdownTimeout: 60

#The command to send to the server when stopping it
StopCommand: stop

#If ViaProxy should be stopped when the server stops
StopViaProxyOnServerStop: true
```

</details>
