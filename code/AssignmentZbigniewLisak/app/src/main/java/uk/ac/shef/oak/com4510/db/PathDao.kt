package uk.ac.shef.oak.com4510.db

import androidx.lifecycle.LiveData
import androidx.room.*
import uk.ac.shef.oak.com4510.db.relation.*

@Dao
interface PathDao{
    @Transaction
    @Query("SELECT * FROM path WHERE id = :pathId")
    fun getPathWithMeasurements(pathId: Long) : LiveData<PathDataWithMeasurements>

    @Transaction
    @Query("SELECT * FROM path")
    fun getAllPathsWithMeasurements() : LiveData<List<PathDataWithMeasurements>>

    @Query("SELECT * FROM photo WHERE id = :photoId")
    fun getLivePhoto(photoId: Long) : LiveData<Photo>

    @Query("SELECT * FROM photo WHERE id = :photoId")
    suspend fun getPhoto(photoId: Long) : Photo

    @Query("SELECT * FROM path_point JOIN photo ON photo.path_id = path_point.path_id WHERE photo.id = :photoId AND path_point.datetime < photo.datetime ORDER BY path_point.datetime DESC LIMIT 1")
    suspend fun getPhotoMeasurement(photoId: Long) : Measurement

    @Query("SELECT * FROM photo")
    fun getAllPhotos() : LiveData<List<Photo>>

    @Query("SELECT * FROM path WHERE id = :pathId")
    suspend fun getPathData(pathId: Long): PathData

    @Transaction
    @Query("SELECT * FROM path WHERE id = :pathId")
    fun getPathWithPhotos(pathId: Long) : LiveData<PathDataWithPhotos>

    @Transaction
    @Query("SELECT * FROM path")
    fun getAllPathsWithPhotos() : LiveData<List<PathDataWithPhotos>>

    @Transaction
    @Query("SELECT * FROM path")
    fun getFullPaths():LiveData<List<FullPath>>

    @Transaction
    @Query("SELECT * FROM path WHERE path.id = :id")
    fun getFullPath(id:Long):LiveData<FullPath>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPath(path: PathData): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMeasurement(measurement : Measurement): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhoto(photo : Photo): Long


}