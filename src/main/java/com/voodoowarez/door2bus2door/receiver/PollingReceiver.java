import com.ning.http.client.*;
import java.util.concurrent.Future;


public class JavaCustomReceiver extends Receiver<List<BusPositions>> {

	public void onStart() {

		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		Future<Response> f = asyncHttpClient.prepareGet("http://www.ning.com/").execute();
		Response r = f.get();
	}
 
 	public void onStop() {
	}
}


