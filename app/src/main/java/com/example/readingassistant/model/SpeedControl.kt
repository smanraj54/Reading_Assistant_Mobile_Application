package com.example.readingassistant.model
/**
 * This class is used to manage the speed of the media player
 * @param speeds array of possible speeds
 */
class SpeedControl(speeds:DoubleArray) {
    private var speeds:DoubleArray = speeds
    private var currentIndex:Int = speeds.size/2 - 1

    fun increaseSpeed() {
        if (currentIndex<speeds.size-1) {
            currentIndex++
        }
    }

    fun decreaseSpeed() {
        if (currentIndex>0) {
            currentIndex--
        }
    }

    fun getCurrentSpeed():Double {
        return speeds[currentIndex]
    }

    fun getLowerSpeed():Double {
        if (currentIndex>0) {
            return speeds[currentIndex-1]
        }
        return speeds[currentIndex]
    }

    fun getHigherSpeed():Double {
        if (currentIndex<speeds.size-1) {
            return speeds[currentIndex + 1]
        }
        return speeds[currentIndex]
    }

    fun getMaxSpeed():Double {
        return speeds.last()
    }

    fun getMinSpeed():Double {
        return speeds.first()
    }
}