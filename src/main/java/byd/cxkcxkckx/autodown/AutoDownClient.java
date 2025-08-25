package byd.cxkcxkckx.autodown;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AutoDownClient implements ClientModInitializer {
    private static final Logger LOGGER = LogManager.getLogger("autodown-client");

    @Override
    public void onInitializeClient() {
        LOGGER.info("AutoDownClient initialized");
        // If downloads were already performed during common init, show the restart screen immediately
        if (AutoDownMod.downloadsPerformed) {
            LOGGER.info("AutoDownClient: downloads already performed on init, showing restart screen");
            MinecraftClient.getInstance().execute(() -> {
                Screen s = new ForceRestartScreen();
                MinecraftClient.getInstance().setScreen(s);
            });
            // only show once
            AutoDownMod.downloadsPerformed = false;
            return;
        }
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && AutoDownMod.downloadsPerformed) {
                LOGGER.info("AutoDownClient: 显示重启提示界面");
                MinecraftClient.getInstance().execute(() -> {
                    Screen s = new ForceRestartScreen();
                    MinecraftClient.getInstance().setScreen(s);
                });
                // only show once
                AutoDownMod.downloadsPerformed = false;
            }
        });
    }
}
