package org.kalmanfilter.calibration.measurement

import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.linear.RealVector


interface MeasurementCalibration {

    val hiddenStates: List<RealVector>
    val measuredStates: List<RealVector>
    val measurementCovariances: List<RealMatrix>

    fun iterate(count: Int)
}