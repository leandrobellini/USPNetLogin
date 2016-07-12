package usp.br.uspnetlogin;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by bellini on 29/05/15.
 */
public class ThreadLogin extends AsyncTask<String, String, Void> {
    TextView statusView;
    Context context;
    RelativeLayout botoesAtalho;

    public ThreadLogin(TextView statusView, Context context, RelativeLayout botoesAtalho){

        this.statusView = statusView;
        this.context = context;
        this.botoesAtalho = botoesAtalho;
    }

    @Override
    protected Void doInBackground(String... params) {
        String usuario = params[0];
        String senha = params[1];

        URL url = null;

        try {

            final KeyStore trustStore = KeyStore.getInstance("BKS");
            final InputStream entrada = context.getResources().openRawResource(
                    R.raw.certificadouspnet);
            trustStore.load(entrada, null);

            final TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            final SSLContext sslCtx = SSLContext.getInstance("TLS");
            sslCtx.init(null, tmf.getTrustManagers(),
                    new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslCtx
                    .getSocketFactory());


            url = new URL("https://gwsc.sc.usp.br:8003/");
            //url = new URL("https://www.facebook.com/");

            HttpURLConnection urlConnection = null;

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");


            String parametros = "auth_user="+usuario+"&auth_pass="+senha+"&redirurl=www.google.com&accept=Continue";
            //parametros = "auther_user="+URLEncoder.encode("valor1","UTF-8")+"&senha="+URLEncode..."

            urlConnection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
            wr.writeBytes(parametros);
            wr.flush();
            wr.close();

            int resposta = urlConnection.getResponseCode();
            Log.v("Dados", String.valueOf(resposta));

            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }
            in.close();

            //Redirecting to
            if(response.toString().toLowerCase().contains("redirecting to")){
                publishProgress("Logado com sucesso...");
            }else{
                publishProgress("Erro no login :/");
            }

            Log.v("Dados", response.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        return null;

    }

    protected void onProgressUpdate(String... params){
        statusView.setText(params[0]);
    }
}








