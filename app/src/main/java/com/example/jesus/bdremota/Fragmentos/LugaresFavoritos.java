package com.example.jesus.bdremota.Fragmentos;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jesus.bdremota.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LugaresFavoritos extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    MapView mMapView;
    View mView;

    private GoogleMap googleMap;
    private Marker marcador;
    double lat = 0.0;
    double lng = 0.0;
    String mensaje1;
    String direccion = "";
    String pais = "";


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public LugaresFavoritos() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LugaresFavoritos newInstance(String param1, String param2) {
        LugaresFavoritos fragment = new LugaresFavoritos();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView= inflater.inflate(R.layout.fragment_lugares_favoritos, container, false);
        return mView;
    }

    //HERE MY CODE
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView=(MapView) mView.findViewById(R.id.map);
        if (mMapView!=null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mMap=googleMap;
        //googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //googleMap.addMarker(new MarkerOptions().position(new LatLng(-9.365198,-75.0195))
          //      .title("Peru:")
            //    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        //CameraPosition peru=CameraPosition.builder().target( new LatLng(9.365198,-75.0195)).zoom(4).bearing(0).build();
        //googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(peru));
        miUbicacion();
    }

    private static int PETICION_PERMISO_LOCALIZACION = 101;

    private void miUbicacion() {

        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
            return;
        } else {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            ActualizarUbicacion(location);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1200,0,locListener);
        }
    }
    //agregar el marcador en el mapa
    private void AgregarMarcador(double lat, double lng) {
        LatLng coordenadas = new LatLng(lat, lng);
        //tipo de mapa
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings uiSettings=mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        float zoomlevel=17.5f;
        CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, zoomlevel);

        if (marcador != null) marcador.remove();
        marcador = mMap.addMarker(new MarkerOptions()
                .position(coordenadas)
                .title("Dirección:" + direccion)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        //.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));

        mMap.animateCamera(MiUbicacion);
    }


    //actualizar la ubicacion
    private void ActualizarUbicacion(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            AgregarMarcador(lat, lng);

        }
    }
    //control del gps
    LocationListener locListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

            ActualizarUbicacion(location);

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {
            mensaje1 = ("GPS Activado");
            Mensaje();

        }

        @Override
        public void onProviderDisabled(String s) {
            mensaje1 = ("GPS Desactivado");
            Mensaje();

        }
    };

    public void Mensaje() {
        Toast toast = Toast.makeText(getContext(), mensaje1, Toast.LENGTH_LONG);
        //toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
