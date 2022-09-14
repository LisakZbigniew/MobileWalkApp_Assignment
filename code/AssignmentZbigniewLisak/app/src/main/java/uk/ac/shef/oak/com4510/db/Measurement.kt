package uk.ac.shef.oak.com4510.db

import androidx.room.*
import uk.ac.shef.oak.com4510.util.*
/**
 * Entity representing a point on the path ie. all the measurements in time
 */
@Entity(tableName = "path_point",primaryKeys = ["path_id","datetime"])
data class Measurement (

    @ColumnInfo (name = "datetime") val datetime : Long,
    @Embedded val location : Location,
    @ColumnInfo (name = "temperature")val temperature: Int,
    @ColumnInfo (name = "pressure")val pressure: Float,
    @ColumnInfo (name = "path_id")var pathId: Long = 0

)