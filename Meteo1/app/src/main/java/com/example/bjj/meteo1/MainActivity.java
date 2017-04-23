package com.example.bjj.meteo1;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    TextView dateView,tempExtView,tempIntView,lampeIntView,lampeExtView,diversInfoExtView,diversInfoIntView,consoHistoView,consoActuelView, chauffageView, scenarioView,uvTextView;
    TextView ventTextView,pluieTextView;
    ImageView lampeIntImageView,lampeExtImageView,chauffageIntImageView;
    Button lampeIntOnButton,lampeIntOffButton,lampeExtOnButton,lampeExtOffButton;
    //la requête pour récupérer l'objet JSON
    RequestQueue requestQueue;
    //l'url de la vrai bdd
    // String url = "http://172.30.0.230/rest/api.php/Historique/3";
    //l'url en local pour test
    String url = "http://192.168.1.81/rest/api.php/historique/3";
    private String jsonResponse;
    //pour récupérer les données de la bdd
    //
    static int i=0;
    //
    String lampeIntOn="p1$1",lampeIntOff="p1$0",lampeExtOn="p2$1",lampeExtOff="p2$0",chauffageOn="p3$1",chauffageOff="p3$0",scenOn="s1$1",scenOff="s1$0";
    //pour l'auto-refresh des données
    Handler mHandler;
    //initialisation du socket
    private Socket socket;
    private static final int SERVERPORT = 5000;
    private static final String SERVER_IP = "172.30.0.230";
    //
    //ToggleButton lampeButton;
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
        diversInfoIntView = (TextView) findViewById(R.id.diversIntView);
        diversInfoExtView = (TextView) findViewById(R.id.diversExtView);
        consoHistoView = (TextView) findViewById(R.id.consohistoView);
        consoActuelView = (TextView) findViewById(R.id.consoView);;
        lampeIntView = (TextView) findViewById(R.id.lampeIntView);
        lampeExtView = (TextView) findViewById(R.id.lampeExtView);
        chauffageView = (TextView) findViewById(R.id.chauffageView);
        scenarioView = (TextView) findViewById(R.id.scenarioView);
        dateView = (TextView) findViewById(R.id.dateView);
        uvTextView = (TextView) findViewById(R.id.uvTextView);
        //
        lampeIntOnButton = (Button) findViewById(R.id.lampeIntOnButton);
        lampeIntOffButton = (Button) findViewById(R.id.lampeIntOffButton);
        lampeExtOnButton = (Button) findViewById(R.id.lampeExtOnButton);
        lampeExtOffButton = (Button) findViewById(R.id.lampeExtOffButton);
        //
        lampeIntOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"activate lampe int",Toast.LENGTH_LONG).show();
                activate(lampeIntOn);
            }
        });
        lampeIntOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"deactivate lampe int",Toast.LENGTH_LONG).show();
                deactivate(lampeIntOff);
            }
        });
        //
        lampeExtOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"activate lampe ext",Toast.LENGTH_LONG).show();
                activate(lampeExtOn);
            }
        });
        lampeExtOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"deactivate lampe ext",Toast.LENGTH_LONG).show();
                deactivate(lampeExtOff);
            }
        });
        //request JSON
        requestQueue = Volley.newRequestQueue(this);
        //
       /* lampeButton = (ToggleButton) findViewById(R.id.lampeIntButton);
        //
        lampeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    activate(lampeIntOn);
                } else {
                    // The toggle is disabled
                    deactivate(lampeIntOff);
                }
            }
        });
        */




        //l'auto-refresh
        this.mHandler = new Handler();
        //activation de la méhthode Runnable, qui boucle toutes les 5 secondes
        m_Runnable.run();

      /*  imgButton =(ImageButton)findViewById(R.id.lampeExtButton);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Allumage des feux extérieur",Toast.LENGTH_LONG).show();

            }
        });
*/

    }

    //méthode pour allumer la lampe
    public void activate(String str) {
        try {
            //lancement de la connexion Socket
            new Thread(new ClientThread()).start();
            Toast.makeText(MainActivity.this,"Allumage des feux du salon", Toast.LENGTH_SHORT).show();
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
        Thread.currentThread().interrupt();
    }
    //méthode pour éteindre la lampe
    public void deactivate(String str) {
        try {
            Toast.makeText(MainActivity.this,"Extinction des feux du salon", Toast.LENGTH_SHORT).show();
            new Thread(new ClientThread()).start();
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
        Thread.currentThread().interrupt();
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
                                String histo_date_time, histo_temp_ext, histo_temp_int,histo_etat_act_1,histo_etat_act_2,histo_etat_act_3,un="1",zero="0",allumee="allumée",diversInfoExt,diversInfoInt;
                                String histo_hum_int,histo_vent_valeur,histo_direction_vent,histo_mesure_pluie,consoHisto,consoActuel, chauffage, scenario;

                                   //date d'actualisation selon le format ci-dessous
                                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy  HH:mm");
                                String date = df.format(Calendar.getInstance().getTime());
                                    //récupération des éléments de l'array JSON
                                histo_date_time = response.getString("histo_date_time");
                                histo_temp_ext = response.getString("histo_temp_ext");
                                histo_temp_int = response.getString("histo_temp_int");
                                histo_hum_int = response.getString("histo_hum_int");
                                histo_mesure_pluie = response.getString("histo_mesure_pluie");
                                histo_direction_vent = response.getString("histo_direction_vent");
                                histo_vent_valeur = response.getString("histo_vent_valeur");
                                String histo_uv = response.getString("histo_uv");
                                String histo_conso_elect = response.getString("histo_conso_elect");
                                String histo_bun_id = response.getString("histo_bun_id");
                                histo_etat_act_1 = response.getString("histo_etat_act_1");

                                //pour vérifier dès le départ si les actionneurs sont allumés pour que les boutons d'activation soient cohérents
                                if(histo_etat_act_1.equals(un)) {
                                    histo_etat_act_1 = "allumée";
                                }
                                else if(histo_etat_act_1.equals(zero)) {
                                    histo_etat_act_1 = "éteinte";
                                }
                                //
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
                                    histo_etat_act_3 = "allumé";
                                }
                                else if(histo_etat_act_3.equals(zero)) {
                                    histo_etat_act_3 = "éteint";
                                }
                                //initialisalisation du bouton d'activation, pour qu'il soit dans le bon état
                               /* if (i==0){
                                if(histo_etat_act_1.equals(allumee))lampeButton.setChecked(true);else lampeButton.setChecked(false);i++;}*/

                                    //mise en page des éléments
                                //
                                histo_temp_ext += "°c ";
                                //
                                histo_temp_int += "°c\n"+ histo_hum_int +"%";
                                //
                                diversInfoExt ="";
                                diversInfoExt += "Pluie : " + histo_mesure_pluie + " mm \n" +"UV : " + histo_uv + "\n";
                                diversInfoExt += "Vent : " + histo_vent_valeur + "\n";
                                //
                                diversInfoInt ="";
                                diversInfoInt += "Vous êtes dans le bungalow numéro " + histo_bun_id + "\n\n";
                                //
                                consoHisto ="";
                                consoHisto += "" + date ;
                                //
                                consoActuel ="";
                                consoActuel += "Consommation éléctrique : " + histo_conso_elect + " W\n";
                                //
                                histo_etat_act_1 = "Eclairage du Salon : " + histo_etat_act_1;
                                //
                                histo_etat_act_2 = "Eclairage extérieur : " + histo_etat_act_2;
                                //
                                histo_etat_act_3 = "Chauffage : " + histo_etat_act_3;
                                //
                                scenario = "Scénario :";
                                    //déclenchement de l'affichage
                                tempExtView.setText(histo_temp_ext);
                                tempIntView.setText(histo_temp_int);
                               // diversInfoExtView.setText(diversInfoExt);
                                diversInfoIntView.setText(diversInfoInt);
                                dateView.setText(date);
                             //   consoHistoView.setText(consoHisto);
                              //  consoActuelView.setText(consoActuel);
                              //  lampeIntView.setText(histo_etat_act_1);
                              //  lampeExtView.setText(histo_etat_act_2);
                              //  chauffageView.setText(histo_etat_act_3);
                              //  scenarioView.setText(scenario);
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
            //relance de la méthode dans le mainActivity toutes les 10 secondes ou 10000ms
            MainActivity.this.mHandler.postDelayed(m_Runnable,10000);
        }
    };
}