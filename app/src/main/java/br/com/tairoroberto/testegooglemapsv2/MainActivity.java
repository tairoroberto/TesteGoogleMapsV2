package br.com.tairoroberto.testegooglemapsv2;


import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;


public class MainActivity extends FragmentActivity {
    private SupportMapFragment mapFragment;
    private SupportMapFragment mapFragment2;
    private GoogleMap googleMap;
    private GoogleMap googleMap2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //MAPS VIA XML
        //Declara o fragment do maps
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        googleMap = mapFragment.getMap();
        //ativa o mapa do xml
        configmap();


        //MAPS VIA API
        //Opção de ordenação do mapa e NavigatorDrawer
        GoogleMapOptions options = new GoogleMapOptions();
        options.zOrderOnTop(true);

        //Declara o fragment do maps
        mapFragment2 = SupportMapFragment.newInstance(options);

        //Add o mapFragment ao layout
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.layout,mapFragment2);
        transaction.commit();



    }

    //o mapa deve ser carregado no onresume
    @Override
    protected void onResume() {
        super.onResume();
        new Thread(){
            public void run(){
                while (mapFragment2.getMap() == null){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        configmap2();
                    }
                });

            }
        }.start();
    }
    public void configmap(){
        //Se mapFragment não for null, será carregado os googleMap
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Latitude e logitude
        LatLng latLng = new LatLng(-23.548998, -46.633058);

        //Configura a posição da camera, local, zoom etc.
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).bearing(0).tilt(45).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

        //atualiza a posicao da camera no mapa
        //googleMap.moveCamera(cameraUpdate);

        //animação do mapa
        googleMap.animateCamera(cameraUpdate, 3000, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                Log.i("Script","Animação: CancelableCallback.onFinish()");
            }

            @Override
            public void onCancel() {
                Log.i("Script","Animação: CancelableCallback.onCancel()");
            }
        });
    }

    public void configmap2(){
        //Se mapFragment não for null, será carregado os googleMap
        googleMap2 = mapFragment2.getMap();
        googleMap2.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Latitude e logitude
        LatLng latLng = new LatLng(-23.548998, -46.633058);

        //Configura a posição da camera, local, zoom etc.
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(18).bearing(0).tilt(90).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

        //atualiza a posicao da camera no mapa
        //googleMap.moveCamera(cameraUpdate);

        //animação do mapa
        googleMap2.animateCamera(cameraUpdate, 3000, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                Log.i("Script","Animação: CancelableCallback.onFinish()");
            }

            @Override
            public void onCancel() {
                Log.i("Script","Animação: CancelableCallback.onCancel()");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
