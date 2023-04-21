# <img src="resources/META-INF/pluginIcon.svg" alt="pluginIcon.svg" width="60" align="absmiddle"/> Arduino Support

A JetBrains CLion plugin to integrate
[Arduino CMake](https://github.com/francoiscampbell/arduino-cmake) into the IDE.

[JetBrains Plugin Page](https://plugins.jetbrains.com/plugin/11301-arduino-support)

Install and create Arduino CMake projects in one click with new project wizard types and view
communications with the integrated serial monitor tool window.

* `Arduino Sketch` and `Arduino Library` project types to new project wizard, with Options to
  select board type, cpu, programmer and port.

* `Arduino Sketch` new file action.

* Serial port monitor tool window with options to disconnect on build start and reconnect on
  build complete.

* Change current build parameters `Tools` > `Arduino Support` > `Change build settings`.

* Customize template files used for creating new projects.

* Use bundled `boards.txt` and `programmers.txt` or your own versions.

![Screenshot_NewProject.png](/assets/images/Screenshot_NewProject.png)

![Screenshot_ProjectSettings](/assets/images/Screenshot_ProjectSettings.png)

![Screenshot_SerialMonitor](/assets/images/Screenshot_SerialMonitor.png)

![Screenshot_ChangeBuildSettings](/assets/images/Screenshot_ChangeBuildSettings.png)

![Screenshot_ChangeBuildDiff](/assets/images/Screenshot_ChangeBuildDiff.png)

#### :warning: `Serial Port Monitor` plugin may conflict with `Arduino Support`

This plugin uses [Simple Serial Connector Service] plugin, which uses
[jscc - Java Simple Serial Connector] serial library, as does `Serial Port Monitor` plugin. Only
one plugin can load the native libraries. If you encounter conflicts please uninstall or disable
`Serial Port Monitor` plugin, or this plugin.

## Uploading a sketch

Select the upload configuration from the dropdown. The type (debug/release) does not matter.

![select upload from dropdown](/assets/images/Screenshot_RunDebugConfiguration.png)

Use the build action to upload your sketch. Do __not__ use the run action.

![Use build action, not run](/assets/images/Screenshot_BuildNotRun.png)

## Settings (Preferences on Mac)

Some settings can also be changed in settings under Languages & Frameworks > Arduino, for
convenience.

Additionally, you can point the plugin to your own versions of `boards.txt` and
`programmers.txt`.

![Screenshot_AppSettings](/assets/images/Screenshot_AppSettings.png)

The `Show Difference` button is only displayed when a valid file is selected to let you quickly
see if there are differences from the bundled version.

The `Create From Bundled` button is only displayed when a non-existent directory is given and it
can be created. Pressing it will create the directory and populate it with the bundled
templates. For layout and caveats of this directory please refer to the
[README.md](/resources/com/vladsch/clionarduinoplugin/templates/README.md) file in that
directory.

`On Disconnect Delay`, 50ms default. Introduces a short (0-100ms) delay before a build when
disconnecting a port to allow the port to become available. On some projects with very short
build times, disconnecting a port does not allow enough time for the port to become available,
causing the upload to fail with "Resource busy" error. Adding a short delay after disconnect
solves the problem. On some project it is not needed on others 20ms was sufficient.

## Status

Updated to work with CLion 2020.3 to 2023.1, with fixes in the ArduinoToolchain.cmake.

Added an option not to use bundled toolchain file. This opens the possibility of using another
toolchain.

Updated all native serial port code to [jscc - Java Simple Serial Connector] and moved code to
[Simple Serial Connector Service] plugin to allow multiple plugins to provide serial port
monitoring without conflicts on native libraries.

## Wish List

* [x] Serial Terminal tool window
  * [ ] Add Hex pane in addition to text view. Either one or both could be displayed. With
        coordinated highlighting carets between the two. That way text view is not mangled into
        a block but naturally flows as you would expect, while hex view can be either a block or
        flow to match the text.
  * [ ] Display of ascii codes without resorting to hex display in text mode console.
* [ ] Refactoring support which CLion does not handle:
  * [ ] Add file to sources or headers (CLion cannot make sense of the Arduino project file)
  * [ ] Add updating of `keywords.txt` when identifiers in source or headers are renamed.
  * [ ] Add reload of `CMakeLists.txt` when library includes are added.
* [ ] Add Import Arduino IDE config, sketches and libraries
* [ ] Load Additional Libraries from URLs
* [ ] Add: `Generate keywords.txt` action to generate keywords.txt based on contained project
      classes and functions.

## Release notes

[Version Notes]

## History

This plugin is a fork of [Original CLionArduinoPlugin] written by Francois Campbell.

## Notes

For serial port list and functionality the plugin uses [jscc - Java Simple Serial Connector],
Licensed under [GNU Lesser GPL]

[GNU Lesser GPL]: http://www.gnu.org/licenses/lgpl.html
[Version Notes]: VERSION.md
[Original CLionArduinoPlugin]: https://github.com/francoiscampbell/CLionArduinoPlugin
[Simple Serial Connector Service]: https://plugins.jetbrains.com/plugin/21550-simpleserialconnectorservice
[jscc - Java Simple Serial Connector]: https://github.com/java-native/jssc

