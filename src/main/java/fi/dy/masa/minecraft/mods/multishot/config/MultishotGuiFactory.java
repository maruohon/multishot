package fi.dy.masa.minecraft.mods.multishot.config;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.DefaultGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import fi.dy.masa.minecraft.mods.multishot.reference.Reference;

public class MultishotGuiFactory extends DefaultGuiFactory
{
    public MultishotGuiFactory()
    {
        super(Reference.MOD_ID, getTitle());
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parent)
    {
        return new GuiConfig(parent, getConfigElements(), this.modid, false, false, this.title);
    }

    private static List<IConfigElement> getConfigElements()
    {
        List<IConfigElement> configElements = new ArrayList<IConfigElement>();

        Configuration config = Configs.loadConfigsFromFile();
        configElements.add(new ConfigElement(config.getCategory(Configs.CATEGORY_FREECAMERA)));
        configElements.add(new ConfigElement(config.getCategory(Configs.CATEGORY_GENERIC)));

        return configElements;
    }

    private static String getTitle()
    {
        return GuiConfig.getAbridgedConfigPath(Configs.getConfigFile().toString());
    }
}
