package plugin.config;

import java.awt.Color;

import lombok.experimental.UtilityClass;

@UtilityClass
//todo make it configurable
public class PluginConfig {

    public static final int MAX_LENGTH = 120;

    public static final int DIVINE_ON = 3;

    //border around sql
    public static final Color SELECTED_BORDER_COLOR = new Color(243, 139, 3);

    public static final Color HOVER_BORDER_COLOR = new Color(67, 67, 68, 255);
}
