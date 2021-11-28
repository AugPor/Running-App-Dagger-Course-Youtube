package com.example.runningappdaggercourse.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

/**The Room's @TypeConverter annotation indicates that a function can be used to convert a complex type, such as a Bitmap, into a simpler type
 * that can be saved in the database. So, Room will use these functions to do this conversion.
 */
class Converters {

    /**Takes in a ByteArray and converts it into a Bitmap.*/
    @TypeConverter
    fun toBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes,0, bytes.size)
    }

    /**Takes in a Bitmap and returns an ByteArray that represents the picture in the Bitmap.*/
    @TypeConverter
    fun fromBitmap(bmp: Bitmap): ByteArray {
        //To convert the Bitmap into a ByteArray, we need to create a ByteArrayOutputStream, compress the bitmap in it and use the output stream to return the ByteArray.
        val outputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)      //This function saves a compressed version of the Bitmap in an output stream given in the third parameter, using the format given by the first parameter (in this case, png) and a quality indicated from 0-100 in the second parameter (what this means depends on the format set in the first parameter).
        return outputStream.toByteArray()
    }
}