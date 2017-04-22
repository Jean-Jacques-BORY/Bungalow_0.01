package com.example.bjj.meteo1;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
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
    TextView tempExtView;
    TextView tempIntView;
    TextView lampeIntView;
    //la requête pour récupérer l'objet JSON
    RequestQueue requestQueue;
    //l'url de la vrai bdd
    // String url = "http://172.30.0.230/rest/api.php/Historique/3";
    //l'url en local pour test
    String url = "http://192.168.1.81/rest/api.php/historique/3";
    private String jsonResponse;

    //pour récupérer les données de la bdd
    String histo_date_time, histo_temp_ext, histo_temp_int,histo_etat_act_1,histo_etat_act_2,histo_etat_act_3,un="1",zero="0";
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
    ImageButton imgButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //A la création de l'activité
        super.onCreate(savedInstanceState);
        //affichage du activity_main.xml
        setContentView(R.layout.activity_main);
        // le textview où sont affichés les données
        tempExtView = (TextView) findViewById(R.id.tempExtView);
        tempIntView = (TextView) findViewById(R.id.tempIntView);

        lampeIntView = (TextView) findViewById(R.id.lampeIntView);
        //request JSON
        requestQueue = Volley.newRequestQueue(this);
        //l'auto-refresh
        this.mHandler = new Handler();
        //activation de la méhthode Runnable, qui boucle toutes les 5 secondes
        m_Runnable.run();
        //
        lampeButton = (ToggleButton) findViewById(R.id.lampeIntButton);
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

        imgButton =(ImageButton)findViewById(R.id.lampeExtButton);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Allumage des feux extérieur",Toast.LENGTH_LONG).show();

            }
        });

    }

    //méthode pour allumer la lampe
    public void lampeOn() {
        try {
            //lancement de la connexion Socket
            new Thread(new ClientThread()).start();
            Toast.makeText(MainActivity.this,"Allumage des feux du salon", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(MainActivity.this,"Extinction des feux du salon", Toast.LENGTH_SHORT).show();
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
                                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm:ss");
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
                                histo_etat_act_1 = response.getString("histo_etat_act_1");
                                if(histo_etat_act_1.equals(un)) {
                                    histo_etat_act_1 = "allumée";
                                }
                                else if(histo_etat_act_1.equals(zero)) {
                                    histo_etat_act_1 = "éteinte";
                                }

                                histo_etat_act_2 = response.getString("histo_etat_act_2");
                                if(histo_etat_act_2.equals(un)) {
                                    histo_etat_act_2 = "allumée";
                                }
                                else if(histo_etat_act_2.equals(zero)) {
                                    histo_etat_act_2 = "éteinte";
                                }

                                histo_etat_act_3 = response.getString("histo_etat_act_3");
                                if(histo_etat_act_3.equals(un)) {
                                    histo_etat_act_3 = "allumé";
                                }
                                else if(histo_etat_act_3.equals(zero)) {
                                    histo_etat_act_3 = "éteint";
                                }


                                    //mise en page des éléments
                                jsonResponse = "";
                                jsonResponse += "Actualisé le : " + date + "\n\n";
                                jsonResponse += "Numéro du bungalow : " + histo_bun_id + "\n\n";
                                jsonResponse += "Température extérieure : " + histo_temp_ext + " Degrés\n\n";
                                jsonResponse += "Température intérieure : " + histo_temp_int + " Degrés\n\n";
                                jsonResponse += "Pluie : " + histo_mesure_pluie + " mm\n\n";
                                jsonResponse += "Direction du vent : " + histo_direction_vent + "\n\n";
                                jsonResponse += "Force du vent : " + histo_vent_valeur + "\n\n";
                                jsonResponse += "UV : " + histo_uv + "\n\n";
                                jsonResponse += "Consommation éléctrique : " + histo_conso_elect + " W\n\n";
                                jsonResponse += "Lampe intérieure : " + histo_etat_act_1 + "\n\n";
                                jsonResponse += "Lampe extérieure : " + histo_etat_act_2 + "\n\n";
                                jsonResponse += "Chauffage : " + histo_etat_act_3 + "\n\n";

                                histo_temp_ext += "°C";
                                histo_temp_int += "°C";
                                histo_etat_act_1 = "Eclairage du Salon : " + histo_etat_act_1;
                                histo_etat_act_2 = "Eclairage extérieur : " + histo_etat_act_2;


                                    //déclenchement de l'affichage
                                tempExtView.setText(histo_temp_ext);
                                tempIntView.setText(histo_temp_int);
                                lampeIntView.setText(histo_etat_act_1);


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
            MainActivity.this.mHandler.postDelayed(m_Runnable,5000);
        }
    };
}