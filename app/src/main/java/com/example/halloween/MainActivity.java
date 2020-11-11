package com.example.halloween;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    int[] sonidosHalloween = {R.raw.animales,R.raw.ataud,R.raw.cadenas,R.raw.campana,R.raw.cristal,R.raw.crujido,R.raw.gato,R.raw.murcielagos,R.raw.perro,R.raw.puerta};
    String[] nombreSonidos = {"animales","ataud","cadenas","campana","cristal","crujido","gato","murcielagos","perro","puerta"};
    /*getResources().getString(R.string.animales),
                                getResources().getString(R.string.ataud),
                                getResources().getString(R.string.cadenas),
                                getResources().getString(R.string.campana),
                                getResources().getString(R.string.cristal),
                                getResources().getString(R.string.crujido),
                                getResources().getString(R.string.gato),
                                getResources().getString(R.string.murcielagos),
                                getResources().getString(R.string.perro),
                                getResources().getString(R.string.puerta)};*/
    static Button[] arrayBotones;
    static EditText[] duracionSonidos;
    static Spinner[] sonidos;
    static reproducirMusica[] arrayHilos;
    static MediaPlayer[] arrayPlayer;
    static Thread[] arrayThread;
    EditText numeroSonidos;
    Button susto;
    LinearLayout layout;
    int cuentaSustos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numeroSonidos = findViewById(R.id.numeroSonidos);
        susto = findViewById(R.id.botonSustos);
        layout = findViewById(R.id.layout2);




        susto.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {

                cuentaSustos = Integer.parseInt(numeroSonidos.getText().toString());
                crearBotones(cuentaSustos,layout);

            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void crearBotones(int n, LinearLayout linear){

        arrayBotones = new Button[n];
        duracionSonidos = new EditText[n];
        sonidos = new Spinner[n];
        arrayHilos = new reproducirMusica[n];
        arrayPlayer = new MediaPlayer[n];
        arrayThread = new Thread[n];

            for(int i=0;i<n;i++){

                arrayHilos[i]=null;
                arrayThread[i]=null;

                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout layout2 = new LinearLayout(this);
                layout2.setLayoutParams(params1);
                layout2.setPadding(0,0,0,20);
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
                b.setText("Reproducir");
                layout2.addView(b);

                linear.addView(layout2);

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        b.setEnabled(false);
                        int p = Arrays.asList(arrayBotones).indexOf(b);

                        int pos = sp.getSelectedItemPosition();
                        int id = sonidosHalloween[pos];
                        arrayPlayer[p] = crearSonido(id);

                        if(arrayThread[p]==null){

                            arrayThread[p] = new Thread(){
                                @Override
                                public void run() {

                                    int tiempo = Integer.parseInt(duracionSonidos[p].getText().toString());
                                    int aux = tiempo;
                                    arrayPlayer[p].start();

                                    while(tiempo>0){

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                duracionSonidos[p].setText(Integer.toString(aux));

                                            }
                                        });

                                        try {
                                            sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                        tiempo--;

                                    }

                                }
                            };

                            arrayThread[p].start();

                        }




                         //arrayHilos[p] = new reproducirMusica(p,id,duracionSonidos[p],tiempo,arrayPlayer[p]);
                         //arrayHilos[p].execute();

                    }
                });


            }



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