package br.com.fiap.mapas

import android.location.Address
import android.location.Geocoder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btPesquisar.setOnClickListener {
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
}
