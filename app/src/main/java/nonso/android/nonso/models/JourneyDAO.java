package nonso.android.nonso.models;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import java.util.List;

@Dao
@TypeConverters(DateConverter.class)
public interface JourneyDAO {
    @Query("select * from JourneyModel")
    LiveData<List<JourneyModel>> getAllJourneyItems();

    @Query("select * from JourneyModel where journeyId = :id")
    JourneyModel getJourneyById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertJourney(JourneyModel journeyModel);

    @Delete
    void deleteJourney(JourneyModel journeyModel);
}
