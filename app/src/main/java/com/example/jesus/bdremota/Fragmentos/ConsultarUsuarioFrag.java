package com.example.jesus.bdremota.Fragmentos;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.jesus.bdremota.Interfaces.iFragments;
import com.example.jesus.bdremota.R;
import com.example.jesus.bdremota.entidades.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConsultarUsuarioFrag extends Fragment implements Response.Listener<JSONObject>,Response.ErrorListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText campoDni;
    TextView campoNombre,campoProfesion;
    Button btnConsultar;
    ImageView campoImagen;
    ProgressDialog progreso;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    private OnFragmentInteractionListener mListener;

    public ConsultarUsuarioFrag() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static ConsultarUsuarioFrag newInstance(String param1, String param2) {
        ConsultarUsuarioFrag fragment = new ConsultarUsuarioFrag();
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
        View view=inflater.inflate(R.layout.fragment_consultar_usuario, container, false);
        campoDni=(EditText)view.findViewById(R.id.ETDni);
        campoNombre=(TextView) view.findViewById(R.id.TVNombre);
        campoProfesion=(TextView) view.findViewById(R.id.TVProfesion);
        btnConsultar=(Button) view.findViewById(R.id.btnConsultar);
        campoImagen=(ImageView) view.findViewById(R.id.IVimagen);
        request= Volley.newRequestQueue(getContext());

        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarWebService();
            }
        });

        return view;
    }

    private void cargarWebService() {

        progreso= new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();
        String url="http://"+ iFragments.ip+"/ejemploBDremota/wsJSONConsultarUsuarioImagen.php?DNI="+campoDni.getText().toString();
        //  String url="http://192.168.0.24/ejemploBDremota/wsJSONConsultarUsuario.php?DNI="+campoDni.getText().toString();
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        //permite conectarse al webservis y ahora puedo usar los 2 siguentes metodos onErrorResponse, onResponse
        request.add(jsonObjectRequest);

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(),"error "+error.toString(),Toast.LENGTH_SHORT).show();
        progreso.hide();
        Log.i("ERROR ==>> ",error.toString());
    }

    @Override
    public void onResponse(JSONObject response) {

        progreso.hide();
        Usuario user=null;
        JSONArray json=response.optJSONArray("usuario");
        try {
            for (int i=0;i<json.length();i++){
                user=new Usuario();
                JSONObject jsonObject=null;
                jsonObject=json.getJSONObject(0);
                user.setNombre(jsonObject.optString("NOMBRE"));
                user.setProfesion(jsonObject.optString("PROFESION"));
                user.setDato(jsonObject.optString("IMAGEN"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        campoNombre.setText(user.getNombre());
        campoProfesion.setText(user.getProfesion());
        if (user.getImagen()!=null){
            campoImagen.setImageBitmap(user.getImagen());
        }else{
            campoImagen.setImageResource(R.drawable.home);
        }

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
