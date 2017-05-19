package com.example.bjj.meteo1;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
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
    TextView dateView, tempExtView, tempIntView, lampeIntView, lampeExtView, diversInfoExtView, diversInfoIntView, consoHistoView, consoActuelView, chauffageView, scenarioView, uvTextView;
    TextView ventTextView, pluieTextView;
    ImageView lampeIntImageView, lampeExtImageView, chauffageIntImageView;
    Button lampeIntOnButton, lampeIntOffButton, lampeExtOnButton, lampeExtOffButton,chauffageOnButton,chauffageOffButton;
    //la requête pour récupérer l'objet JSON
    RequestQueue requestQueue;
    //l'url de la vrai bdd
    String url = "http://172.30.0.230/rest/api.php/Historique/3";
    //l'url en local pour test
    //String url = "http://192.168.1.81/rest/api.php/historique/3";
    //String url = "http://192.168.42.240/rest/api.php/historique/3";
    static String allumee = "allumée",histo_etat_act_1="", histo_etat_act_2="", histo_etat_act_3="";
    private String jsonResponse;
    //pour récupérer les données de la bdd
    //
    static int i = 0;
    //
    //String lampeIntOn = "1$1$1", lampeIntOff = "1$1$0", lampeExtOn = "1$2$1", lampeExtOff = "1$2$0", chauffageOn = "1$3$1", chauffageOff = "1$3$0", scenOn = "1$4$1", scenOff = "1$4$0";
    static String ONOFF = "";
    //pour l'auto-refresh des données
    Handler mHandler;
    //initialisation du socket
    private Socket socket;
    private static final int SERVERPORT = 5000;
    private static final String SERVER_IP = "172.30.0.230";
    //private static final String SERVER_IP = "192.168.1.98";
    //
    //ToggleButton lampeButton;
    //
    //Switch lampeSwitch1;
    //ImageButton imgButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //A la création de l'activité
        super.onCreate(savedInstanceState);
        //affichage du activity_main.xml
        setContentView(R.layout.activity_main);
        //

        // le textview où sont affichés les données
        tempExtView = (TextView) findViewById(R.id.tempExtView);
        tempIntView = (TextView) findViewById(R.id.tempIntView);
        diversInfoIntView = (TextView) findViewById(R.id.diversIntView);
        /*diversInfoExtView = (TextView) findViewById(R.id.diversExtView);
        consoHistoView = (TextView) findViewById(R.id.consohistoView);
        consoActuelView = (TextView) findViewById(R.id.consoView);*/
        lampeIntView = (TextView) findViewById(R.id.lampeIntView);
        lampeExtView = (TextView) findViewById(R.id.lampeExtView);
        //chauffageView = (TextView) findViewById(R.id.chauffageView);
        scenarioView = (TextView) findViewById(R.id.scenarioView);
        dateView = (TextView) findViewById(R.id.dateView);
        uvTextView = (TextView) findViewById(R.id.uvTextView);
        consoActuelView = (TextView) findViewById(R.id.consoActuelView);

        //les boutons on et off
        lampeIntOnButton = (Button) findViewById(R.id.lampeIntOnButton);
        lampeIntOffButton = (Button) findViewById(R.id.lampeIntOffButton);
        lampeExtOnButton = (Button) findViewById(R.id.lampeExtOnButton);
        lampeExtOffButton = (Button) findViewById(R.id.lampeExtOffButton);
        chauffageOnButton = (Button) findViewById(R.id.chauffageOnButton);
        chauffageOffButton = (Button) findViewById(R.id.chauffageOffButton);

        //initialiser les vues Images avec leur identifiants XML
        lampeIntImageView = (ImageView) findViewById(R.id.lampeIntImageView);
        lampeExtImageView = (ImageView) findViewById(R.id.lampeExtImageView);
        chauffageIntImageView = (ImageView) findViewById(R.id.chauffageIntImageView);

        //les méthodes pour les actionneurs
        lampeIntOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Allumage de l'éclairage salon en cours", Toast.LENGTH_LONG).show();
                ONOFF="1$1$1";
                new Thread(new ClientThread()).start();
            }
        });
        lampeIntOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Extinction de l'éclairage salon en cours", Toast.LENGTH_LONG).show();
                ONOFF="1$1$0";
                new Thread(new ClientThread()).start();
            }
        });
        //
        lampeExtOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Allumage de l'éclairage extérieur en cours", Toast.LENGTH_LONG).show();
                ONOFF="1$2$1";
                new Thread(new ClientThread()).start();
            }
        });
        lampeExtOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Extinction de l'éclairage extérieur en cours", Toast.LENGTH_LONG).show();
                ONOFF="1$2$0";
                new Thread(new ClientThread()).start();
            }
        });
        chauffageOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Allumage du chauffage en cours", Toast.LENGTH_LONG).show();
                ONOFF="1$3$1";
                new Thread(new ClientThread()).start();
            }
        });
        chauffageOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Extinction du chauffage en cours", Toast.LENGTH_LONG).show();
                ONOFF="1$3$0";
                new Thread(new ClientThread()).start();
            }
        });
        //request JSON
        requestQueue = Volley.newRequestQueue(this);
        //l'auto-refresh
        this.mHandler = new Handler();
        //activation de la méhthode Runnable, qui boucle toutes les 5 secondes
        m_Runnable.run();
        m_Action.run();
    }

    //classe pour envoyer les commandes aux actionneurs
    private class ClientThread implements Runnable {
        @Override
        public void run() {
            try {
                //ouverture du socket avec l'adresse ip du serveur et son port
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVERPORT);
                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())),true);
                //out.println(str);
                out.print(ONOFF);
                out.flush();
                out.close();
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    //Runnable auto-Refresh et récupération des données
    private final Runnable m_Runnable = new Runnable() {
        public void run()

        {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                String histo_date_time, histo_temp_ext, histo_temp_int, un = "1", zero = "0",  diversInfoExt, diversInfoInt;
                                String histo_hum_int, histo_vent_valeur, histo_direction_vent, histo_mesure_pluie, consoHisto, consoActuel, chauffage, scenario;

                                //date d'actualisation selon le format ci-dessous
                                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy  HH:mm");
                                String date = df.format(Calendar.getInstance().getTime());
                                //récupération des éléments de l'array JSON
                                String histo_bun_id = response.getString("histo_bun_id");
                                //histo_date_time = response.getString("histo_date_time");
                                histo_temp_ext = response.getString("histo_temp_ext");
                                //
                                histo_temp_int = response.getString("histo_temp_int");
                                histo_hum_int = response.getString("histo_hum_int");
                                histo_temp_int += "°c\n" + histo_hum_int + "%";
                                //
                                histo_mesure_pluie = response.getString("histo_mesure_pluie");
                                //histo_direction_vent = response.getString("histo_direction_vent");
                                //
                                histo_vent_valeur = response.getString("histo_vent_valeur");
                                //
                                String histo_uv = response.getString("histo_uv");
                                //mise en page des éléments si nécessaire
                                //
                                histo_temp_ext += "°c ";
                                //
                                //diversInfoExt = "";
                                //diversInfoExt += "Pluie : " + histo_mesure_pluie + " mm \n" + "UV : " + histo_uv + "\n";
                                //diversInfoExt += "Vent : " + histo_vent_valeur + "\n";
                                //
                                diversInfoInt = "";
                                diversInfoInt += "Vous êtes dans le bungalow numéro " + histo_bun_id + "\n\n";
                                //
                                consoHisto = "";
                                consoHisto += "" + date;
                                //
                                String histo_conso_elect = "Actuel \n";
                                histo_conso_elect += response.getString("histo_conso_elect");
                                histo_conso_elect += " W";
                                //
                                //on modifie la valeur 1 en allumée et zéro en éteinte, afin de pouvoir faire des comparaisons entre chaînes de caractères par la suite
                                histo_etat_act_1 = response.getString("histo_etat_act_1");
                                if (histo_etat_act_1.equals(un)) {
                                    histo_etat_act_1 = "allumée";
                                } else if (histo_etat_act_1.equals(zero)) {
                                    histo_etat_act_1 = "éteinte";
                                }
                                histo_etat_act_2 = response.getString("histo_etat_act_2");
                                if(histo_etat_act_2.equals(un)) {
                                    histo_etat_act_2 = "allumée";
                                }
                                else if(histo_etat_act_2.equals(zero)) {
                                    histo_etat_act_2 = "éteinte";
                                }
                                //
                                histo_etat_act_3 = response.getString("histo_etat_act_3");
                                if(histo_etat_act_3.equals(un)) {
                                    histo_etat_act_3 = "allumée";
                                }
                                else if(histo_etat_act_3.equals(zero)) {
                                    histo_etat_act_3 = "éteinte";
                                }

                                //déclenchement de l'affichage
                                tempExtView.setText(histo_temp_ext);
                                diversInfoIntView.setText(diversInfoInt);
                                dateView.setText(date);
                                tempIntView.setText(histo_temp_int);
                                consoActuelView.setText(histo_conso_elect);
                                uvTextView.setText(histo_uv);
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
            //relance de la méthode dans le mainActivity toutes les 10 secondes ou 10000ms
            MainActivity.this.mHandler.postDelayed(m_Runnable, 10000);
        }

    };
    //Runnable auto-Refresh et récupération des données
    private final Runnable m_Action = new Runnable() {
        @Override
        public void run() {
            String allumee ="allumée";
            if(histo_etat_act_1.equals(allumee))
            {
                lampeIntImageView.setImageResource(R.drawable.lampe_int_on1);
            }
            else {
                lampeIntImageView.setImageResource(R.drawable.lampe_int_off1);
            }

            if(histo_etat_act_2.equals(allumee))
            {
                lampeExtImageView.setImageResource(R.drawable.lampe_int_on1);
            }
            else {
                lampeExtImageView.setImageResource(R.drawable.lampe_int_off1);
            }

            if(histo_etat_act_3.equals(allumee))
            {
                chauffageIntImageView.setImageResource(R.drawable.lampe_int_on1);
            }
            else {
                chauffageIntImageView.setImageResource(R.drawable.lampe_int_off1);
            }
            //boucle toutes les secondes
            MainActivity.this.mHandler.postDelayed(m_Action,1000);
        }
    };

}

