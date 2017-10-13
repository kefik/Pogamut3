package cz.cuni.amis.pogamut.ut2004.navmeshmaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

public class NavMeshMakerStep_ExportMapHash extends NavMeshMakerStep {

	private File hashFile;

	private String oldHash;
	
	private String newHash;

	private boolean continueWarning = false;

	public NavMeshMakerStep_ExportMapHash(UT2004NavMeshMaker owner) {
		super(owner);
		this.hashFile = owner.getOutputFile(".md5");
	}
	
	public File getHashFile() {
		return hashFile;
	}
	
	public boolean hasOldHash() {
		return hashFile.exists() && hashFile.isFile();
	}
	
	public boolean hasNewHash() {
		return newHash != null;
	}
	
	public String readOldHash() {
		if (oldHash != null) return oldHash;
		try {			
			FileInputStream fis = new FileInputStream(getHashFile());
			oldHash = IOUtils.readLines(fis).get(0);
			fis.close();
		} catch (Exception e) {
			if (owner.isContinueMode()) {
				continueWarning();
			}
			return null;
		}
		return oldHash;
	}
	
	private void continueWarning() {
		if (continueWarning) return;
		warning("Hash for the map does not exist, continuing 'on-blind' assuming the map did not change...");
		continueWarning = true;
	}

	public String readNewHash() {
		if (newHash != null) return newHash;
		try {
			FileInputStream fis = new FileInputStream(owner.getUt2004Map());
			newHash = DigestUtils.md5Hex(fis);
			fis.close();
		} catch (Exception e) {
			severe("Failed to create MD5 hash from: " + owner.getUt2004Map().getAbsolutePath());
			e.printStackTrace();
			return null;
		}
		
		return newHash;	
	}
	
	public boolean hashNotExistOrEquals() {
		return hashesEquals() || oldHash == null; 
	}
	
	public boolean hashesEquals() { 
		readOldHash();
		readNewHash();
		return oldHash != null && newHash != null && oldHash.equals(newHash);		
	}
	
	public boolean backupHash() {		
		if (hashesEquals()) {
			return true;
		}
		if (!hasNewHash()) {
			warning("Cannot backup MD5 hash, hash was not created.");
			return false;
		}
		
		info("Backing MD5 hash of the map into: " + getHashFile().getAbsolutePath());
		
		try {
			if (getHashFile().exists()) getHashFile().delete();
			FileOutputStream fos = new FileOutputStream(getHashFile(), false);
			IOUtils.write(newHash, fos, "utf8");
			fos.close();
		} catch (Exception e) {
			severe("Failed to save MD5 hash into: " + getHashFile().getAbsolutePath());
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
}
