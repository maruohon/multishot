package fi.dy.masa.minecraft.mods.multishot.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import fi.dy.masa.minecraft.mods.multishot.reference.Constants;

public class ScreenCamera extends ScreenBase
{
    public ScreenCamera()
    {
        super();
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        super.drawScreen(par1,  par2,  par3);

        int xl = (this.width / 2) - 130;
        int yc = (this.height / 2);

        this.fontRendererObj.drawString(I18n.format("multishot.gui.info.scroll"), xl + 2, yc - 30, 0xffcccccc);
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

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.initGui();
    }
}
