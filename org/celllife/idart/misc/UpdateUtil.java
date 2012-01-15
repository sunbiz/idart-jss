package org.celllife.idart.misc;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class UpdateUtil {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length < 2)
			throw new IllegalArgumentException(
			"Expecting path to updates folder and new to copy installer to.");

		String updatePath = args[0];
		System.out.println("updatepath: " + updatePath);
		File updateDir = new File(updatePath);
		if (!updateDir.exists() || !updateDir.isDirectory())
			throw new IllegalArgumentException(
					"Supplied path does not exist or is not a directory: "
					+ updatePath);

		String newPath = args[1];
		System.out.println("newPath: " + newPath);
		File newFile = new File(newPath);
		if (newFile.exists()) {
			newFile.delete();
		}

		File[] listFiles = updateDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("idart") && name.endsWith("jar");
			}
		});

		if (listFiles.length == 0) {
			System.err.println("No installers found");
			System.exit(1);
		}

		List<File> files = Arrays.asList(listFiles);
		Collections.sort(files, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				// reverse sort so that newest is first in list
				return o2.getName().compareTo(o1.getName());
			}
		});

		System.out.println("All files:");
		for (File file : files) {
			System.out.println("  " + file.getName());
		}

		System.out.println("Selected file:");
		File selectedFile = files.get(0);
		System.out.println("  " + selectedFile.getName());

		System.out.println("Copying file to new location: "
				+ newFile.getAbsolutePath());
		FileUtils.copyFile(selectedFile, newFile);
	}

}
