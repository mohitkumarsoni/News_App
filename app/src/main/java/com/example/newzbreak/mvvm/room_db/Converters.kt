package com.example.newzbreak.mvvm.room_db

import androidx.room.TypeConverter
import com.example.newzbreak.ui_layers.model.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }

}