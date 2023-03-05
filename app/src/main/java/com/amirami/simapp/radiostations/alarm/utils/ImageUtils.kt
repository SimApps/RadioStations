package com.amirami.simapp.radiostations.alarm.utils

import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.load
import coil.request.CachePolicy
import coil.transform.RoundedCornersTransformation
import com.amirami.simapp.radiostations.R
import java.io.File


fun ImageView.getBackgroundImage(backgroundUrl: Uri?) {

    if (backgroundUrl != null) {


        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

        val cursor: Cursor? = this.context?.contentResolver?.query(
            backgroundUrl, filePathColumn, null, null, null
        )
        cursor?.moveToFirst()

        val columnIndex: Int? = cursor?.getColumnIndex(filePathColumn[0])
        val filePath: String? = columnIndex?.let { cursor?.getString(it) }
        cursor?.close()


        val imageLoader = ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
                if (Build.VERSION.SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
                else add(GifDecoder.Factory())
            }
            .build()

        this.load(filePath?.let { File(it) }, imageLoader) {
            // if (mainiconSting.contains(COUNTRY_FLAGS_BASE_URL))
            transformations(RoundedCornersTransformation(16f))
            //  else transformations(RoundedCornersTransformation(8f))
            error(R.drawable.temp2)
            diskCachePolicy(CachePolicy.ENABLED)
            memoryCachePolicy(CachePolicy.ENABLED)
            placeholder(R.drawable.temp2) // image shown when loading image
        }


    }
}
