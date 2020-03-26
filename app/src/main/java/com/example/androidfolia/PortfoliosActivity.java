package com.example.androidfolia;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PortfoliosActivity extends ListActivity {

    // paramètres de connexion à transmettre par intent à l'activité suivante :
    String userLogin = "";
    String userPwd = "";
    private static final String TAG_USER_LOGIN = "login";
    private static final String TAG_USER_PWD = "pwd";

    // tableau des items devant contenir les éléments de la vue demandée sous la forme d'une table de hachage :
    ArrayList<HashMap<String, String>> itemsList = new ArrayList<HashMap<String, String>>();

    // noms des noeuds JSON :
    private static final String TAG_TASK = "portfolios";
    private static final String TAG_ID = "id";
    private static final String TAG_LIBELLE = "libelle";
    private static final String TAG_THEME = "theme";
    private static final String TAG_ETUDIANT = "etudiant";
    private static final String TAG_CLASSE = "classe";
    private static final String TAG_CREATED = "created";
    private static final String TAG_HITS = "hits";
    private static final String TAG_NBCOM = "nbcom";

    // tableau JSON de la liste des items :
    JSONArray items = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolios);

        // récupération de l'intent de la vue courante :
        Intent i = getIntent();
        // obtention des paramètres de connexion à partir de l'intent :
        userLogin = i.getStringExtra(TAG_USER_LOGIN);
        userPwd = i.getStringExtra(TAG_USER_PWD);

        // chargement des items en fil d'exécution de fond (background thread) :
        new LoadItems().execute();

        // création de la ListView lv :
        ListView lv = getListView();

        // événement de clic sur lv déclenchant l'affichage de la vue de détail :
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // recherche de l'aid de l'élément sélectionné (ListItem) dans la liste
                String aid = ((TextView) view.findViewById(R.id.aid)).getText().toString();
                // DEBUG : affichage temporaire de aid
                // Toast.makeText(MesMessagesActivity.this, "Messages aid : "+aid, Toast.LENGTH_LONG).show();

                // création d'une nouvelle intention (intent)
                /*Intent in = new Intent(getApplicationContext(), PortfolioActivity.class);
                in.putExtra(TAG_USER_LOGIN, userLogin);
                in.putExtra(TAG_USER_PWD, userPwd);
                // envoi de l'aid à l'activité suivante (activity)
                in.putExtra(TAG_ID, aid);
                // lancement de la nouvelle activité (vue de détail) en attente d'une réponse
                startActivityForResult(in, 100);*/
            }
        });
    }

    // réponse de l'activité vue de détail :
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            // raffraichissement de l'écran :
            Intent in = getIntent();
            finish();
            startActivity(in);
        }
    }

    /**
     * Tâche de fond pour charger la liste des items par une requête HTTP :
     */
    class LoadItems extends AsyncTask<String, String, JSONObject> {

        // url pour obtenir la liste des items :
        String apiUrl = "";

        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";

        // affiche une barre de progression avant d'activer la tâche de fond :
        @Override
        protected void onPreExecute() {
            // super.onPreExecute();
            pDialog = new ProgressDialog(PortfoliosActivity.this);
            pDialog.setMessage("Attente de connexion...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            apiUrl = "http://" + getString(R.string.pref_default_api_url_loc) + "/index.php";
            // apiUrl = "http://" + getString(R.string.pref_default_api_url_dist) + "/index.php";

            // Toast.makeText(MesMessagesActivity.this, "URL de l'API : " + apiUrl, Toast.LENGTH_LONG).show();
        }

        // obtention en tâche de fond des items au format JSON par une requête HTTP
        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("login", userLogin);
                params.put("pwd", userPwd);
                params.put("task", TAG_TASK);

                Log.d("request", "starting");

                // JSONObject json = jsonParser.makeHttpRequest(apiUrl, "GET", params);
                JSONObject json = jsonParser.makeHttpRequest(apiUrl, "POST", params);

                if (json != null) {
                    Log.d("JSON result", json.toString());
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        // ferme la boite de dialogue à la terminaison de la tâche de fond
        protected void onPostExecute(JSONObject json) {
            int success = 0;
            String message = "";

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (json != null) {
                //Toast.makeText(MesGroupesActivity.this, json.toString(), Toast.LENGTH_LONG).show();  // TEST/DEBUG
                try {
                    success = json.getInt(TAG_SUCCESS);
                    message = json.getString(TAG_MESSAGE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (success == 1) {
                Log.d("Success!", message);
                // Liste des items trouvées => obtention du tableau des items
                try {
                    items = json.getJSONArray(TAG_TASK);
                    // boucle sur tous les éléments
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject obj = items.getJSONObject(i);

                        // enregistrement de chaque élément JSON dans une variable
                        String id = obj.getString(TAG_ID);
                        String libelle = obj.getString(TAG_LIBELLE);
                        String theme = "Thème : " + obj.getString(TAG_THEME);
                        String etudiant = obj.getString(TAG_ETUDIANT);
                        String classe = obj.getString(TAG_CLASSE);
                        String created = "Créé : " + obj.getString(TAG_CREATED);
                        String hits = "Vue(s) : " + obj.getString(TAG_HITS);
                        String nbcom = "Commentaire(s) : " + obj.getString(TAG_NBCOM);

                        // création d'un nouveau HashMap
                        HashMap<String, String> map = new HashMap<>();

                        // ajout de chaque variable (clé, valeur) dans le HashMap
                        map.put(TAG_ID, id);
                        map.put(TAG_LIBELLE, libelle);
                        map.put(TAG_THEME, theme);
                        map.put(TAG_ETUDIANT, etudiant);
                        map.put(TAG_CLASSE, classe);
                        map.put(TAG_CREATED, created);
                        map.put(TAG_HITS, hits);
                        map.put(TAG_NBCOM, nbcom);

                        // ajout du HashMap dans le tableau des items
                        itemsList.add(map);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.d("Failure", message);
            }

            // mise à jour de l'interface utilisateur (UI) depuis le thread principal
            runOnUiThread(new Runnable() {
                public void run() {
                    // mise à jour de la ListView avec les données JSON mises dans le tableau itemsList
                    ListAdapter adapter;
                    adapter = new SimpleAdapter(
                            PortfoliosActivity.this, itemsList,
                            R.layout.list_portfolio_item, new String[]{TAG_ID, TAG_LIBELLE, TAG_THEME, TAG_ETUDIANT, TAG_CLASSE, TAG_CREATED, TAG_HITS, TAG_NBCOM},
                            new int[]{R.id.aid, R.id.libelle, R.id.theme, R.id.etudiant, R.id.classe, R.id.created, R.id.hits, R.id.nbcom});
                    setListAdapter(adapter);
                }
            });
        }
    }
}