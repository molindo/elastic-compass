package at.molindo.elastic.geonames;

import static org.elasticsearch.node.NodeBuilder.*;
import static org.elasticsearch.common.xcontent.XContentFactory.*;

import java.io.IOException;
import java.util.Date;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.node.Node;

public class Indexer {

	public static void main(String[] args) {

		Node node = nodeBuilder().client(true).node();
		try {
			Client client = node.client();

			XContentBuilder doc;
			try {
				doc = jsonBuilder().startObject().field("user", "kimchy")
						.field("postDate", new Date())
						.field("message", "trying out Elastic Search").endObject();
				IndexResponse response = client.prepareIndex("twitter", "tweet", "1")
						.setSource(doc).execute().actionGet();
				
				System.out.println(response);
				
			} catch (IOException e) {
				e.printStackTrace();
			}

		} finally {
			node.close();
		}
	}
}
