package byd.cxkcxkckx.autodown;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ForceRestartScreen extends Screen {
    protected ForceRestartScreen() {
        super(Text.literal("AutoDown - 需要重启"));
    }

    @Override
    protected void init() {
        super.init();
        int btnWidth = 200;
        int x = (this.width - btnWidth) / 2;
        int y = this.height / 2;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("退出游戏并重启客户端"), btn -> {
            // close client
            MinecraftClient.getInstance().scheduleStop();
        }).dimensions(x, y, btnWidth, 20).build());
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    // simple background
    drawContext.fill(0, 0, this.width, this.height, 0x88000000);
    drawContext.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 30, 0xFFFFFF);
        super.render(drawContext, mouseX, mouseY, delta);
    }
}
