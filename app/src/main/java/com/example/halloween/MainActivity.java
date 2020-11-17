package com.example.halloween;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    int[] sonidosHalloween = {R.raw.animales,R.raw.ataud,R.raw.cadenas,R.raw.campana,R.raw.cristal,R.raw.crujido,R.raw.gato,R.raw.murcielagos,R.raw.perro,R.raw.puerta};

    //VERSIÓN FINAL



    static Button[] arrayBotones;
    static EditText[] duracionSonidos;
    static Spinner[] sonidos;
    static reproducirMusica[] arrayHilos;
    static MediaPlayer[] arrayPlayer;
    static Thread[] arrayThread;
    static int[] arrayTiempo;
    EditText numeroSonidos;
    Button susto;
    Button reproducirTodo;
    int flag=0;
    int tiempo;
    int contador;
    int contadorAux=0;
    LinearLayout layout;
    int cuentaSustos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numeroSonidos = findViewById(R.id.numeroSonidos);
        susto = findViewById(R.id.botonSustos);
        layout = findViewById(R.id.layout2);

        String[] nombreSonidos = {
                getResources().getString(R.string.animales),
                getResources().getString(R.string.ataud),
                getResources().getString(R.string.cadenas),
                getResources().getString(R.string.campana),
                getResources().getString(R.string.cristal),
                getResources().getString(R.string.crujido),
                getResources().getString(R.string.gato),
                getResources().getString(R.string.murcielagos),
                getResources().getString(R.string.perro),
                getResources().getString(R.string.puerta)};


        susto.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(numeroSonidos.getText())){

                    Toast toast1 = Toast.makeText(getApplicationContext(),getResources().getString(R.string.errorSonidos),Toast.LENGTH_LONG);
                    toast1.show();

                }else{

                    int aux = Integer.parseInt(numeroSonidos.getText().toString());

                    if(aux>10){

                        Toast toast3 =  Toast.makeText(getApplicationContext(),getResources().getString(R.string.maximoSonidos),Toast.LENGTH_LONG);
                        toast3.show();

                    }else{

                        susto.setEnabled(false);
                        contadorAux=0;
                        cuentaSustos = Integer.parseInt(numeroSonidos.getText().toString());
                        crearBotones(cuentaSustos,layout,nombreSonidos);

                    }


                }

            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void crearBotones(int n, LinearLayout linear, String[] nombreSonidos){

        arrayBotones = new Button[n];
        duracionSonidos = new EditText[n];
        sonidos = new Spinner[n];
        arrayHilos = new reproducirMusica[n];
        arrayPlayer = new MediaPlayer[n];
        arrayThread = new Thread[n];
        arrayTiempo = new int[n];
        contador=n;

            for(int i=0;i<n;i++){


                arrayHilos[i]=null;
                arrayThread[i]=null;

                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout layout2 = new LinearLayout(this);
                layout2.setLayoutParams(params1);
                layout2.setPadding(0,0,0,0);
                layout2.setOrientation(LinearLayout.HORIZONTAL);

                //Lista de sonidos

                Spinner sp = new Spinner(this);
                sp.setId(View.generateViewId());
                ArrayAdapter<String> adaptadorModulos;
                adaptadorModulos = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,nombreSonidos);
                sp.setAdapter(adaptadorModulos);
                sonidos[i]=sp;
                sp.setLayoutParams(params1);
                layout2.addView(sp);


                //EditText duracion

                EditText tx = new EditText(this);
                tx.setId(View.generateViewId());
                tx.setInputType(InputType.TYPE_CLASS_NUMBER);
                duracionSonidos[i]=tx;
                tx.setPadding(60,0,60,0);
                tx.setLayoutParams(params1);
                layout2.addView(tx);


                //Boton play

                Button b = new Button(this);
                arrayBotones[i]=b;
                b.setId(View.generateViewId());
                b.setLayoutParams(params1);
                b.setText(getResources().getString(R.string.reproducir));
                layout2.addView(b);

                linear.addView(layout2);

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int p = Arrays.asList(arrayBotones).indexOf(b);

                        if(TextUtils.isEmpty(duracionSonidos[p].getText())){

                            Toast toast2 =
                                    Toast.makeText(getApplicationContext(),
                                            getResources().getString(R.string.cuentaAtrás), Toast.LENGTH_SHORT);
                            toast2.show();
                            flag=1;

                        }else{

                        flag=0;
                        b.setEnabled(false);
                        sonidos[p].setEnabled(false);
                        duracionSonidos[p].setEnabled(false);

                        int pos = sp.getSelectedItemPosition();
                        int id = sonidosHalloween[pos];
                        arrayPlayer[p] = crearSonido(id);
                        arrayTiempo[p] =  Integer.parseInt(duracionSonidos[p].getText().toString());
                        Boolean hiloActivo = true;


                            if(arrayThread[p]==null){

                                arrayThread[p] = new Thread(){
                                    @Override
                                    public void run() {


                                        //arrayPlayer[p].start();

                                        while(arrayTiempo[p]>=0){



                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    duracionSonidos[p].setText(Integer.toString(arrayTiempo[p]));
                                                    if(arrayTiempo[p]==0){

                                                        arrayPlayer[p].start();

                                                    }

                                                }
                                            });

                                            try {
                                                sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                            arrayTiempo[p]--;

                                        }

                                    }
                                };

                                arrayThread[p].start();



                            }

                            arrayPlayer[p].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    layout2.removeView(arrayBotones[p]);
                                    layout2.removeView(duracionSonidos[p]);
                                    layout2.removeView(sonidos[p]);

                                    contadorAux++;

                                    if(contadorAux==contador){

                                        susto.setEnabled(true);
                                        layout2.removeView(reproducirTodo);
                                        layout2.removeAllViews();
                                        linear.removeAllViews();

                                    }

                                }
                            });

                        }



                    }


                });

            }

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout layout2 = new LinearLayout(this);
        layout2.setLayoutParams(params1);
        layout2.setPadding(0,0,0,0);
        layout2.setOrientation(LinearLayout.HORIZONTAL);

        reproducirTodo = new Button(this);
        reproducirTodo.setId(View.generateViewId());
        reproducirTodo.setLayoutParams(params1);
        reproducirTodo.setText(getResources().getString(R.string.reproducirTodo));
        layout2.addView(reproducirTodo);

        linear.addView(layout2);

        reproducirTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                for(int i=0;i<n;i++){

                    arrayBotones[i].performClick();

                }
                if(flag==0){

                    reproducirTodo.setEnabled(false);

                }

            }
        });



    }

    public MediaPlayer crearSonido(int id){

        MediaPlayer mediaPlayer = MediaPlayer.create(this,id);

        return mediaPlayer;

    }

    class reproducirMusica extends AsyncTask<String,String,String> {

        int pos,id,tiempo;
        EditText tx;
        MediaPlayer mp;

        public reproducirMusica(int pos, int id,EditText tx, int tiempo, MediaPlayer mp){

            this.pos=pos;
            this.id=id;
            this.tx=tx;
            this.tiempo=tiempo;
            this.mp=mp;

        }

        @Override
        protected void onProgressUpdate(String... values) {
            tx.setText(Integer.toString(tiempo));
        }

        @Override
        protected String doInBackground(String... strings) {

            mp.start();

            while(tiempo>0){

                tiempo--;

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                publishProgress();

            }

            if(tiempo==0){
                mp.stop();
            }


            return null;
        }
    }

}