package incorporation.app.primera.mi.proyectofinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Login extends AppCompatActivity {

    private String correo_comp = "", contraseña_comp = "";
    private EditText etCorreo, etContraseña;

    private int optLog;     //1 = Google | 2 = Facebook | 3 = Normal
    private String nombre_a_pasar, correo_a_pasar = "", foto, contraseña_a_pasar = "";
    private Uri uriperfil;
    //declaraciones necesarias para Facebook -------------------------------------------------------
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    //declaraciones necesarias para Google----------------------------------------------------------
    GoogleApiClient mGoogleApiClient;
    private int RC_SIGN_IN = 5678;

    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etCorreo = (EditText) findViewById(R.id.correo_id);
        etContraseña = (EditText) findViewById(R.id.contraseña_id);

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
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        //Facebook----------------------------------------------------------------------------------
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email"));//para extraer el email y poderlo usar

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                if (AccessToken.getCurrentAccessToken() != null) {
                    GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {

                            final JSONObject json = response.getJSONObject();

                            try {
                                if (json != null) {
                                    optLog = 2;                 //Facebook
                                    nombre_a_pasar = object.getString("name");
                                    correo_a_pasar = object.getString("email");
                                    foto = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                    Toast.makeText(getApplicationContext(), "Login Exitoso", Toast.LENGTH_SHORT).show();
                                    goMainActivity();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            /*Profile profile=com.facebook.Profile.getCurrentProfile();
                            uriperfil=profile.getProfilePictureUri(400,400); //foto de tamaño 400x400
                            foto=uriperfil.toString();*/
                        }
                    });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,picture");
                    request.setParameters(parameters);
                    request.executeAsync();
                }//fin metodo
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Login Cancelado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Error en Login", Toast.LENGTH_SHORT).show();
            }
        });
        //------------------------------------------------------------------------------------------
        /*
        //try catch necesario para que me de la llave (KEYHASH) cuando estoy creando el boton de Facebook
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "incorporation.app.primera.mi.proyectofinal",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        */
        //------------------------------------------------------------------------------------------
        Bundle datos = getIntent().getExtras();
        if (datos != null){
            optLog = datos.getInt("opcion");
            if (optLog == 3){
                contraseña_a_pasar = datos.getString("contraseña");
                correo_a_pasar = datos.getString("correo");
                Log.d("correo",correo_a_pasar);
            }
        }
    }//fin OnCreate

    //google----------------------------------------------------------------------------------------
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("Google", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            correo_a_pasar = acct.getEmail();//obtener email
            nombre_a_pasar = acct.getDisplayName();
            uriperfil = acct.getPhotoUrl();
            optLog = 1;                 //Google

            if (uriperfil == null) {
                foto = null;
            } else {
                foto = uriperfil.toString();
            }
            //Log.d("Nombre de usuario", acct.getDisplayName());
            goMainActivity();
        } else {
            Toast.makeText(getApplicationContext(), "Error en Login Google", Toast.LENGTH_SHORT).show();
        }
    }

    //----------------------------------------------------------------------------------------------
    public void iniciar(View view) {
        optLog = 3;                 //registro normalito
        goMainActivity();
    }

    //----------------------------------------------------------------------------------------------
    private void goMainActivity() {

        Intent intent;

        if (optLog == 1) {                                              //Google
            intent = new Intent(Login.this, Main.class);
            intent.putExtra("nombre", nombre_a_pasar);
            intent.putExtra("correo", correo_a_pasar);
            intent.putExtra("foto", foto);
            intent.putExtra("opcion", optLog);
            startActivity(intent);
            finish();

        } else if (optLog == 2) {                                       //Facebook
            intent = new Intent(Login.this, Main.class);
            intent.putExtra("nombre", nombre_a_pasar);
            intent.putExtra("correo", correo_a_pasar);
            intent.putExtra("foto", foto);
            intent.putExtra("opcion", optLog);
            startActivity(intent);
            finish();

        } else if (optLog == 3) {                                       //Normalito
            //primero obtenemos los String ingresados en el Login
            correo_comp = etCorreo.getText().toString();
            contraseña_comp = etContraseña.getText().toString();
            //se hacen las validaciones
            if (correo_comp.equals("") || contraseña_comp.equals("")) {
                Toast.makeText(getApplicationContext(), "Verifique datos o registrese!", Toast.LENGTH_SHORT).show();
            } else if ((correo_a_pasar.equals(correo_comp)) && (contraseña_comp.equals(contraseña_a_pasar))) {
                intent = new Intent(Login.this, Main.class);
                intent.putExtra("correo", correo_a_pasar);
                intent.putExtra("contraseña", contraseña_a_pasar);
                intent.putExtra("opcion", optLog);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Error, verifique datos o registrese!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //OnActivvityResult-----------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1234 && resultCode == RESULT_OK) {                     //login normal
            correo_a_pasar = data.getExtras().getString("correo");
            contraseña_a_pasar = data.getExtras().getString("contraseña");
            //Toast.makeText(this, correo_a_pasar, Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, contraseña_a_pasar, Toast.LENGTH_SHORT).show();

        } else if (requestCode == RC_SIGN_IN) {                                  //Login con Google
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {                                                                //Login con Facebook
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    //----------------------------------------------------------------------------------------------

    public void registro(View view) {
        Intent intent = new Intent(Login.this, Registrarse.class);
        startActivityForResult(intent, 1234);
    }

    //google----------------------------------------------------------------------------------------
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    //----------------------------------------------------------------------------------------------

}