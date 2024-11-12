package dev.dhyces.trimmed.impl.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class InfoToast implements Toast {
    public static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("toast/advancement");
    private static final int MIDDLE_OF_TOAST = 24;
    private final Component title;
    private final List<FormattedCharSequence> messageLines;
    private final int height;
    private Visibility desiredVisibility = Visibility.SHOW;

    public InfoToast(Component title, Component message) {
        this.title = title;
        this.messageLines = Minecraft.getInstance().font.split(message, this.width()-8);
        this.height = 19 + messageLines.size() * (Minecraft.getInstance().font.lineHeight+2);
    }

    public static InfoToast reloadClientInfo() {
        return new InfoToast(Component.translatable("trimmed.info.datapacksReloadedTitle").withStyle(Style.EMPTY.withUnderlined(true)), Component.translatable("trimmed.info.datapacksReloaded"));
    }

    @Override
    public Visibility getWantedVisibility() {
        return desiredVisibility;
    }

    @Override
    public void update(ToastManager toastManager, long timeSinceLast) {
        if (timeSinceLast > 5000f * toastManager.getNotificationDisplayTimeMultiplier()) {
            desiredVisibility = Visibility.HIDE;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, Font font, long timeSinceLast) {
        // TODO: Fix, if ever used
        if (height() <= 32) {
//            guiGraphics.blit(TEXTURE, 0, 0, 0, 0, width(), height());
        } else {
//            guiGraphics.blit(TEXTURE, 0, 0, 0, 0, width(), 4);
            int middleAmount = height()-8;
            int yPos = 4;
            int iterTimes = (int) Math.ceil(middleAmount / 28f);
            for (; iterTimes >= 0; iterTimes--) {
                int amountToDraw = Math.min(24, middleAmount);
//                guiGraphics.blit(TEXTURE, 0, yPos, 0, 4, width(), amountToDraw);
                middleAmount -= amountToDraw;
                yPos += amountToDraw;
            }
//            guiGraphics.blit(TEXTURE, 0, yPos, 0, 28, width(), 4);
        }

        guiGraphics.drawString(font, title, 5, 5, 0xFFFFFF);
        int lineNum = 1;
        for (FormattedCharSequence messageLine : messageLines) {
            guiGraphics.drawString(font, messageLine, 5, 7 + ((font.lineHeight+2) * lineNum++), 0xFFFFFF);
        }
    }

    @Override
    public int height() {
        return height;
    }
}
