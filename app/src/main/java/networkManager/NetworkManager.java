package networkManager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.home.flipcardndroid.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import card.CardView;
import constants.Constants;


public class NetworkManager {
    private Context context;
    private String sUrl = "192.168.0.105:3000/deal";
    private static NetworkManager sharedInstance = null;
    private NetworkManager() {
        context = null;
    }
    private CardView cards[];
    public static NetworkManager getSharedInstance() {
        if (sharedInstance == null) sharedInstance = new NetworkManager();
        return sharedInstance;
    }
    public void setUrl(String url) {
        this.sUrl = url;
    }
    public String getUrl() {
        return this.sUrl;
    }
    public void getCards(CardView cards[], Context context) {
        this.context = context;
        this.cards = cards;
        new GetUrlContentTask().execute("http://" + sUrl);
    }
    public void updateCards(String jsonObj) {
        JSONObject object = null;
        try {
            object = new JSONObject(jsonObj);
            JSONArray array = object.getJSONArray("cards");
            for(int i=0; i<Constants.cardCount; i++) {
                String card = Constants.cardNames[array.optInt(i)];
                this.cards[i].setCard(card);
            }
            this.makeToast("Fetched New Cards");
        }
        catch (Exception e) {
            Log.e("json parse error", e.getMessage());
            try {
                this.makeToast(object.getString("msg"));
            }
            catch (Exception ex) {
                Log.e("json parse error", e.getMessage());
                this.makeToast("Failed to fetch cards");
            }
        }
    }
    private void makeToast(String msg) {
        if (context!= null) Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}

class GetUrlContentTask extends AsyncTask<String, Integer, String> {
    protected String doInBackground(String... urls) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urls[0]);
            connection = (HttpURLConnection) url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String content = "", line;
            while ((line = rd.readLine()) != null) {
                content += line + "\n";
            }
            return content;
        }
        catch (Exception e) {
            Log.e("http error", e.toString());
        }
        finally {
            connection.disconnect();
        }
        return "";
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(String result) {
        // this is executed on the main thread after the process is over
        // update your UI here
        NetworkManager.getSharedInstance().updateCards(result);
    }
}

