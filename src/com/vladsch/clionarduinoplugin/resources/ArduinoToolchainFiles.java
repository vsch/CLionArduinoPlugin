package com.vladsch.clionarduinoplugin.resources;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.io.IOUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class ArduinoToolchainFiles {
    public static InputStream getArduinoToolchainCmake() {
        return ArduinoToolchainFiles.class.getResourceAsStream("arduino-cmake/cmake/ArduinoToolchain.cmake");
    }

    public static InputStream getArduinoCmake() {
        return ArduinoToolchainFiles.class.getResourceAsStream("arduino-cmake/cmake/Platform/Arduino.cmake");
    }

    public static File[] copyToDirectory(final File toolchainRoot) {
        ArrayList<File> files = new ArrayList<>();

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                try {
                    File platformDirectory = new File(toolchainRoot, "Platform");

                    File arduinoToolchain = new File(toolchainRoot, "ArduinoToolchain.cmake");
                    File arduino = new File(platformDirectory, "Arduino.cmake");

                    if (!toolchainRoot.exists()) {
                        if (!toolchainRoot.mkdirs()) {
                            return;
                        }
                    }

                    if (!platformDirectory.exists()) {
                        if (!platformDirectory.mkdirs()) {
                            return;
                        }
                    }

                    OutputStream arduinoToolchainOutputStream = new FileOutputStream(arduinoToolchain);
                    OutputStream arduinoOutputStream = new FileOutputStream(arduino);

                    InputStream arduinoToolchainInputStream = getArduinoToolchainCmake();
                    InputStream arduinoInputStream = getArduinoCmake();

                    try {
                        IOUtils.copy(arduinoToolchainInputStream, arduinoToolchainOutputStream);
                        IOUtils.copy(arduinoInputStream, arduinoOutputStream);
                        files.add(arduinoToolchain);
                        files.add(arduino);
                    } finally {
                        closeStreams(arduinoToolchainOutputStream,
                                arduinoOutputStream,
                                arduinoToolchainInputStream,
                                arduinoInputStream);
                    }

                    if (System.getProperty("os.name").toLowerCase().contains("win")) {
                        String arduinoSdkPathEnv = System.getenv("ARDUINO_SDK_PATH").replace("\\", "/");
                        String windowsPathsString =
                                "set(ARDUINO_SDK_PATH \"" + arduinoSdkPathEnv + "\")" + "\r\n" +
                                        "message(\"ARDUINO_SDK_PATH ${ARDUINO_SDK_PATH}\")" + "\r\n";

                        File windowsPaths = new File(platformDirectory, "WindowsPaths.cmake");
                        OutputStream windowsPathsOutputStream = new FileOutputStream(windowsPaths);
                        InputStream windowsPathsInputStream = IOUtils.toInputStream(windowsPathsString, Charset.defaultCharset());

                        try {
                            IOUtils.copy(windowsPathsInputStream, windowsPathsOutputStream);
                        } finally {
                            closeStreams(windowsPathsOutputStream, windowsPathsInputStream);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return files.toArray(new File[0]);
    }

    public static VirtualFile[] copyToDirectory(final VirtualFile projectRoot) {
        ArrayList<VirtualFile> files = new ArrayList<>();

        ApplicationManager.getApplication().runWriteAction(new Runnable() {

            @Override
            public void run() {
                try {
                    VirtualFile cmakeDirectory = projectRoot.createChildDirectory(this, "cmake");
                    VirtualFile platformDirectory = cmakeDirectory.createChildDirectory(this, "Platform");

                    VirtualFile arduinoToolchain = cmakeDirectory.createChildData(this, "ArduinoToolchain.cmake");
                    VirtualFile arduino = platformDirectory.createChildData(this, "Arduino.cmake");

                    OutputStream arduinoToolchainOutputStream = arduinoToolchain.getOutputStream(this);
                    OutputStream arduinoOutputStream = arduino.getOutputStream(this);

                    InputStream arduinoToolchainInputStream = getArduinoToolchainCmake();
                    InputStream arduinoInputStream = getArduinoCmake();

                    try {
                        IOUtils.copy(arduinoToolchainInputStream, arduinoToolchainOutputStream);
                        IOUtils.copy(arduinoInputStream, arduinoOutputStream);
                        files.add(arduinoToolchain);
                        files.add(arduino);
                    } finally {
                        closeStreams(arduinoToolchainOutputStream,
                                arduinoOutputStream,
                                arduinoToolchainInputStream,
                                arduinoInputStream);
                    }

                    if (System.getProperty("os.name").toLowerCase().contains("win")) {
                        String arduinoSdkPathEnv = System.getenv("ARDUINO_SDK_PATH").replace("\\", "/");
                        String windowsPathsString =
                                "set(ARDUINO_SDK_PATH \"" + arduinoSdkPathEnv + "\")" + "\r\n" +
                                        "message(\"ARDUINO_SDK_PATH ${ARDUINO_SDK_PATH}\")" + "\r\n";

                        VirtualFile windowsPaths = platformDirectory.createChildData(this, "WindowsPaths.cmake");
                        OutputStream windowsPathsOutputStream = windowsPaths.getOutputStream(this);
                        InputStream windowsPathsInputStream = new FileInputStream(windowsPathsString);

                        try {
                            IOUtils.copy(windowsPathsInputStream, windowsPathsOutputStream);
                        } finally {
                            closeStreams(windowsPathsOutputStream, windowsPathsInputStream);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return files.toArray(new VirtualFile[0]);
    }

    static void closeStreams(Closeable... streams) {
        for (Closeable c : streams) {
            IOUtils.closeQuietly(c);
        }
    }
}
