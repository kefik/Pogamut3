package cz.cuni.amis.nb.pogamut.base.project.template.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author ik
 */
public class ProjectCreationUtils {
    
    /** Creates a new instance of ProjectCreationUtils */
    public ProjectCreationUtils() {
    }
    
    /**
     * Make common actions according to WizardDescriptor.
     */
    public static void handleWizardDescriptor(WizardDescriptor wiz) {
    }
    
    /** Substitute pattern in file. */
    public static void substituteInFile(File file, Pattern pattern, String strToSubst) throws IOException {
        String str = loadFileToString(file);
        // recreate it
        file.delete();
        file.createNewFile();
        // substitute
        Matcher matcher = pattern.matcher(str);
        String newStr = matcher.replaceAll(strToSubst);
        
        // write it
        BufferedWriter outBuffer = new BufferedWriter(new OutputStreamWriter(new
                FileOutputStream(file)));
        outBuffer.write(newStr, 0, newStr.length());
        outBuffer.close();
    }
    
    /**
     * Loads file to string.
     */
    public static String loadFileToString(File file) throws IOException {
        FileInputStream projectXml = new FileInputStream(file);
        byte[] b = new byte[projectXml.available()];
        projectXml.read(b);
        projectXml.close();
        return new String(b);
    }

    public static void unZipFile(InputStream source, FileObject projectRoot) throws IOException {
        try {
            ZipInputStream str = new ZipInputStream(source);
            ZipEntry entry;
            while ((entry = str.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    FileUtil.createFolder(projectRoot, entry.getName());
                } else {
                    FileObject fo = FileUtil.createData(projectRoot, entry.getName());
                    FileLock lock = fo.lock();
                    try {
                        OutputStream out = fo.getOutputStream(lock);
                        try {
                            FileUtil.copy(str, out);
                        } finally {
                            out.close();
                        }
                    } finally {
                        lock.releaseLock();
                    }
                }
            }
        } finally {
            source.close();
        }
    }
}
