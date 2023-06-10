package mx.edu.utxj.ti.idgs.dwi.demo_api;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button btnGuardar;
    private Button btnBuscar;
    private Button btnEliminar;
    private Button btnActualizar;

    private EditText etCodigoBarrras;
    private EditText etDescripcion;
    private EditText etMarca;
    private EditText etPrecioCompra;
    private EditText etPrecioVenta;
    private EditText etExistencia;

    private ListView lvProductos;

    private RequestQueue colaPeticiones;

    private JsonArrayRequest jsonArrayRequest;

    private ArrayList<String> origenDatos = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private String url = "http://10.10.62.19:3300/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnActualizar = findViewById(R.id.btnActualizar);

        etCodigoBarrras = findViewById(R.id.etCodigoBarrras);
        etDescripcion = findViewById(R.id.etDescripcion);
        etMarca = findViewById(R.id.etMarca);
        etPrecioCompra = findViewById(R.id.etPrecioCompra);
        etPrecioVenta = findViewById(R.id.etPrecioVenta);
        etExistencia = findViewById(R.id.etExistencia);

        lvProductos = findViewById(R.id.lvProductos);

        colaPeticiones = Volley.newRequestQueue(this);
        listarProductos();

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObjectRequest peticion = new JsonObjectRequest(
                        Request.Method.GET,
                        url + etCodigoBarrras.getText().toString(),
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (response.has("status"))
                                    Toast.makeText(MainActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                                else {
                                    try {
                                        etDescripcion.setText(response.getString("descripcion"));
                                        etMarca.setText(response.getString("marca"));
                                        etPrecioCompra.setText(String.valueOf(response.getInt("preciocompra")));
                                        etPrecioVenta.setText(String.valueOf(response.getInt("precioventa")));
                                        etExistencia.setText(String.valueOf(response.getInt("existencias")));
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                colaPeticiones.add(peticion);
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject producto = new JSONObject();
                try {
                    producto.put("codigobarras", etCodigoBarrras.getText().toString());
                    producto.put("descripcion", etDescripcion.getText().toString());
                    producto.put("marca", etMarca.getText().toString());
                    producto.put("preciocompra", Float.parseFloat(etPrecioCompra.getText().toString()));
                    producto.put("precioventa",Float.parseFloat(etPrecioVenta.getText().toString()));
                    producto.put("existencias", Integer.parseInt(etExistencia.getText().toString()));



                    etCodigoBarrras.setText("");
                    etDescripcion.setText("");
                    etMarca.setText("");
                    etPrecioCompra.setText("");
                    etPrecioVenta.setText("");
                    etExistencia.setText("");
                } catch (JSONException e){
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        url + "insert/",
                        producto,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals("Producto insertado"))
                                        Toast.makeText(MainActivity.this, "Producto Insertado con Exito", Toast.LENGTH_SHORT).show();
                                    listarProductos();
                                } catch (JSONException e){
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );

                colaPeticiones.add(jsonObjectRequest);
            }
        });




        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etCodigoBaras.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Primero busca un producto", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject productos = new JSONObject();
                    try {
                        productos.put("codigobarras", etCodigoBaras.getText().toString());
                        if (!etDescripcion.getText().toString().isEmpty()) {
                            productos.put("descripcion", etDescripcion.getText().toString());
                        }

                        if (!etMarca.getText().toString().isEmpty()) {
                            productos.put("marca", etMarca.getText().toString());
                        }

                        if (!etPrecioCompra.getText().toString().isEmpty()) {
                            productos.put("preciocompra", Float.parseFloat(etPrecioCompra.getText().toString()));
                        }

                        if (!etPrecioVenta.getText().toString().isEmpty()) {
                            productos.put("precioventa", Float.parseFloat(etPrecioVenta.getText().toString()));
                        }

                        if (!etExistencia.getText().toString().isEmpty()) {
                            productos.put("existencias", Float.parseFloat(etExistencia.getText().toString()));
                        }

                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    JsonObjectRequest update = new JsonObjectRequest(
                            Request.Method.PUT,
                            url + "/actualizar/" + etCodigoBaras.getText().toString(),
                            productos,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("status").equals("Producto actualizado")) {
                                            Toast.makeText(MainActivity.this, "Producto actualizado con EXITO!", Toast.LENGTH_SHORT).show();
                                            etCodigoBaras.setText("");
                                            etDescripcion.setText("");
                                            etMarca.setText("");
                                            etPrecioCompra.setText("");
                                            etPrecioVenta.setText("");
                                            etExistencia.setText("");
                                            adapter.clear();
                                            lvProductos.setAdapter(adapter);
                                            listarProductos();
                                        } else if (response.getString("status").equals("Not Found")) {
                                            Toast.makeText(MainActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                    colaPeticiones.add(update);
                }
            }
        });


        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject producto = new JSONObject();
                try {
                    producto.put("codigobarras", etCodigoBarrras.getText().toString());

                } catch (JSONException e){
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.DELETE,
                        url + "borrar/"+etCodigoBarrras.getText().toString(),
                        producto,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals("Producto Eliminado")) {
                                        Toast.makeText(MainActivity.this, "Producto Eliminado con Exito", Toast.LENGTH_SHORT).show();
                                        etCodigoBarrras.setText("");
                                        etDescripcion.setText("");
                                        etMarca.setText("");
                                        etPrecioCompra.setText("");
                                        etPrecioVenta.setText("");
                                        etExistencia.setText("");

                                        adapter.clear();
                                        lvProductos.setAdapter(adapter);
                                        listarProductos();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Producto no Encontrado", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e){
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                );
                colaPeticiones.add(jsonObjectRequest);
                adapter.clear();
                lvProductos.setAdapter(adapter);
                listarProductos();
            }
        });




    }

    protected void listarProductos(){
        jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i=0; i<response.length();i++){
                            try {
                                String codigobarras=response.getJSONObject(i).getString("codigobarras")+":";
                                String descripcion=response.getJSONObject(i).getString("descripcion") +":";
                                String marca=response.getJSONObject(i).getString("marca") +":";
                                origenDatos.add(codigobarras+":"+descripcion+":"+marca);
                            }catch (JSONException e){

                            }
                        }

                        adapter = new ArrayAdapter<>(MainActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,origenDatos);
                        lvProductos.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT). show();
                    }
                }
        );
        colaPeticiones.add(jsonArrayRequest);
    }
}