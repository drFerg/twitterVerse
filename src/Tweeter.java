import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;






public class Tweeter implements StatusListener{
	private SpaceField space;
	
	public Tweeter(SpaceField space){
		super();
		this.space = space;
	}

	 @Override
     public void onStatus(Status status) {
         System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
         space.addTweetPlanet(status.getText());
     }

     @Override
     public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
         System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
     }

     @Override
     public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
         System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
     }

     @Override
     public void onScrubGeo(long userId, long upToStatusId) {
         System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
     }


     @Override
     public void onException(Exception ex) {
         ex.printStackTrace();
     }


}
