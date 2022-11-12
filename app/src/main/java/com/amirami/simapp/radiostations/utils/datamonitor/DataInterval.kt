package com.amirami.simapp.radiostations.utils.datamonitor

import java.util.*

object DataInterval {

    val today: DataTimeInterval
        get() {
            val calendar = Calendar.getInstance()

            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)
            val start = calendar.timeInMillis

            calendar.add(Calendar.DATE, 1)
            val end = calendar.timeInMillis

            return DataTimeInterval(start, end)
        }

    val yesterday: DataTimeInterval
        get() {
            val calendar = Calendar.getInstance()

            calendar.add(Calendar.DATE, -1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)
            val start = calendar.timeInMillis

            calendar.add(Calendar.DATE, 1)
            val end = calendar.timeInMillis

            return DataTimeInterval(start, end)
        }

    val last7days: DataTimeInterval
        get() {
            val calendar = Calendar.getInstance()

            val end = calendar.timeInMillis

            calendar.add(Calendar.DATE, -7)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)
            val start = calendar.timeInMillis

            return DataTimeInterval(start, end)
        }

    val last30days: DataTimeInterval
        get() {
            val calendar = Calendar.getInstance()

            calendar.set(Calendar.AM_PM, Calendar.AM)
            val end = calendar.timeInMillis

            calendar.add(Calendar.DATE, -30)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)
            val start = calendar.timeInMillis

            return DataTimeInterval(start, end)
        }

    val week: DataTimeInterval
        get() {
            val calendar = Calendar.getInstance()

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)
            val start = calendar.timeInMillis

            calendar.add(Calendar.DATE, 6)
            val end = calendar.timeInMillis

            return DataTimeInterval(start, end)
        }

    val month: DataTimeInterval
        get() {
            val calendar = Calendar.getInstance()

            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)
            val start = calendar.timeInMillis

            calendar.add(Calendar.MONTH, 1)
            val end = calendar.timeInMillis

            return DataTimeInterval(start, end)
        }

    fun monthlyPlan(startDay: Int): DataTimeInterval {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_MONTH, startDay)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.clear(Calendar.MINUTE)
        calendar.clear(Calendar.SECOND)
        calendar.clear(Calendar.MILLISECOND)
        val start = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        val end = calendar.timeInMillis

        return DataTimeInterval(start, end)
    }

    fun weeklyPlan(startDay: Int): DataTimeInterval {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_WEEK, startDay)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.clear(Calendar.MINUTE)
        calendar.clear(Calendar.SECOND)
        calendar.clear(Calendar.MILLISECOND)
        val start = calendar.timeInMillis

        calendar.add(Calendar.DATE, 6)
        val end = calendar.timeInMillis

        return DataTimeInterval(start, end)
    }
}
