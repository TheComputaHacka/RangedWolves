package me.BlahBerrys.rangedwolves;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;

public class RWData {

	
	private static void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				deleteDir(new File(dir, children[i]));
			}
		}
		dir.delete();
	}

	public static void createFiles() throws Exception {
		File configFile = new File(RWMain.getInstance().getDataFolder(), "config.yml");

		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			copy(RWMain.getInstance().getResource("config.yml"), configFile);
			RWMain.log.info(RWMain.getInstance().title + "'config.yml' didn't exist. Created it.");
		}
	}

	public static void saveData() {
		File f = new File(RWMain.getInstance().getDataFolder(), "data.bin");
		try {
			if (!f.exists()) {
				RWMain.getInstance().getDataFolder().mkdirs();
				f.createNewFile();
			}
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeInt(1);
			oos.writeObject(RWListener.WOLVES);
			oos.flush();
			oos.close();
			RWMain.log.info(RWMain.getInstance().title + "Saved data successfully.");
		} catch (Exception e) {
			RWMain.log.info(RWMain.getInstance().title + "Failed to save data.");
			e.printStackTrace();
			return;
		}
	}

	@SuppressWarnings("unchecked")
	public static void loadData() {
		File f = new File(RWMain.getInstance().getDataFolder(), "data.bin");
		if (f.exists()) {
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
				RWMain.getInstance().dataVersion = ois.readInt();
				RWListener.WOLVES = (HashMap<String, Set<UUID>>) ois.readObject();
				RWMain.log.info(RWMain.getInstance().title + "Loaded data successfully.");
			} catch (Exception e) {
				RWMain.log.info(RWMain.getInstance().title + "Failed to load data.");
				e.printStackTrace();
				Bukkit.getServer().getPluginManager().disablePlugin(RWMain.getInstance());
				return;
			}
		}
	}

}
