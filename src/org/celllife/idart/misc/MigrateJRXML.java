package org.celllife.idart.misc;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.xml.JRXmlWriter;

/**
 * The purpose of this app is to convert report designs from a older jrxml format to a newer format.
 * The converter assumes that the new jasper reports library is backwards compatible and able to read the
 * newer jrxml file format. It will open all jrxml files under a given directory and write them again using
 * the current serializer for jrxml.
 *
 * @author toesterdahl, Torbjörn Österdahl, ultra-marine.org 2009
 */
public class MigrateJRXML {

    private final static String JRXML_FILEENDING = ".jrxml";

    private void run(File rootDirectory) throws Exception {
        System.out.println("Processing all jrxml files under the root: " + rootDirectory.getCanonicalPath());

        if (rootDirectory.isDirectory()) {
            processFile(rootDirectory);
        } else {
            // Convert it to a directory if it isn't
            processFile(new File(rootDirectory.getCanonicalPath() + "\""));
        }
    }

    private void processFile(File file) throws Exception {
        System.out.println("Searching directory: " + file.getCanonicalPath());

        // Find jrxml files
        File[] jrxmlFiles = file.listFiles(new FilenameFilter() {
            @Override
			public boolean accept(File dir, String fileName) {
                return fileName.endsWith(JRXML_FILEENDING);
            }
        });

        // compile all jrxml files
        if (jrxmlFiles != null) {
            for (File jrxmlFile : jrxmlFiles) {
                try {
                    System.out.println("Compiling report file: " + jrxmlFile.getCanonicalPath());
                    convertReport(jrxmlFile);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JRException e) {
                    e.printStackTrace();
                }
            }
        }

        // Find additional directories
        File[] dirs = file.listFiles(new FileFilter() {
            @Override
			public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        // recurse into all directories
        if (dirs != null) {
            for (File dir : dirs) {
                processFile(dir);
            }
        }

    }

    private void convertReport(File jrxmlFile) throws JRException, IOException {
        JasperDesign jasperDesign = JRXmlLoader.load(jrxmlFile);
        JRXmlWriter.writeReport(jasperDesign, jrxmlFile.getCanonicalPath(), "UTF-8");
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        MigrateJRXML app = new MigrateJRXML();

        File rootDirectory;
        if (args.length == 0) {
            // using default directory
        	throw new IllegalArgumentException("Please supply reports folder");
        } else if (args.length == 1) {
            rootDirectory = new File(args[0]);
        } else {
            System.out.println("invalid path");
            throw new RuntimeException("Invalid Path");
        }

        app.run(rootDirectory);

        System.out.println("Finished successfully - Exiting");
    }

}