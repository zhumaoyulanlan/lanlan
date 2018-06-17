package com.lanlan.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;  
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;  
import java.util.Enumeration;  
import java.util.List;  
import java.util.jar.JarEntry;  
import java.util.jar.JarFile;

import org.junit.Test;  

/**
 * @see https://blog.csdn.net/wangpeng047/article/details/8206427
 * @author other 摘自博客
 * @date 2018年6月13日
 */
public class PackageUtil {  

	@Test
	public static void test() throws Exception {  
		String packageName = "com.wang.vo.request.hotel";  
		List<String> classNames = getClassName(packageName, false);  
		if (classNames != null) {  
			for (String className : classNames) {  
				System.out.println(className);  
			}  
		}  
	}  

	/** 
	 * 获取某包下（包括该包的所有子包）所有类 
	 * @param packageName 包名 
	 * @return 类的完整名称 
	 * @throws UnsupportedEncodingException 
	 */  
	public static List<String> getClassName(String packageName) throws UnsupportedEncodingException {  
		return getClassName(packageName, true);  
	}  

	/** 
	 * 获取某包下所有类 
	 * @param packageName 包名 
	 * @param childPackage 是否遍历子包 
	 * @return 类的完整名称 
	 * @throws UnsupportedEncodingException 
	 */  
	public static List<String> getClassName(String packageName, boolean childPackage) throws UnsupportedEncodingException {  
		List<String> fileNames = null;  
		ClassLoader loader = Thread.currentThread().getContextClassLoader();  
		String packagePath = packageName.replace(".", File.separator);  
		URL url =loader.getResource(packagePath);  
		if (url != null) {  
			String type = url.getProtocol();  
			if (type.equals("file")) {  
				fileNames = getClassNameByFile(URLDecoder.decode(url.getPath(),"utf-8"), null, childPackage);  
			} else if (type.equals("jar")) {  
				fileNames = getClassNameByJar(URLDecoder.decode(url.getPath(),"utf-8"), childPackage);  
			}  
		} else {  
			//修改写法
			fileNames=getClassNameByJars(System.getProperty("java.class.path").split(":"), packagePath, childPackage);
			//以下原代码写法
			//fileNames = getClassNameByJars(((URLClassLoader) loader).getURLs(), packagePath, childPackage);  
		}  
		//处理fileName去除截至到class的路径
		//String classBasePath = URLDecoder.decode(loader.getResource("").getPath(),"utf-8");
		
		
		return fileNames;  
	}  

	/** 
	 * 从项目文件获取某包下所有类 
	 * @param filePath 文件路径 
	 * @param className 类名集合 
	 * @param childPackage 是否遍历子包 
	 * @return 类的完整名称 
	 * @throws UnsupportedEncodingException 
	 */  
	private static List<String> getClassNameByFile(String filePath, List<String> className, boolean childPackage) throws UnsupportedEncodingException {  
		List<String> myClassName = new ArrayList<String>();  
		File file = new File(filePath);  
		File[] childFiles = file.listFiles();  
		for (File childFile : childFiles) {  
			if (childFile.isDirectory()) {  
				if (childPackage) {  
					myClassName.addAll(getClassNameByFile(childFile.getPath(), myClassName, childPackage));  
				}  
			} else {  
				String childFilePath = childFile.getPath();  
				if (childFilePath.endsWith(".class")) {  
					//childFilePath = childFilePath.substring(childFilePath.indexOf("\\classes") + 9, childFilePath.lastIndexOf("."));
					int beginIndex = URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource("").getPath(),"utf-8").length();
					childFilePath = childFilePath.substring(beginIndex, childFilePath.lastIndexOf(".class"));  
					childFilePath = childFilePath.replace(File.separator, ".");//此处有修改  
					myClassName.add(childFilePath);  
				}  
			}  
		}  

		return myClassName;  
	}  

	/** 
	 * 从jar获取某包下所有类 
	 * @param jarPath jar文件路径 
	 * @param childPackage 是否遍历子包 
	 * @return 类的完整名称 
	 */  
	private static List<String> getClassNameByJar(String jarPath, boolean childPackage) {  
		List<String> myClassName = new ArrayList<String>();  
		String[] jarInfo = jarPath.split("!");  
		String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf(File.separator));  
		String packagePath = jarInfo[1].substring(1);  
		try(JarFile jarFile = new JarFile(jarFilePath)) {  
			Enumeration<JarEntry> entrys = jarFile.entries();  
			while (entrys.hasMoreElements()) {  
				JarEntry jarEntry = entrys.nextElement();  
				String entryName = jarEntry.getName();  
				if (entryName.endsWith(".class")) {  
					if (childPackage) {  
						if (entryName.startsWith(packagePath)) {  
							entryName = entryName.replace(File.separator, ".").substring(0, entryName.lastIndexOf("."));  
							myClassName.add(entryName);  
						}  
					} else {  
						int index = entryName.lastIndexOf(File.separator);  
						String myPackagePath;  
						if (index != -1) {  
							myPackagePath = entryName.substring(0, index);  
						} else {  
							myPackagePath = entryName;  
						}  
						if (myPackagePath.equals(packagePath)) {  
							entryName = entryName.replace(File.separator, ".").substring(0, entryName.lastIndexOf("."));  
							myClassName.add(entryName);  
						}  
					}  
				}  
			}  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
		return myClassName;  
	}  

	/** 
	 * 从所有jar中搜索该包，并获取该包下所有类 
	 * @param urls URL集合 
	 * @param packagePath 包路径 
	 * @param childPackage 是否遍历子包 
	 * @return 类的完整名称 
	 */  
	private static List<String> getClassNameByJars(String[] urls, String packagePath, boolean childPackage) {  
		List<String> myClassName = new ArrayList<String>();  
		if (urls != null) {  
			for(String urlPath:urls) {
				//以下注释是原代码写法.ps:另将urls的类型从URL[]改称String[]
				//for (int i = 0; i < urls.length; i++) {  
				//URL url = urls[i];  
				//String urlPath = url.getPath();  
				// 不必搜索classes文件夹  
				if (urlPath.endsWith("classes/")) {  
					continue;  
				}  
				String jarPath = urlPath + "!/" + packagePath;  
				myClassName.addAll(getClassNameByJar(jarPath, childPackage));  
			}  
		}  
		return myClassName;  
	}  
}  

