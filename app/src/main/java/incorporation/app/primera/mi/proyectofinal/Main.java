package incorporation.app.primera.mi.proyectofinal;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class Main extends AppCompatActivity {

    private Toolbar toolbar;
    GoogleApiClient mGoogleApiClient;
    private int optLog;     //1 = Google | 2 = Facebook | 3 = Normal

    private String nombre, correo, foto, contraseña;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        Bundle datos = getIntent().getExtras();
        optLog = datos.getInt("opcion");

        if (optLog == 1) {//Google
            Toast.makeText(getApplicationContext(), "Google", Toast.LENGTH_SHORT).show();
            nombre = datos.getString("nombre");
            correo = datos.getString("correo");
            foto = datos.getString("foto");
        } else if (optLog == 2) {//Facebook
            Toast.makeText(getApplicationContext(), "Facebook", Toast.LENGTH_SHORT).show();
            nombre = datos.getString("nombre");
            correo = datos.getString("correo");
            foto = datos.getString("foto");

        } else {//Normalito
            Toast.makeText(getApplicationContext(), "Registro", Toast.LENGTH_SHORT).show();
            correo = datos.getString("correo");
            contraseña = datos.getString("contraseña");

        }
        //Toast.makeText(this, optLog, Toast.LENGTH_SHORT).show();
        TextView opcion = (TextView) findViewById(R.id.opcion);
        opcion.setText("" + optLog);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menumain) {

        getMenuInflater().inflate(R.menu.menu, menumain);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem opcion_menu) {
        Intent intent;
        int id = opcion_menu.getItemId();//coge el id de la opcion que selecciona el usuario
        if (id == R.id.miperfil) {
            if (optLog == 1) {//google
                intent = new Intent(Main.this, Perfil.class);
                intent.putExtra("nombre", nombre);
                intent.putExtra("correo", correo);
                intent.putExtra("foto", foto);
                intent.putExtra("opcion", optLog);
                //finish();
            } else if (optLog == 2) {//Facebook
                intent = new Intent(Main.this, Perfil.class);
                intent.putExtra("nombre", nombre);
                intent.putExtra("correo", correo);
                intent.putExtra("foto", foto);
                intent.putExtra("opcion", optLog);
                //finish();
            } else {//normalito
                intent = new Intent(Main.this,Perfil.class);
                intent.putExtra("correo",correo);
                intent.putExtra("contraseña",contraseña);
                intent.putExtra("opcion",optLog);
                //finish();
            }
            startActivity(intent);

        }
        if (id == R.id.cerrarmain) {
            if (optLog == 1) {//Google

                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(//cerrar sesion google
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                // ...
                            }
                        });
                intent = new Intent(Main.this, Login.class);

            } else if (optLog == 2) {//Facebook

                LoginManager.getInstance().logOut();// cerrar sesion en facebook
                intent = new Intent(Main.this, Login.class);

            } else {//Normalito

                intent = new Intent(Main.this, Login.class);
                intent.putExtra("correo",correo);
                intent.putExtra("contraseña",contraseña);
                intent.putExtra("opcion",optLog);

            }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(opcion_menu);//llama al padre y le pasa nuestro item por parametro para que funcione bien
    }
}
