package org.kalman.calibration.measurement

import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.linear.RealVector
import kotlin.random.Random
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.Array2DRowRealMatrix


class Linear2DMeasurementCalibration : MeasurementCalibration {

    private val START_VALUE = ArrayRealVector(
        doubleArrayOf(
            50.0
        )
    )

    private val NOISE_RANGE = 5000.0

    private val mHiddenStates = mutableListOf<RealVector>()
    private val mMeasuredStates = mutableListOf<RealVector>()
    private val mMeasurementCovariances = mutableListOf<RealMatrix>()


    override val hiddenStates: List<RealVector>
        get() = mHiddenStates
    override val measuredStates: List<RealVector>
        get() = mMeasuredStates
    override val measurementCovariances: List<RealMatrix>
        get() = mMeasurementCovariances

    override fun iterate(count: Int) {
        repeat(count) {

            // generate new true hidden state
            val createNewHiddenState = if (hiddenStates.isEmpty()) {
                START_VALUE
            } else {
                // y = x + 140
                hiddenStates.last().mapAdd(140.0)
            }

            // create noise/variance for each state
            val variance = Random.nextDouble(-NOISE_RANGE, NOISE_RANGE)

            //generate new measured state, representing real world noise.
            val createNewMeasuredState = createNewHiddenState.mapAdd(variance)

            mHiddenStates.add(createNewHiddenState)
            mMeasuredStates.add(createNewMeasuredState)
            mMeasurementCovariances.add(
                Array2DRowRealMatrix(
                    arrayOf(
                        doubleArrayOf(variance * variance) // assuming the state is still
                    )
                )
            )
        }
    }

}