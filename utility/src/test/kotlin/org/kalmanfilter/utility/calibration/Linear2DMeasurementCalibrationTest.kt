package org.kalmanfilter.utility.calibration

import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test
import kotlin.math.pow

class Linear2DMeasurementCalibrationTest {

    private lateinit var calibration: MeasurementCalibration

    @Test
    fun test_iterate_30_validate_size() {
        calibration = Linear2DMeasurementCalibration()
        calibration.iterate(30)

        assertEquals(calibration.hiddenStates.size, 30)
        assertEquals(calibration.measuredStates.size, 30)
        assertEquals(calibration.measurementCovariances.size, 30)

        repeat(30) { index ->

            val hiddenState = calibration.hiddenStates[index].maxValue

            val measuredState = calibration.measuredStates[index].maxValue

            val measurementCovariance = calibration.measurementCovariances[index].getEntry(0, 0)

            // convariance == noise.pow(2)
            assertEquals((hiddenState - measuredState).pow(2), measurementCovariance)
        }
    }

    @Test
    fun test_iterate_15_validate_variances_created() {
        calibration = Linear2DMeasurementCalibration()
        calibration.iterate(15)

        repeat(15) { index ->

            val hiddenState = calibration.hiddenStates[index].maxValue

            val measuredState = calibration.measuredStates[index].maxValue

            val measurementCovariance = calibration.measurementCovariances[index].getEntry(0, 0)

            // convariance == noise.pow(2)
            assertEquals((hiddenState - measuredState).pow(2), measurementCovariance)
        }
    }


    @Test
    fun test_iterate_55_validate_linear_formula() {
        calibration = Linear2DMeasurementCalibration()
        calibration.iterate(55)

        repeat(55) { index ->


            if (index != 0) {
                val current = calibration.hiddenStates[index].maxValue
                val past = calibration.hiddenStates[index - 1].maxValue

                assertEquals(current, past + 140.0)
            }
        }
    }


}