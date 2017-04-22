package com.example.bjj.meteo1;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    //le bouton pour envoyer la commande d'activation
    Button start;
    //l'endroit où sont affichées les données
    TextView dataView;
    //la requête pour récupérer l'objet JSON
    RequestQueue requestQueue;
    //l'url de la bdd
    String url = "http://172.30.0.230/rest/api.php/Historique/3";
    private String jsonResponse;
    //pour récupérer les données de la bdd
    String histo_date_time, histo_temp_ext, histo_temp_int;
    //pour l'auto-refresh des données
    Handler mHandler;
    //initialisation du socket
    private Socket socket;
    private static final int SERVERPORT = 5000;
    private static final String SERVER_IP = "172.30.0.230";
    //
    ToggleButton lampeButton;
    //
    Switch lampeSwitch1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //A la création de l'activité
        super.onCreate(savedInstanceState);
        //affichage du activity_main.xml
        setContentView(R.layout.activity_main);
        // le textview où sont affichés les données
        dataView = (TextView) findViewById(R.id.dataView);
        //request JSON
        requestQueue = Volley.newRequestQueue(this);
        //l'auto-refresh
        this.mHandler = new Handler();
        //activation de la méhthode Runnable, qui boucle toutes les 60 secondes
        m_Runnable.run();
        //
        lampeButton = (ToggleButton) findViewById(R.id.lampeButton);
        //
        lampeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    lampeOn();
                } else {
                    // The toggle is disabled
                    lampeOff();
                }
            }
        });



    }

    //méthode pour allumer la lampe
    public void lampeOn() {
        try {
            //lancement de la connexion Socket
            new Thread(new ClientThread()).start();
            Toast.makeText(MainActivity.this,"on", Toast.LENGTH_SHORT).show();
            String str = "p1$1";
            PrintWriter out = new PrintWriter(new BufferedWriter(
            new OutputStreamWriter(socket.getOutputStream())),
            true);
            out.println(str);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //méthode pour éteindre la lampe
    public void lampeOff() {
        try {
            Toast.makeText(MainActivity.this,"off", Toast.LENGTH_SHORT).show();
            String str = "p1$0";
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())),
                    true);
            out.println(str);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//ouverture du socket avec l'adresse ip du serveur et son port

        private class ClientThread implements Runnable {
        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVERPORT);
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

//Runnable auto-Refresh et récupération des données
        private final Runnable m_Runnable = new Runnable()
    {
        public void run()

        {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                   //date d'actualisation selon le format ci-dessous
                                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                                String date = df.format(Calendar.getInstance().getTime());
                                    //récupération des éléments de l'array JSON
                                histo_date_time = response.getString("histo_date_time");
                                histo_temp_ext = response.getString("histo_temp_ext");
                                histo_temp_int = response.getString("histo_temp_int");
                                String histo_mesure_pluie = response.getString("histo_mesure_pluie");
                                String histo_direction_vent = response.getString("histo_direction_vent");
                                String histo_vent_valeur = response.getString("histo_vent_valeur");
                                String histo_uv = response.getString("histo_uv");
                                String histo_conso_elect = response.getString("histo_conso_elect");
                                String histo_bun_id = response.getString("histo_bun_id");
                                    //mise en page des éléments
                                jsonResponse = "";
                                jsonResponse += "Dernière actualisation : " + date + "\n\n";
                                jsonResponse += "Bungalow numéro : " + histo_bun_id + "\n\n";
                                jsonResponse += "Dernier relevé disponible : " + histo_date_time + "\n\n";
                                jsonResponse += "Température extérieure : " + histo_temp_ext + "\n\n";
                                jsonResponse += "Température intérieure : " + histo_temp_int + "\n\n";
                                jsonResponse += "Pluie en mm : " + histo_mesure_pluie + "\n\n";
                                jsonResponse += "Direction du vent : " + histo_direction_vent + "\n\n";
                                jsonResponse += "Force du vent : " + histo_vent_valeur + "\n\n";
                                jsonResponse += "UV : " + histo_uv + "\n\n";
                                jsonResponse += "Consommation éléctrique : " + histo_conso_elect + "\n\n";
                                    //déclenchement de l'affichage
                                dataView.setText(jsonResponse);
                                //boucle pour récupérer la totalité des éléments
                                // for (int i = 0; i < /*jsonArray.length()*/ 3; i++) {
                                //  JSONObject list = jsonArray.getJSONObject(i);
                                // String dt = list.getString("dt");

                                   /*     textView.append("dt : " + dt + "\n");*/
                                //on attrape les exceptions s'il y en a
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                                //message d'erreur dans la console si un problème survient
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("VOLLEY", "ERROR");
                        }
                    });
            requestQueue.add(jsonObjectRequest);
            // décommenter le test de l'auto-refresh pour vérifier qu'il marche bien
          //  Toast.makeText(MainActivity.this,"in runnable",Toast.LENGTH_SHORT).show();
            //relance de la méthode dans le mainActivity toutes les minutes ou 60000ms
            MainActivity.this.mHandler.postDelayed(m_Runnable,60000);
        }
    };
}