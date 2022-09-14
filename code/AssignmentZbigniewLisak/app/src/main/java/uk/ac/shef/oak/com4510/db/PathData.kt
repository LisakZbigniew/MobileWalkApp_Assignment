package uk.ac.shef.oak.com4510.db

import androidx.room.*

/**
 * Entity representing a single path
 */

@Entity(tableName = "path")
data class PathData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "title") val title: String
)