package usp.br.uspnetlogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private static final String CONFIGURACOES_APP = "app_configs";
    private static final String USER_APP = "app_user";
    private static final String PASS_APP = "app_pass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void onResume(){
        super.onResume();

        final RelativeLayout botoesAtalho = (RelativeLayout) findViewById(R.id.botoesAtalho);
        botoesAtalho.setVisibility(View.INVISIBLE);

        final SharedPreferences configuracoesApp = getSharedPreferences(CONFIGURACOES_APP, MODE_PRIVATE);

        final TextView statusView = (TextView) findViewById(R.id.status_view);

        final ImageView imageWhats = (ImageView) findViewById(R.id.whatsapp);
        final ImageView imageChrome = (ImageView) findViewById(R.id.chrome);
        final ImageView imageFB = (ImageView) findViewById(R.id.fb);

        final EditText editUsuario = (EditText) findViewById(R.id.edit_usuario);
        final EditText editSenha = (EditText) findViewById(R.id.edit_senha);

        String usuario = configuracoesApp.getString(USER_APP, null);
        String senha = configuracoesApp.getString(PASS_APP, null);

        if(usuario != null && senha != null){
            ThreadLogin login = new ThreadLogin(statusView, MainActivity.this, botoesAtalho);
            statusView.setText("Tentando logar...");
            login.execute(usuario,senha);
        }

        editUsuario.setText(usuario);
        editSenha.setText(senha);

        Button botaoLogin = (Button) findViewById(R.id.botao_login);
        botaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                String novoUsuario = editUsuario.getEditableText().toString();
                String novaSenha = editSenha.getEditableText().toString();

                SharedPreferences.Editor editor = configuracoesApp.edit();
                editor.putString(USER_APP, novoUsuario);
                editor.putString(PASS_APP, novaSenha);
                editor.commit();

                ThreadLogin login = new ThreadLogin(statusView, MainActivity.this, botoesAtalho);
                statusView.setText("Tentando logar...");
                login.execute(novoUsuario,novaSenha);


            }
        });

        imageWhats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                startActivity(launchIntent);
            }
        });

        imageFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.category.LAUNCHER");
                intent.setClassName("com.facebook.katana", "com.facebook.katana.LoginActivity");
                startActivity(intent);
            }
        });

        imageChrome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("http://www.google.com.br"));
                startActivity(intent);

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.campus2) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
