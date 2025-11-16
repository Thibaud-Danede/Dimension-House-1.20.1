package net.ravadael.dimensionhouse.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final KeyMapping TELEPORT_KEY = new KeyMapping(
            "key.dimensionhouse.teleport",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "key.categories.misc"
    );
}
