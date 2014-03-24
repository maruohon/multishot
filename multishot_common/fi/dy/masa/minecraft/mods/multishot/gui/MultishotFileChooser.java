/*
package fi.dy.masa.minecraft.mods.multishot.gui;

import java.awt.BorderLayout;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import fi.dy.masa.minecraft.mods.multishot.config.MultishotConfigs;

public class MultishotFileChooser extends JPanel
{
	JFileChooser chooser;
	private MultishotConfigs multishotConfigs;

	public MultishotFileChooser(MultishotConfigs cfg)
	{
		super(new BorderLayout());
		this.multishotConfigs = cfg;
	}

	public String getPath()
	{
		String s = "";
		chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File(this.multishotConfigs.getSavePath()));
		chooser.setDialogTitle("Select Multishot save directory");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		//if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
			System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
		}
		else
		{
			System.out.println("No selection");
		}
		return s;
	}
}
*/