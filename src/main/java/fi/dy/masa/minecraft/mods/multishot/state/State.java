package fi.dy.masa.minecraft.mods.multishot.state;

import fi.dy.masa.minecraft.mods.multishot.config.Configs;

public class State
{
    private static boolean stateRecording = false;
    private static boolean stateMotion = false;
    private static boolean statePaused = false;
    private static boolean stateGuiHidden = false;
    private static boolean stateControlsLocked = false;
    private static int shotCounter = 1;

    public static void setStateFromConfigs()
    {
        setControlsLocked(Configs.getControlsLocked());
        setHideGui(Configs.getHideGui());
    }

    public static boolean getRecording()
    {
        return stateRecording;
    }

    public static boolean getMotion()
    {
        return stateMotion;
    }

    public static boolean getPaused()
    {
        return statePaused;
    }

    public static boolean getHideGui()
    {
        return stateGuiHidden;
    }

    public static boolean getControlsLocked()
    {
        return stateControlsLocked;
    }

    public static void setRecording(boolean par1)
    {
        stateRecording = par1;
    }

    public static void setMotion(boolean par1)
    {
        stateMotion = par1;
    }

    public static void setPaused(boolean par1)
    {
        statePaused = par1;
    }

    public static void setHideGui(boolean par1)
    {
        stateGuiHidden = par1;
    }

    public static void setControlsLocked(boolean par1)
    {
        stateControlsLocked = par1;
    }

    public static void toggleRecording()
    {
        stateRecording = ! stateRecording;
    }

    public static void toggleMotion()
    {
        stateMotion = ! stateMotion;
    }

    public static void togglePaused()
    {
        statePaused = ! statePaused;
    }

    public static void toggleHideGui()
    {
        stateGuiHidden = ! stateGuiHidden;
    }

    public static void toggleControlsLocked()
    {
        stateControlsLocked = ! stateControlsLocked;
    }

    public static void setShotCounter(int c)
    {
        shotCounter = c;
    }

    public static void resetShotCounter()
    {
        shotCounter = 1;
    }

    public static int getShotCounter()
    {
        return shotCounter;
    }

    public static void incrementShotCounter()
    {
        shotCounter++;
    }
}
