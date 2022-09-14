package uk.ac.shef.oak.com4510.db

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.exifinterface.media.ExifInterface
import android.util.Log
import androidx.room.*
import pl.aprilapps.easyphotopicker.MediaFile
import uk.ac.shef.oak.com4510.db.relation.PathDataWithMeasurements
import uk.ac.shef.oak.com4510.util.*
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
/**
 * Entity representing image added to the app
 */
@Entity
data class Photo(
    @PrimaryKey(autoGenerate = true) val id : Long = 0,
    @ColumnInfo(name = "uri") val uri: String,
    @Embedded var location: Location,
    @ColumnInfo(name = "datetime") val datetime: Long,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "thumbnail_uri") val thumbnailUri: String,
    @ColumnInfo(name = "path_id") var pathId : Long? = null

){
    @delegate:Ignore
    val thumbnail : Bitmap by lazy{val options = BitmapFactory.Options()
        options.inJustDecodeBounds = false
        BitmapFactory.decodeFile(thumbnailUri, options)}
    @delegate:Ignore
    val bitmap : Bitmap by lazy{val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(uri, options)}

    companion object {

        private fun decodeSampledBitmapFromResource(filePath: String, reqWidth: Int, reqHeight: Int): Bitmap {
            // First decode with inJustDecodeBounds=true to check dimensions
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filePath, options)
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeFile(filePath, options)
        }

        private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            // Raw height and width of image
            val height = options.outHeight; val width = options.outWidth
            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) {
                val halfHeight = (height / 2)
                val halfWidth = (width / 2)
                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2
                }
            }
            return inSampleSize
        }


        suspend fun build(f: MediaFile, p: PathDataWithMeasurements?): Photo {
            var time: Long? = null
            var location: Location? = null
            var thumbnailPath = ""
            try {

                val meta = (runCatching{ExifInterface(f.file.absolutePath)}).getOrThrow()
                val dateString = meta.getAttribute(ExifInterface.TAG_DATETIME)
                val df = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.UK)
                time = df.parse(dateString?: "2021:12:17 00:00:00")?.time
                val temp = FloatArray(2)
                if (meta.getLatLong(temp)) {
                    location = Location(temp[1], temp[0])
                }
                thumbnailPath = f.file.parent?.plus( "/" + f.file.nameWithoutExtension + "_thumbnail.jpg")?: ""
                runCatching {
                    val out = FileOutputStream(thumbnailPath)
                    out.use {
                        val thumbnail =
                            decodeSampledBitmapFromResource(f.file.absolutePath, 150, 120)
                        thumbnail.compress(Bitmap.CompressFormat.JPEG, 70, out)
                    }
                }

            } catch (e: IOException) {
                Log.i("Photo", "File ${f.file.absoluteFile} not found")
            }

            return Photo(
                uri = f.file.absolutePath,
                datetime = time ?: 0,
                location = location ?: Location(0.0f, 0.0f),
                thumbnailUri = thumbnailPath,
                pathId = p?.path?.id
            )
        }

        suspend fun build(f: MediaFile) : Photo{
            return build(f,null)
        }
    }
}