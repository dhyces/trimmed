package dhyces.trimmed.impl.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class InfoToast implements Toast {
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
    public Visibility render(PoseStack pPoseStack, ToastComponent pToastComponent, long pTimeSinceLastVisible) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        if (height() <= 32) {
            Gui.blit(pPoseStack, 0, 0, 0, 0, width(), height());
        } else {
            Gui.blit(pPoseStack, 0, 0, 0, 0, width(), 4);
            int middleAmount = height()-8;
            int yPos = 4;
            int iterTimes = (int) Math.ceil(middleAmount / 28f);
            for (; iterTimes >= 0; iterTimes--) {
                int amountToDraw = Math.min(24, middleAmount);
                Gui.blit(pPoseStack, 0, yPos, 0, 4, width(), amountToDraw);
                middleAmount -= amountToDraw;
                yPos += amountToDraw;
            }
            Gui.blit(pPoseStack, 0, yPos, 0, 28, width(), 4);
        }

        Font font = pToastComponent.getMinecraft().font;
        Gui.drawString(pPoseStack, font, title, 5, 5, 0xFFFFFF);
        int lineNum = 1;
        for (FormattedCharSequence messageLine : messageLines) {
            Gui.drawString(pPoseStack, font, messageLine, 5, 7 + ((font.lineHeight+2) * lineNum++), 0xFFFFFF);
        }

        return pTimeSinceLastVisible > 5000f * pToastComponent.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public int height() {
        return height;
    }
}
