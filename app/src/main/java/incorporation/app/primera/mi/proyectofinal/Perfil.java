package incorporation.app.primera.mi.proyectofinal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;


import com.google.android.gms.common.api.GoogleApiClient;

public class Perfil extends AppCompatActivity {
    private String nombre, correo, contraseña, foto;
    private int optLog;     //1 = Google | 2 = Facebook | 3 = Normal
    private TextView tvNombre, tvCorreo, tvContraseña;
    private ImageView Foto_perfil;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        tvNombre = (TextView) findViewById(R.id.Nombre);
        tvCorreo = (TextView) findViewById(R.id.Correo);
        tvContraseña = (TextView) findViewById(R.id.Contraseña);
        Foto_perfil = (ImageView) findViewById(R.id.Foto);

        Bundle datos = getIntent().getExtras();
        optLog = datos.getInt("opcion");

        if (optLog == 1) {
            nombre = datos.getString("nombre");
            correo = datos.getString("correo");
            foto = datos.getString("foto");

            tvNombre.setText("Nombre: " + nombre);
            tvCorreo.setText("Correo: " + correo);
            tvContraseña.setText("");
            loadImageFromUrl(foto);

        } else if (optLog == 2) {
            nombre = datos.getString("nombre");
            correo = datos.getString("correo");
            foto = datos.getString("foto");

            tvNombre.setText("Nombre: " + nombre);
            tvCorreo.setText("Correo: " + correo);
            tvContraseña.setText("");
            loadImageFromUrl(foto);

        } else {
            correo = datos.getString("correo");
            contraseña = datos.getString("contraseña");

            tvNombre.setText("");
            tvCorreo.setText("Correo: " + correo);
            tvContraseña.setText("Contraseña: " + contraseña);

        }

        //login con Google--------------------------------------------------------------------------
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getApplicationContext(), "Error en login a los putazos", Toast.LENGTH_SHORT).show();
                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        //------------------------------------------------------------------------------------------
    }

    //Funcion para cargar la imagen en ImageView----------------------------------------------------
    private void loadImageFromUrl(String foto) {
        Picasso.with(this).load(foto).placeholder(R.mipmap.ic_launcher)
                .into(Foto_perfil, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                    }
                });
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menumain) {

        getMenuInflater().inflate(R.menu.menuperfil, menumain);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem opcion_menu) {
        Intent intent;
        int id = opcion_menu.getItemId();//coge el id de la opcion que selecciona el usuario

        if (id == R.id.principal) {
            if (optLog == 1) {//google
                intent = new Intent(Perfil.this, Main.class);
                intent.putExtra("nombre", nombre);
                intent.putExtra("correo", correo);
                intent.putExtra("foto", foto);
                intent.putExtra("opcion", optLog);
            } else if (optLog == 2) {//Facebook
                intent = new Intent(Perfil.this, Main.class);
                intent.putExtra("nombre", nombre);
                intent.putExtra("correo", correo);
                intent.putExtra("foto", foto);
                intent.putExtra("opcion", optLog);
            } else {//normalito
                intent = new Intent(Perfil.this, Main.class);
                intent.putExtra("correo",correo);
                intent.putExtra("contraseña",contraseña);
                intent.putExtra("opcion",optLog);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        }
        if (id == R.id.cerrarperfil) {

            intent = new Intent(Perfil.this, Login.class);

            if (optLog == 1) {//Google

                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(//cerrar sesion google
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                // ...
                            }
                        });

            } else if (optLog == 2) {//Facebook

                LoginManager.getInstance().logOut();// cerrar sesion en facebook

            } else {//Normalito

            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }

        return super.onOptionsItemSelected(opcion_menu);//llama al padre y le pasa nuestro item por parametro para que funcione bien
    }
}
