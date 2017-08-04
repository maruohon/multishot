package fi.dy.masa.minecraft.mods.multishot.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.config.Configs;
import fi.dy.masa.minecraft.mods.multishot.reference.Constants;

@SideOnly(Side.CLIENT)
public class ScreenGeneric extends ScreenBase
{
    public ScreenGeneric ()
    {
        super();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        int xl = (this.width / 2) - 130;
        int xr = (this.width / 2) + 5;
        int yc = (this.height / 2);

        this.fontRenderer.drawString(I18n.format("multishot.gui.label.video") + " @24:", xl + 2,     yc - 0, 0xffffffff);
        this.fontRenderer.drawString(":",                                                xl + 76,    yc - 0, 0xffffffff);
        this.fontRenderer.drawString(":",                                                xl + 101,   yc - 0, 0xffffffff);
        this.fontRenderer.drawString(I18n.format("multishot.gui.label.real.time") + ":", xl + 2,     yc + 21, 0xffffffff);
        this.fontRenderer.drawString(":",                                                xl + 76,    yc + 21, 0xffffffff);
        this.fontRenderer.drawString(":",                                                xl + 101,   yc + 21, 0xffffffff);
        this.fontRenderer.drawString(I18n.format("multishot.gui.label.shots") + ":",     xl + 2,     yc + 42, 0xffffffff);

        // Print information about (estimated) output from a timed recording
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        float m = 0.5f;
        GlStateManager.scale(m, m, m);
        int x = (int)(((double)xr + 2) / m);
        int y = (int)(((double)yc + 17) / m);
        long num = Configs.getActiveTimerNumShots();
        this.fontRenderer.drawString(I18n.format("multishot.gui.label.screenshots") + ": " + num,  x, y, 0xffffffff);
        long size = num * 1024L * 1024L; // Estimate at 1 MB per screenshot
        this.fontRenderer.drawString(I18n.format("multishot.gui.label.size.estimate") + ": " + this.formatByteSize(size) + " (@ 1MB/" + I18n.format("multishot.gui.label.shot") + ")", x, y + 10, 0xffffffff);
        x = (int)(((double)xl + 2) / m);
        y = (int)(((double)yc + 60) / m);
        this.fontRenderer.drawString(I18n.format("multishot.gui.label.save.path") + ":", x, y, 0xffffffff);
        String s = Configs.getSavePath();
        if (s.length() < 65)
        {
            this.fontRenderer.drawString(s, x, y + 12, 0xffffffff);
        }
        else if (s.length() < 130)
        {
            this.fontRenderer.drawString(s.substring(0, 65), x, y + 12, 0xffffffff);
            this.fontRenderer.drawString(s.substring(65, s.length()), x, y + 22, 0xffffffff);
        }
        else
        {
            this.fontRenderer.drawString(I18n.format("multishot.gui.label.toolong"), x, y + 12, 0xffffffff);
        }
        GlStateManager.popMatrix();

        this.fontRenderer.drawString(I18n.format("multishot.gui.info.scroll"), xl + 2, yc + 110, 0xffcccccc);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.guiButtonScreenGeneric.enabled = false;

        int xl = (this.width / 2) - 130;
        int xr = (this.width / 2) + 5;
        int yt = (this.height / 2) - 75;
        int yc = (this.height / 2);

        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_INTERVAL,           xl, yt + 0, 125, 20));
        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_ZOOM,               xl, yt + 23, 125, 20));
        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_BROWSE,             xr + 45, yt + 132, 80, 20));
        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_MULTISHOT_ENABLED,  xr, yt + 0, 125, 20));
        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_MOTION_ENABLED,     xr, yt + 23, 125, 20));
        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_LOCK_CONTROLS,      xr, yt + 46, 125, 20));
        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_HIDE_GUI,           xr, yt + 69, 125, 20));
        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_IMG_FORMAT,         xr, yc + 36, 45, 20));
        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_LOAD_DEFAULTS,      xr + 45, yc + 36, 80, 20));
        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_GUI_POSITION,       xr + 36, yt - 25, 88, 20));

        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_TIMER_SELECT,       xl + 0, yc - 29, 125, 20));
        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_TIME_VIDEO_HOUR,    xl + 54, yc - 6, 20, 20));
        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_TIME_VIDEO_MINUTE,  xl + 79, yc - 6, 20, 20));
        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_TIME_VIDEO_SECOND,  xl + 104, yc - 6, 20, 20));
        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_TIME_REAL_HOUR,     xl + 54, yc + 15, 20, 20));
        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_TIME_REAL_MINUTE,   xl + 79, yc + 15, 20, 20));
        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_TIME_REAL_SECOND,   xl + 104, yc + 15, 20, 20));
        buttonList.add(createGuiButton(Constants.GUI_BUTTON_ID_TIME_NUM_SHOTS,     xl + 54, yc + 36, 71, 20));
    }
}
