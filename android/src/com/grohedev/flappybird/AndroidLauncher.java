package com.grohedev.flappybird;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grohedev.flappybird.FlappyBird;

public class AndroidLauncher extends AndroidApplication {


    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference salvarPontuacao = databaseReference.child("usuarios").child("pontuacao");
    private int maiorPontoSalvo;

	FlappyBird classe = new FlappyBird();
    private int pontuacao = classe.maiorPontuacao;


	//public static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
	//public static DatabaseReference salvarPontuacao = databaseReference.child("usuarios").child("pontuacao");

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new FlappyBird(), config);

		metSalvarPontuacao(pontuacao);
		Log.i("pontuacao", String.valueOf(pontuacao));

	}

	 private void metSalvarPontuacao(int ponto) {
		 salvarPontuacao.child("Will").setValue(ponto);
	}


}

