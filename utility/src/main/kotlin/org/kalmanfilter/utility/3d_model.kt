package org.kalmanfilter.utility

import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.linear.RealVector
import org.apache.commons.math3.linear.ArrayRealVector

fun main() {
    // Define a 3x3 state transition matrix (A)
    val stateTransition: RealMatrix = MatrixUtils.createRealMatrix(arrayOf(
        doubleArrayOf(1.0, 0.0, 0.0),
        doubleArrayOf(0.0, 1.0, 0.0),
        doubleArrayOf(0.0, 0.0, 1.0)
    ))

    // Define a 3x3 control input matrix (B); here, we set it to a zero matrix (unused)
    val control: RealMatrix = MatrixUtils.createRealMatrix(arrayOf(
        doubleArrayOf(0.0, 0.0, 0.0),
        doubleArrayOf(0.0, 0.0, 0.0),
        doubleArrayOf(0.0, 0.0, 0.0)
    ))

    // Define a 3x3 process noise covariance matrix (Q)
    val processNoise: RealMatrix = MatrixUtils.createRealIdentityMatrix(3)

    // Initial state estimate (a 3-dimensional vector)
    val initialStateEstimate: RealVector = ArrayRealVector(doubleArrayOf(0.0, 0.0, 0.0))

    // Define a 3x3 initial error covariance matrix (P0)
    val initialErrorCovariance: RealMatrix = MatrixUtils.createRealMatrix(arrayOf(
        doubleArrayOf(1.0, 0.0, 0.0),
        doubleArrayOf(0.0, 1.0, 0.0),
        doubleArrayOf(0.0, 0.0, 1.0)
    ))

    // Define a 3x3 measurement matrix (H)
    val measMatrix: RealMatrix = MatrixUtils.createRealIdentityMatrix(3)

    // Define a 3x3 measurement noise covariance matrix (R)
    val measNoise: RealMatrix = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(0.1)

    // Create a list of measurement vectors (each is a 3-dimensional vector)
    val measurementVectors = listOf(
        ArrayRealVector(doubleArrayOf(1.0, 0.5, 0.2)),
        ArrayRealVector(doubleArrayOf(1.1, 1.5, 1.2)),
        ArrayRealVector(doubleArrayOf(4.5, 6.0, 7.6)),
        ArrayRealVector(doubleArrayOf(4.5, 6.0, 7.6)),
    )

    // Create a list of measurement covariance matrices.
    // Note: All matrices must be identical. Here we use the same 'measNoise' matrix.
    val measurementCovariance = listOf(measNoise.copy(),measNoise.copy(),measNoise.copy(),measNoise.copy())

    // Instantiate the dataset with the process and measurement parameters
    val dataset = GeneratedApacheDataSet(
        stateTransition = stateTransition,
        control = control,
        processNoise = processNoise,
        initialStateEstimate = initialStateEstimate,
        initialErrorCovariance = initialErrorCovariance,
        measMatrix = measMatrix,
        measNoise = measNoise,
        measurementVectors = measurementVectors,
        measurementCovariance = measurementCovariance
    )

    // Run the filter: for each measurement, predict and correct steps are performed.
    dataset.generate()

    // Print out the state estimations and error covariances after each update.
    println("State Estimations:")
    println(dataset.stateEstimations)


    println("\nError Covariances:")
    println(dataset.errorCovariances)
}
