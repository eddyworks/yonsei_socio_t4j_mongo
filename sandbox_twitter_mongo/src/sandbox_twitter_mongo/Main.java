package sandbox_twitter_mongo;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class Main {
	/* http://twitter4j.org/en/index.html */
	/* https://apps.twitter.com/ 페이지에서 트위터 앱 등록 신청 후 키를 얻어야 합니다. */
	private static final String CONSUMER_KEY = "MxWfsQxFA292lQDspXG6yloko";
	private static final String CONSUMER_SECRET = "GdV0MQENLF7xC5zROcqBFzGvwU2Vx9ccj0hFdV4DGkjTBW6TaG";
	private static final String ACCESS_KEY = "1932804632-f0oRqMckQ7OrNiCmjS5sqmneFHy5VcMsnQDMsBu";
	private static final String ACCESS_SECRET = "8I3K11atpMKYqZkKeH0K8bjehQYJ1Oz4GZX2gTtByO7FD";

	public static void main(String[] args) {
		try {
			ConfigurationBuilder cb = new ConfigurationBuilder();

			cb.setDebugEnabled(true).setOAuthConsumerKey(CONSUMER_KEY)
					.setOAuthConsumerSecret(CONSUMER_SECRET)
					.setOAuthAccessToken(ACCESS_KEY)
					.setOAuthAccessTokenSecret(ACCESS_SECRET);

			Twitter twitter = new TwitterFactory(cb.build()).getInstance();
			try {
				Query query = new Query("박원순");
				QueryResult result;
				do {
					result = twitter.search(query);
					List<Status> tweets = result.getTweets();
					for (Status tweet : tweets) {
						System.out.println("@"
								+ tweet.getUser().getScreenName() + " - "
								+ tweet.getText());
					}
				} while ((query = result.nextQuery()) != null);
				System.exit(0);
			} catch (TwitterException te) {
				te.printStackTrace();
				System.out.println("Failed to search tweets: "
						+ te.getMessage());
				System.exit(-1);
			}

			MongoCredential credential = MongoCredential
					.createMongoCRCredential("sociology1", "twitter",
							"tkghlgkrrhk`12".toCharArray());

			MongoClient mongoClient = new MongoClient(new ServerAddress(
					"165.132.98.87"), Arrays.asList(credential));

			System.out.println(String.format("auth result - %s",
					mongoClient.getVersion()));

			DB db = mongoClient.getDB("twitter");

			Set<String> list = db.getCollectionNames();

			System.out.println(String.format("getCollectionNames count - %d",
					list.size()));

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
