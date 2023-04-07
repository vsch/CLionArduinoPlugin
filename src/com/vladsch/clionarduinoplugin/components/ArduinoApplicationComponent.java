package com.vladsch.clionarduinoplugin.components;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.util.Disposer;
import com.vladsch.clionarduinoplugin.resources.Strings;
import com.vladsch.clionarduinoplugin.serial.SerialPortManager;

public class ArduinoApplicationComponent implements Disposable {
    public ArduinoApplicationComponent() {
        ApplicationManager.getApplication().invokeLater(() ->
                ApplicationManager.getApplication().runWriteAction(() -> {
                    FileType cpp = FileTypeManager.getInstance().getFileTypeByExtension(Strings.CPP_EXT);
                    FileTypeManager.getInstance().associateExtension(cpp, Strings.INO_EXT);
                    FileTypeManager.getInstance().associateExtension(cpp, Strings.PDE_EXT);
                }));
        
        Disposer.register(SerialPortManager.getInstance(), this);
    }

    public void dispose() {
        ApplicationManager.getApplication().invokeLater(() ->
                ApplicationManager.getApplication().runWriteAction(() -> {
                    FileType cpp = FileTypeManager.getInstance().getFileTypeByExtension(Strings.CPP_EXT);
                    FileTypeManager.getInstance().removeAssociatedExtension(cpp, Strings.INO_EXT);
                    FileTypeManager.getInstance().removeAssociatedExtension(cpp, Strings.PDE_EXT);
                }));
    }

    static public ArduinoApplicationComponent getInstance() {
        return ApplicationManager.getApplication().getService(ArduinoApplicationComponent.class);
    } 
}
