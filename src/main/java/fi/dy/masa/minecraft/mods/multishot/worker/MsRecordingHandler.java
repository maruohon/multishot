package fi.dy.masa.minecraft.mods.multishot.worker;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fi.dy.masa.minecraft.mods.multishot.state.MsClassReference;
import fi.dy.masa.minecraft.mods.multishot.state.MsState;

@SideOnly(Side.CLIENT)
public class MsRecordingHandler
{

	private void startRecording()
	{
		MsState.storeFov(MsClassReference.getMinecraft().gameSettings.fovSetting);

		if (MsClassReference.getMultishotConfigs().getZoom() != 0)
		{
			MsClassReference.getMinecraft().gameSettings.fovSetting = -((float)MsClassReference.getMultishotConfigs().getZoom() / 69.0f);
		}

		if (MsClassReference.getMultishotConfigs().getInterval() > 0)
		{
			MsThread t;
			MsState.resetShotCounter();
			t = new MsThread(	MsClassReference.getMultishotConfigs().getSavePath(),
								MsClassReference.getMultishotConfigs().getInterval(),
								MsClassReference.getMultishotConfigs().getImgFormat());
			MsState.setMultishotThread(t); // FIXME remove
			MsClassReference.setMultishotThread(t);
			t.start();
		}
	}

	private void stopRecording()
	{
		if (MsClassReference.getMultishotThread() != null)
		{
			MsClassReference.getMultishotThread().setStop();
		}

		// Disable the paused state when the recording ends
		if (MsState.getPaused() == true)
		{
			MsState.setPaused(false);
		}

		MsClassReference.getMinecraft().setIngameFocus();
		// Restore the normal FoV value
		MsClassReference.getMinecraft().gameSettings.fovSetting = MsState.getFov();
	}

	private void toggleRecording()
	{
		MsState.toggleRecording();

		if (MsState.getRecording() == true)
		{
			this.startRecording();
		}
		else
		{
			this.stopRecording();
		}
	}
}
