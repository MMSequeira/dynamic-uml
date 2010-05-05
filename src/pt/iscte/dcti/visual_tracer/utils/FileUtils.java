package pt.iscte.dcti.visual_tracer.utils;

/**
 * Visual Tracer - An Application of Java Code Instrumentation using AspectJ 
 * Copyright (C) 2010  
 * Carlos Correia - mail.cefc@gmail.com 
 * Rute Oliveira - rute23@gmail.com
 * Manuel Menezes de Sequeira - manuel.sequeira@iscte.pt
 * 
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils 
{

	public static String getExtension(File file)
	{
		String ext = "";
		int indexBeginExtension = file.getName().lastIndexOf(".");
		if(indexBeginExtension!=-1)		
			ext = file.getName().substring(indexBeginExtension+1);		
		return ext;
	}
	public static boolean copyFileToDir(File inputFile,File outputDir)
	{
		try
		{
			String outputFileName = inputFile.getName();
			int index =1;
			while(existFileInDir(outputFileName, outputDir))
			{
				outputFileName = index+inputFile.getName();
				index++;
			}
			String directory = getDirectoryWithSlash(outputDir.getAbsolutePath());
			File outputFile = new File(directory+outputFileName);

			FileReader in = new FileReader(inputFile);
			FileWriter out = new FileWriter(outputFile);
			int c;

			while ((c = in.read()) != -1)
				out.write(c);

			in.close();
			out.close();
		}
		catch (IOException e) 
		{
			return false;
		}
		return true;
	}

	public static boolean existFileInDir(String fileName,File dir)
	{
		String directory = getDirectoryWithSlash(dir.getAbsolutePath());
		return new File(directory+fileName).exists();
	}
	
	/**
	 * Create a file with name and extension based on file argument in a directory and with a content
	 * @param content - content that file may contain
	 * @param directory - directory to save the file in
	 * @param file - the name and extension of the file
	 * @return
	 */
	public static boolean createFile(String content,String directory,String file,boolean append)
	{
		boolean response = true;
		try
		{
			directory = getDirectoryWithSlash(directory);
			File file_directory = new File(directory);
			if(!file_directory.exists())
				file_directory.mkdirs();
			File file_file = new File(directory+file);
			response = file_file.createNewFile();
			
			BufferedWriter out = new BufferedWriter(new FileWriter(file_file,append));
			out.write(content);
			out.close();
		}
		catch(Exception ex)
		{
			response = false;
			//write log
		}
		return response;
	}
	
	
	/**
	 * Create a file with name and extension based on file argument in a directory and with a content
	 * @param content - content that file may contain
	 * @param directory - directory to save the file in
	 * @param file - the name and extension of the file
	 * @return
	 * @throws IOException 
	 */
	public static String getTextFile(File file) throws IOException
	{
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
    	String stringReader = "";
    	while (bufferedReader.ready()) 
    		stringReader += bufferedReader.readLine() + "\n";
    	bufferedReader.close();
    	return stringReader;
	}
	
	/**
	 * Create a file with name and extension based on file argument in a directory and with a content
	 * @param content - content that file may contain
	 * @param directory - directory to save the file in
	 * @param file - the name and extension of the file
	 * @return
	 * @throws IOException 
	 */
	public static boolean saveTextFile(String content,File file,boolean append) throws IOException
	{
		boolean response = true;			
		BufferedWriter out = new BufferedWriter(new FileWriter(file,append));
		out.write(content);
		out.close();
		return response;
	}
	
	/**
	 * Validate if this string have final slash
	 * e.g. C:\folder\ or C:\folder/
	 * @param directory
	 * @return
	 */
	public static String getDirectoryWithSlash(String directory)
	{
		if(directory.lastIndexOf("\\") != (directory.length()-1) && directory.lastIndexOf("/") != (directory.length()-1))
			directory+="\\";
		return directory;
	}

}

