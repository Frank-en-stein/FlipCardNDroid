package networkManager;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import android.net.*;
import android.net.wifi.WifiManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import card.CardView;
import constants.Constants;

import static java.lang.String.valueOf;


public class NetworkManager {
    private Context context;
    private String sUrl = "192.168.0.105:3000";
    private static NetworkManager sharedInstance = null;
    private NetworkManager() {
        context = null;
    }
    private CardView cards[];
    private int gameId = 0;
    private WifiManager wifii;
    public static NetworkManager getSharedInstance() {
        if (sharedInstance == null) sharedInstance = new NetworkManager();
        return sharedInstance;
    }
    public void setUrl(String url) {
        this.sUrl = url;
    }
    public String getUrl() {
        wifii= (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo d=wifii.getDhcpInfo();
        WifiInfo wInfo = wifii.getConnectionInfo();
        return intToIp(d.gateway);
    }
    public void getCards(CardView cards[], Context context) {
        this.context = context;
        this.cards = cards;
        this.sUrl = getUrl() + ":3000";
        executeTask();
    }
    private void executeTask() {
        new GetUrlContentTask().execute("http://" + sUrl + "/deal/" + gameId);
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
            this.gameId = object.getInt("gameId");
            this.makeToast("Fetched New Cards" + gameId);
            for (CardView card : cards) card.flipBack();
        }
        catch (Exception e) {
            Log.e("json parse error", e.getMessage());
            try {
                String msg = object.getString("msg");
                if (!msg.toLowerCase().equals("same game")) this.makeToast(msg);
            }
            catch (Exception ex) {
                Log.e("json parse error", e.getMessage());
                this.makeToast("Failed to fetch cards");
            }
        }
        finally {
            executeTask();
        }
    }
    private void makeToast(String msg) {
        if (context!= null) Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    private String intToIp(int addr) {
        return  ((addr & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF));
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

