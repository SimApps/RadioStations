package com.amirami.simapp.radiostations


import alirezat775.lib.downloader.Downloader
import alirezat775.lib.downloader.core.OnDownloadListener
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.TransitionDrawable
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Environment
import android.os.Environment.*
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.NonNull
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.*
import androidx.core.graphics.toColorInt
import androidx.core.widget.NestedScrollView
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import coil.request.CachePolicy
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.amirami.simapp.radiostations.Exoplayer.player
import com.amirami.simapp.radiostations.MainActivity.Companion.GlobalRadioName
import com.amirami.simapp.radiostations.MainActivity.Companion.GlobalRadiourl
import com.amirami.simapp.radiostations.MainActivity.Companion.color1
import com.amirami.simapp.radiostations.MainActivity.Companion.color2
import com.amirami.simapp.radiostations.MainActivity.Companion.color3
import com.amirami.simapp.radiostations.MainActivity.Companion.color4
import com.amirami.simapp.radiostations.MainActivity.Companion.currentNativeAd
import com.amirami.simapp.radiostations.MainActivity.Companion.customdownloader
import com.amirami.simapp.radiostations.MainActivity.Companion.darkTheme
import com.amirami.simapp.radiostations.MainActivity.Companion.downloader
import com.amirami.simapp.radiostations.MainActivity.Companion.handlers
import com.amirami.simapp.radiostations.MainActivity.Companion.icyandState
import com.amirami.simapp.radiostations.MainActivity.Companion.isDownloadingCustomurl
import com.amirami.simapp.radiostations.MainActivity.Companion.mInterstitialAd
import com.amirami.simapp.radiostations.MainActivity.Companion.userRecord
import com.amirami.simapp.radiostations.model.RecordInfo
import com.amirami.simapp.radiostations.utils.Constatnts.COUNTRY_FLAGS_BASE_URL
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import java.io.File
import java.io.IOException
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.*


object RadioFunction {
    fun shareRadio(context: Context, radioName: String, radioHomepage: String, radioStreamURL: String, radioCountry: String, radioLanguage: String, radioBitrate: String) {
        if(radioName!=""){
            if (Exoplayer.is_playing_recorded_file) {
                try {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.type = "text/plain"
                    intent.putExtra(
                        Intent.EXTRA_TEXT,
                        "I am listening to $radioName recorded with ${context.resources.getString(R.string.app_name)} application.\n" +
                                "Information about redorded file : $icyandState \n" +
                                "Download App from :  http://play.google.com/store/apps/details?id=" + context.packageName
                    )
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Radio Name : $radioName")
                    context.startActivity(Intent.createChooser(intent, "Share Radio Information Via"))
                } catch (e: Exception) {
                    //e.toString();
                }
            }
            else {
                try {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.type = "text/plain"
                    intent.putExtra(
                        Intent.EXTRA_TEXT,
                        "I am listening to $radioName on ${context.resources.getString(R.string.app_name)}  application.\n" +
                                "Radio Homepage : $radioHomepage \n" +
                                "Radio Stream URL : $radioStreamURL \n" +
                                "Radio Country : $radioCountry  \n" +
                                "Radio Language : $radioLanguage \n" +
                                "Radio Bitrate : $radioBitrate \n \n" +
                                "Download App from :  http://play.google.com/store/apps/details?id=" + context.packageName
                    )
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Radio Name : $radioName")
                    context.startActivity(Intent.createChooser(intent, "Share Radio Information Via"))

                } catch (e: Exception) {
                    //e.toString();
                }
            }
        }

        else  DynamicToast.makeError(context,context.resources.getString(R.string.Select_radio_toshare) , 9).show()


    }



    fun startServices(context: Context) {
        try {
            if(player!=null){
                val serviceIntent = Intent(context, NotificationChannelService::class.java)
                serviceIntent.putExtra("input_radio_name",GlobalRadioName)
                startForegroundService(context, serviceIntent)
            }
        }
        catch (e: IOException) { e.printStackTrace() }
        catch (e: IllegalArgumentException) { e.printStackTrace() }
        catch (e: SecurityException) { e.printStackTrace() }
        catch (e: IllegalStateException) { e.printStackTrace() }
    }



    fun stopService(context: Context) {
        val serviceIntent = Intent(context, NotificationChannelService::class.java)
        context.stopService(serviceIntent)
    }








    fun unwrap(context: Context):Activity {
        var scontext=context
        while (scontext !is Activity && scontext is ContextWrapper)
        {
            scontext = scontext.baseContext
        }
        return scontext as Activity
    }


    fun countryCodeToName(GlobalCountriesJsons: String?):String{
        return when (GlobalCountriesJsons) {
            "AF" -> "Afghanistan"
            "AX" -> "Aland Islands"
            "AL" -> "Albania"
            "DZ" -> "Algeria"
            "AS" -> "American Samoa"
            "AD" -> "Andorra"
            "AO" -> "Angola"
            "AI" -> "Anguilla"
            "AQ" -> "Antarctica"
            "AG" -> "Antigua And Barbuda"
            "AR" -> "Argentina"
            "AM" -> "Armenia"
            "AW" -> "Aruba"
            "AU" -> "Australia"
            "AT" -> "Austria"
            "AZ" -> "Azerbaijan"
            "BS" -> "Bahamas"
            "BH" -> "Bahrain"
            "BD" -> "Bangladesh"
            "BB" -> "Barbados"
            "BY" -> "Belarus"
            "BE" -> "Belgium"
            "BZ" -> "Belize"
            "BJ" -> "Benin"
            "BM" -> "Bermuda"
            "BT" -> "Bhutan"
            "BO" -> "Bolivia"
            "BA" -> "Bosnia And Herzegovina"
            "BW" -> "Botswana"
            "BV" -> "Bouvet Island"
            "BR" -> "Brazil"
            "IO" -> "British Indian Ocean Territory"
            "BN" -> "Brunei Darussalam"
            "BG" -> "Bulgaria"
            "BF" -> "Burkina Faso"
            "BI" -> "Burundi"
            "BQ" -> "Bonaire, Saint Eustatius and Saba"
            "KH" -> "Cambodia"
            "CM" -> "Cameroon"
            "CA" -> "Canada"
            "CV" -> "Cape Verde"
            "KY" -> "Cayman Islands"
            "CF" -> "Central African Republic"
            "TD" -> "Chad"
            "CL" -> "Chile"
            "CN" -> "China"
            "CX" -> "Christmas Island"
            "CC" -> "Cocos (Keeling) Islands"
            "CO" -> "Colombia"
            "KM" -> "Comoros"
            "CG" -> "Congo"
            "CD" -> "Congo, Democratic Republic"
            "CK" -> "Cook Islands"
            "CR" -> "Costa Rica"
            "CI" -> "Cote D\\'Ivoire"
            "HR" -> "Croatia"
            "CU" -> "Cuba"
            "CY" -> "Cyprus"
            "CW" -> "Curacao"
            "CZ" -> "Czech Republic"
            "DK" -> "Denmark"
            "DJ" -> "Djibouti"
            "DM" -> "Dominica"
            "DO" -> "Dominican Republic"
            "EC" -> "Ecuador"
            "EG" -> "Egypt"
            "SV" -> "El Salvador"
            "GQ" -> "Equatorial Guinea"
            "ER" -> "Eritrea"
            "EE" -> "Estonia"
            "ET" -> "Ethiopia"
            "FK" -> "Falkland Islands (Malvinas)"
            "FO" -> "Faroe Islands"
            "FJ" -> "Fiji"
            "FI" -> "Finland"
            "FR" -> "France"
            "GF" -> "French Guiana"
            "PF" -> "French Polynesia"
            "TF" -> "French Southern Territories"
            "GA" -> "Gabon"
            "GM" -> "Gambia"
            "GE" -> "Georgia"
            "DE" -> "Germany"
            "GH" -> "Ghana"
            "GI" -> "Gibraltar"
            "GR" -> "Greece"
            "GL" -> "Greenland"
            "GD" -> "Grenada"
            "GP" -> "Guadeloupe"
            "GU" -> "Guam"
            "GT" -> "Guatemala"
            "GG" -> "Guernsey"
            "GN" -> "Guinea"
            "GW" -> "Guinea-Bissau"
            "GY" -> "Guyana"
            "HT" -> "Haiti"
            "HM" -> "Heard Island & Mcdonald Islands"
            "VA" -> "Holy See (Vatican City State)"
            "HN" -> "Honduras"
            "HK" -> "Hong Kong"
            "HU" -> "Hungary"
            "IS" -> "Iceland"
            "IN" -> "India"
            "ID" -> "Indonesia"
            "IR" -> "Iran, Islamic Republic Of"
            "IQ" -> "Iraq"
            "IE" -> "Ireland"
            "IM" -> "Isle Of Man"
            "IL" -> "Israel"
            "IT" -> "Italy"
            "JM" -> "Jamaica"
            "JP" -> "Japan"
            "JE" -> "Jersey"
            "JO" -> "Jordan"
            "KZ" -> "Kazakhstan"
            "KE" -> "Kenya"
            "KI" -> "Kiribati"
            "KR" -> "Korea"
            "KW" -> "Kuwait"
            "KG" -> "Kyrgyzstan"
            "LA" -> "Lao People\\'s Democratic Republic"
            "LV" -> "Latvia"
            "LB" -> "Lebanon"
            "LS" -> "Lesotho"
            "LR" -> "Liberia"
            "LY" -> "Libyan Arab Jamahiriya"
            "LI" -> "Liechtenstein"
            "LT" -> "Lithuania"
            "LU" -> "Luxembourg"
            "MO" -> "Macao"
            "MK" -> "Macedonia"
            "MG" -> "Madagascar"
            "MW" -> "Malawi"
            "MY" -> "Malaysia"
            "MV" -> "Maldives"
            "ML" -> "Mali"
            "MT" -> "Malta"
            "MH" -> "Marshall Islands"
            "MQ" -> "Martinique"
            "MR" -> "Mauritania"
            "MU" -> "Mauritius"
            "YT" -> "Mayotte"
            "MX" -> "Mexico"
            "FM" -> "Micronesia, Federated States Of"
            "MD" -> "Moldova"
            "MC" -> "Monaco"
            "MN" -> "Mongolia"
            "ME" -> "Montenegro"
            "MS" -> "Montserrat"
            "MA" -> "Morocco"
            "MZ" -> "Mozambique"
            "MM" -> "Myanmar"
            "NA" -> "Namibia"
            "NR" -> "Nauru"
            "NP" -> "Nepal"
            "NL" -> "Netherlands"
            "AN" -> "Netherlands Antilles"
            "NC" -> "New Caledonia"
            "NZ" -> "New Zealand"
            "NI" -> "Nicaragua"
            "NE" -> "Niger"
            "NG" -> "Nigeria"
            "NU" -> "Niue"
            "NF" -> "Norfolk Island"
            "MP" -> "Northern Mariana Islands"
            "NO" -> "Norway"
            "OM" -> "Oman"
            "PK" -> "Pakistan"
            "PW" -> "Palau"
            "KP" -> "North Korea"
            "PS" -> "Palestinian Territory, Occupied"
            "PA" -> "Panama"
            "PG" -> "Papua New Guinea"
            "PY" -> "Paraguay"
            "PE" -> "Peru"
            "PH" -> "Philippines"
            "PN" -> "Pitcairn"
            "PL" -> "Poland"
            "PT" -> "Portugal"
            "PR" -> "Puerto Rico"
            "QA" -> "Qatar"
            "RE" -> "Reunion"
            "RO" -> "Romania"
            "RU" -> "Russian Federation"
            "RW" -> "Rwanda"
            "BL" -> "Saint Barthelemy"
            "SH" -> "Saint Helena"
            "KN" -> "Saint Kitts And Nevis"
            "LC" -> "Saint Lucia"
            "MF" -> "Saint Martin"
            "PM" -> "Saint Pierre And Miquelon"
            "VC" -> "Saint Vincent And Grenadines"
            "WS" -> "Samoa"
            "SM" -> "San Marino"
            "ST" -> "Sao Tome And Principe"
            "SA" -> "Saudi Arabia"
            "SN" -> "Senegal"
            "RS" -> "Serbia"
            "SC" -> "Seychelles"
            "SL" -> "Sierra Leone"
            "SG" -> "Singapore"
            "SK" -> "Slovakia"
            "SI" -> "Slovenia"
            "SB" -> "Solomon Islands"
            "SS" -> "South Sudan"
            "SO" -> "Somalia"
            "ZA" -> "South Africa"
            "GS" -> "South Georgia And Sandwich Isl."
            "ES" -> "Spain"
            "LK" -> "Sri Lanka"
            "SD" -> "Sudan"
            "SR" -> "Suriname"
            "SJ" -> "Svalbard And Jan Mayen"
            "SZ" -> "Swaziland"
            "SE" -> "Sweden"
            "CH" -> "Switzerland"
            "SY" -> "Syrian Arab Republic"
            "TW" -> "Taiwan"
            "TJ" -> "Tajikistan"
            "TZ" -> "Tanzania"
            "TH" -> "Thailand"
            "TL" -> "Timor-Leste"
            "TG" -> "Togo"
            "TK" -> "Tokelau"
            "TO" -> "Tonga"
            "TT" -> "Trinidad And Tobago"
            "TN" -> "Tunisia"
            "TR" -> "Turkey"
            "TM" -> "Turkmenistan"
            "TC" -> "Turks And Caicos Islands"
            "TV" -> "Tuvalu"
            "UG" -> "Uganda"
            "UA" -> "Ukraine"
            "AE" -> "United Arab Emirates"
            "GB" -> "United Kingdom"
            "US" -> "United States"
            "UM" -> "United States Outlying Islands"
            "UY" -> "Uruguay"
            "UZ" -> "Uzbekistan"
            "VU" -> "Vanuatu"
            "VE" -> "Venezuela"
            "VN" -> "Viet Nam"
            "VG" -> "Virgin Islands, British"
            "VI" -> "Virgin Islands, U.S."
            "WF" -> "Wallis And Futuna"
            "EH" -> "Western Sahara"
            "YE" -> "Yemen"
            "ZM" -> "Zambia"
            "ZW" -> "Yemen"
            "XK" -> "Kosovo"


            //  else -> GlobalCountriesJsons!!
            else -> GlobalCountriesJsons!!
        }



    }

    fun isNumber(s: String?): Boolean = if (s.isNullOrEmpty()) false else s.all { Character.isDigit(it) }


    fun getDownloader(context: Context) {


            var recordFileName= GlobalRadioName + "_ _" + icyandState + " " +  System.currentTimeMillis()
            recordFileName=recordFileName.replace(Regex("[\\\\/:*?\"<>|]"), " ")


          //  val sdfDate = SimpleDateFormat("MMM d yy_HH-mm-ss", Locale.getDefault())
          //  var recordFileName= GlobalRadioName + "_ _" + icyandState + " " + sdfDate.format(Date())




            downloader = Downloader.Builder(context, GlobalRadiourl).downloadListener(object : OnDownloadListener {
                override fun onStart() {
                    Exoplayer.is_downloading = true

                    handlers.post {
                        Exoplayer.Observer.changeImageRecord("Main record image view", R.drawable.rec_on)
                        Exoplayer.Observer.changeImageRecord("Main stop image view", R.drawable.rec_on)

                        DynamicToast.make(context, "Recording Started . . .", getDrawable(context, R.drawable.rec_on), getColor(context, R.color.blue), getColor(context, R.color.violet_medium), 9).show()
                        startServices(unwrap(context))

                    }
                }

                override fun onPause() {
                    Exoplayer.is_downloading = false
                    //
                    DynamicToast.makeSuccess(context, "Recording paused", 3).show()
                    handlers.post {
                        Exoplayer.Observer.changeImageRecord("Main record image view", R.drawable.rec_2)
                        Exoplayer.Observer.changeImageRecord("Main stop image view", R.drawable.stop_2)

                        startServices(unwrap(context))
                    }
                }

                override fun onResume() {
                    Exoplayer.is_downloading = true

                    DynamicToast.makeSuccess(context, "Recording resumed", 3).show()
                    handlers.post {
                        Exoplayer.Observer.changeImageRecord("Main record image view", R.drawable.rec_on)
                        Exoplayer.Observer.changeImageRecord("Main stop image view", R.drawable.rec_on)

                        startServices(unwrap(context))
                    }
                    //  Log.d(TAG, "onResume")
                }

                override fun onProgressUpdate(percent: Int, downloadedSize: Int, totalSize: Int) {
                    Exoplayer.is_downloading = true


                    handlers.post {
                        Exoplayer.Observer.changeImageRecord("Main record image view", R.drawable.rec_on)
                        Exoplayer.Observer.changeImageRecord("Main stop image view", R.drawable.rec_on)
                        //  current_status_txt.text = "onProgressUpdate"
                        //   percent_txt.text = percent.toString().plus("%")
                        //   size_txt.text = getSize(downloadedSize)
                        //   total_size_txt.text = getSize(totalSize)
                        //  download_progress.progress = percent

                    }
                    // Log.d(TAG, "onProgressUpdate: percent --> $percent downloadedSize --> $downloadedSize totalSize --> $totalSize ")
                }

                override fun onCompleted(file: File?) {

                    Exoplayer.is_downloading = false

                    handlers.post {
                        Exoplayer.Observer.changeImageRecord("Main record image view", R.drawable.rec_2)
                        Exoplayer.Observer.changeImageRecord("Main stop image view", R.drawable.stop_2)

                        DynamicToast.make(
                            context, "Recording Saved",
                            getDrawable(context, R.drawable.rec_on),
                            getColor(context, R.color.blue),
                            getColor(context, R.color.violet_medium), 9
                        ).show()

                        startServices(unwrap(context))
                    }
                    // Log.d(TAG, "onCompleted: file --> $file")
                }

                override fun onFailure(reason: String?) {


                    handlers.post {

                        Exoplayer.is_downloading = false
                        Exoplayer.Observer.changeImageRecord("Main record image view", R.drawable.rec_2)
                        Exoplayer.Observer.changeImageRecord("Main stop image view", R.drawable.stop_2)


                        DynamicToast.makeError(context, reason!!, 9).show()

                        startServices(unwrap(context))
                    }
                    //  Log.d(TAG, "onFailure: reason --> $reason")
                }

                override fun onCancel() {

                    handlers.post {
                        Exoplayer.is_downloading = false
                        Exoplayer.Observer.changeImageRecord("Main record image view", R.drawable.rec_2)
                        Exoplayer.Observer.changeImageRecord("Main stop image view", R.drawable.stop_2)

                        DynamicToast.make(context, "Recording Saved",
                            getDrawable(context, R.drawable.rec_on),
                            getColor(context, R.color.blue),
                            getColor(context, R.color.violet_medium), 9
                        ).show()


                        startServices(unwrap(context))
                    }
                    //  Log.d(TAG, "onCancel")
                }
            }).fileName(recordFileName, "mp3" ).downloadDirectory(getDownloadDir(context).toString()).

            build()






    }


    fun getCutomDownloader(context: Context, nameRecord:String, url:String) {

          //  val sdfDate = SimpleDateFormat("MMM d yy_HH-mm-ss", Locale.getDefault())
            // var recordFileName= nameRecord+ "_ _"  + sdfDate.format(Date())

            var recordFileName= nameRecord+ "_ _"  + System.currentTimeMillis()
            recordFileName=recordFileName.replace(Regex("[\\\\/:*?\"<>|]"), " ")


            customdownloader = Downloader.Builder(context, url).downloadListener(object : OnDownloadListener {
                override fun onStart() {
                    isDownloadingCustomurl=true

                    handlers.post {
                        DynamicToast.make(context, "Download Started . . .", getDrawable(context, R.drawable.rec_on), getColor(context, R.color.blue), getColor(context, R.color.violet_medium), 9).show()
                        Exoplayer.Observer.changeImageRecord("floatingActionAddDownload", R.drawable.ic_download_button_on)

                        Exoplayer.Observer.changeImageRecord("Main record image view", R.drawable.rec_on)
                        Exoplayer.Observer.changeImageRecord("Main stop image view", R.drawable.rec_on)
                    }
                    //  Log.d(TAG, "onStart")
                }

                override fun onPause() {
                    isDownloadingCustomurl=false
                    // icy="Download Completed"

                    handlers.post {
                        DynamicToast.makeSuccess(context, "Download paused", 3).show()

                        Exoplayer.Observer.changeImageRecord("Main record image view", R.drawable.rec_2)
                        Exoplayer.Observer.changeImageRecord("Main stop image view", R.drawable.stop_2)
                        Exoplayer.Observer.changeImageRecord("floatingActionAddDownload", R.drawable.ic_download_button)
                        Exoplayer.Observer.changeText("Main text view", "Download Completed")
                        Exoplayer.Observer.changeText("text view", "Download Completed")
                    }
                    // Log.d(TAG, "onPause")

                }

                override fun onResume() {
                    isDownloadingCustomurl=true

                    handlers.post {
                        Exoplayer.Observer.changeImageRecord("Main record image view", R.drawable.rec_on)
                        Exoplayer.Observer.changeImageRecord("Main stop image view", R.drawable.rec_on)
                        Exoplayer.Observer.changeImageRecord("floatingActionAddDownload", R.drawable.ic_download_button_on)
                        DynamicToast.makeSuccess(context, "Download resumed", 3).show()
                    }
                    //  Log.d(TAG, "onResume")
                }

                override fun onProgressUpdate(percent: Int, downloadedSize: Int, totalSize: Int) {
                    isDownloadingCustomurl=true

                    handlers.post {
                        Exoplayer.Observer.changeImageRecord("Main record image view", R.drawable.rec_on)
                        Exoplayer.Observer.changeImageRecord("Main stop image view", R.drawable.rec_on)
                        Exoplayer.Observer.changeImageRecord("floatingActionAddDownload", R.drawable.ic_download_button_on)
                        Exoplayer.Observer.changeText("Main text view",
                            if(percent<0){ nameRecord+  " is Downloading" }
                            else{
                                nameRecord+  " is Downloading: "+bytesIntoHumanReadable(downloadedSize.toLong()) + " / " + bytesIntoHumanReadable(totalSize.toLong())
                            })

                        Exoplayer.Observer.changeText("text view",
                            if(percent<0){ nameRecord+  " is Downloading" }
                            else{
                                nameRecord+  " is Downloading: "+bytesIntoHumanReadable(downloadedSize.toLong()) + " / " + bytesIntoHumanReadable(totalSize.toLong())
                            })
                        //  current_status_txt.text = "onProgressUpdate"
                        //   percent_txt.text = percent.toString().plus("%")
                        //   size_txt.text = getSize(downloadedSize)
                        //   total_size_txt.text = getSize(totalSize)
                        //  download_progress.progress = percent
                    }
                    // Log.d(TAG, "onProgressUpdate: percent --> $percent downloadedSize --> $downloadedSize totalSize --> $totalSize ")
                }

                override fun onCompleted(file: File?) {

                    isDownloadingCustomurl=false
                    // icy="Download Completed"

                    handlers.post {
                        Exoplayer.Observer.changeImageRecord("Main record image view", R.drawable.rec_2)
                        Exoplayer.Observer.changeImageRecord("Main stop image view", R.drawable.stop_2)
                        Exoplayer.Observer.changeImageRecord("floatingActionAddDownload", R.drawable.ic_download_button)
                        DynamicToast.make(
                            context, "Download Complete",
                            getDrawable(context, R.drawable.rec_on),
                            getColor(context, R.color.blue),
                            getColor(context, R.color.violet_medium), 9
                        ).show()
                        Exoplayer.Observer.changeText("Main text view", "Download Completed")
                        Exoplayer.Observer.changeText("text view", "Download Completed")
                    }
                    // Log.d(TAG, "onCompleted: file --> $file")
                }

                override fun onFailure(reason: String?) {

                    isDownloadingCustomurl=false

                    handlers.post {
                        Exoplayer.Observer.changeImageRecord("Main record image view", R.drawable.rec_2)
                        Exoplayer.Observer.changeImageRecord("Main stop image view", R.drawable.stop_2)
                        Exoplayer.Observer.changeImageRecord("floatingActionAddDownload", R.drawable.ic_download_button)
                        // icy="Download Completed"
                        Exoplayer.Observer.changeText("Main text view", "Download Failed : "+ reason)
                        Exoplayer.Observer.changeText("text view", "Download Failed : "+ reason)

                        DynamicToast.makeError(context, /*"Download Failed"*/reason, 9).show()
                        // DynamicToast.makeError(context, GlobalRadiourl, 9).show()
                    }
                    //  Log.d(TAG, "onFailure: reason --> $reason")
                }

                override fun onCancel() {

                    isDownloadingCustomurl=false

                    handlers.post {
                        // icy="Download Completed"
                        Exoplayer.Observer.changeText("Main text view", "Download Completed")
                        Exoplayer.Observer.changeText("text view", "Download Completed")
                        Exoplayer.Observer.changeImageRecord("Main record image view", R.drawable.rec_2)
                        Exoplayer.Observer.changeImageRecord("Main stop image view", R.drawable.stop_2)
                        Exoplayer.Observer.changeImageRecord("floatingActionAddDownload", R.drawable.ic_download_button)

                        DynamicToast.make(
                            context, "Download Saved",
                            getDrawable(context, R.drawable.rec_on),
                            getColor(context, R.color.blue),
                            getColor(context, R.color.violet_medium), 9
                        ).show()
                    }
                    //  Log.d(TAG, "onCancel")
                }
            }).fileName(recordFileName,"" ).downloadDirectory(getDownloadDir(context).toString()).

            build()




    }

    fun shortformateDate(Date:String):String{
        // SimpleDateFormat("d/MM/yyyy", Locale.getDefault()).format(Date())
        // SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(Date())

        return if(isNumber(Date)) SimpleDateFormat("d/MM/yyyy", Locale.getDefault()).format(Date.toLong())
        else Date
    }

    fun removeWord(value: String, wordtoremove: String): String {
        var result = ""
        var possibleMatch = ""
        var i = 0
        var j = 0
        while ( i in value.indices) {
            if ( value[i] == wordtoremove[j] ) {
                if ( j == wordtoremove.length - 1 ) { // match
                    possibleMatch = "" // discard word
                    j = 0
                }
                else {
                    possibleMatch += value[i]
                    j++
                }
            }
            else {

                result += possibleMatch
                possibleMatch = ""

                if ( j == 0 ) {

                    result += value[i]
                }
                else {

                    j = 0
                    i-- // re-test
                }
            }

            i++
        }

        return result
    }

    fun bytesIntoHumanReadable(bytes: Long): String {
        var byte = bytes.toDouble()
        val str = arrayOf("B", "KB", "MB", "GB", "TB")
        for (aStr in str) {
            if (byte < 1024) {
                return String.format(Locale.getDefault(), "%1$,.1f %2\$s", byte, aStr)
            }
            byte /= 1024
        }
        return String.format(
            Locale.getDefault(), "%1$,.1f %2\$s", byte * 1024,
            str[str.size - 1]
        )
    }



    fun deleteRecordedItem(index: Int,  context: Context) {
        val file = getDownloadDir(context).listFiles()!![index]

        try {
            deleteAllFileAndContents(file,context)

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteAllFileAndContents(@NonNull file: File, context: Context) {
        if (file.exists()) {

            if (file.isDirectory) {
                val contents = file.listFiles()
                if (contents != null) {
                    for (content in contents) {
                        deleteAllFileAndContents(content,context)
                    }
                }
            }
            file.delete()
        }
    }





    fun String?.indexesOf(substr: String, ignoreCase: Boolean = true): List<Int> {
        return this?.let {
            val regex = if (ignoreCase) Regex(substr, RegexOption.IGNORE_CASE) else Regex(substr)
            regex.findAll(this).map { it.range.first }.toList()
        } ?: emptyList()
    }



    fun allPermissionsGranted(context: Context):Boolean {
        if ((checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
            (checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
        {
            // Permission is not granted
            return false
        }
        return true
    }

    fun openRecordFolder(context: Context){


        if (getDownloadDir(context).exists()) {

            // val intent = Intent(Intent.ACTION_GET_CONTENT)
            val intent = Intent(Intent.ACTION_VIEW)


            val uri = Uri.parse(getDownloadDir(context).absolutePath/*.path.absolutePath*//*.path.toString()*/ /*+ File.separator + "files" + File.separator*/)
            //       DynamicToast.makeSuccess(context, uri.toString(), 3).show()
            intent.setDataAndType(uri, "*/*")//"resource/folder"
            //  context.startActivity(Intent.createChooser(intent, "Choose a file manager to open downloaded record folder"))
            try {
                // Yes there is one start it then
                val chooserIntent = Intent.createChooser(intent, context.resources.getString(R.string.choosefilemanager))
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                context.startActivity(chooserIntent)
            } catch (ex: ActivityNotFoundException) {
                DynamicToast.makeError(context,context.resources.getString(R.string.filemanagernotfound) , 9).show()
            }
            /* if (intent.resolveActivityInfo(context.packageManager, 0) != null) {
                 // Yes there is one start it then
                 val chooserIntent = Intent.createChooser(intent, context.resources.getString(R.string.choosefilemanager))
                 chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                 context.startActivity(chooserIntent)
             } else {
                 // Did not find any activity capable of handling that intent on our system
                 DynamicToast.makeError(context,context.resources.getString(R.string.filemanagernotfound) , 9).show()
             }*/


        }
    }

    private fun getDownloadDir(context: Context):File{

// Find all audio files on the primary external storage device.
        val audioCollection =
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
             else MediaStore.Audio.Media.EXTERNAL_CONTENT_URI



        //  val f = File(context.externalCacheDir!!.absolutePath, "/RadioStation_Download")
     //   val f = File(context.applicationContext.getExternalFilesDir(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString()/*+"/RadioStation_Download"*/)!!/*.absolutePath*/, "/RadioStation_Download")
      //  val f = File(Environment.getExternalStorageDirectory().toString() + "/RadioStation_Download")
        // File(context.getExternalFilesDir(DIRECTORY_DOWNLOADS).toString() + File.separator + "RadioStation")
        //  File(getExternalStorageDirectory(DIRECTORY_DOWNLOADS).toString() + File.separator + "RadioStation")
        // File(getStorageDirectory().toString() +File.separator  + "RadioStation")

        //File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).toString() +File.separator  + "RadioStation")
        // File(context/*.applicationContext*/.getExternalFilesDir(Environment.DIRECTORY_MUSIC.toString()/*+"/RadioStation_Download"*/)!!.absolutePath, "/RadioStation")


        val f =     File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).toString() +File.separator  + "RadioStation")

        return if (f.isDirectory) {
            f// getExternalFilesDirs(context, /*DIRECTORY_DOWNLOADS*/null)[0]
        } else {

            // create a File object for the parent directory
            // val wallpaperDirectory = File("/sdcard/RadioStation_Download/")
            // have the object build the directory structure, if needed.
            f.mkdirs()
            f
            // create a File object for the output file
            // val outputFile = File(wallpaperDirectory, filename)
            // now attach the OutputStream to the file object, instead of a String representation
            //   val fos = FileOutputStream(outputFile)
        }        //   return  context.filesDir
      // return    getExternalFilesDirs(context, /*DIRECTORY_DOWNLOADS*/null)[0]

    }

    fun getRecordedFiles(context: Context): ArrayList<RecordInfo> {
        val RcordInfo = ArrayList<RecordInfo>()
            //TARGET FOLDER
            var s: RecordInfo
            if (getDownloadDir(context).exists()) {
                //GET ALL FILES IN DOWNLOAD FOLDER
                val files = getDownloadDir(context).listFiles()
                if (files != null) {
                    if(files.isNotEmpty()){
                        //LOOP THRU THOSE FILES GETTING NAME AND URI
                        for (i in files!!.indices) {
                            val file = files[i]
                            s = RecordInfo()
                            s.name = file.name
                            s.uri = Uri.fromFile(file)
                            RcordInfo.add(s)
                        }
                    }
                }

                //  DynamicToast.makeSuccess(context, getDownloadDir(context).toString(), 9).show()
            }
            else{
                DynamicToast.makeError(context, "not found", 3).show()
            }



        return RcordInfo
    }
    fun interatialadsLoad(context: Context){
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(context,context.resources.getString(R.string.interstial_adUnitId), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }
        })



    }
    fun interatialadsShow(context:Context){
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                val adRequest = AdRequest.Builder().build()
                InterstitialAd.load(context,context.resources.getString(R.string.interstial_adUnitId), adRequest, object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        mInterstitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        mInterstitialAd = interstitialAd
                    }
                })
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            }
            override fun onAdShowedFullScreenContent() {
                mInterstitialAd = null
            }
        }

        if (mInterstitialAd != null) {
            mInterstitialAd?.show(unwrap(context))
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }


    }



    fun loadImageString(context: Context,mainiconSting: String, erroricon: Int, imageview: ImageView){

        if(!MainActivity.saveData || mainiconSting.contains(COUNTRY_FLAGS_BASE_URL)){
            val imageLoader = ImageLoader.Builder(context)
                .componentRegistry {
                    // video-support
                    // gif-support
                    if (SDK_INT >= 28) {
                        add(ImageDecoderDecoder(context))
                    } else {
                        add(GifDecoder())
                    }
                }
                //  .logger(DebugLogger(Log.VERBOSE))
                .build()

           // Coil.setImageLoader(imageLoader)

            imageview.load(mainiconSting,imageLoader) {
                // crossfade(true)
                // crossfade(500)
                transformations(RoundedCornersTransformation(16f))
                error(erroricon)
                diskCachePolicy(CachePolicy.ENABLED)
                memoryCachePolicy(CachePolicy.ENABLED)
                placeholder(erroricon) //image shown when loading image
                scale(Scale.FILL)
                //   transformations(CircleCropTransformation())
                // transformations(GrayscaleTransformation())
                //   transformations(BlurTransformation(applicationContext))
                //  transformations(BlurTransformation(applicationContext, 5f))
            }
        }
    }

    fun loadImageInt(mainiconSting: Int, erroricon: Int, imageview: ImageView){
        imageview.load(mainiconSting) {
         //   crossfade(true)
         //   crossfade(500)
            transformations(RoundedCornersTransformation(16f))
            error(erroricon)
            diskCachePolicy(CachePolicy.ENABLED)
            memoryCachePolicy(CachePolicy.ENABLED)
            scale(Scale.FIT)
            placeholder(erroricon) //image shown when loading image
        //    transformations(CircleCropTransformation())
            // transformations(GrayscaleTransformation())
            //   transformations(BlurTransformation(applicationContext))
            //  transformations(BlurTransformation(applicationContext, 5f))
        }
    }
     fun loadImageSkipCache(mainiconDrawable: Drawable?, erroricon: Int, imageview: ImageView){

         imageview.load(mainiconDrawable) {
         //    crossfade(true)
          //   crossfade(500)
             transformations(RoundedCornersTransformation(16f))
             error(erroricon)
             diskCachePolicy(CachePolicy.ENABLED)
             memoryCachePolicy(CachePolicy.ENABLED)
             scale(Scale.FIT)
             placeholder(erroricon) //image shown when loading image
            // transformations(CircleCropTransformation())
             // transformations(GrayscaleTransformation())
             //   transformations(BlurTransformation(applicationContext))
             //  transformations(BlurTransformation(applicationContext, 5f))
         }

    }



    fun loadImageSkipCacheInt(mainiconDrawable: Int, erroricon: Int, imageview: ImageView){

        imageview.load(mainiconDrawable) {
            //    crossfade(true)
            //   crossfade(500)
            transformations(RoundedCornersTransformation(16f))
            error(erroricon)
            diskCachePolicy(CachePolicy.ENABLED)
            memoryCachePolicy(CachePolicy.ENABLED)
            scale(Scale.FIT)
            placeholder(erroricon) //image shown when loading image
            // transformations(CircleCropTransformation())
            // transformations(GrayscaleTransformation())
            //   transformations(BlurTransformation(applicationContext))
            //  transformations(BlurTransformation(applicationContext, 5f))
        }

    }



    fun switchColor(switch: SwitchCompat,theme:Boolean){
        if (theme) {
            switch.setTextColor(parseColor("#FFFFFF"))

        }
        else {
            switch.setTextColor(parseColor("#000000"))

        }
    }

    fun maintextviewColor(textcolor:TextView,theme:Boolean) {
        if (theme) textcolor.setTextColor(parseColor("#FFFFFF"))
        else textcolor.setTextColor(parseColor("#000000"))
    }

    fun maintextviewColored(textcolor:TextView,theme:Boolean) {
        if (theme) textcolor.setTextColor(parseColor("#00C0FF"))
        else textcolor.setTextColor(parseColor("#02142B"))
    }

    fun secondarytextviewColor(textcolor:TextView,theme:Boolean) {
        if (theme) textcolor.setTextColor(parseColor("#BABABA"))
        else textcolor.setTextColor(parseColor("#0E0E0E"))
    }

    fun hintColor(textcolor:EditText,theme:Boolean) {
        if (theme) textcolor.setHintTextColor(parseColor("#BABABA"))
        else textcolor.setHintTextColor(parseColor("#000000"))
    }

    @SuppressLint("SoonBlockedPrivateApi")
    fun setNumberPickerTextColor(numberPicker: NumberPicker ,theme:Boolean) {
        try {
            val selectorWheelPaintField: Field = numberPicker.javaClass
                .getDeclaredField("mSelectorWheelPaint")
            selectorWheelPaintField.isAccessible = true
            (selectorWheelPaintField.get(numberPicker) as Paint).color = if (theme)
                parseColor("#BABABA")  else  parseColor("#000000")
        } catch (e: NoSuchFieldException) {
            Log.w("setNumberPickerTxtColor", e)
        } catch (e: IllegalAccessException) {
            Log.w("setNumberPickerTxtColor", e)
        } catch (e: java.lang.IllegalArgumentException) {
            Log.w("setNumberPickerTxtColor", e)
        }
        val count = numberPicker.childCount
        for (i in 0 until count) {
            val child = numberPicker.getChildAt(i)
            if (child is EditText) child.setTextColor(  if (theme)
                parseColor("#BABABA")  else  parseColor("#000000"))
        }
        numberPicker.invalidate()
    }


    fun nativeadstexViewColor(textcolor1:TextView,textcolor2:TextView,textcolor3:TextView,textcolor4:TextView,textcolor5:TextView,theme:Boolean){
        if (theme) {
            textcolor1.setTextColor(parseColor("#BABABA"))
            textcolor2.setTextColor(parseColor("#BABABA"))
            textcolor3.setTextColor(parseColor("#BABABA"))
            textcolor4.setTextColor(parseColor("#BABABA"))
            textcolor5.setTextColor(parseColor("#BABABA"))

        }
        else {
            textcolor1.setTextColor(parseColor("#000000"))
            textcolor2.setTextColor(parseColor("#000000"))
            textcolor3.setTextColor(parseColor("#000000"))
            textcolor4.setTextColor(parseColor("#000000"))
            textcolor5.setTextColor(parseColor("#000000"))

        }
    }

    fun buttonColor(buttoncolor:Button,theme:Boolean){
        val gdDefault = GradientDrawable()

        if (theme){
            //  gdDefault.setColor(parseColor("#33CAEEFF"))
            // gdDefault.cornerRadius = 9f
            // gdDefault.setStroke(1, parseColor("#26000000"))
            //  buttoncolor.background = gdDefault
            buttoncolor.setTextColor(parseColor("#FFFFFF"))
        }
        else{
            //  gdDefault.setColor(parseColor("#2602142B"))
            // gdDefault.cornerRadius = 9f
            //  gdDefault.setStroke(1, parseColor("#40FFFFFF"))
            // buttoncolor.background = gdDefault
            buttoncolor.setTextColor(parseColor("#000000"))
        }
    }



    fun viewColor(view: View,theme:Boolean){
        val gdDefault = GradientDrawable()
        if (theme){
            gdDefault.setColor(parseColor("#33CAEEFF"))
            gdDefault.cornerRadius = 9f
            gdDefault.setStroke(1, parseColor("#26CAEEFF"))
            view.background = gdDefault
        }
        else{
            gdDefault.setColor(parseColor("#2602142B"))
            gdDefault.cornerRadius = 9f
            gdDefault.setStroke(1, parseColor("#4002142B"))
            view.background = gdDefault
        }

    }

    fun fabColor(mFab: FloatingActionButton,theme:Boolean){
        if (theme){
            mFab.backgroundTintList = ColorStateList.valueOf(parseColor("#000000"))

        }
        else{
            mFab.backgroundTintList = ColorStateList.valueOf(parseColor("#FFFFFF"))
        }
    }


    fun textcolorSearchviewTransition(container: androidx.appcompat.widget.SearchView,theme:Boolean){
        if (!theme){
            color1= parseColor("#000000")//-256
            color2= parseColor("#000000")//-65536
            color3= parseColor("#000000")
            color4= parseColor("#000000")
        }
        else{
            color1=parseColor("#FFFFFF")
            color2=parseColor("#FFFFFF")
            color3=parseColor("#FFFFFF")
            color4=parseColor("#FFFFFF")
        }


       //val searchView = findViewById(R.id.search) as SearchView
         val searchEditText = container.findViewById(R.id.search_src_text) as EditText
        searchEditText.setTextColor(color1)
        searchEditText.setHintTextColor(color1)
    }


    fun gradiancolorNestedScrollViewTransitionseconcolor(container: NestedScrollView, duration: Int, theme:Boolean){
        if (theme){
            color1= parseColor("#070326")//-256
            color2= parseColor("#070326")//-65536
            color3= parseColor("#070326")
            color4= parseColor("#070326")
        }
        else{
            color1=parseColor("#F0FFFF")
            color2=parseColor("#F0FFFF")
            color3=parseColor("#F0FFFF")
            color4=parseColor("#F0FFFF")
        }
        val gd1 = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(color1, color2))
        gd1.cornerRadius = 0f

        val gd = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(color3, color4))
        gd.cornerRadius = 0f


        val color = arrayOf(gd, gd1)
        val trans = TransitionDrawable(color)
        container.background = trans
        trans.startTransition(duration)
    }

    fun gradiancolorNestedScrollViewTransition(container: NestedScrollView, duration: Int, theme:Boolean){
        if (theme){
            color1= parseColor("#000000")//-256
            color2= parseColor("#000000")//-65536
            color3= parseColor("#000000")
            color4= parseColor("#000000")
        }
        else{
            color1=parseColor("#FFFFFF")
            color2=parseColor("#FFFFFF")
            color3=parseColor("#FFFFFF")
            color4=parseColor("#FFFFFF")
        }
        val gd1 = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(color1, color2))
        gd1.cornerRadius = 0f

        val gd = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(color3, color4))
        gd.cornerRadius = 0f


        val color = arrayOf(gd, gd1)
        val trans = TransitionDrawable(color)
        container.background = trans
        trans.startTransition(duration)
    }


    fun gradiancolorTransition(container: RelativeLayout, duration: Int,theme:Boolean){
        if (theme){
            color1= parseColor("#000000")//-256
            color2= parseColor("#000000")//-65536
            color3= parseColor("#000000")
            color4= parseColor("#000000")
        }
        else{
            color1=parseColor("#FFFFFF")
            color2=parseColor("#FFFFFF")
            color3=parseColor("#FFFFFF")
            color4=parseColor("#FFFFFF")
        }
        val gd1 = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(color1, color2))
        gd1.cornerRadius = 0f

        val gd = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(color3, color4))
        gd.cornerRadius = 0f


        val color = arrayOf(gd, gd1)
        val trans = TransitionDrawable(color)
        container.background = trans
        trans.startTransition(duration)
    }

    fun gradiancolorTransitionBottomSheet(container: RelativeLayout, duration: Int,theme:Boolean){
        if (theme){
            color1= parseColor("#070326")//-256
            color2= parseColor("#070326")//-65536
            color3= parseColor("#070326")
            color4= parseColor("#070326")
        }
        else{
            color1=parseColor("#F0FFFF")
            color2=parseColor("#F0FFFF")
            color3=parseColor("#F0FFFF")
            color4=parseColor("#F0FFFF")
        }
        val gd1 = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(color1, color2))
        gd1.cornerRadius = 0f

        val gd = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(color3, color4))
        gd.cornerRadius = 0f


        val color = arrayOf(gd, gd1)
        val trans = TransitionDrawable(color)
        container.background = trans
        trans.startTransition(duration)
    }

    fun gradiancolorConstraintBottomSheet(container: ConstraintLayout, duration: Int,theme:Boolean){
        if (theme){
            color1= parseColor("#070326")//-256
            color2= parseColor("#070326")//-65536
            color3= parseColor("#070326")
            color4= parseColor("#070326")
        }
        else{
            color1=parseColor("#F0FFFF")
            color2=parseColor("#F0FFFF")
            color3=parseColor("#F0FFFF")
            color4=parseColor("#F0FFFF")
        }
        val gd1 = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(color1, color2))
        gd1.cornerRadius = 0f

        val gd = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(color3, color4))
        gd.cornerRadius = 0f


        val color = arrayOf(gd, gd1)
        val trans = TransitionDrawable(color)
        container.background = trans
        trans.startTransition(duration)
    }
    fun gradiancolorTransitionConstraint(container: ConstraintLayout, duration: Int,theme:Boolean){
        if (theme){
            color1= parseColor("#000000")//-256
            color2= parseColor("#000000")//-65536
            color3= parseColor("#000000")
            color4= parseColor("#000000")
        }
        else{
            color1=parseColor("#FFFFFF")
            color2=parseColor("#FFFFFF")
            color3=parseColor("#FFFFFF")
            color4=parseColor("#FFFFFF")
        }
        val gd1 = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(color1, color2))
        gd1.cornerRadius = 0f

        val gd = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(color3, color4))
        gd.cornerRadius = 0f


        val color = arrayOf(gd, gd1)
        val trans = TransitionDrawable(color)
        container.background = trans
        trans.startTransition(duration)
    }


    fun gradiancolorLinearlayoutTransitionBottomSheet(container: LinearLayout, duration: Int,theme:Boolean){
        if (theme){
            color1= parseColor("#070326")//-256
            color2= parseColor("#070326")//-65536
            color3= parseColor("#070326")
            color4= parseColor("#070326")
        }
        else{
            color1=parseColor("#F0FFFF")
            color2=parseColor("#F0FFFF")
            color3=parseColor("#F0FFFF")
            color4=parseColor("#F0FFFF")
        }
        val gd1 = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(color1, color2))
        gd1.cornerRadius = 0f

        val gd = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(color3, color4))
        gd.cornerRadius = 0f


        val color = arrayOf(gd, gd1)
        val trans = TransitionDrawable(color)
        container.background = trans
        trans.startTransition(duration)

    }
    fun gradiancolorLinearlayoutTransition(container: LinearLayout, duration: Int,theme:Boolean){
        if (theme){
            color1= parseColor("#000000")//-256
            color2= parseColor("#000000")//-65536
            color3= parseColor("#000000")
            color4= parseColor("#000000")
        }
        else{
            color1=parseColor("#BABABA")
            color2=parseColor("#BABABA")
            color3=parseColor("#BABABA")
            color4=parseColor("#BABABA")
        }
        val gd1 = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(color1, color2))
        gd1.cornerRadius = 0f

        val gd = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(color3, color4))
        gd.cornerRadius = 0f


        val color = arrayOf(gd, gd1)
        val trans = TransitionDrawable(color)
        container.background = trans
        trans.startTransition(duration)

    }

    fun gradiancolorConstraintLayoutTransition(container: ConstraintLayout, duration: Int, theme:Boolean){
        if (theme){
            color1= parseColor("#000000")//-256
            color2= parseColor("#000000")//-65536
            color3= parseColor("#000000")
            color4= parseColor("#000000")
        }
        else{
            color1=parseColor("#FFFFFF")
            color2=parseColor("#FFFFFF")
            color3=parseColor("#FFFFFF")
            color4=parseColor("#FFFFFF")
        }
        val gd1 = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(color1, color2))
        gd1.cornerRadius = 0f

        val gd = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(color3, color4))
        gd.cornerRadius = 0f


        val color = arrayOf(gd, gd1)
        val trans = TransitionDrawable(color)
        container.background = trans
        trans.startTransition(duration)

    }


    fun gradiancolorFrameLayout(container: FrameLayout, duration: Int,theme:Boolean){

        if (theme){
            color1= parseColor("#000000")//-256
            color2= parseColor("#000000")//-65536
            color3= parseColor("#000000")
            color4= parseColor("#000000")
        }
        else{
            color1=parseColor("#FFFFFF")
            color2=parseColor("#FFFFFF")
            color3=parseColor("#FFFFFF")
            color4=parseColor("#FFFFFF")
        }
        val gd1 = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(
                color1,
                color2
            )
        )
        gd1.cornerRadius = 0f

        val gd = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(
                color3,
                color4
            )
        )
        gd.cornerRadius = 0f


        val color = arrayOf(gd, gd1)
        val trans = TransitionDrawable(color)
        container.background = trans
        trans.startTransition(duration)
    }


    fun gradiancolorNativeAdslayout(container: FrameLayout, duration: Int){

        color1=parseColor("#00000000")
        color2=parseColor("#00000000")
        color3=parseColor("#00000000")
        color4=parseColor("#00000000")
        val gd1 = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(color1, color2))
        gd1.cornerRadius = 0f

        val gd = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(color3, color4))
        gd.cornerRadius = 0f


        val color = arrayOf(gd, gd1)
        val trans = TransitionDrawable(color)
        container.background = trans
        trans.startTransition(duration)
    }

    fun parseColor(colorString: String):Int {

        return colorString.toColorInt()
    }

    fun colouriseToolbar(appBarLayout: CollapsingToolbarLayout, @ColorInt background:Int, @ColorInt foreground:Int) {
        appBarLayout.setBackgroundColor(background)
        val toolbar = appBarLayout.getChildAt(0) as Toolbar
        toolbar.setTitleTextColor(foreground)
        toolbar.setSubtitleTextColor(foreground)
        val colorFilter = PorterDuffColorFilter(foreground, PorterDuff.Mode.MULTIPLY)
        for (i in 0 until toolbar.childCount)
        {
            val view = toolbar.getChildAt(i)
            //todo: cal icon?
            //Back button or drawer open button
            if (view is ImageButton)
            {
                view.drawable.colorFilter = colorFilter
            }
            if (view is ActionMenuView)
            {
                for (j in 0 until view.childCount)
                {
                    val innerView = view.getChildAt(j)
                    //Any ActionMenuViews - icons that are not back button, text or overflow menu
                    if (innerView is ActionMenuItemView)
                    {
                        val drawables = innerView.compoundDrawables
                        for (k in drawables.indices)
                        {
                            val drawable = drawables[k]
                            if (drawable != null)
                            {
                                //Set the color filter in separate thread
                                //by adding it to the message queue - won't work otherwise
                                innerView.post {
                                    innerView.compoundDrawables[k].colorFilter =
                                        colorFilter
                                }
                            }
                        }
                    }
                }
            }
        }
        //Overflow icon
        val overflowIcon = toolbar.overflowIcon
        if (overflowIcon != null)
        {
            overflowIcon.colorFilter = colorFilter
            toolbar.overflowIcon = overflowIcon
        }
    }




    fun nativeSmallAds(context: Context, ad_frame: FrameLayout, adView:NativeAdView){
        fun populateUnifiedNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {

            // You must call destroy on old ads when you are done with them,
            // otherwise you will have a memory leak.
            currentNativeAd?.destroy()
            currentNativeAd = nativeAd


            // Set the media view. Media content will be automatically populated in the media view once
            // adView.setNativeAd() is called.
            adView.mediaView = adView.findViewById(R.id.ad_media)



            // Set other ad assets.
            adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            adView.iconView = adView.findViewById(R.id.ad_app_icon)
            adView.priceView = adView.findViewById(R.id.ad_price)
            adView.starRatingView = adView.findViewById(R.id.ad_stars)
            adView.storeView = adView.findViewById(R.id.ad_store)
            adView.advertiserView = adView.findViewById(R.id.ad_advertiser)


            if(adView.headlineView!=null){
                    nativeadstexViewColor(
                        adView.headlineView as TextView,
                        adView.advertiserView as TextView,
                        adView.bodyView as TextView,
                        adView.priceView as TextView,
                        adView.storeView as TextView,
                        darkTheme
                    )

                // The headline is guaranteed to be in every UnifiedNativeAd.
                (adView.headlineView as TextView).text = nativeAd.headline

                // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
                // check before trying to display them.
                if (nativeAd.body == null) {
                    adView.bodyView!!.visibility = View.INVISIBLE//INVISIBLE
                } else {
                    adView.bodyView!!.visibility = View.VISIBLE
                    (adView.bodyView as TextView).text = nativeAd.body
                }

                if (nativeAd.callToAction == null) {
                    adView.callToActionView!!.visibility = View.INVISIBLE//INVISIBLE


                } else {
                    adView.callToActionView!!.visibility = View.VISIBLE
                    (adView.callToActionView as Button).text = nativeAd.callToAction


                }

                if (nativeAd.icon == null) {
                    adView.iconView!!.visibility = View.INVISIBLE//INVISIBLE
                } else {
                    (adView.iconView as ImageView).setImageDrawable(
                        nativeAd.icon!!.drawable
                    )
                    adView.iconView!!.visibility = View.VISIBLE
                }

                if (nativeAd.price == null) {
                    adView.priceView!!.visibility = View.INVISIBLE//INVISIBLE
                } else {
                    adView.priceView!!.visibility = View.VISIBLE
                    (adView.priceView as TextView).text = nativeAd.price
                }

                if (nativeAd.store == null) {
                    adView.storeView!!.visibility = View.INVISIBLE//INVISIBLE
                } else {
                    adView.storeView!!.visibility = View.VISIBLE
                    (adView.storeView as TextView).text = nativeAd.store
                }

                if (nativeAd.starRating == null) {
                    adView.starRatingView!!.visibility = View.INVISIBLE//INVISIBLE
                } else {
                    (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
                    adView.starRatingView!!.visibility = View.VISIBLE
                }

                if (nativeAd.advertiser == null) {
                    adView.advertiserView!!.visibility = View.INVISIBLE//INVISIBLE
                }
                else {
                    (adView.advertiserView as TextView).text = nativeAd.advertiser
                    adView.advertiserView!!.visibility = View.VISIBLE
                }

                adView.mediaView!!.visibility = View.GONE
                adView.priceView!!.visibility = View.GONE
                adView.storeView!!.visibility = View.GONE
                adView.advertiserView!!.visibility = View.GONE
            }





            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad. The SDK will populate the adView's MediaView
            // with the media content from this native ad.
            adView.setNativeAd(nativeAd)

            // Get the video controller for the ad. One will always be provided, even if the ad doesn't
            // have a video asset.
            /*  val vc = nativeAd.videoController

              // Updates the UI to say whether or not this ad has a video asset.


             if (vc.hasVideoContent()) {
                  videostatus_text.text = String.format(
                      Locale.getDefault(),
                      "Video status: Ad contains a %.2f:1 video asset.",
                      vc.aspectRatio)

                  // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
                  // VideoController will call methods on this object when events occur in the video
                  // lifecycle.
                  vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
                      override fun onVideoEnd() {
                          // Publishers should allow native ads to complete video playback before
                          // refreshing or replacing them with another ad in the same UI location.
                          refresh_button.isEnabled = true
                          videostatus_text.text = "Video status: Video playback has ended."
                          super.onVideoEnd()
                      }
                  }
              } else {
                  videostatus_text.text = "Video status: Ad does not contain a video asset."
                  refresh_button.isEnabled = true
              }*/
        }


        fun refreshAd() {
            //refresh_button.isEnabled = false

            val builder = AdLoader.Builder(
                context, context.resources.getString(
                    R.string.native_Advanced_adUnitId
                )/*"ca-app-pub-5900899997553420/8708850645"*/
            )

            builder.forNativeAd { unifiedNativeAd ->
                // OnUnifiedNativeAdLoadedListener implementation.
                //     val adView = layoutInflater.inflate(R.layout.ad_unified, null) as UnifiedNativeAdView


                populateUnifiedNativeAdView(unifiedNativeAd, adView)
                ad_frame.removeAllViews()
                ad_frame.addView(adView)


            }

            val videoOptions = VideoOptions.Builder()
                // .setStartMuted(start_muted_checkbox.isChecked)
                .build()

            val adOptions = NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build()

            builder.withNativeAdOptions(adOptions)


            val adLoader = builder.withAdListener(object : AdListener() {

                override fun onAdLoaded() {
                    // Code to be executed when an ad finishes loading.


                }


                override fun onAdOpened() {
                    // Code to be executed when the ad is displayed.

                }

                override fun onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                }

                override fun onAdClosed() {
                    // Code to be executed when the interstitial ad is closed.

                }
            }).build()


            adLoader.loadAd(AdRequest.Builder().build())

            // videostatus_text.text = ""
        }
        refreshAd()
    }

    fun dialogueBackround():Int{
        return if (darkTheme){
            R.drawable.round_corner_dark
        }
        else{
            R.drawable.round_corner_light
        }
    }





    fun homepageChrome(context: Context, homepageJson: String) {
        if (homepageJson!="") {

            if (!homepageJson.startsWith("http://") && !homepageJson.startsWith("https://")){
                val browserIntent = Intent()
                    .setAction(Intent.ACTION_VIEW)
               //    .addCategory(Intent.CATEGORY_BROWSABLE)
               //     .putExtra(SearchManager.QUERY, "http://$homepageJson")
            //        .setData(Uri.fromParts("http", "", null))
                browserIntent.data = Uri.parse("http://$homepageJson")
                unwrap(context).startActivity(browserIntent)
            }
            else{
                val browserIntent = Intent()
                    .setAction(Intent.ACTION_VIEW)
                   // .addCategory(Intent.CATEGORY_BROWSABLE)
                 //   .putExtra(SearchManager.QUERY, homepageJson)
                 //   .setData(Uri.fromParts("http", "", null))
                browserIntent.data = Uri.parse(homepageJson)
                unwrap(context).startActivity(browserIntent)
            }



   /*
            val url: String = homepageJson
            val intent = Intent(Intent.ACTION_VIEW)

            if (!url.startsWith("http://") && !url.startsWith("https://")){
                intent.data = Uri.parse(url)
                unwrap(context).startActivity(intent)
            }
            else{
                intent.data = Uri.parse(url)
                unwrap(context).startActivity(intent)
            }
*/
        }
        else DynamicToast.makeError(context,context.resources.getString(R.string.No_Homepage) , 9).show()
    }

    fun copytext(context: Context, textToCopy:String) {


        val myClipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val myClip: ClipData = ClipData.newPlainText("note_copy", textToCopy)
        myClipboard.setPrimaryClip(myClip)
        DynamicToast.makeSuccess(context, context.resources.getString(R.string.StreamInfoCopied), 3).show()

    }





    class SafeClickListener(

        private var defaultInterval: Int = 2000,
        private val onSafeCLick: (View) -> Unit
    ) : View.OnClickListener {
        private var lastTimeClicked: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
                return
            }
            lastTimeClicked = SystemClock.elapsedRealtime()
            onSafeCLick(v)
        }
    }


    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }

    fun infoString():String{
        return if(icyandState=="") MainActivity.icybackup else icyandState
    }












/*
    fun OnfailiurejsonCall(context: Context){

        if (repeat_tryconnect_server < server_arraylist.size - 1) {
            repeat_tryconnect_server += 1
            server_arraylist[repeat_tryconnect_server]
            //    val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            //    val editor = sharedPref.edit()

            //   editor.putString("prefered_servers", server_arraylist[repeat_tryconnect_server])
            //    editor.apply()
            BASE_URL = server_arraylist[repeat_tryconnect_server]
       //     jSon_list_of_country_Calls()
        }
        else {
            repeat_tryconnect_server=-1

            DynamicToast.makeError(context, "Failed to connect to $BASE_URL", 3).show()
            /* val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
             val editor = sharedPref.edit()

             editor.putString("prefered_servers", "de1.api.radio-browser.info")
             editor.apply()
             BASE_URL = "de1.api.radio-browser.info"*/
        }
    }
*/


fun getuserid():String{
    return if(userRecord.uid!=null) userRecord.uid!!
    else "no_user"
}

    fun errorToast(context: Context,message: String) {
        DynamicToast.make(context, message, 9).show()
    }

    fun succesToast(context: Context,message: String) {
        DynamicToast.make(context, message, 9).show()
    }

    fun warningToast(context: Context,message: String) {
        DynamicToast.makeWarning(context, message, 9).show()
    }
    fun dynamicToast(context: Context,message: String) {
        DynamicToast.make(context, message, 9).show()
    }
}




