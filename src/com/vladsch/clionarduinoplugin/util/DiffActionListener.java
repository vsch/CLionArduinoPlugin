/*
 * Copyright (c) 2016-2023 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.vladsch.clionarduinoplugin.util;

import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffContentFactoryEx;
import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.DiffManager;
import com.intellij.diff.DiffRequestFactory;
import com.intellij.diff.actions.impl.MutableDiffRequestChain;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileWithoutContent;
import com.vladsch.clionarduinoplugin.resources.ResourceUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.util.function.Supplier;

public class DiffActionListener implements ActionListener {
    private final @Nullable Project myProject;
    private final @NotNull String myBundledWindowTitle;
    private final @NotNull String myBundledText;
    private final @Nullable FileType myBundledTextType;
    private final @NotNull String myBundledTextTitle;
    private final @NotNull Supplier<String> myFilePathSupplier;

    public DiffActionListener(@Nullable Project project, @NotNull String bundledWindowTitle, @NotNull String bundledText, @Nullable FileType bundledTextType, @NotNull String bundledTextTitle, @NotNull Supplier<String> filePathSupplier) {
        myProject = project;
        myBundledWindowTitle = bundledWindowTitle;
        myBundledText = bundledText;
        myBundledTextType = bundledTextType;
        myBundledTextTitle = bundledTextTitle;
        myFilePathSupplier = filePathSupplier;
    }

    public static boolean hasContent(VirtualFile file) {
        return !(file instanceof VirtualFileWithoutContent);
    }

    public static boolean canCompare(@NotNull VirtualFile file1, @NotNull VirtualFile file2) {
        return !file1.equals(file2) && hasContent(file1) && hasContent(file2);
    }

    @NotNull
    public static MutableDiffRequestChain createMutableChainFromFiles(
            @Nullable Project project,
            @NotNull VirtualFile file1,
            @NotNull VirtualFile file2
    ) {
        DiffContentFactory contentFactory = DiffContentFactory.getInstance();
        DiffRequestFactory requestFactory = DiffRequestFactory.getInstance();

        DiffContent content1 = contentFactory.create(project, file1);
        DiffContent content2 = contentFactory.create(project, file2);

        MutableDiffRequestChain chain = new MutableDiffRequestChain(content1, content2);

        chain.setWindowTitle(requestFactory.getTitle(file1, file2));
        chain.setTitle1(requestFactory.getContentTitle(file1));
        chain.setTitle2(requestFactory.getContentTitle(file2));

        return chain;
    }

    @NotNull
    public static MutableDiffRequestChain createMutableChainFromFile(
            @Nullable Project project,
            @NotNull String windowTitle,
            @NotNull String file1Title,
            @NotNull String file1Text,
            @Nullable FileType file1Type,
            @NotNull VirtualFile file2
    ) {
        DiffContentFactory contentFactory = DiffContentFactory.getInstance();
        DiffRequestFactory requestFactory = DiffRequestFactory.getInstance();

        DiffContent content1 = contentFactory.create(project, file1Text, file1Type);
        DiffContent content2 = contentFactory.create(project, file2);

        MutableDiffRequestChain chain = new MutableDiffRequestChain(content1, content2);

        chain.setWindowTitle(windowTitle);
        chain.setTitle1(file1Title);
        chain.setTitle2(requestFactory.getContentTitle(file2));

        return chain;
    }

    public static DiffContent getContent(String content) {
        return DiffContentFactoryEx.getInstanceEx().create(content, PlainTextFileType.INSTANCE, true);
    }

    public static void showDiff(DiffRequest request) {
        if (request == null) return;

        DiffManager.getInstance().showDiff(null, request);
    }

    @Override
    public void actionPerformed(ActionEvent ignored) {
        File file = new File(myFilePathSupplier.get());
        if (!file.exists()) {
        } else if (file.isDirectory()) {
        } else {
            boolean diffShown = false;

            try {
                VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByUrl(String.valueOf(file.toURI().toURL()).replaceFirst("^file:", "file://"));

                if (virtualFile != null && hasContent(virtualFile)) {
                    MutableDiffRequestChain chain = createMutableChainFromFile(myProject, myBundledWindowTitle,
                            myBundledTextTitle, myBundledText, myBundledTextType
                            , virtualFile);

                    DiffManager.getInstance().showDiff(null, chain, DiffDialogHints.DEFAULT);
                    diffShown = true;
                }
            } catch (MalformedURLException ignored1) {

            }

            if (!diffShown) {
                String fileTxt = ResourceUtils.getFileContent(file);
                if (fileTxt == null) {
                    fileTxt = "";
                }

                SimpleDiffRequest request = new SimpleDiffRequest(
                        myBundledWindowTitle,
                        getContent(myBundledText),
                        getContent(fileTxt),
                        myBundledTextTitle,
                        myFilePathSupplier.get());
                showDiff(request);
            }
        }
    }
}
