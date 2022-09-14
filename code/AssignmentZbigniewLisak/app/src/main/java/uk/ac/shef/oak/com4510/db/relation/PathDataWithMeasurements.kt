package uk.ac.shef.oak.com4510.db.relation

import androidx.room.*
import uk.ac.shef.oak.com4510.db.*
/**
 * Class representing a path with all the measurements
 */
data class PathDataWithMeasurements (
    @Embedded val path: PathData,
    @Relation(
        parentColumn = "id",
        entityColumn = "path_id"
    )
    val measurements: List<Measurement>
)