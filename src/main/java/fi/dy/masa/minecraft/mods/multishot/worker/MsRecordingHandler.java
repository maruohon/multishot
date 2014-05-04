package fi.dy.masa.minecraft.mods.multishot.worker;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
		MsState.storeFov(MsClassReference.getMinecraft().gameSettings.fovSetting);

		if (MsClassReference.getMsConfigs().getZoom() != 0)
		{
			MsClassReference.getMinecraft().gameSettings.fovSetting = -((float)MsClassReference.getMsConfigs().getZoom() / 69.0f);
		}

		if (MsClassReference.getMsConfigs().getInterval() > 0)
		{
			MsThread t;
			MsState.resetShotCounter();
			t = new MsThread(	MsClassReference.getMsConfigs().getSavePath(),
								MsClassReference.getMsConfigs().getInterval(),
								MsClassReference.getMsConfigs().getImgFormat());
			MsState.setMultishotThread(t); // FIXME remove
			MsClassReference.setThread(t);
			t.start();
		}
	}

	public static void stopRecording()
	{
		if (MsClassReference.getThread() != null)
		{
			MsClassReference.getThread().setStop();
		}

		// Disable the paused state when the recording ends
		if (MsState.getPaused() == true)
		{
			MsState.setPaused(false);
		}

		MsClassReference.getMinecraft().setIngameFocus();
		// Restore the normal FoV value
		MsClassReference.getMinecraft().gameSettings.fovSetting = MsState.getFov();
		MsClassReference.getTickEvent().reset();
	}

	public static void toggleRecording()
	{
		MsState.toggleRecording();

		if (MsState.getRecording() == true)
		{
			startRecording();
		}
		else
		{
			stopRecording();
		}
	}
}
