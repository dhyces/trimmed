package dhyces.trimmed.impl.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class InfoToast implements Toast {
    public static final ResourceLocation TEXTURE = new ResourceLocation("toast/advancement");
    private static final int MIDDLE_OF_TOAST = 24;
    private final Component title;
    private final List<FormattedCharSequence> messageLines;
    private final int height;

    public InfoToast(Component title, Component message) {
        this.title = title;
        this.messageLines = Minecraft.getInstance().font.split(message, this.width()-8);
        this.height = 19 + messageLines.size() * (Minecraft.getInstance().font.lineHeight+2);
    }

    public static InfoToast reloadClientInfo() {
        return new InfoToast(Component.translatable("trimmed.info.datapacksReloadedTitle").withStyle(Style.EMPTY.withUnderlined(true)), Component.translatable("trimmed.info.datapacksReloaded"));
    }


    @Override
    public Visibility render(GuiGraphics guiGraphics, ToastComponent toastComponent, long timeSinceLast) {
        if (height() <= 32) {
            guiGraphics.blit(TEXTURE, 0, 0, 0, 0, width(), height());
        } else {
            guiGraphics.blit(TEXTURE, 0, 0, 0, 0, width(), 4);
            int middleAmount = height()-8;
            int yPos = 4;
            int iterTimes = (int) Math.ceil(middleAmount / 28f);
            for (; iterTimes >= 0; iterTimes--) {
                int amountToDraw = Math.min(24, middleAmount);
                guiGraphics.blit(TEXTURE, 0, yPos, 0, 4, width(), amountToDraw);
                middleAmount -= amountToDraw;
                yPos += amountToDraw;
            }
            guiGraphics.blit(TEXTURE, 0, yPos, 0, 28, width(), 4);
        }

        Font font = toastComponent.getMinecraft().font;
        guiGraphics.drawString(font, title, 5, 5, 0xFFFFFF);
        int lineNum = 1;
        for (FormattedCharSequence messageLine : messageLines) {
            guiGraphics.drawString(font, messageLine, 5, 7 + ((font.lineHeight+2) * lineNum++), 0xFFFFFF);
        }

        return timeSinceLast > 5000f * toastComponent.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public int height() {
        return height;
    }
}
