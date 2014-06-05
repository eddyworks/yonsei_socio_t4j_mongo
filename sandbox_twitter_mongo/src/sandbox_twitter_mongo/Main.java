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

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
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
	private static final int defPort = 27017;	//학교 방화벽에 막히는 듯
	private static final int altPort = 8081;

	public static void main(String[] args) {
		try {
			// MongoDB 기초 - http://docs.mongodb.org/manual/tutorial/getting-started/
			// MongoDB Java 활용 기초 - http://docs.mongodb.org/ecosystem/tutorial/getting-started-with-java-driver/
			MongoClient mongoClient = 몽고DB접속();

			DB db = mongoClient.getDB("twitter");

			// messages 컬렉션의 document 갯수 파악 
			DBCollection coll = db.getCollection("messages");
			System.out.println(String.format("messages count - %d",
					coll.count()));

			// messages 컬렉션에 이미 들어 있는 document 조회 
			
			
			// 트위터 API 인증 준비
			ConfigurationBuilder cb = new ConfigurationBuilder();

			cb.setDebugEnabled(true).setOAuthConsumerKey(CONSUMER_KEY)
					.setOAuthConsumerSecret(CONSUMER_SECRET)
					.setOAuthAccessToken(ACCESS_KEY)
					.setOAuthAccessTokenSecret(ACCESS_SECRET);

			Twitter twitter = new TwitterFactory(cb.build()).getInstance();

			try {
				// 박원순으로 검색
				Query query = new Query("박원순");
				// 최대 100건 검색
				query.setCount(3);
				// 참고 http://twitter4j.org/javadoc/twitter4j/Query.html#getCount()

				QueryResult result;
				do {
					result = twitter.search(query);
					System.out.println("Tweets Count: " + result.getCount());

					// 검색된 트윗 가져오기
					List<Status> tweets = result.getTweets();
					
					// 트윗 하나하나 돌아가며 몽고디비 document로 추가
					for (Status tweet : tweets) {
						//
						BasicDBObject doc = new BasicDBObject("userId", tweet.getUser().getId())
						.append("id", tweet.getId())
						.append("createdAt", tweet.getCreatedAt())
				        .append("userScreenName", tweet.getUser().getScreenName())
				        .append("text", tweet.getText());
				        //.append("info", new BasicDBObject("x", 203).append("y", 102));
				coll.insert(doc);
						System.out.println("@"
								+ tweet.getUser().getScreenName() + " - "
								+ tweet.getText());
					}
				} while (false && (query = result.nextQuery()) != null);
				// System.exit(0);
			} catch (TwitterException te) {
				te.printStackTrace();
				System.out.println("Failed to search tweets: "
						+ te.getMessage());
				System.exit(-1);
			}
			
			System.out.println(String.format("messages count - %d",
					coll.count()));

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static MongoClient 몽고DB접속() throws UnknownHostException {
		MongoCredential credentialCR = MongoCredential
				.createMongoCRCredential("javatest", "twitter",
						"javatest".toCharArray()); // 사용자, DB, 비번 순

		MongoClient mongoClient = new MongoClient(new ServerAddress(
				"165.132.98.87", altPort), Arrays.asList(credentialCR));
		return mongoClient;
	}

}
