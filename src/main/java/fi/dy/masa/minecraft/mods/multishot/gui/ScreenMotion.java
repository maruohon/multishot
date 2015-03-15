package fi.dy.masa.minecraft.mods.multishot.gui;

import java.io.IOException;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.reference.Constants;

@SideOnly(Side.CLIENT)
public class ScreenMotion extends ScreenBase
{
    public ScreenMotion ()
    {
        super();
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        super.drawScreen(par1,  par2,  par3);
        int xl = (this.width / 2) - 130;
        int xr = (this.width / 2) + 0;
        int y = (this.height / 2) - 75;
        this.fontRendererObj.drawString(I18n.format("multishot.gui.label.nonlinear") + ":", xl + 5, y + 32   , 0xffffffff);
        this.fontRendererObj.drawString(I18n.format("multishot.gui.label.linear") + ":", xr + 5, y + 10, 0xffffffff);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        super.initGui();
        this.guiButtonScreenMotion.enabled = false;

        int xl = (this.width / 2) - 130;
        int xr = (this.width / 2) + 0;
        int y = (this.height / 2) - 75;

        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_MOTION_MODE,    xl, y + 0, 120, 20));
        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_MOTION_SPEED,   xl, y + 44, 120, 20));

        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_MOTION_X,       xr, y + 22, 130, 20));
        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_MOTION_Z,       xr, y + 44, 130, 20));
        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_MOTION_Y,       xr, y + 66, 130, 20));
        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_ROTATION_YAW,   xr, y + 88, 130, 20));
        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_ROTATION_PITCH, xr, y + 110, 130, 20));
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.initGui();
    }
}
