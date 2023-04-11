Integrates [Arduino CMake] into the CLion IDE.

Install and create Arduino CMake projects in one click with new project wizard types and view
communications with the integrated serial monitor tool window.

* `Arduino Sketch` and `Arduino Library` project types to new project wizard, with Options to
  select board type, cpu, programmer and port

* `Arduino Sketch` new file action

* Serial port monitor tool window with options to disconnect on build start and reconnect on
  build complete.

* Change current build parameters with `Build` > `Arduino Support` > `Change build settings`.
  Preview changes to `CMakeLists.txt` before committing to them with the `Show Difference`
  button, or use it to copy the desired changes to apply them manually.

* Customize template files used for creating new projects.

* Use bundled `boards.txt` and `programmers.txt` or your own versions.

* Use bundled `ArduinoToolchain` or your own version.

### ** `Serial Port Monitor` plugin cannot be used with `Arduino Support`

Both plugins use [jSSC-2.8.0] serial library and only one plugin can load the native libraries.
Please uninstall or disable `Serial Port Monitor` plugin before installing `Arduino Support`.

| [Issues][] | [Plugin Source][] |

<hr>

![Screenshot_NewProject.png](https://github.com/vsch/CLionArduinoPlugin/raw/master/assets/images/Screenshot_NewProject.png){height=248px width=400px}  
![Screenshot_SerialMonitor](https://github.com/vsch/CLionArduinoPlugin/raw/master/assets/images/Screenshot_SerialMonitor.png){height=191px width=400px}  
![Screenshot_ChangeBuildSettings](https://github.com/vsch/CLionArduinoPlugin/raw/master/assets/images/Screenshot_ChangeBuildSettings.png){height=281px width=400px}

<hr>

**\*** This plugin is a fork of [Original CLionArduinoPlugin] written by Francois Campbell.

[Arduino CMake]: https://github.com/francoiscampbell/arduino-cmake
[Issues]: https://github.com/vsch/CLionArduinoPlugin/issues
[jSSC-2.8.0]: https://github.com/scream3r/java-simple-serial-connector
[Original CLionArduinoPlugin]: https://github.com/francoiscampbell/CLionArduinoPlugin
[Plugin Source]: https://github.com/vsch/CLionArduinoPlugin
[rjuang/CLionArduinoPlugin]: https://github.com/rjuang/CLionArduinoPlugin

