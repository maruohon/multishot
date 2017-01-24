package fi.dy.masa.minecraft.mods.multishot.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.config.Configs;
import fi.dy.masa.minecraft.mods.multishot.reference.Constants;
import fi.dy.masa.minecraft.mods.multishot.reference.Reference;
import fi.dy.masa.minecraft.mods.multishot.state.State;

@SideOnly(Side.CLIENT)
public abstract class ScreenBase extends GuiScreen
{
    protected Minecraft mc;
    protected GuiButton guiButtonScreenGeneric;
    protected GuiButton guiButtonScreenMotion;
    protected GuiButton guiButtonScreenCamera;
    protected GuiButton guiButtonBackToGame;
    protected final ResourceLocation infoTex;
    protected final List<String> lines = new ArrayList<String>();

    public ScreenBase ()
    {
        this.mc = Minecraft.getMinecraft();

        this.infoTex = new ResourceLocation("multishot", "textures/gui/hud.png");

        this.lines.add(I18n.format("multishot.gui.info.keys.notepause"));
        this.lines.add(I18n.format("multishot.gui.info.keys.togglemotion"));
        this.lines.add(I18n.format("multishot.gui.info.keys.togglerecording"));
        this.lines.add(I18n.format("multishot.gui.info.keys.pause.recording"));
        this.lines.add(I18n.format("multishot.gui.info.keys.pause.notrecording"));
        this.lines.add(I18n.format("multishot.gui.info.keys.setcenterpoint"));
        this.lines.add(I18n.format("multishot.gui.info.keys.settargetpoint"));
        this.lines.add(I18n.format("multishot.gui.info.keys.removecenterpoint"));
        this.lines.add(I18n.format("multishot.gui.info.keys.removetargetpoint"));
        this.lines.add(I18n.format("multishot.gui.info.keys.removeallpoints"));
        this.lines.add(I18n.format("multishot.gui.info.keys.reloadpath"));
        this.lines.add(I18n.format("multishot.gui.info.keys.nextpath"));
        this.lines.add(I18n.format("multishot.gui.info.keys.previouspath"));
        this.lines.add(I18n.format("multishot.gui.info.keys.reversepathdirection"));
        this.lines.add(I18n.format("multishot.gui.info.keys.movetopathstart"));
        this.lines.add(I18n.format("multishot.gui.info.keys.movetoclosestpathpoint"));
        this.lines.add(I18n.format("multishot.gui.info.keys.insertpointbefore"));
        this.lines.add(I18n.format("multishot.gui.info.keys.insertpointafter"));
        this.lines.add(I18n.format("multishot.gui.info.keys.removenearestpoint"));
        this.lines.add(I18n.format("multishot.gui.info.keys.storepathpoint"));
        this.lines.add(I18n.format("multishot.gui.info.keys.movepathpoint"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();    // The default dark background
        super.drawScreen(mouseX, mouseY, partialTicks);

        this.drawGuiContainerBackgroundLayer(mouseX, mouseY, partialTicks);
        this.drawGuiContainerForegroundLayer(mouseX, mouseY);
        this.drawTooltips(mouseX, mouseY);
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = I18n.format("multishot.gui.label.settings");
        int textWidth = this.fontRendererObj.getStringWidth(s);
        int x = (this.width / 2);
        int y = (this.height / 2);
        this.fontRendererObj.drawString(s, x - (textWidth / 2), y - 115, 0xffffffff);
        s = " v" + Reference.VERSION;
        this.fontRendererObj.drawString(s, x - 130, y - 115, 0xffb0b0b0);
    }

    protected void drawGuiContainerBackgroundLayer(int mouseX, int mouseY, float partialTicks)
    {
        int infoX = 2;
        int infoY = 2;

        this.mc.getTextureManager().bindTexture(this.infoTex);
        this.drawTexturedModalRect(infoX, infoY, 238, 0, 18, 18);
    }

    protected void drawTooltips(int mouseX, int mouseY)
    {
        int infoX = 2;
        int infoY = 2;

        if (mouseX >= infoX && mouseX <= (infoX + 18) && mouseY >= infoY && mouseY <= (infoY + 18))
        {
            this.drawHoveringText(this.lines, mouseX, mouseY + 20, this.fontRendererObj);
        }
    }

    @Override
    public void initGui()
    {
        // Create the settings screen buttons
        int x = (this.width / 2) - 130;
        int y = (this.height / 2) - 100;
        this.guiButtonScreenGeneric = new GuiButton(Constants.GUI_BUTTON_ID_SCREEN_GENERIC,   x + 0, y + 0, 50, 20, I18n.format("multishot.gui.label.button.generic"));
        this.guiButtonScreenMotion  = new GuiButton(Constants.GUI_BUTTON_ID_SCREEN_MOTION,    x + 54, y + 0, 50, 20, I18n.format("multishot.gui.label.button.motion"));
        this.guiButtonScreenCamera  = new GuiButton(Constants.GUI_BUTTON_ID_SCREEN_CAMERA,    x + 108, y + 0, 50, 20, I18n.format("multishot.gui.label.button.camera"));
        this.guiButtonBackToGame    = new GuiButton(Constants.GUI_BUTTON_ID_BACK_TO_GAME, (this.width / 2) - 100, (this.height / 2) + 80, 200, 20, I18n.format("multishot.gui.label.button.backtogame"));

        this.buttonList.clear();
        this.buttonList.add(this.guiButtonScreenGeneric);
        this.buttonList.add(this.guiButtonScreenMotion);
        this.buttonList.add(this.guiButtonScreenCamera);
        this.buttonList.add(this.guiButtonBackToGame);
    }

    @Override
    public void keyTyped(char keyChar, int keyID)
    {
        if (keyID == Keyboard.KEY_ESCAPE)
        {
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
            Configs.getConfig().writeToConfiguration();

            State.setStateFromConfigs();
        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();

        int dWheel = Mouse.getEventDWheel();

        if (dWheel != 0)
        {
            dWheel /= 120;
            int eventX = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int eventY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            this.mouseScrolled(eventX, eventY, dWheel);
        }

        this.initGui();
    }

    public void mouseScrolled(int x, int y, int value)
    {
        GuiButton guiButton;

        for (int i = 0; i < this.buttonList.size(); ++i)
        {
            guiButton = (GuiButton)this.buttonList.get(i);

            if (guiButton.mousePressed(this.mc, x, y))
            {
                if (isConfigButton(guiButton))
                {
                    int mode = this.getButtonModifier(); // 0..3 for 1/10/100/1000 at a time
                    // value is the number of "notches" the wheel was scrolled, positive for up, negative for down
                    Configs.getConfig().changeValue(guiButton.id, mode, 0, value);
                    this.updateGuiButton(guiButton, guiButton.id);
                }
                break;
            }
        }
    }

    private int getButtonModifier()
    {
        if (isCtrlKeyDown() && isShiftKeyDown()) { return 3; }
        else if(isShiftKeyDown()) { return 2; }
        else if (isCtrlKeyDown()) { return 1; }
        return 0;
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3)
    {
        for (int l = 0; l < this.buttonList.size(); ++l)
        {
            GuiButton guiButton = (GuiButton)this.buttonList.get(l);

            if (guiButton.mousePressed(this.mc, par1, par2))
            {
                guiButton.playPressSound(this.mc.getSoundHandler());

                if (par3 == 0) // Left click
                {
                    this.actionPerformedLeft(guiButton);
                }
                else if (par3 == 1) // Right click
                {
                    this.actionPerformedRight(guiButton);
                }
                else if (par3 == 2) // Middle click
                {
                    this.actionPerformedMiddle(guiButton);
                }
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.id == Constants.GUI_BUTTON_ID_BACK_TO_GAME)
        {
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
            Configs.getConfig().writeToConfiguration();
            State.setStateFromConfigs();
        }
        else if (this.isMenuScreenButton(par1GuiButton))
        {
            this.changeActiveScreen(par1GuiButton);
        }
        else if (par1GuiButton.id == Constants.GUI_BUTTON_ID_LOAD_DEFAULTS)
        {
            Configs.getConfig().resetAllConfigs();
        }
        else if (isConfigButton(par1GuiButton))
        {
            int mode = this.getButtonModifier(); // 0..4 for 1/10/100/1000/10000 at a time
            Configs.getConfig().changeValue(par1GuiButton.id, mode, 0);
        }
    }

    protected void actionPerformedLeft(GuiButton par1GuiButton)
    {
        this.actionPerformed(par1GuiButton);
    }

    protected void actionPerformedRight(GuiButton par1GuiButton)
    {
        if (isConfigButton(par1GuiButton))
        {
            int mode = this.getButtonModifier(); // 0..4 for 1/10/100/1000/10000 at a time
            Configs.getConfig().changeValue(par1GuiButton.id, mode, 1);
        }
    }

    protected void actionPerformedMiddle(GuiButton par1GuiButton)
    {
        if (isConfigButton(par1GuiButton))
        {
            int mode = this.getButtonModifier(); // 0..4 for 1/10/100/1000/10000 at a time
            if (mode == 1) // CTRL held
            {
                Configs.getConfig().invertValue(par1GuiButton.id);
            }
            else if (mode == 0) // no modifiers held
            {
                Configs.getConfig().resetValue(par1GuiButton.id);
            }
        }
    }

    // Is this button one that changes the menu screen?
    protected boolean isMenuScreenButton(GuiButton btn)
    {
        int id = btn.id;
        if (id == Constants.GUI_BUTTON_ID_SCREEN_GENERIC ||
            id == Constants.GUI_BUTTON_ID_SCREEN_MOTION ||
            id == Constants.GUI_BUTTON_ID_SCREEN_CAMERA)
        {
            return true;
        }

        return false;
    }

    protected boolean isConfigButton(GuiButton btn)
    {
        // FIXME This is really error prone!!
        return ! this.isMenuScreenButton(btn);
    }

    // Change the active menu screen
    protected void changeActiveScreen(GuiButton btn)
    {
        if (btn.id == Constants.GUI_BUTTON_ID_SCREEN_GENERIC)
        {
            this.mc.displayGuiScreen(new ScreenGeneric());
        }
        else if (btn.id == Constants.GUI_BUTTON_ID_SCREEN_MOTION)
        {
            this.mc.displayGuiScreen(new ScreenMotion());
        }
        else if (btn.id == Constants.GUI_BUTTON_ID_SCREEN_CAMERA)
        {
            this.mc.displayGuiScreen(new ScreenCamera());
        }
    }

    public GuiButton createGuiButton (int id, int x, int y, int w, int h)
    {
        String s;
        s = getButtonDisplayString(id);
        return new GuiButton(id, x, y, w, h, s);
    }

    public void updateGuiButtonString(GuiButton btn, int id)
    {
        btn.displayString = getButtonDisplayString(id);
    }

    public void updateGuiButton(GuiButton btn, int id)
    {
        this.updateGuiButtonString(btn, id);
    }

    public String getButtonDisplayString(int id)
    {
        String s;
        s = getButtonDisplayStringBase(id) + Configs.getConfig().getDisplayString(id);
        return s;
    }

    public String getButtonDisplayStringBase (int id)
    {
        String s = "";
        switch(id)
        {
            case Constants.GUI_BUTTON_ID_MULTISHOT_ENABLED:
                s = I18n.format("multishot.gui.label.button.multishot.enabled") + ": ";
                break;
            case Constants.GUI_BUTTON_ID_MOTION_ENABLED:
                s = I18n.format("multishot.gui.label.button.motion.enabled") + ": ";
                break;
            case Constants.GUI_BUTTON_ID_LOCK_CONTROLS:
                s = I18n.format("multishot.gui.label.button.lock.controls") + ": ";
                break;
            case Constants.GUI_BUTTON_ID_HIDE_GUI:
                s = I18n.format("multishot.gui.label.button.hide.gui") + ": ";
                break;
            case Constants.GUI_BUTTON_ID_USE_FREE_CAMERA:
                s = I18n.format("multishot.gui.label.button.use.free.camera") + ": ";
                break;
            case Constants.GUI_BUTTON_ID_FREE_CAMERA_WIDTH:
                s = I18n.format("multishot.gui.label.button.free.camera.width") + ": ";
                break;
            case Constants.GUI_BUTTON_ID_FREE_CAMERA_HEIGHT:
                s = I18n.format("multishot.gui.label.button.free.camera.height") + ": ";
                break;
            case Constants.GUI_BUTTON_ID_INTERVAL:
                s = I18n.format("multishot.gui.label.button.interval") + ": ";
                break;
            case Constants.GUI_BUTTON_ID_ZOOM:
                s = I18n.format("multishot.gui.label.button.zoom") + ": ";
                break;
            case Constants.GUI_BUTTON_ID_TIMER_SELECT:
                s = I18n.format("multishot.gui.label.button.timer.selection") + ": ";
                break;
            case Constants.GUI_BUTTON_ID_MOTION_MODE:
                s = I18n.format("multishot.gui.label.button.motion.mode") + ": ";
                break;
            case Constants.GUI_BUTTON_ID_MOTION_X:
                s = I18n.format("multishot.gui.label.button.motion.x") + ": ";
                break;
            case Constants.GUI_BUTTON_ID_MOTION_Z:
                s = I18n.format("multishot.gui.label.button.motion.z") + ": ";
                break;
            case Constants.GUI_BUTTON_ID_MOTION_Y:
                s = I18n.format("multishot.gui.label.button.motion.y") + ": ";
                break;
            case Constants.GUI_BUTTON_ID_ROTATION_YAW:
                s = I18n.format("multishot.gui.label.button.rotation.yaw") + ": ";
                break;
            case Constants.GUI_BUTTON_ID_ROTATION_PITCH:
                s = I18n.format("multishot.gui.label.button.rotation.pitch") + ": ";
                break;
            case Constants.GUI_BUTTON_ID_MOTION_SPEED:
                s = I18n.format("multishot.gui.label.button.motion.speed") + ": ";
                break;
            case Constants.GUI_BUTTON_ID_BROWSE:
                s = I18n.format("multishot.gui.label.button.browse");
                break;
            case Constants.GUI_BUTTON_ID_IMG_FORMAT:
                s = I18n.format("multishot.gui.label.button.img.format");
                break;
            case Constants.GUI_BUTTON_ID_GUI_POSITION:
                s = I18n.format("multishot.gui.label.button.gui.position") + ": ";
                break;
            case Constants.GUI_BUTTON_ID_LOAD_DEFAULTS:
                s = I18n.format("multishot.gui.label.button.load.defaults");
                break;
            default:
                break;
        }

        return s;
    }

    public String formatByteSize (long size)
    {
        double sized = (double)size;
        if (size >= (1024L * 1024L * 1024L * 1024L)) // TB
        {
            return String.format("%.2f TB", sized / (1024.0 * 1024.0 * 1024.0 * 1024.0));
        }
        if (size >= (1024L * 1024L * 1024L)) // GB
        {
            return String.format("%.2f GB", sized / (1024.0 * 1024.0 * 1024.0));
        }
        if (size >= (1024L * 1024L))    // MB
        {
            return String.format("%.2f MB", sized / (1024.0 * 1024.0));
        }
        if (size >= 1024L)  // kB
        {
            return String.format("%.2f kB", sized / 1024.0);
        }
        return String.format("%d B", size); // B
    }
}
