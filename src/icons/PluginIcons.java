package icons;

import com.vladsch.plugin.util.ui.Helpers;

import javax.swing.Icon;
import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class PluginIcons {
    private static Icon load(String path) {
        return Helpers.load(path, PluginIcons.class);
    }

    private static final AlphaComposite disabledComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);

    private static Icon createDisabledIcon(Icon icon) {
        return new Icon() {
            public int getIconWidth() { return icon.getIconWidth(); }

            public int getIconHeight() { return icon.getIconHeight(); }

            @Override
            public String toString() { return icon.toString(); }

            @Override
            public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
                ((Graphics2D) g).setComposite(disabledComposite);
                icon.paintIcon(c, g, x, y);
            }
        };
    }

    public static final Icon arduino_logo = load("/icons/arduino_logo.png");
    public static final Icon arduino_plugin_logo = load("/icons/arduino_plugin_logo.png");
    public static final Icon arduino_library = load("/icons/arduino_library.png");
    public static final Icon arduino_project = load("/icons/arduino_project.png");
    public static final Icon arduino_sketch = load("/icons/arduino_sketch.png");
    public static final Icon serial_port = load("/icons/serial_port.png");
    public static final Icon serial_port_disconnected = load("/icons/serial_port_disconnected.png");
    /*
        public static final Icon serial_port_disconnected_disabled = createDisabledIcon(IconUtil.desaturate(serial_port_disconnected));
        public static final Icon serial_port_send = load("/icons/serial_port_send.png");
        public static final Icon serial_port_monitor = load("/icons/serial_port_monitor.png");
        public static final Icon information_source = load("/icons/information_source.png");
    */
    public static final Icon show_send_options = load("/icons/show_send_options.png");
    public static final Icon serial_port_tool = load("/icons/serial_port_tool.png");
    public static final Icon warning = load("/icons/warning.png");
}
