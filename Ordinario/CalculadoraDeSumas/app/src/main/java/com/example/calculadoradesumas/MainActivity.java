package com.example.calculadoradesumas;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    TextView resultado;
    Button boton;
    EditText num1;
    EditText num2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        resultado = findViewById(R.id.resultadoView);
        boton = findViewById(R.id.button);
        num1 = findViewById(R.id.num1Input);
        num2 =findViewById(R.id.num2Input);

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double numero1 = Double.parseDouble(num1.getText().toString());
                double numero2 = Double.parseDouble(num2.getText().toString());

                double total = numero1 + numero2;

                String totalstr = String.valueOf(total);

                resultado.setText(totalstr);

            }
        });
    }
}
