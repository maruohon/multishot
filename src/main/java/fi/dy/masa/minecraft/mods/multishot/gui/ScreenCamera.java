package fi.dy.masa.minecraft.mods.multishot.gui;

import net.minecraft.client.resources.I18n;
import fi.dy.masa.minecraft.mods.multishot.reference.Constants;

public class ScreenCamera extends ScreenBase
{
    public ScreenCamera()
    {
        super();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        int xl = (this.width / 2) - 130;
        int yc = (this.height / 2);

        this.fontRenderer.drawString(I18n.format("multishot.gui.info.scroll"), xl + 2, yc - 30, 0xffcccccc);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.guiButtonScreenCamera.enabled = false;

        int xl = (this.width / 2) - 130;
        int yt = (this.height / 2) - 75;

        this.buttonList.add(this.createGuiButton(Constants.GUI_BUTTON_ID_USE_FREE_CAMERA,   xl +   0, yt +   0, 100, 20));
        this.buttonList.add(this.createGuiButton(Constants.GUI_BUTTON_ID_FREE_CAMERA_WIDTH, xl +   0, yt +  22,  80, 20));
        this.buttonList.add(this.createGuiButton(Constants.GUI_BUTTON_ID_FREE_CAMERA_HEIGHT,xl +  82, yt +  22,  80, 20));
    }
}
