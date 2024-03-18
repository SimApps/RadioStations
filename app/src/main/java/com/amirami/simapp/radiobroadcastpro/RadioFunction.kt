package com.amirami.simapp.radiobroadcastpro


import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.Environment.getExternalStoragePublicDirectory
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.toColorInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.util.UnstableApi
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.load
import coil.request.CachePolicy
import coil.transform.RoundedCornersTransformation
import com.amirami.simapp.radiobroadcastpro.MainActivity.Companion.userRecord
import com.amirami.simapp.radiobroadcastpro.model.RadioEntity
import com.amirami.simapp.radiobroadcastpro.utils.Constatnts.COUNTRY_FLAGS_BASE_URL
import com.amirami.simapp.radiobroadcastpro.utils.Constatnts.RECORDS_FILE_NAME
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@UnstableApi object RadioFunction {

    fun getCurrentDate(): Long {
        return System.currentTimeMillis() // DateFormat.getDateTimeInstance().format(currentDate) // formated
    }


    fun shortformateDate(Date: String): String {

        return if (isNumber(Date)) SimpleDateFormat("d MMM yyyy HH:mm", Locale.getDefault()).format(
            Date.toLong()
        )
        else Date
    }

    fun shareRadio(
        context: Context,
        radio: RadioEntity,
        icy : String,
        isRec : Boolean
    ) {
        if (radio.name != "") {
            if (isRec) {
                try {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.type = "text/plain"
                    intent.putExtra(
                        Intent.EXTRA_TEXT,
                        "I am listening to ${radio.name} recorded with ${context.resources.getString(R.string.app_name)} application.\n" +
                            "Information about redorded file : $icy \n" +
                            "Download App from :  http://play.google.com/store/apps/details?id=" + context.packageName
                    )
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Radio Name : ${radio.name}")
                    context.startActivity(
                        Intent.createChooser(
                            intent,
                            "Share Radio Information Via"
                        )
                    )
                } catch (e: Exception) {
                    // e.toString();
                }
            } else {
                try {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.type = "text/plain"
                    intent.putExtra(
                        Intent.EXTRA_TEXT,
                        "I am listening to ${radio.name} on ${context.resources.getString(R.string.app_name)}  application.\n" +
                            "Radio Homepage : ${radio.homepage} \n" +
                            "Radio Stream URL : ${radio.streamurl} \n" +
                            "Radio Country : ${radio.country}  \n" +
                            "Radio Language : ${radio.language} \n" +
                            "Radio Bitrate : ${radio.bitrate} \n \n" +
                            "Download App from :  http://play.google.com/store/apps/details?id=" + context.packageName
                    )
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Radio Name : ${radio.name}")
                    context.startActivity(
                        Intent.createChooser(
                            intent,
                            "Share Radio Information Via"
                        )
                    )
                } catch (e: Exception) {
                    // e.toString();
                }
            }
        } else DynamicToast.makeError(
            context,
            context.resources.getString(R.string.Select_radio_toshare),
            9
        ).show()
    }



    fun unwrap(context: Context): Activity {
        var scontext = context
        while (scontext !is Activity && scontext is ContextWrapper) {
            scontext = scontext.baseContext
        }
        return scontext as Activity
    }

    fun countryCodeToName(GlobalCountriesJsons: String?): String {
        return when (GlobalCountriesJsons!!.uppercase()) {
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
            else -> GlobalCountriesJsons
        }
    }

    fun isNumber(s: String?): Boolean =
        if (s.isNullOrEmpty()) false else s.all { Character.isDigit(it) }




    fun removeWord(value: String, wordtoremove: String): String {
        var result = ""
        var possibleMatch = ""
        var i = 0
        var j = 0
        while (i in value.indices) {
            if (value[i] == wordtoremove[j]) {
                if (j == wordtoremove.length - 1) { // match
                    possibleMatch = "" // discard word
                    j = 0
                } else {
                    possibleMatch += value[i]
                    j++
                }
            } else {
                result += possibleMatch
                possibleMatch = ""

                if (j == 0) {
                    result += value[i]
                } else {
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
            Locale.getDefault(),
            "%1$,.1f %2\$s",
            byte * 1024,
            str[str.size - 1]
        )
    }

    fun deleteRecordedItem(index: Int, context: Context) {
        val file = getDownloadDir().listFiles()!![index]

        try {
            deleteAllFileAndContents(file, context)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteAllFileAndContents(file: File, context: Context) {
        if (file.exists()) {
            /*
              // if (file.isDirectory) {
                   val contents = file.listFiles()
                   if (contents != null) {
                       for (content in contents) {
                           deleteAllFileAndContents(content,context)
                       }
                   }
             //  }
               */

            val result = file.delete()
            if (result) errorToast(
                context,
                context.getString(R.string.sucssessDeleteRecord, shortRecordFileName(file))
            )
            else errorToast(
                context,
                context.getString(R.string.errorDeleteRecord, shortRecordFileName(file))
            )
        }
    }

    fun shortRecordFileName(file: File): String {
        return if (file.name.contains("_ _", true)) file.name.substring(
            0,
            file.name.indexesOf("_ _", true)[0]
        )
        else file.name
    }

    fun String?.indexesOf(substr: String, ignoreCase: Boolean = true): List<Int> {
        return this?.let {
            val regex = if (ignoreCase) Regex(substr, RegexOption.IGNORE_CASE) else Regex(substr)
            regex.findAll(this).map { it.range.first }.toList()
        } ?: emptyList()
    }


    fun openRecordFolder(context: Context) {
        if (getDownloadDir().exists()) {
            // val intent = Intent(Intent.ACTION_GET_CONTENT)
            // val intent = Intent(Intent.ACTION_VIEW)
            val uri = Uri.parse(getDownloadDir().absolutePath)

            val intent = Intent(Intent.ACTION_VIEW).apply {
                // addCategory(Intent.CATEGORY_OPENABLE)
                setDataAndType(uri, "*/*")
                // data = uri
            }

            try {
                // Yes there is one start it then
                val chooserIntent = Intent.createChooser(
                    intent,
                    context.resources.getString(R.string.choosefilemanager)
                )
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                context.startActivity(chooserIntent)
            } catch (ex: ActivityNotFoundException) {
                DynamicToast.makeError(
                    context,
                    context.resources.getString(R.string.filemanagernotfound),
                    9
                ).show()
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

     fun getDownloadDir(): File {
        val f = File(
            getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).toString() +
                File.separator + RECORDS_FILE_NAME
        )
      /*  val f = File(
            context.getExternalFilesDir(null).toString()
                   /* + File.separator + RECORDS_FILE_NAME*/
        )*/

        return if (f.isDirectory) f
        else {
            f.mkdirs()
            f
        }
    }

    fun getRecordedFiles(context: Context): ArrayList<RadioEntity> {
        val RcordInfo = ArrayList<RadioEntity>()
        // TARGET FOLDER
        var s: RadioEntity
        if (getDownloadDir().exists()) {
            // GET ALL FILES IN DOWNLOAD FOLDER
            val files = getDownloadDir().listFiles()
            if (files != null) {
                if (files.isNotEmpty()) {
                    // LOOP THRU THOSE FILES GETTING NAME AND URI
                    for (i in files.indices) {
                        val file = files[i]
                        s = RadioEntity()




                        if (file.name.contains("_ _", true) && file.name.contains("___", true)) {
                            s.name = file.name.substring(0, file.name.indexesOf("_ _", true)[0])

                            s.icyState = file.name.substring(
                                file.name.indexesOf("_ _", true)[0] + 3,
                                file.name.indexesOf("___", true)[0]
                            ) + " " +
                                    shortformateDate(
                                        file.name.substring(
                                            file.name.indexesOf("___", true)[0] + 3,
                                            file.name.length - 4
                                        )
                                    ) + ".mp3"
                        } else {
                            s.name = file.name
                            s.icyState = ""
                        }



                        s.streamurl = file.toString()//Uri.fromFile(file)
                        s.isRec = true
                        RcordInfo.add(s)
                    }
                }
            }

            //  DynamicToast.makeSuccess(context, getDownloadDir(context).toString(), 9).show()
        } else {
            DynamicToast.makeError(context, "not found", 3).show()
        }

        return RcordInfo
    }


    fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
        } else {
            @Suppress("DEPRECATION") getPackageInfo(packageName, flags)
        }
    fun loadImageString(
        context: Context,
        mainiconSting: String,
        erroricon: Int,
        imageview: ImageView,
        cornerRadius: Float
    ) {
        Log.d("nndhsnsn",mainiconSting)
        if (!MainActivity.saveData || mainiconSting.contains(COUNTRY_FLAGS_BASE_URL)) {
            val imageLoader = ImageLoader.Builder(context)
                .components {
                    add(SvgDecoder.Factory())
                    if (SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
                    else add(GifDecoder.Factory())
                }
                .build()

            imageview.load(mainiconSting, imageLoader) {
                // if (mainiconSting.contains(COUNTRY_FLAGS_BASE_URL))
                transformations(RoundedCornersTransformation(/*16f*/cornerRadius))
                //  else transformations(RoundedCornersTransformation(8f))
                error(erroricon)
                diskCachePolicy(CachePolicy.ENABLED)
                memoryCachePolicy(CachePolicy.ENABLED)
           //     placeholder(erroricon) // image shown when loading image
            }
        }
    }

    fun loadImageInt(mainiconSting: Int, erroricon: Int, imageview: ImageView) {
        imageview.load(mainiconSting) {
            transformations(RoundedCornersTransformation(8f))
            error(erroricon)
            diskCachePolicy(CachePolicy.ENABLED)
            memoryCachePolicy(CachePolicy.ENABLED)
            placeholder(erroricon) // image shown when loading image
        }
    }

    fun parseColor(colorString: String): Int {
        return colorString.toColorInt()
    }



    fun homepageChrome(context: Context, homepageJson: String) {
        if (homepageJson != "") {
            if (!homepageJson.startsWith("http://") && !homepageJson.startsWith("https://")) {
                val browserIntent = Intent()
                    .setAction(Intent.ACTION_VIEW)
                //    .addCategory(Intent.CATEGORY_BROWSABLE)
                //     .putExtra(SearchManager.QUERY, "http://$homepageJson")
                //        .setData(Uri.fromParts("http", "", null))
                browserIntent.data = Uri.parse("http://$homepageJson")
                unwrap(context).startActivity(browserIntent)
            } else {
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
        } else DynamicToast.makeError(context, context.resources.getString(R.string.No_Homepage), 9)
            .show()
    }

    fun copytext(context: Context, textToCopy: String) {
        val myClipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val myClip: ClipData = ClipData.newPlainText("note_copy", textToCopy)
        myClipboard.setPrimaryClip(myClip)
        DynamicToast.makeSuccess(context, context.resources.getString(R.string.StreamInfoCopied), 3)
            .show()
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



    fun getuserid(): String {
        return if (userRecord.uid != null) userRecord.uid!!
        else "no_user"
    }

    fun errorToast(context: Context, message: String) {
        DynamicToast.make(context, message, 19).show()
    }

    fun succesToast(context: Context, message: String) {
        DynamicToast.make(context, message, 9).show()
    }

    fun warningToast(context: Context, message: String) {
        DynamicToast.makeWarning(context, message, 9).show()
    }

    fun dynamicToast(context: Context, message: String) {
        DynamicToast.make(context, message, 9).show()
    }


    fun ImageView.setFavIcon(isFav : Boolean){

        if (isFav)
            this.setImageResource(R.drawable.ic_liked)
        else
            this.setImageResource(R.drawable.ic_like)


    }
    fun   moveItemToFirst(array: MutableList<RadioEntity>, item: RadioEntity) : List<RadioEntity> {
        val index = array.indexOf(item)
        for (i in index downTo 1) {
            array[i] = array[i - 1]
        }
        array[0] = item

        return array.toList()
    }

     fun <T> collectLatestLifecycleFlow(lifecycleOwner : LifecycleOwner, flow: Flow<T>, collect: suspend (T) -> Unit) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                flow.collectLatest(collect)
            }
        }
    }



  /*  private fun handleFavClick(context: Context,radioVar : RadioEntity, isRec: Boolean){
        if (!isRec) {
            val isFav = infoViewModel.isFavRadio(radioVar)
            if (!isFav && radioVar.stationuuid != "") {
                addFavoriteRadioIdInArrayFirestore(radioVar.stationuuid)
            } else if (isFav) {
                deleteFavoriteRadioFromArrayinfirestore(radioVar.stationuuid)
            }

            infoViewModel.setFavRadio(radioVar)
            binding.radioplayer.likeImageViewPlayermain.setFavIcon(!isFav)
            binding.radioplayer.likeImageView.setFavIcon(!isFav)
            //  simpleMediaViewModel.setRadioVar(radioVar)


        } else {
            if (MainActivity.firstTimeopenRecordfolder) {
                MainActivity.firstTimeopenRecordfolder = false
                if (!isFinishing) {
                    val navController = Navigation.findNavController(
                        this@MainActivity,
                        R.id.fragment_container
                    )
                    navController.navigateUp()
                    val bundle = bundleOf(
                        "title" to getString(R.string.Keep_in_mind),
                        "msg" to getString(R.string.recordmessage)
                    )
                    navController.navigate(R.id.infoBottomSheetFragment, bundle)
                }
                dataViewModel.saveFirstTimeopenRecordFolder(MainActivity.firstTimeopenRecordfolder)

            }
            RadioFunction.openRecordFolder(this@MainActivity)
        }
    }
    private fun addFavoriteRadioIdInArrayFirestore(context: Context,
                                                   lifecycleOwner : LifecycleOwner,
                                                   radioUid: String,favoriteFirestoreViewModel: FavoriteFirestoreViewModel) {
        val addFavoritRadioIdInArrayFirestore =
            favoriteFirestoreViewModel.addFavoriteRadioidinArrayFirestore(
                radioUid,
                getCurrentDate()
            )
        addFavoritRadioIdInArrayFirestore.observe(lifecycleOwner) {
            // if (it != null)  if (it.data!!)  prod name array updated
            RadioFunction.interatialadsShow(context)
            if (it.e != null) {
                // prod bame array not updated
                errorToast(context, it.e!!)

                if(it.e!!.contains( "NOT_FOUND") ){
                    val isProductAddLiveData = favoriteFirestoreViewModel.addUserDocumentInFirestore(
                        FavoriteFirestore()
                    )

                    isProductAddLiveData.observe(lifecycleOwner) { dataOrException ->
                        val isProductAdded = dataOrException.data
                        if (isProductAdded != null) {
                            //   hideProgressBar()

                        }
                        if (dataOrException.e != null) {
                            errorToast(context,dataOrException.e!!)
                            /*   if(dataOrException.e=="getRadioUID"){

                               }*/

                        }
                    }
                }

            }
        }
    }

    private fun deleteFavoriteRadioFromArrayinfirestore(context: Context,
                                                        lifecycleOwner : LifecycleOwner,
                                                        radioUid: String,
                                                        favoriteFirestoreViewModel: FavoriteFirestoreViewModel) {
        val deleteFavoriteRadiofromArrayInFirestore =
            favoriteFirestoreViewModel.deleteFavoriteRadioFromArrayinFirestore(radioUid)
        deleteFavoriteRadiofromArrayInFirestore.observe(lifecycleOwner) {
            RadioFunction.interatialadsShow(context)
            // if (it != null)  if (it.data!!)  prod name array updated
            if (it.e != null) {
                // prod bame array not updated
                RadioFunction.dynamicToast(context, it.e!!)
            }
        }
    }
*/

}
