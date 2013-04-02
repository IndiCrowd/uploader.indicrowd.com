package com.indicrowd.uploader;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.gdata.client.Query;
import com.google.gdata.client.youtube.YouTubeQuery;
import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.Category;
import com.google.gdata.data.extensions.Rating;
import com.google.gdata.data.geo.impl.GeoRssWhere;
import com.google.gdata.data.media.MediaFileSource;
import com.google.gdata.data.media.mediarss.MediaCategory;
import com.google.gdata.data.media.mediarss.MediaDescription;
import com.google.gdata.data.media.mediarss.MediaKeywords;
import com.google.gdata.data.media.mediarss.MediaPlayer;
import com.google.gdata.data.media.mediarss.MediaThumbnail;
import com.google.gdata.data.media.mediarss.MediaTitle;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.data.youtube.VideoFeed;
import com.google.gdata.data.youtube.YouTubeMediaContent;
import com.google.gdata.data.youtube.YouTubeMediaGroup;
import com.google.gdata.data.youtube.YouTubeMediaRating;
import com.google.gdata.data.youtube.YouTubeNamespace;
import com.google.gdata.data.youtube.YtPublicationState;
import com.google.gdata.data.youtube.YtStatistics;
import com.google.gdata.util.AuthenticationException;

public class YoutubeUploader {

	private static Logger logger = Logger.getLogger(YoutubeUploader.class);
	private YoutubeUploaderProperties properties = YoutubeUploaderProperties.getInstance();
	private YouTubeService service;
	
	public YoutubeUploader() throws AuthenticationException
	{
		try {
			service = athenticationYoutube();
		} catch (AuthenticationException ae) {
			logger.error(ae);
			
			throw ae;
		}
	}
	
	private YtPublicationState getUploadedMovieState(VideoEntry entry)
	{
		if(entry.isDraft()) {
			System.out.println("Video is not live");
			YtPublicationState pubState = entry.getPublicationState();
			if(pubState.getState() == YtPublicationState.State.PROCESSING) {
				logger.info("Video is still being processed.");
			}
			else if(pubState.getState() == YtPublicationState.State.REJECTED) {
				logger.info("Video has been rejected because: " + pubState.getDescription());
			}
			else if(pubState.getState() == YtPublicationState.State.FAILED) {
				logger.info("Video failed uploading because: " + pubState.getDescription());
			}
			return pubState;
		}
		return null;
	}
	
	public VideoEntry uploadMovie(long concertId)
	{

		try{
			VideoEntry newEntry = generateNewEntry(concertId);
			String uploadUrl =
			  "http://uploads.gdata.youtube.com/feeds/api/users/default/uploads";

			VideoEntry createdEntry = service.insert(new URL(uploadUrl), newEntry);

			return createdEntry;
		} catch(Exception ae) {
			logger.error(ae);
			
			return null;
		}
	}

	
	public void deleteMovie(long concertId)
	{
		List<VideoEntry> videoEntries = selectUploadedMovie(concertId);

		try {
			for(VideoEntry videoEntry : videoEntries)
			{
					videoEntry.delete();
				
			}
		} catch(Exception ex) {
			logger.warn(ex);
		}
	}
	
	public List<VideoEntry> selectUploadedMovie(long concertId)
	{
		try{

			YouTubeQuery query = new YouTubeQuery(new URL("http://gdata.youtube.com/feeds/api/videos"));
			Query.CategoryFilter categoryFilter = new Query.CategoryFilter();
			categoryFilter.addCategory(new Category(YouTubeNamespace.DEVELOPER_TAG_SCHEME, "conId"+concertId));
			      
			query.addCategoryFilter(categoryFilter);

			VideoFeed videoFeed = service.query(query, VideoFeed.class);
			
			return videoFeed.getEntries();
			
		} catch(Exception ae) {
			logger.error(ae);
			
			return null;
		}		
	}
	
	public void selectAllUploadedMovie()
	{
		try{
			String feedUrl = "http://gdata.youtube.com/feeds/api/users/default/uploads";
			VideoFeed videoFeed = service.getFeed(new URL(feedUrl), VideoFeed.class);
			printVideoFeed(videoFeed, true);
			
		} catch(Exception ae) {
			logger.error(ae);
		}		
	}


	private YouTubeService athenticationYoutube()
			throws AuthenticationException {
		YouTubeService service = new YouTubeService(properties.getClientID(), 
				properties.getDeveloper_key());
		service.setUserCredentials(properties.getUserID(), properties.getUserPassword());
		return service;
	}



	private VideoEntry generateNewEntry(long concertId) {
		String filePath = properties.getDefaultSavedVideoDirectory() + "/" + concertId + ".flv";
		VideoEntry newEntry = new VideoEntry();

		YouTubeMediaGroup mg = newEntry.getOrCreateMediaGroup();
		mg.setTitle(new MediaTitle());
		mg.getTitle().setPlainTextContent("My Test Movie3");
		mg.addCategory(new MediaCategory(YouTubeNamespace.DEVELOPER_TAG_SCHEME, "conId"+concertId));
		mg.addCategory(new MediaCategory(YouTubeNamespace.CATEGORY_SCHEME, "Autos"));
		mg.setKeywords(new MediaKeywords());
		mg.getKeywords().addKeyword("cars");
		mg.getKeywords().addKeyword("funny");
		mg.setDescription(new MediaDescription());
		mg.getDescription().setPlainTextContent("My description");
		mg.setPrivate(true);
		
		MediaFileSource ms = new MediaFileSource(new File(filePath), "video/x-flv");
		newEntry.setMediaSource(ms);
		return newEntry;
	}
	
	public static void printVideoFeed(VideoFeed videoFeed, boolean detailed) {
		for(VideoEntry videoEntry : videoFeed.getEntries() ) {
			printVideoEntry(videoEntry, detailed);
		}
	}
	
	public static void printVideoEntry(VideoEntry videoEntry, boolean detailed) {
		System.out.println("Title: " + videoEntry.getTitle().getPlainText());

		if(videoEntry.isDraft()) {
			System.out.println("Video is not live");
			YtPublicationState pubState = videoEntry.getPublicationState();
			if(pubState.getState() == YtPublicationState.State.PROCESSING) {
				System.out.println("Video is still being processed.");
			}
			else if(pubState.getState() == YtPublicationState.State.REJECTED) {
				System.out.print("Video has been rejected because: ");
				System.out.println(pubState.getDescription());
				System.out.print("For help visit: ");
				System.out.println(pubState.getHelpUrl());
			}
			else if(pubState.getState() == YtPublicationState.State.FAILED) {
				System.out.print("Video failed uploading because: ");
				System.out.println(pubState.getDescription());
				System.out.print("For help visit: ");
				System.out.println(pubState.getHelpUrl());
			}
		}

		if(videoEntry.getEditLink() != null) {
			System.out.println("Video is editable by current user.");
		}

		if(detailed) {

			YouTubeMediaGroup mediaGroup = videoEntry.getMediaGroup();
			System.out.println("Uploaded by: " + mediaGroup.getUploader());
			
			System.out.println("Video ID: " + mediaGroup.getVideoId());
			System.out.println("Description: " + 
					mediaGroup.getDescription().getPlainTextContent());

			MediaPlayer mediaPlayer = mediaGroup.getPlayer();
			System.out.println("Web Player URL: " + mediaPlayer.getUrl());
			MediaKeywords keywords = mediaGroup.getKeywords();
			System.out.print("Keywords: ");
			for(String keyword : keywords.getKeywords()) {
				System.out.print(keyword + ",");
			}
			
			Set<Category> categories = videoEntry.getCategories();
			for(Category category : categories)
			{
				System.out.println(category);
			}

			GeoRssWhere location = videoEntry.getGeoCoordinates();
			if(location != null) {
				System.out.println("Latitude: " + location.getLatitude());
				System.out.println("Longitude: " + location.getLongitude());
			}

			Rating rating = videoEntry.getRating();
			if(rating != null) {
				System.out.println("Average rating: " + rating.getAverage());
			}

			YtStatistics stats = videoEntry.getStatistics();
			if(stats != null ) {
				System.out.println("View count: " + stats.getViewCount());
			}
			System.out.println();

			System.out.println("\tThumbnails:");
			for(MediaThumbnail mediaThumbnail : mediaGroup.getThumbnails()) {
				System.out.println("\t\tThumbnail URL: " + mediaThumbnail.getUrl());
				System.out.println("\t\tThumbnail Time Index: " +
						mediaThumbnail.getTime());
				System.out.println();
			}

			System.out.println("\tMedia:");
			for(YouTubeMediaContent mediaContent : mediaGroup.getYouTubeContents()) {
				System.out.println("\t\tMedia Location: "+ mediaContent.getUrl());
				System.out.println("\t\tMedia Type: "+ mediaContent.getType());
				System.out.println("\t\tDuration: " + mediaContent.getDuration());
				System.out.println();
			}

			for(YouTubeMediaRating mediaRating : mediaGroup.getYouTubeRatings()) {
				System.out.println("Video restricted in the following countries: " +
						mediaRating.getCountries().toString());
			}
			
		}
	}
}
