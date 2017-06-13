package com.example.bjj.meteo1;

import android.content.Intent;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
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
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //le bouton pour envoyer la commande d'activation
    Button start;
    //l'endroit où sont affichées les données
    TextView dateView, tempExtView, tempIntView, lampeIntView, lampeExtView, diversIntView, diversInfoIntView, consohistoTextView, consoActuelView, chauffageView, scenarioView, uvTextView;
    TextView ventTextView, pluieTextView,scenarioView1,scenarioView2;
    ImageView lampeIntImageView, lampeExtImageView, chauffageIntImageView, scenarioImageView1, scenarioImageView2;
    Button lampeIntOnButton, lampeIntOffButton, lampeExtOnButton, lampeExtOffButton,chauffageOnButton,chauffageOffButton, scenarioOnButton1, scenarioOffButton1,scenarioOnButton2,scenarioOffButton2;
    //la requête pour récupérer l'objet JSON
    RequestQueue requestQueue;
    //l'url de la vrai bdd
    String url = "http://172.30.0.230/rest/api.php/Bungalow/1";
    String url_post = "http://172.30.0.230/rest/scenario.php";
    //variables contenant le numéro du scénario et l'action à effectuer
    static String num_scenario="",action="";

	//String url_scenario = "http://172.30.0.230/rest/api.php/bungalow/13
    //l'url en local pour test
    //String url = "http://192.168.1.81/rest/api.php/Bungalow/14";
    //String url_post = "http://192.168.1.81/scenario.php";
    //String url = "http://192.168.42.240/rest/api.php/bungalow/13";
    static String allumee = "allumée",histo_etat_act_1="", histo_etat_act_2="";
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
        diversIntView = (TextView) findViewById(R.id.diversIntView);
        pluieTextView = (TextView) findViewById(R.id.pluieTextView);
        ventTextView = (TextView) findViewById(R.id.ventTextView);
        /*diversInfoExtView = (TextView) findViewById(R.id.diversExtView);*/
        consohistoTextView = (TextView) findViewById(R.id.consohistoTextView);
        consoActuelView = (TextView) findViewById(R.id.consoActuelView);
        lampeIntView = (TextView) findViewById(R.id.lampeIntView);
        lampeExtView = (TextView) findViewById(R.id.lampeExtView);
        //chauffageView = (TextView) findViewById(R.id.chauffageView);
        scenarioView = (TextView) findViewById(R.id.scenarioView);
        dateView = (TextView) findViewById(R.id.dateView);
        uvTextView = (TextView) findViewById(R.id.uvTextView);
        scenarioView1 = (TextView) findViewById(R.id.scenarioView1);
        scenarioView2 = (TextView) findViewById(R.id.scenarioView2);

        //les boutons on et off
        lampeIntOnButton = (Button) findViewById(R.id.lampeIntOnButton);
        lampeIntOffButton = (Button) findViewById(R.id.lampeIntOffButton);
        lampeExtOnButton = (Button) findViewById(R.id.lampeExtOnButton);
        lampeExtOffButton = (Button) findViewById(R.id.lampeExtOffButton);
        chauffageOnButton = (Button) findViewById(R.id.chauffageOnButton);
        chauffageOffButton = (Button) findViewById(R.id.chauffageOffButton);
        scenarioOnButton1 = (Button) findViewById(R.id.scenarioOnButton1);
        scenarioOffButton1 = (Button) findViewById(R.id.scenarioOffButton1);
        scenarioOnButton2 = (Button) findViewById(R.id.scenarioOnButton2);
        scenarioOffButton2 = (Button) findViewById(R.id.scenarioOffButton2);

        //initialiser les vues Images avec leur identifiants XML
        lampeIntImageView = (ImageView) findViewById(R.id.lampeIntImageView);
        lampeExtImageView = (ImageView) findViewById(R.id.lampeExtImageView);
        chauffageIntImageView = (ImageView) findViewById(R.id.chauffageIntImageView);
        scenarioImageView1 = (ImageView) findViewById(R.id.scenarioImageView1);
        scenarioImageView2 = (ImageView) findViewById(R.id.scenarioImageView2);

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

        chauffageOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Allumage du chauffage en cours", Toast.LENGTH_LONG).show();
                ONOFF="1$2$1";
                new Thread(new ClientThread()).start();
            }
        });
        chauffageOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Extinction du chauffage en cours", Toast.LENGTH_LONG).show();
                ONOFF="1$2$0";
                new Thread(new ClientThread()).start();
            }
        });
        //
        scenarioOnButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Chauffage automatique activé", Toast.LENGTH_LONG).show();
                num_scenario="scenario_1";action="1";
                scenario();

            }
        });
        scenarioOffButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Chauffage automatique désactivé", Toast.LENGTH_LONG).show();
                num_scenario="scenario_1";action="0";
                scenario();
            }
        });
        //
        scenarioOnButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Eclairage extérieur automatique activé", Toast.LENGTH_LONG).show();
                num_scenario="scenario_2";action="1";
                scenario();

            }
        });
        scenarioOffButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Eclairage extérieur automatique désactivé", Toast.LENGTH_LONG).show();
                num_scenario="scenario_2";action="0";
                scenario();
            }
        });

        //request JSON
        requestQueue = Volley.newRequestQueue(this);
        //l'auto-refresh
        this.mHandler = new Handler();
        //activation de la méhthode Runnable, qui boucle toutes les 5 secondes
        m_Runnable.run();
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

    private void scenario(){

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_post,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to request
                params.put("num_scenario", num_scenario);
                params.put("action", action);

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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
                                String histo_date_time, histo_temp_int, un = "1", zero = "0",diversInfoInt;
                                String histo_hum_int, histo_vent_valeur, histo_direction_vent, histo_mesure_pluie, consoHisto, consoActuel, chauffage, scenario1, scenario2;

                                //date d'actualisation selon le format ci-dessous
                                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy  HH:mm");
                                String date = df.format(Calendar.getInstance().getTime());
                                dateView.setText(date);
                                //récupération des éléments de l'array JSON
                                //histo_date_time = response.getString("histo_date_time");

                                //température et humidité extérieure
                                String histo_temp_ext = response.getString("val_temp_ext");
                                String histo_hum_ext = response.getString("val_hum_ext");
                                histo_temp_ext += "°c\n" + histo_hum_ext + "%";
                                tempExtView.setText(histo_temp_ext);

                                //température et humidité intérieure
                                histo_temp_int = response.getString("val_temp_int");
                                histo_hum_int = response.getString("val_hum_int");
                                histo_temp_int += "°c\n" + histo_hum_int + "%";
                                tempIntView.setText(histo_temp_int);

                                //numéro du bungalow
                                String histo_bun_id = response.getString("bun_id");
                                diversInfoInt = "";
                                diversInfoInt += "Vous êtes dans le bungalow numéro " + histo_bun_id + "\n\n";
                                diversIntView.setText(diversInfoInt);

                                //anénomètre
                                histo_mesure_pluie = response.getString("val_pluie");
                                histo_mesure_pluie += "mm/h";
                                pluieTextView.setText(histo_mesure_pluie);

                                //consommation éléctrique
                                String conso_elect = response.getString("val_conso_3");
                                float conso = Float.valueOf(conso_elect);
                                conso = conso * 230;
                                String histo_conso_elect = String.format("Actuel\n %.0f W",conso);
                                consoActuelView.setText(histo_conso_elect);

                                //prix au kilowatt/heure
                                float consommation = (((float)conso/1000) * (float)0.145);
                                String prix_consommation = String.format("%.2f \n euros/h",consommation);
                                consohistoTextView.setText(prix_consommation);

                                //Valeur UV
                                String val_uv = response.getString("val_uv");
                                uvTextView.setText(val_uv);

                                // girouette
                                //histo_direction_vent = response.getString("histo_direction_vent");
                                histo_vent_valeur = response.getString("val_vent_vitesse");
                                histo_vent_valeur += " km/h";
                                ventTextView.setText(histo_vent_valeur);

                                //on modifie la valeur 1 en allumée et zéro en éteinte, afin de pouvoir faire des comparaisons entre chaînes de caractères par la suite
                                //etat de l'actionneur 1
                                histo_etat_act_1 = response.getString("val_conso_1");
                                if (histo_etat_act_1.equals(zero)) {
                                    lampeIntImageView.setImageResource(R.drawable.lampe_int_off1);
                                } else {
                                    lampeIntImageView.setImageResource(R.drawable.lampe_int_on1);
                                }
                                //état de l'actionneur 2
                                histo_etat_act_2 = response.getString("val_conso_2");
                                if(histo_etat_act_2.equals(zero)) {
                                    chauffageIntImageView.setImageResource(R.drawable.lampe_int_off1);
                                }
                                else {
                                    chauffageIntImageView.setImageResource(R.drawable.lampe_int_on1);
                                }
                                //les scénarii
                                //scénario 1
								scenario1 = response.getString("scenario_1");
                                if (scenario1.equals(zero)) {
                                    scenarioImageView1.setImageResource(R.drawable.rond_gris);
                                } else {
                                    scenarioImageView1.setImageResource(R.drawable.rond_vert);
                                }
                                //scénario 2
                                scenario2 = response.getString("scenario_2");
                                if (scenario2.equals(zero)) {
                                    scenarioImageView2.setImageResource(R.drawable.rond_gris);
                                } else {
                                    scenarioImageView2.setImageResource(R.drawable.rond_vert);
                                }
                                //Détails des scénarii
                                //Scénario 1
                                String scenario_text1 = "Chauffage auto jusqu'à ";
                                scenario_text1 += response.getString("temp_chauffage_off");
                                scenario_text1 += "°C \n";
                                scenario_text1 += "Chauffage auto en dessous de ";
                                scenario_text1 += response.getString("temp_chauffage_on");
                                scenario_text1 += "°C";
                                scenarioView1.setText(scenario_text1);

                                //Scénario 2
                                String scenario_text2 = "Eclairage extérieur auto, durée : ";
                                scenario_text2 += response.getString("duree_allumage_lampe");
                                scenario_text2 += " min";
                                scenarioView2.setText(scenario_text2);
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
}

