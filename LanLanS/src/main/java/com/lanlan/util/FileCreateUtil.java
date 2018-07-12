package com.lanlan.util;

import java.io.File;

public class FileCreateUtil {

	public static void mkDir(File file) {
		File parentFile = file.getParentFile();
		if(!parentFile.exists()) {
			FileCreateUtil.mkDir(parentFile);
		}else {
			if(file.isDirectory()) {
				file.mkdir();
			}
		}
	}
	
}
