package com.example.jesus.bdremota.Fragmentos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.icu.util.TimeZone;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ByteArrayPool;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jesus.bdremota.Interfaces.iFragments;
import com.example.jesus.bdremota.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
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
 * {@link RegistrarUsuarioFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegistrarUsuarioFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrarUsuarioFrag extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    //sirven
    private final String CARPETA_RAIZ="Imagenes/";
    private final String RUTA_IMAGEN=CARPETA_RAIZ+"fotos";
    final int CodeSelecciona=10;
    final int COdeFoto=20;
    ImageView campoImagen;
    EditText campoDNI,campoNOMBRE,campoPROFESION;
    Button btnRegisUser,btnExaminar;
    ProgressDialog progreso;
    //establecer conexion con el server;
    RequestQueue request;
    String path;//ruta imagen
    JsonObjectRequest jsonObjectRequest;
    StringRequest stringRequest;
    Bitmap bitmap;
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RegistrarUsuarioFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistrarUsuarioFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistrarUsuarioFrag newInstance(String param1, String param2) {
        RegistrarUsuarioFrag fragment = new RegistrarUsuarioFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    private boolean validarPermisos() {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==100){
            if (grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED &&
                    grantResults[1]==PackageManager.PERMISSION_GRANTED ){
                btnRegisUser.setEnabled(true);
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

  /*  private void solicitarPermisosManual() {
        final CharSequence opciones[]={"si","no"};
        final AlertDialog.Builder alertOption=new AlertDialog.Builder(getContext());
        alertOption.setTitle("Desea configiarar de forma manual");
        alertOption.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(opciones[i].equals("si")){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
              /      Uri uri=Uri.fromParts("package",getPackageName(getContext()),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else {
                    dialogInterface.dismiss();//cierra el dialogo
                }
            }


        });
        alertOption.show();
    }  */

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_registrar_usuario, container, false);
        campoDNI=(EditText) view.findViewById(R.id.ETDni);
        campoNOMBRE=(EditText) view.findViewById(R.id.ETNombre);
        campoPROFESION=(EditText) view.findViewById(R.id.ETProfesion);
        btnRegisUser=(Button) view.findViewById(R.id.btnRegistrar);

        request= Volley.newRequestQueue(getContext());
        btnRegisUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarWebService();
            }
        });
        btnExaminar=(Button) view.findViewById(R.id.btnExamina);
        btnExaminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarImagen();
            }
        });
        campoImagen=(ImageView) view.findViewById(R.id.IVimagenReg);
        bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.nodisponible);
        campoImagen.setImageBitmap(bitmap);

        super.onCreate(savedInstanceState);
        if(validarPermisos()){
            btnRegisUser.setEnabled(true);
        }else {
            btnRegisUser.setEnabled(false);
        }
        return view;
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

    private void TomarFotografia() {
        File fileImagen=new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);
        boolean iscreada=fileImagen.exists();
        String nombreImg="";
        if (iscreada==false){
            iscreada=fileImagen.mkdirs();
        } if(iscreada==true){
            nombreImg= (TimeZone.SystemTimeZoneType.values())+".jpg";
            Log.i("archivo ","Patch: "+nombreImg);

            //ruta de almacenamiento de la imagen
            path=Environment.getExternalStorageDirectory()+File.separator+RUTA_IMAGEN+File.separator+nombreImg;
            File imagen=new File(path);
            //acrivar camara
            Intent intent=null;
            intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(imagen));
            //para andrid 7
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                String authorities=getContext().getPackageName()+".provider";
                Uri imageUri= FileProvider.getUriForFile(getContext(),authorities,imagen);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
            }else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(imagen));
            }

            //hasta aqui 7

            //?
            startActivityForResult(intent,COdeFoto);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            switch (requestCode){
                case CodeSelecciona:{
                    Uri Mypath=data.getData();
                    campoImagen.setImageURI(Mypath);
                    Toast.makeText(getContext(),Mypath.toString()+"        "+data.getData()+"       "
                            +campoImagen.getContext().getPackageResourcePath(),Toast.LENGTH_LONG).show();
                    try {
                        bitmap=MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),Mypath);
                        campoImagen.setImageBitmap(bitmap);
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

                    campoImagen.setImageBitmap(bitmap);

                    break;
                }default:{

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

    private void cargarWebService() {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();
        //   String url="http://192.168.0.24/ejemploBDremota/wsJSONregistro.php?DNI="+campoDNI.getText().toString()+"&NOMBRE="+
        //            campoNOMBRE.getText().toString()+"&PROFESION="+campoPROFESION.getText().toString();
        String url="http://"+ iFragments.ip+"/ejemploBDremota/wsJSONRegistroMovil.php";
        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progreso.hide();
                if (response.trim().equals("registra")){
                    campoDNI.setText("");
                    campoNOMBRE.setText("");
                    campoPROFESION.setText("");
                    Toast.makeText(getContext(),"se registro con exito",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(),"error al registrar",Toast.LENGTH_SHORT).show();
                    Log.i("errror ",response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),error.getMessage().toString(),Toast.LENGTH_SHORT).show();
                Log.i("errror 2 ",error.getMessage().toString());
                progreso.hide();
            }
        }){//para enviar los parametros a nuestro web service
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String dni=campoDNI.getText().toString();
                String nombre=campoNOMBRE.getText().toString();
                String profesion=campoPROFESION.getText().toString();
                String imagen=convertirImagenString(bitmap);

                Map<String,String> parametros=new HashMap<>();
                parametros.put("DNI",dni);
                parametros.put("NOMBRE",nombre);
                parametros.put("PROFESION",profesion);
                parametros.put("IMAGEN",imagen);

                return parametros;
            }
        };
        request.add(stringRequest);
    }
    private String convertirImagenString(Bitmap bitmap) {
        ByteArrayOutputStream array=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte [] imageBytes=array.toByteArray();
        String imagenString= Base64.encodeToString(imageBytes,Base64.DEFAULT);
        return imagenString;
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
