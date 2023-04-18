package com.vladsch.clionarduinoplugin.components;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.vladsch.clionarduinoplugin.resources.Strings;

import static com.intellij.openapi.application.ApplicationManager.getApplication;

public class ArduinoApplicationComponent implements Disposable {
    public ArduinoApplicationComponent() {
        getApplication().invokeLater(() ->
                getApplication().runWriteAction(() -> {
                    FileType cpp = FileTypeManager.getInstance().getFileTypeByExtension(Strings.CPP_EXT);
                    FileTypeManager.getInstance().associateExtension(cpp, Strings.INO_EXT);
                    FileTypeManager.getInstance().associateExtension(cpp, Strings.PDE_EXT);
                }));
    }

    public void dispose() {
        if (!getApplication().isDisposed()) {
            FileTypeManager fileTypeManager = FileTypeManager.getInstance();
            FileType cpp = fileTypeManager.getFileTypeByExtension(Strings.CPP_EXT);
            fileTypeManager.removeAssociatedExtension(cpp, Strings.INO_EXT);
            fileTypeManager.removeAssociatedExtension(cpp, Strings.PDE_EXT);
        }
    }

    static public ArduinoApplicationComponent getInstance() {
        return getApplication().getService(ArduinoApplicationComponent.class);
    }
}
