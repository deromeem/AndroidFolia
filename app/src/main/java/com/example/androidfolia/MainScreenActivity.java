package com.example.androidfolia;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainScreenActivity extends AppCompatActivity {

    // paramètres de connexion à transmettre par intent à l'activité suivante
    String userLogin = "";
    String userPwd = "";
    private static final String TAG_USER_LOGIN = "login";
    private static final String TAG_USER_PWD = "pwd";

    // contrôles de la vue (layout) correspondante
    TextView txtUserName;
    Button btnViewMesGroupes;
    Button btnViewPortfolios;
    Button btnViewEtudiants;

    String userName = "";
    private static final String TAG_USER_NAME = "name";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        // récupération de l'intent de la vue courante
        Intent in = getIntent();
        // obtention des paramêtres du user connecté à partir de l'intent
        userName = in.getStringExtra(TAG_USER_NAME);
        userLogin = in.getStringExtra(TAG_USER_LOGIN);
        userPwd = in.getStringExtra(TAG_USER_PWD);

        // déclaration des textes et boutons
        txtUserName = (TextView) findViewById(R.id.txtUserName);
        txtUserName.setText(userName);
        btnViewMesGroupes = (Button) findViewById(R.id.btnViewMesGroupes);
        btnViewPortfolios = (Button) findViewById(R.id.btnViewPortfolios);
        btnViewEtudiants = (Button) findViewById(R.id.btnViewEtudiants);

        // événement de clic sur le bouton de visualisation de Mes groupes
        btnViewMesGroupes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(), MesGroupesActivity.class);
                in.putExtra(TAG_USER_LOGIN, userLogin);
                in.putExtra(TAG_USER_PWD, userPwd);
                startActivity(in);
            }
        });

        // événement de clic sur le bouton de visualisation des portfolios
        btnViewPortfolios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(), PortfoliosActivity.class);
                in.putExtra(TAG_USER_LOGIN, userLogin);
                in.putExtra(TAG_USER_PWD, userPwd);
                startActivity(in);
            }
        });

        // événement de clic sur le bouton de visualisation des etudiants
        btnViewEtudiants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(), EtudiantsActivity.class);
                in.putExtra(TAG_USER_LOGIN, userLogin);
                in.putExtra(TAG_USER_PWD, userPwd);
                startActivity(in);
            }
        });

    }
}