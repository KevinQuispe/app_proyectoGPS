package com.example.jesus.bdremota.Fragmentos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.icu.util.TimeZone;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jesus.bdremota.Interfaces.iFragments;
import com.example.jesus.bdremota.R;
import com.example.jesus.bdremota.entidades.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static android.support.v4.content.ContextCompat.checkSelfPermission;
import static android.Manifest.permission.CAMERA;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConsultarUsuario2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConsultarUsuario2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsultarUsuario2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ImageView campoImg;
    RequestQueue requestQueue;
    JsonObjectRequest jsonObjectRequest;
    Button cargar,btnEliminar,btnActualizar;
    private final String CARPETA_RAIZ="Imagenes/";
    private final String RUTA_IMAGEN=CARPETA_RAIZ+"fotos";
    final int CodeSelecciona=10;
    final int COdeFoto=20;
    EditText ETdni, txtNombre,txtProfesion;;
    ProgressDialog dialog;
    RequestQueue request;
    String path;//ruta imagen
    StringRequest stringRequest;
    Bitmap bitmap;
    private OnFragmentInteractionListener mListener;

    public ConsultarUsuario2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConsultarUsuario2.
     */
    // TODO: Rename and change types and number of parameters
    public static ConsultarUsuario2 newInstance(String param1, String param2) {
        ConsultarUsuario2 fragment = new ConsultarUsuario2();
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
        View vista=inflater.inflate(R.layout.fragment_consultar_usuario2, container, false);
        campoImg=(ImageView) vista.findViewById(R.id.IVimagen2);
        campoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarImagen();
            }
        });
        requestQueue= Volley.newRequestQueue(getContext());
        txtNombre=(EditText) vista.findViewById(R.id.TVNombre2);
        txtProfesion=(EditText) vista.findViewById(R.id.TVProfesion2);
        ETdni=(EditText) vista.findViewById(R.id.ETDni2);
        cargar=(Button) vista.findViewById(R.id.btnCargar2);
        cargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarWebService();
            }
        });
        btnEliminar=(Button) vista.findViewById(R.id.btnEliminar);
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarUsuario(ETdni.getText().toString());
            }
        });
        btnActualizar=(Button) vista.findViewById(R.id.btnActualizar);
        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webServiceActualizar();
            }
        });
        if(validarPermisosParaVersionesSuperiores()){
            btnEliminar.setEnabled(true);
            btnActualizar.setEnabled(true);
        }else {
            btnEliminar.setEnabled(false);
            btnActualizar.setEnabled(false);
        }
        return vista;
    }
    private void cargarImagen() {
        //Intent.ACTION_GET_CONTENT permite visualisar imagenes reciens y con diversos programas
        final CharSequence opciones[]={"Tomar Foto","Cargar Imagen","Cancelar"};
        final AlertDialog.Builder alertOption=new AlertDialog.Builder(getContext());
        alertOption.setTitle("Seleccione una opcion");
        alertOption.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(opciones[i].equals("Tomar Foto")){
                    TomarFotografia();
                }else if (opciones[i].equals("Cargar Imagen")){
                    Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/");
                    startActivityForResult(intent.createChooser(intent,"Seleccione aplicacion"),CodeSelecciona);
                }else {
                    dialogInterface.dismiss();//cierra el dialogo
                }
            }


        });
        alertOption.show();

    }
    private boolean validarPermisosParaVersionesSuperiores() {
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }
        if ((checkSelfPermission(getContext(),CAMERA)== PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(getContext(),WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)){
            return true;
        }
        if ((shouldShowRequestPermissionRationale(CAMERA))||(shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            cargarDialogoRecomendacion();
        }else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
        }
        return false;
    }

    private void TomarFotografia() {
        File fileImagen=new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);
        boolean iscreada=fileImagen.exists();
        String nombreImg="";
        if (iscreada==false){
            iscreada=fileImagen.mkdirs();
        } if(iscreada==true) {

            //revisar
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                nombreImg = (TimeZone.SystemTimeZoneType.values()) + ".jpg";
            }
            Log.i("archivo ", "Patch: " + nombreImg);

            //ruta de almacenamiento de la imagen
            path = Environment.getExternalStorageDirectory() + File.separator + RUTA_IMAGEN + File.separator + nombreImg;
            File imagen = new File(path);
            //acrivar camara
            Intent intent = null;
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
            //para andrid 7
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                String authorities = getContext().getPackageName() + ".provider";
                Uri imageUri = FileProvider.getUriForFile(getContext(), authorities, imagen);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
            }

            //hasta aqui 7

            //?
            startActivityForResult(intent, COdeFoto);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==100){
            if (grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED &&
                    grantResults[1]==PackageManager.PERMISSION_GRANTED ){
                btnEliminar.setEnabled(true);
            }else {
                solicitarPermisosManual();
            }
        }
    }
    private void solicitarPermisosManual() {
        final CharSequence[] opciones={"si","no"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(getContext());//estamos en fragment
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("si")){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getContext().getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(),"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }
    private void cargarWebService() {
        dialog=new ProgressDialog(getContext());
        dialog.setMessage("cargando...");
        dialog.show();
        String url="http://"+ iFragments.ip+"ejemploBDRemota/wsJSONConsultarUsuarioImagenURL.php?DNI="+ETdni.getText().toString();
        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.hide();
                Usuario user=null;
                JSONArray json=response.optJSONArray("usuario");
                try {
                    for (int i=0;i<json.length();i++){
                        user=new Usuario();
                        JSONObject jsonObject=null;
                        jsonObject=json.getJSONObject(0);
                        user.setNombre(jsonObject.optString("NOMBRE"));
                        user.setProfesion(jsonObject.optString("PROFESION"));
                        user.setRuta(jsonObject.optString("ruta_imagen"));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                txtNombre.setText(user.getNombre());
                txtProfesion.setText(user.getProfesion());
                String urlImagen="http://"+ iFragments.ip+"/ejemploBDRemota/"+user.getRuta();

                cargarImagenServer(urlImagen);


            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"error",Toast.LENGTH_SHORT).show();
                dialog.hide();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void cargarDialogoRecomendacion() {
        final AlertDialog.Builder dialogo=new AlertDialog.Builder(getContext());
        dialogo.setTitle("Permisos desactivados");
        dialogo.setMessage("debe aceptar los permisos");
        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
            }
        });
        dialogo.show();
    }

    private void cargarImagenServer(String urlI) {
        urlI=urlI.replace(" ","%20");
        ImageRequest imageRequest=new ImageRequest(urlI, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                bitmap=response;//SE MODIFICA
                campoImg.setImageBitmap(response);
            }
        },0,0,ImageView.ScaleType.CENTER,null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(imageRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            switch (requestCode){
                case CodeSelecciona:{
                    Uri Mypath=data.getData();
                    campoImg.setImageURI(Mypath);
                    try {
                        bitmap= MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),Mypath);
                        campoImg.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case COdeFoto:{
                    //permito que se almacenen las imagenes den los dispositivos
                    MediaScannerConnection.scanFile(getContext(), new String[]{path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String s, Uri uri) {
                                    Log.i("Ruta almacenamiento","Patch: "+path);
                                }
                            });
                    bitmap= BitmapFactory.decodeFile(path);

                    campoImg.setImageBitmap(bitmap);

                    break;
                }
            }

        }
        bitmap=redimencionarImagen(bitmap,600,800);
    }

    private Bitmap redimencionarImagen(Bitmap bitmap, float anchoN, float altoN) {
        int ancho=bitmap.getWidth();
        int alto=bitmap.getHeight();
        if (ancho>anchoN || alto>altoN){
            float escalaAncha=anchoN/ancho;
            float escalaAlto=altoN/alto;
            //manipulando la imagen
            Matrix matrix=new Matrix();
            matrix.postScale(escalaAncha,escalaAlto);
            return Bitmap.createBitmap(bitmap,0,0,ancho,alto,matrix,false);
        }else {
            return bitmap;
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
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    private void eliminarUsuario(String dni) {
        dialog=new ProgressDialog(getContext());
        dialog.setMessage("Cargando...");
        dialog.show();

        String url="http://"+ iFragments.ip+"/ejemploBDRemota/wsJSONDeleteMovil.php?DNI="+dni;

        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.hide();

                if (response.trim().equalsIgnoreCase("elimina")){
                    txtNombre.setText("");
                    txtProfesion.setText("");
                    ETdni.setText("");
                    campoImg.setImageResource(R.drawable.nodisponible);
                    Toast.makeText(getContext(),"Se ha Eliminado con exito",Toast.LENGTH_SHORT).show();
                }else{
                    if (response.trim().equalsIgnoreCase("noExiste")){
                        Toast.makeText(getContext(),"No se encuentra la persona ",Toast.LENGTH_SHORT).show();
                        Log.i("RESPUESTA: ",""+response);
                    }else{
                        Toast.makeText(getContext(),"No se ha Eliminado ",Toast.LENGTH_SHORT).show();
                        Log.i("RESPUESTA: ",""+response);
                    }

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"No se ha podido conectar",Toast.LENGTH_SHORT).show();
                dialog.hide();
            }
        });
        requestQueue.add(stringRequest);
        //VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(stringRequest);
    }

    private void webServiceActualizar() {
        dialog=new ProgressDialog(getContext());
        dialog.setMessage("Cargando...");
        dialog.show();

        String url="http://"+ iFragments.ip+"/ejemploBDRemota/wsJSONUpdateMovil.php?";

        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.hide();

                if (response.trim().equalsIgnoreCase("actualiza")){
                    // etiNombre.setText("");
                    //  txtDocumento.setText("");
                    //   etiProfesion.setText("");
                    Toast.makeText(getContext(),"Se ha Actualizado con exito",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(),"No se ha Actualizado ",Toast.LENGTH_SHORT).show();
                    Log.i("RESPUESTA: ",""+response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),error.getMessage().toString(),Toast.LENGTH_SHORT).show();
                dialog.hide();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String DNI=ETdni.getText().toString();
                String nombre=txtNombre.getText().toString();
                String profesion=txtProfesion.getText().toString();

                String imagen=convertirImgString(bitmap);

                Map<String,String> parametros=new HashMap<>();
                parametros.put("DNI",DNI);
                parametros.put("NOMBRE",nombre);
                parametros.put("PROFESION",profesion);
                parametros.put("IMAGEN",imagen);

                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    private String convertirImgString(Bitmap bitmap) {

        ByteArrayOutputStream array=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte[] imagenByte=array.toByteArray();
        String imagenString= Base64.encodeToString(imagenByte, Base64.DEFAULT);

        return imagenString;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
