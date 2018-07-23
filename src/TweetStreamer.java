/*
 * Copyright 2007 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;



/**
 * <p>This is a code example of Twitter4J Streaming API - sample method support.<br>
 * Usage: java twitter4j.examples.PrintSampleStream<br>
 * </p>
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public class TweetStreamer extends StatusAdapter{
	private final static String TOKEN = "47666014-pJkt2YbLa0HUpdGWfjy40Q6OtWbs0tTs1V9uWuM";
	private final static String TOKENSECRET = "hDAitQ6imIv64TTOPHoWQn1kaz0ZYMrMbYhISzuAUw";
	private static StatusListener listener;
	private static TwitterStream twitterStream;
	private static ArrayList<String> follow;
	private static SpaceField space;

	public TweetStreamer (SpaceField space){
		super();
		this.space = space;
	}
	
	
	public void run(String keyword) {
		try {
			init();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		twitterStream.addListener(listener);
		follow = new ArrayList<String>();
        follow.add(keyword);
		twitterStream.filter(new FilterQuery(0,new long[0],follow.toArray(new String[follow.size()])));
      
        System.out.println("DOne");
        
	}
	
	private static void init() throws IOException{
		 follow = new ArrayList<String>();
		 twitterStream = new TwitterStreamFactory().getInstance();
		 twitterStream.setOAuthConsumer("EzzNkpKbi1rN0l53P7j5g", "F9ksl9cwG5YGL3TtYm3Ivrc8KYnTXeQHr8wx0cMxq20");
		 twitterStream.setOAuthAccessToken(new AccessToken(TOKEN,TOKENSECRET));
		 listener = new Tweeter(space);
//		 listener = new StatusListener() {
//	            @Override
//	            public void onStatus(Status status) {
//	                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
//	            }
//
//	            @Override
//	            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
//	                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
//	            }
//
//	            @Override
//	            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
//	                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
//	            }
//
//	            @Override
//	            public void onScrubGeo(long userId, long upToStatusId) {
//	                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
//	            }
//
//
//	            @Override
//	            public void onException(Exception ex) {
//	                ex.printStackTrace();
//	            }
//	        };
	     
	}
	
	public void addToQuery(String keyword){
		follow.add(keyword);
		System.out.println("Current queries: " +follow);
		twitterStream.cleanUp();
		twitterStream.filter(new FilterQuery(0,new long[0],follow.toArray(new String[follow.size()])));
	}
	public void changeQuery(String keyword){
		follow.clear();
		follow.add(keyword);
		twitterStream.cleanUp();
		twitterStream.filter(new FilterQuery(0,new long[0],follow.toArray(new String[follow.size()])));
	}
	
	
	
	
	
	
	
}
