package org.kalmanfilter.utility.validation

import org.apache.commons.math3.filter.KalmanFilter
import org.apache.commons.math3.filter.DefaultProcessModel
import org.apache.commons.math3.filter.DefaultMeasurementModel
import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.linear.RealVector

class GeneratedApacheDataSet(
    //State
    private val stateTransition: RealMatrix,             // State transition matrix (A)
    private val initialStateEstimate: RealVector,        // Initial state estimate
    private val initialErrorCovariance: RealMatrix,      // Initial error covariance matrix (P0)
    //Process noise
    private val processNoise: RealMatrix,                // Process noise covariance matrix (Q)
    //Measurement
    private val measurementMatrix: RealMatrix,           // Measurement matrix (H)
    private val measurementCovariance: RealMatrix,       // Measurement noise covariance matrix (R)
    private val measurementVectors: List<RealVector>,    // List of measurement vectors for correct() steps
    //Control
    private val control: RealMatrix,                     // Control input matrix (B); pass null if unused
    private val controlVectors: List<RealVector>         // List of control vector for each prediction
) {

    // Lists to store the state estimations and error covariance matrices from each iteration.
    val stateEstimations: MutableList<RealVector> = mutableListOf()
    val errorCovariances: MutableList<RealMatrix> = mutableListOf()

    // Initialize the Kalman filter with the process and measurement models.
    private val kalmanFilter: KalmanFilter

    init {

        check(controlVectors.size == measurementVectors.size){"Control vectors must have same size with measure vectors"}

        val processModel = DefaultProcessModel(
            stateTransition,
            control,
            processNoise,
            initialStateEstimate,
            initialErrorCovariance
        )
        val measurementModel = DefaultMeasurementModel(measurementMatrix, measurementCovariance)
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
        measurementVectors.zip(controlVectors).forEach { (measurement, control) ->
            kalmanFilter.predict(control)
            kalmanFilter.correct(measurement)
            // Store copies of the current state estimation vector and error covariance matrix.
            stateEstimations.add(kalmanFilter.stateEstimationVector.copy())
            errorCovariances.add(kalmanFilter.errorCovarianceMatrix.copy())
        }
    }
}
