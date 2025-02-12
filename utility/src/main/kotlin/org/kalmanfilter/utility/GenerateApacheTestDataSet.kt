package org.kalmanfilter.utility

import org.apache.commons.math3.filter.KalmanFilter
import org.apache.commons.math3.filter.DefaultProcessModel
import org.apache.commons.math3.filter.DefaultMeasurementModel
import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.linear.RealVector

class GeneratedApacheDataSet(
    // Inputs for the process and measurement models.
    private val stateTransition: RealMatrix,             // State transition matrix (A)
    private val control: RealMatrix,                     // Control input matrix (B); pass null if unused
    private val processNoise: RealMatrix,                // Process noise covariance matrix (Q)
    private val initialStateEstimate: RealVector,        // Initial state estimate
    private val initialErrorCovariance: RealMatrix,      // Initial error covariance matrix (P0)
    private val measMatrix: RealMatrix,                  // Measurement matrix (H)
    private val measNoise: RealMatrix,                   // Measurement noise covariance matrix (R)
    private val measurementVectors: List<RealVector>,    // List of measurement vectors for correct() steps
    private val measurementCovariance: List<RealMatrix>  // List of measurement error
) {

    // Lists to store the state estimations and error covariance matrices from each iteration.
    val stateEstimations: MutableList<RealVector> = mutableListOf()
    val errorCovariances: MutableList<RealMatrix> = mutableListOf()

    // Initialize the Kalman filter with the process and measurement models.
    private val kalmanFilter: KalmanFilter

    init {

        check(measurementCovariance.size == measurementVectors.size){ "Measurement vectors must have same size with covariance" }
        check(allMatricesIdentical(measurementCovariance)){"Apache kalman filter doesn't support measurementCovariance change per iteration: $measurementCovariance"}

        val processModel = DefaultProcessModel(
            stateTransition,
            control,
            processNoise,
            initialStateEstimate,
            initialErrorCovariance
        )
        val measurementModel = DefaultMeasurementModel(measMatrix, measNoise)
        kalmanFilter = KalmanFilter(processModel, measurementModel)
    }

    /**
     * Processes each measurement vector by running one prediction and correction cycle per measurement.
     *
     * The method iterates over the [measurementVectors] list, calling `predict()` and then `correct()`
     * for each measurement. After each cycle, it stores copies of the state estimation vector and
     * error covariance matrix.
     */
    fun generate() {
        measurementVectors.forEach { measurement ->
            kalmanFilter.predict()
            kalmanFilter.correct(measurement)
            // Store copies of the current state estimation vector and error covariance matrix.
            stateEstimations.add(kalmanFilter.stateEstimationVector)
            errorCovariances.add(kalmanFilter.errorCovarianceMatrix)
        }
    }

    private fun allMatricesIdentical(matrices: List<RealMatrix>): Boolean {
        if (matrices.isEmpty()) return true
        val reference = matrices.first()
        return matrices.all { it == reference }
    }
}
