package suyat.it32s1.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import suyat.it32s1.finalproject.Artist.ArtistMain;
import suyat.it32s1.finalproject.Controller.ILoginController;
import suyat.it32s1.finalproject.Controller.LoginController;
import suyat.it32s1.finalproject.View.ILoginView;

public class MainActivity extends AppCompatActivity implements ILoginView {

    EditText email,password;
    Button loginb;
    ILoginController loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        requestWindowFeature ( Window.FEATURE_NO_TITLE );
        getSupportActionBar ().hide ();
        setContentView ( R.layout.activity_main );

        email = (EditText) findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        loginb = (Button) findViewById(R.id.loginb);
        loginPresenter = new LoginController ( (ILoginView) this );
        loginb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginPresenter.OnLogin(email.getText().toString().trim(),password.getText().toString().trim());
            }
        });
    }
    @Override
    public void LoginSuccess(String message) {
        Intent intent = new Intent (MainActivity.this, ArtistMain.class);
        startActivity ( intent );
    }
    @Override
    public void LoginError(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}