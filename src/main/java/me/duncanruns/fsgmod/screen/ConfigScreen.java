package me.duncanruns.fsgmod.screen;

import me.duncanruns.fsgmod.FSGMod;
import me.duncanruns.fsgmod.FSGModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;

public class ConfigScreen extends Screen {
    private String installedFilterText;
    private int y;

    public ConfigScreen() {
        super(new LiteralText("FSG Mod Config"));
    }

    private LiteralText getBackgroundFilterText() {
        return new LiteralText("Filter for next seed while playing: " + (FSGMod.shouldRunInBackground() ? "ON" : "OFF"));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        int y = 15;
        this.drawCenteredText(matrices, this.textRenderer, this.title, width / 2, y, 0xFFFFFF);
        y += 60;
        this.drawCenteredString(matrices, this.textRenderer, installedFilterText, width / 2, y, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void init(MinecraftClient client, int width, int height) {
        super.init(client, width, height);
        y = 75;
        if (Files.isDirectory(FSGMod.getFsgDir())) {
            initFilterInstalled(client, width);
        } else {
            initFilterNotInstalled(client, width);
        }
        y += 60;
        addButton(new ButtonWidget(width / 2 - 100, y, 200, 20, getBackgroundFilterText(), b -> {
            FSGMod.toggleRunInBackground();
            b.setMessage(getBackgroundFilterText());
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 168, 200, 20, ScreenTexts.DONE, buttonWidget -> this.client.openScreen(null)));
    }

    private void initFilterInstalled(MinecraftClient client, int width) {
        installedFilterText = "Installed Filter: " + FSGModConfig.getInstance().installedFilter;
        y += 10;
        addButton(new ButtonWidget(width / 2 - 100, y, 200, 20, new LiteralText("Configure Filter (Open Folder)"), b -> Util.getOperatingSystem().open(FSGMod.getFsgDir().toFile())));
        y += 25;
        addButton(new ButtonWidget(width / 2 - 100, y, 200, 20, new LiteralText("Uninstall Filter"), b -> {
            try {
                FileUtils.deleteDirectory(FSGMod.getFsgDir().toFile());
                FSGModConfig.getInstance().installedFilter = "Unknown Filter";
            } catch (IOException e) {
                FSGMod.logError(e);
            }
            client.openScreen(new ConfigScreen());
        }));
    }

    private void initFilterNotInstalled(MinecraftClient client, int width) {
        Util.OperatingSystem os = Util.getOperatingSystem();
        installedFilterText = "No filter installed!";
        y += 21;
        addButton(new ButtonWidget(width / 2 - 100, y, 200, 20, new LiteralText("Install Filter..."), b -> client.openScreen(new FiltersScreen()))).active = (os == Util.OperatingSystem.WINDOWS || os == Util.OperatingSystem.LINUX || os == Util.OperatingSystem.OSX);
        y += 14;
    }
}
