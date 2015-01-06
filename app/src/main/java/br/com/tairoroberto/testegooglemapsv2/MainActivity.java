package br.com.tairoroberto.testegooglemapsv2;


import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends FragmentActivity {
    private SupportMapFragment mapFragment;
    private SupportMapFragment mapFragment2;
    private GoogleMap googleMap;
    private GoogleMap googleMap2;
    private Marker marker;
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
        final LatLng latLng = new LatLng(-23.548998, -46.633058);

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

        //MARCADORES
     //   custonMarker(new LatLng(-23.548998, -46.633058),"Marcador 1","O marcador 1 foi reposicionado");
     //   custonMarker(new LatLng(-23.548905, -46.633002),"Marcador 2","O marcador 2 foi reposicionado");

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                LinearLayout layout = new LinearLayout(getBaseContext());
                layout.setPadding(20,20,20,20);
                layout.setBackgroundColor(Color.GREEN);
                layout.setOrientation(LinearLayout.VERTICAL);

                TextView textView = new TextView(getBaseContext());
                textView.setText(Html.fromHtml("<b><font color=\"#ff0000\">"+marker.getTitle()+":</font></b>"+marker.getSnippet()));

                Button btnTeste = new Button(getBaseContext());
                btnTeste.setText("Botão de teste");
                btnTeste.setBackgroundColor(Color.BLUE);

                //teste com listener de botão dentro do infoWindow
                //o Clique no infoWindow não funciona
                //ele pega o clique do infowindow e não do botão
                //ele trava os filhos do infowindow como se fosse uma imagem
                btnTeste.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("Script","Clique do botão dentro do infoWindow");
                    }
                });

                layout.addView(textView);
                layout.addView(btnTeste);

                return layout;

            }

            @Override
            public View getInfoContents(Marker marker) {
                TextView textView = new TextView(getBaseContext());
                textView.setText(Html.fromHtml("<b><font color=\"#ff0000\">"+marker.getTitle()+":</font></b>"+marker.getSnippet()));
                return textView;
            }
        });

        //Listenerspara ouvir eventos do mapa
        //Evento de quando a camera muda de posição
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.i("Script","setOnCameraChangeListener()");
                if (marker != null){
                    marker.remove();
                }

                custonMarker(new LatLng(cameraPosition.target.latitude,cameraPosition.target.longitude),"1: Marcador alterado","O marcador foi reposicionado");
            }
        });

        //Evento de clique no mapa
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.i("Script","setOnMapClickListener()");
                if (marker != null){
                    marker.remove();
                }

                custonMarker(new LatLng(latLng.latitude,latLng.longitude),"2: Marcador alterado","O marcador foi reposicionado");
            }
        });

        //Evento de clique no marcador do mapa
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i("Script","3: Marker: "+ marker.getTitle());

                //quando retorna false = o android qeu administra as mudanção no marcador
                //quando retorna false = o android assume que vc ira trator todas as mudanção do markador
                return false;
            }
        });

        //Evento de clique no  infoWindow "Janekla de informação"
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.i("Script","4: setOnInfoWindowClickListener()");
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


    //funcão que adiciona o marcador no mapa
    public void custonMarker( LatLng latLng, String title, String snippet){
        MarkerOptions options = new MarkerOptions();
        options.position(latLng).title(title).snippet(snippet).draggable(true);

        //Muda o Pin do mapa "a imagem do marcador"
       // options.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_mapa));

        //Adiciona as opçções ao marker
        marker = googleMap.addMarker(options);

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
