package br.com.fiap.mapas

import android.Manifest
import android.location.Address
import android.location.Geocoder

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.support.v4.app.FragmentActivity

import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.location.LocationRequest
import java.text.DateFormat
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    val REQUEST_GPS = 0

    var mCurrentLocation: Location? = null
    var mLocationRequest: LocationRequest? = null
    var mLastUpdateTime: String? = null

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLocationChanged(location: Location?) {
        mCurrentLocation = location
        mLastUpdateTime = DateFormat.getTimeInstance().format(Date())
        updateUI()
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.i("TAG", "Suspenso")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.i("TAG", "Erro de conexao GPS")
    }

    override fun onConnected(p0: Bundle?) {
        checkPermission()

        //exemplo para atualizar continualmente a cordenada https://javapapers.com/android/android-location-fused-provider/

        val minhaLocalizacao = LocationServices
                .FusedLocationApi
                .getLastLocation(mGoogleApiClient)


        if(minhaLocalizacao != null) {
            adicionarMarcador(minhaLocalizacao.latitude, minhaLocalizacao.longitude, "Aqui")
        }
    }

    private fun checkPermission() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                val builder = AlertDialog.Builder(this)

                builder.setMessage("Necessária a permissao para GPS")
                        .setTitle("Permissao Requerida")

                builder.setPositiveButton("OK") {
                    dialog, id ->
                    requestPermission()
                }

                val dialog = builder.create()
                dialog.show()

            } else {
                requestPermission()
            }
        }
    }

    protected fun requestPermission() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_GPS)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_GPS -> {
                if (grantResults.size == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i("TAG", "Permissão negada pelo usuário")
                } else {
                    Log.i("TAG", "Permissao concedida pelo usuario")
                }
                return
            }
        }
    }


    private lateinit var mMap: GoogleMap
    private lateinit var mGoogleApiClient : GoogleApiClient

    @Synchronized fun callConnection() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API) //colocar o location o Build.gradle
                .build()

        mGoogleApiClient.connect()

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        btPesquisar.setOnClickListener {

            mMap.clear() //Limpar os marcadores do Mapa

            val geoCoder = Geocoder(this)
            var address : List<Address>?

            address = geoCoder.getFromLocationName(etEndereco.text.toString(), 1)

            if(address.isNotEmpty()) {
                val location = address[0]

                adicionarMarcador(location.latitude, location.longitude, "Endereço Pesquisado")
            } else {
                var alert = AlertDialog.Builder(this).create()
                alert.setTitle("Fudeu")
                alert.setMessage("Endereço nao encontrado")

                alert.setCancelable(false)
                alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", {
                    dialogInterface, inteiro ->
                    alert.dismiss()
                })

                alert.show()
            }

        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.add markers or lines, add listeners or move the camera. In this case,
     * we just add
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        callConnection()
    }

    fun adicionarMarcador(latitude: Double, longitude: Double, title : String) {
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(latitude, longitude)
        mMap.addMarker(MarkerOptions()
                .position(sydney)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_location))) /*modificar o marcardor*/

        //para focar
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,16f))
    }

    private fun updateUI() {
        if (null != mCurrentLocation) {
            adicionarMarcador(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude, "Atual")

        } else {
            Log.d("TAG", "location is null ...............")
        }
    }
}
