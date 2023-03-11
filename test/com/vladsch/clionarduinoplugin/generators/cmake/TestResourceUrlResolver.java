package com.vladsch.clionarduinoplugin.generators.cmake;

import com.vladsch.flexmark.test.util.spec.ResourceResolverManager;
import com.vladsch.flexmark.test.util.spec.ResourceUrlResolver;

public class TestResourceUrlResolver {
    public static void registerResourceUrlResolvers() {
        ResourceResolverManager.registerUrlResolver(new OutTestClassUrlResolver());
    }

    private static class OutTestClassUrlResolver implements ResourceUrlResolver {
        static final String OUT_TEST = "/out/test/CLionArduinoPlugin/";

        OutTestClassUrlResolver() {}

        @Override
        public String apply(String externalForm) {
            if (ResourceUrlResolver.isFileProtocol(externalForm)) {
                String noFileProtocol = ResourceUrlResolver.removeProtocol(externalForm);

                int pos = noFileProtocol.indexOf(OUT_TEST);
                if (pos > 0) {
                    //int pathPos = noFileProtocol.indexOf("/", pos + OUT_TEST.length());
                    int pathPos = pos + OUT_TEST.length();
                    if (pathPos > 0) {
                        return "file://" + noFileProtocol.substring(0, pos) + "/test/" + noFileProtocol.substring(pathPos);
                    }
                }
            }

            return null;
        }
    }
}
