package com.indicrowd.uploader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class YoutubeUploaderProperties {
	
	private static YoutubeUploaderProperties instance = null;
	private static Logger logger = Logger.getLogger(YoutubeUploaderProperties.class);
	private Properties realProperties;
	private String clientID;
	private String developer_key;
	private String userID;
	private String userPassword;
	private String defaultSavedVideoDirectory;
	
	private YoutubeUploaderProperties()
	{
		realProperties = new Properties();
		
		try {
			realProperties.load(new FileInputStream("youtube.properties"));
			//realProperties.load(YoutubeUploaderProperties.class.getClassLoader().getResourceAsStream("youtube.properties"));
			
			clientID = realProperties.getProperty("clientID");
			developer_key = realProperties.getProperty("developer_key");
			userID = realProperties.getProperty("userID");
			userPassword = realProperties.getProperty("userPassword");
			defaultSavedVideoDirectory = realProperties.getProperty("defaultSavedVideoDirectory");
			
		} catch(IOException ex) {
			logger.error("can't open youtube.parameters file.");
		}
	}

	public String getClientID()
	{
		return clientID;
	}
	
	public String getDeveloper_key() {
		return developer_key;
	}

	public String getUserID() {
		return userID;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public String getDefaultSavedVideoDirectory() {
		return defaultSavedVideoDirectory;
	}

	public static YoutubeUploaderProperties getInstance()
	{
		if (instance == null)
		{
			instance = new YoutubeUploaderProperties();
		}
		
		return instance;
	}
	
}
