package com.indicrowd.uploader;

import org.apache.log4j.BasicConfigurator;

import com.google.gdata.data.youtube.VideoEntry;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		BasicConfigurator.configure();
		YoutubeUploader uploader = new YoutubeUploader();
		
		
		//VideoEntry b = uploader.uploadMovie(1);
		System.out.println(uploader.selectUploadedMovie(1));
		
		uploader.selectAllUploadedMovie();
		
	}

}