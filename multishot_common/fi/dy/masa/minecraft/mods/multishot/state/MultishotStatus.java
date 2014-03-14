package fi.dy.masa.minecraft.mods.multishot.state;

public class MultishotStatus {
	private boolean isRecording = false;
	private boolean isMoving = false;
	private boolean isPaused = false;
	private boolean isGUIHidden = false;
	private boolean isControlsLocked = false;

	public boolean getIsRecording()
	{
		return isRecording;
	}
	
	public boolean getIsMoving()
	{
		return isMoving;
	}
	
	public boolean getIsPaused()
	{
		return isPaused;
	}
	
	public boolean getIsHidden()
	{
		return isGUIHidden;
	}

	public boolean getIsLocked()
	{
		return isControlsLocked;
	}


	public void setIsRecording(boolean par1)
	{
		isRecording = par1;
	}
	
	public void setIsMoving(boolean par1)
	{
		isMoving = par1;
	}
	
	public void setIsPaused(boolean par1)
	{
		isPaused = par1;
	}
	
	public void setIsHidden(boolean par1)
	{
		isGUIHidden = par1;
	}

	public void setIsLocked(boolean par1)
	{
		isControlsLocked = par1;
	}
}
