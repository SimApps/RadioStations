package com.amirami.simapp.radiostations.utils.datamonitor

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.net.NetworkCapabilities
import android.net.TrafficStats
import android.os.Build

class DataUsageManager(
    private val statsManager: NetworkStatsManager,
    private val subscriberId: String
) {

    /* fun getHead(networkType: DataNetworkType,text:TextView): Observable<DataUsage>  =


         Observable.interval(1, TimeUnit.SECONDS)

              .flatMap {  ObservableSource<DataUsage> {  }  }
             .subscribeOn(Schedulers.io())
             .unsubscribeOn(Schedulers.computation())
             .observeOn(AndroidSchedulers.mainThread())
             .also {

                     when (networkType) {
                         DataNetworkType.MOBILE -> {
                             text.text=    DataUsage(TrafficStats.getMobileRxBytes(), TrafficStats.getMobileTxBytes()).toString()
                         }
                         DataNetworkType.WIFI -> {
                             text.text= RadioFunction.bytesIntoHumanReadable(
                                 DataUsage(TrafficStats.getTotalRxBytes() - TrafficStats.getMobileRxBytes(), TrafficStats.getTotalTxBytes() - TrafficStats.getMobileTxBytes()
                                 ).downloads + DataUsage(TrafficStats.getTotalRxBytes() - TrafficStats.getMobileRxBytes(), TrafficStats.getTotalTxBytes() - TrafficStats.getMobileTxBytes()
                                 ).uploads
                             )


                             //  DynamicToast.makeSuccess(context, "Stream URL Copied", 3).show()
                             //  it.onComplete()
                         }
                     }

             }
 */
// to remove when move to mvvm
    //   implementation "io.reactivex.rxjava2:rxkotlin:2.2.0"
    //   implementation "io.reactivex.rxjava2:rxandroid:2.1.1"
  /*  fun getRealtimeUsage(networkType: DataNetworkType): Observable<DataUsage> {

        return Observable.create {
            when (networkType) {
                DataNetworkType.MOBILE -> {
                    it.onNext(DataUsage(TrafficStats.getMobileRxBytes(), TrafficStats.getMobileTxBytes()))
                }
                DataNetworkType.WIFI -> {
                  it.onNext(DataUsage(TrafficStats.getTotalRxBytes() - TrafficStats.getMobileRxBytes(), TrafficStats.getTotalTxBytes() - TrafficStats.getMobileTxBytes()))


                  //  DynamicToast.makeSuccess(context, "Stream URL Copied", 3).show()
                  //  it.onComplete()
                }
            }
        }
    }
*/
    fun getSpecifiqueUsage(interval: DataTimeInterval, networkType: DataNetworkType, uid: Int): DataUsage {
        val stats = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            statsManager.queryDetails(
                when (networkType) {
                    DataNetworkType.MOBILE -> NetworkCapabilities.TRANSPORT_CELLULAR
                    DataNetworkType.WIFI -> NetworkCapabilities.TRANSPORT_WIFI
                },
                subscriberId,
                interval.start,
                interval.end
            )
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        val bucket = NetworkStats.Bucket()
        val usage = DataUsage()

        while (stats.hasNextBucket()) {
            stats.getNextBucket(bucket)

            usage.downloads += bucket.rxBytes
            usage.uploads += bucket.txBytes
        }

        stats.close()

        // ExoPlayer.Observer.subscribe("Data text view", text)

        return usage
    }

    fun getUsage(uid: Int): DataUsage {
        // ExoPlayer.Observer.subscribe("Data text view", text)
        return DataUsage(TrafficStats.getUidRxBytes(uid), TrafficStats.getUidTxBytes(uid))
    }
    fun getMultiUsage(intervals: List<DataTimeInterval>, networkType: DataNetworkType): List<DataUsage> {
        var start = intervals[0].start
        var end = intervals[0].end

        val usages = mutableMapOf<DataTimeInterval, DataUsage>()

        for (interval in intervals) {
            if (interval.start < start) {
                start = interval.start
            }

            if (interval.end > end) {
                end = interval.end
            }

            usages[interval] = DataUsage()
        }

        val stats = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            statsManager.queryDetails(
                when (networkType) {
                    DataNetworkType.MOBILE -> NetworkCapabilities.TRANSPORT_CELLULAR
                    DataNetworkType.WIFI -> NetworkCapabilities.TRANSPORT_WIFI
                },
                subscriberId,
                start,
                end
            )
        } else {
            TODO("VERSION.SDK_INT < M")
        }

        val bucket = NetworkStats.Bucket()

        while (stats.hasNextBucket()) {
            stats.getNextBucket(bucket)

            for (interval in intervals) {
                if (checkBucketInterval(bucket, interval.start, interval.end)) {
                    usages[interval]!!.downloads += bucket.rxBytes
                    usages[interval]!!.uploads += bucket.txBytes
                }
            }
        }

        stats.close()
        return usages.values.toList()
    }

    private fun checkBucketInterval(bucket: NetworkStats.Bucket, start: Long, end: Long): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((bucket.startTimeStamp in start..end) || (bucket.endTimeStamp in (start + 1) until end))
        } else {
            TODO("VERSION.SDK_INT < M")
        }
    }
}
