package uk.ac.shef.oak.com4510.db.relation

import androidx.room.Embedded
import androidx.room.Relation
import uk.ac.shef.oak.com4510.db.*
/**
 * Class representing a path with its photos
 */
data class PathDataWithPhotos (
    @Embedded val path: PathData,
    @Relation(
        parentColumn = "id",
        entityColumn = "path_id"
    )
    val photos : List<Photo>

        )