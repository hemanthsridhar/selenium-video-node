package com.mooo.aimmac23.hub.videostorage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import org.apache.commons.exec.StreamPumper;
import org.openqa.grid.internal.ExternalSessionKey;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

public class LocalTempFileStore implements IVideoStore {
	
	private static final Logger log = Logger.getLogger(LocalTempFileStore.class.getName());

	
	private static Cache<ExternalSessionKey, File> availableVideos;
	
	static {
		availableVideos = CacheBuilder.newBuilder().maximumSize(200).removalListener(new RemovalListener<ExternalSessionKey, File>() {
			@Override
			public void onRemoval(RemovalNotification<ExternalSessionKey, File> arg0) {
				if(arg0.getValue().delete()) {
					log.info("Deleted recording due to excess videos: " + arg0.getKey());
				}
			}
		}).build();
	}
	

	@Override
	public void storeVideo(InputStream videoStream, String mimeType,
			String sessionId) throws Exception {
		File outputFile = File.createTempFile("screencast", ".webm");
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		try {
			new StreamPumper(videoStream, outputStream).run();
		}
		finally {
			outputStream.close();
		}
		
		availableVideos.put(new ExternalSessionKey(sessionId), outputFile);
		log.info("Successfully retrieved video for session: " + sessionId + " and temporarily stashed it at: " + outputFile);
	
	}

	@Override
	public InputStream retrieveVideo(String sessionId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
