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
    TextView dateView, tempExtView, tempIntView, lampeIntView, lampeExtView, diversIntView, diversInfoIntView, consoHistoView, consoActuelView, chauffageView, scenarioView, uvTextView;
    TextView ventTextView, pluieTextView;
    ImageView lampeIntImageView, lampeExtImageView, chauffageIntImageView, scenarioImageView1, scenarioImageView2, scenarioImageView3;
    Button lampeIntOnButton, lampeIntOffButton, lampeExtOnButton, lampeExtOffButton,chauffageOnButton,chauffageOffButton, scenarioOnButton1, scenarioOffButton1,scenarioOnButton2,scenarioOffButton2,scenarioOnButton3,scenarioOffButton3;
    //la requête pour récupérer l'objet JSON
    RequestQueue requestQueue;
    //l'url de la vrai bdd
    String url = "http://172.30.0.230/rest/api.php/Bungalow/14";
	//String url_scenario = "http://172.30.0.230/rest/api.php/bungalow/13
    //l'url en local pour test
    //String url = "http://192.168.1.81/rest/api.php/bungalow/13";
    //String url = "http://192.168.42.240/rest/api.php/bungalow/13";
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
        diversIntView = (TextView) findViewById(R.id.diversIntView);
        pluieTextView = (TextView) findViewById(R.id.pluieTextView);
        /*diversInfoExtView = (TextView) findViewById(R.id.diversExtView);
        consoHistoView = (TextView) findViewById(R.id.consohistoView);*/
        consoActuelView = (TextView) findViewById(R.id.consoActuelView);
        lampeIntView = (TextView) findViewById(R.id.lampeIntView);
        lampeExtView = (TextView) findViewById(R.id.lampeExtView);
        //chauffageView = (TextView) findViewById(R.id.chauffageView);
        scenarioView = (TextView) findViewById(R.id.scenarioView);
        dateView = (TextView) findViewById(R.id.dateView);
        uvTextView = (TextView) findViewById(R.id.uvTextView);

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
        scenarioOnButton3 = (Button) findViewById(R.id.scenarioOnButton3);
        scenarioOffButton3 = (Button) findViewById(R.id.scenarioOffButton3);

        //initialiser les vues Images avec leur identifiants XML
        lampeIntImageView = (ImageView) findViewById(R.id.lampeIntImageView);
        lampeExtImageView = (ImageView) findViewById(R.id.lampeExtImageView);
        chauffageIntImageView = (ImageView) findViewById(R.id.chauffageIntImageView);
        scenarioImageView1 = (ImageView) findViewById(R.id.scenarioImageView1);
        scenarioImageView2 = (ImageView) findViewById(R.id.scenarioImageView2);
        scenarioImageView3 = (ImageView) findViewById(R.id.scenarioImageView3);
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
        //
        scenarioOnButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Allumage de l'éclairage extérieur en cours", Toast.LENGTH_LONG).show();
                ONOFF="1$2$1";
                new Thread(new ClientThread()).start();
            }
        });
        scenarioOffButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Allumage de l'éclairage extérieur en cours", Toast.LENGTH_LONG).show();
                ONOFF="1$2$1";
                new Thread(new ClientThread()).start();
            }
        });
        //request JSON
        requestQueue = Volley.newRequestQueue(this);
        //l'auto-refresh
        this.mHandler = new Handler();
        //activation de la méhthode Runnable, qui boucle toutes les 5 secondes
        m_Runnable.run();
        //m_Action.run();
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
                                String histo_date_time, histo_temp_int, un = "1", zero = "0",  diversInfoExt, diversInfoInt;
                                String histo_hum_int, histo_vent_valeur, histo_direction_vent, histo_mesure_pluie, consoHisto, consoActuel, chauffage, scenario1, scenario2, scenario3;

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
                                histo_mesure_pluie += "mm";
                                pluieTextView.setText(histo_mesure_pluie);

                                //consommation éléctrique
                                String histo_conso_elect = "Actuel \n";
                                histo_conso_elect += response.getString("val_conso_3");
                                histo_conso_elect += " W";
                                consoActuelView.setText(histo_conso_elect);

                                //Valeur UV
                                //String histo_uv = response.getString("val_uv");

                                // girouette

                                // histo_direction_vent = response.getString("histo_direction_vent");

                                //histo_vent_valeur = response.getString("val_vent_vitesse");

                                //on modifie la valeur 1 en allumée et zéro en éteinte, afin de pouvoir faire des comparaisons entre chaînes de caractères par la suite
                                //etat de l'actionneur 1
                                histo_etat_act_1 = response.getString("val_conso_1");
                                if (histo_etat_act_1.equals(zero)) {
                                    //histo_etat_act_1 = "éteinte";
                                    lampeIntImageView.setImageResource(R.drawable.lampe_int_off1);
                                } else {
                                    //histo_etat_act_1 = "allumée";
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
								scenario1 = response.getString("scenario1");
                                if (scenario1.equals(zero)) {
                                    //histo_etat_act_1 = "éteinte";
                                    lampeIntImageView.setImageResource(R.drawable.lampe_int_off1);
                                } else {
                                    //histo_etat_act_1 = "allumée";
                                    lampeIntImageView.setImageResource(R.drawable.lampe_int_on1);
                                }
                                //scénario 2
                                scenario2 = response.getString("scenario2");
                                if (scenario2.equals(zero)) {
                                    //histo_etat_act_1 = "éteinte";
                                    lampeIntImageView.setImageResource(R.drawable.lampe_int_off1);
                                } else {
                                    //histo_etat_act_1 = "allumée";
                                    lampeIntImageView.setImageResource(R.drawable.lampe_int_on1);
                                }

                                // Toast.makeText(MainActivity.this, "test", Toast.LENGTH_LONG).show();

								//scenario2 = response.getString("scenario2");
								//scenario3 = response.getString("scenario3");

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
   /* private final Runnable m_Action = new Runnable() {
        @Override
        public void run() {
			//rafraîchissement des images
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
			//rafraîchissement des scénarios
			
			
            //boucle toutes les secondes
            MainActivity.this.mHandler.postDelayed(m_Action,10000);
        }
    };*/

}

