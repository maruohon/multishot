package fi.dy.masa.minecraft.mods.multishot.worker;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.config.MsConfigs;
import fi.dy.masa.minecraft.mods.multishot.state.MsClassReference;
import fi.dy.masa.minecraft.mods.multishot.state.MsState;

@SideOnly(Side.CLIENT)
public class MsRecordingHandler
{
	public MsRecordingHandler()
	{
	}

	public static void startRecording()
	{
		MsConfigs mscfg = MsClassReference.getMsConfigs();
		Minecraft mc = Minecraft.getMinecraft();

		MsState.storeFov(mc.gameSettings.fovSetting);

		if (mscfg.getZoom() != 0)
		{
			// -160 - 160 is somewhat "sane"
			mc.gameSettings.fovSetting = 70.0f - (float)mscfg.getZoom() / 100.0f * 70.0f;
		}

		if (mscfg.getInterval() > 0)
		{
			MsThread t;
			MsState.resetShotCounter();
			t = new MsThread(mscfg.getSavePath(), mscfg.getInterval(), mscfg.getImgFormat());
			MsState.setMultishotThread(t); // FIXME remove
			MsClassReference.setThread(t);
			t.start();
		}
		MsState.setRecording(true);
	}

	public static void stopRecording()
	{
		Minecraft mc = Minecraft.getMinecraft();

		if (MsClassReference.getThread() != null)
		{
			MsClassReference.getThread().setStop();
			MsSaveScreenshot.clearInstance();
		}

		MsState.setRecording(false);
		MsState.setPaused(false);
		MsClassReference.getTickEvent().resetScheduler();
		mc.setIngameFocus();
		// Restore the normal FoV value
		mc.gameSettings.fovSetting = MsState.getFov();
	}

	public static void toggleRecording()
	{
		if (MsState.getRecording() == true)
		{
			stopRecording();
		}
		else
		{
			startRecording();
		}
	}
}
