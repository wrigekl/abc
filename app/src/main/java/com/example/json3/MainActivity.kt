package com.example.json3

import android.content.res.Configuration
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MyMath
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlay
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.OverlayItem
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {
    private lateinit var db : SQL
    private lateinit var location : OverlayItem
    private lateinit var Overlays : ItemizedIconOverlay<OverlayItem>
    private lateinit var items : ArrayList<OverlayItem>
    private lateinit var countries : MutableList<String>
    private lateinit var countriesUnact : MutableList<String>
    private lateinit var OverlayS : MutableList<OverlayItem>
    private lateinit var icon : Drawable
    private lateinit var map : MapView
    private lateinit var f : MutableList<String>
    private lateinit var c : MutableList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        map = findViewById(R.id.map)
        org.osmdroid.config.Configuration.getInstance().load(applicationContext , PreferenceManager.getDefaultSharedPreferences(applicationContext))
        db = SQL(this)
        val cn = map.controller
        f = db.readAct()
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        cn.setZoom(15.0)
        c = db.readUnAcut()
        cn.setCenter(GeoPoint(44.444 , 23.4568))
        items = ArrayList()
        icon = resources.getDrawable(R.drawable.ic_launcher_background)
        countries = db.readAct()
        countriesUnact = db.readUnAcut()
        location = OverlayItem("" , "" , GeoPoint(0 , 0))
        location.setMarker(icon)
        OverlayS = mutableListOf()
        Overlays = ItemizedIconOverlay(
            items , null , applicationContext
        )
        init()
    }
    fun init() {
        val res = db.readAct()
        for(i in res) {
            addIcon(db.readX(i) , db.readY(i) , i , true , 0)
        }
    }
    fun addIcon(x : Double , y : Double , country: String , act : Boolean , index : Int ) {
        location = OverlayItem("" , "" , GeoPoint(x , y))
        if(act) {
            f.add(country)
            OverlayS.add(location)
            items.add(location)
            Overlays.addItem(location)
            db.Act(country)
            map.overlays.add(Overlays)
        } else {
            f.remove(country)
            Overlays.removeItem(OverlayS[index])
            items.remove(OverlayS[index])
            OverlayS.remove(OverlayS[index])
            db.UnAct(country)
            map.overlays.remove(Overlays)
        }
        map.invalidate()
    }
    fun delete(view: View) {
        showDialog(false)
    }
    fun add(view: View){
        showDialog(true)
    }
    fun showDialog(act : Boolean){
        val a = AlertDialog.Builder(this)
        var select = -1
        a.setTitle(if(act)"add" else "delete")
        a.setSingleChoiceItems(if(act) c.toTypedArray() else f.toTypedArray() , -1) { dialog , which ->
            select = which
        }
        a.setPositiveButton("OK") {dialog , which->
            if(select == -1) {
                val t = Toast.makeText(this,  "select an option" , Toast.LENGTH_SHORT)
                t.show()
            } else if (act) {
                addIcon(db.readX(c[select]) , db.readY(c[select]) , c[select] , true , select)//unact
            } else {
                addIcon(db.readX(f[select]) , db.readY(f[select]) , f[select] , false , select)
            }
            dialog.dismiss()
        }
        val b = a.create()
        b.show()
    }
}