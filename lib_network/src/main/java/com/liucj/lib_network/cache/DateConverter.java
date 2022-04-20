package com.liucj.lib_network.cache;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * room数据库时间转换工具
 */
public class DateConverter {
    @TypeConverter
    public static Long date2Long(Date date) {
        return date.getTime();
    }

    @TypeConverter
    public static Date long2Date(Long data) {
        return new Date(data);
    }
}
