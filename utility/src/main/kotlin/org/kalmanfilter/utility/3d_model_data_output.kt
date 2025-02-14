package org.kalmanfilter.utility

import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.linear.RealVector
import org.apache.commons.math3.linear.ArrayRealVector
import java.io.File

/**
 * This file is generate output of estimate and covariance to file, and cross-reference to own implementation.
 *
 * TODO(convert output to json format, so this result validation can be applied)
 */
fun main() {
    // State
    val stateTransition: RealMatrix = MatrixUtils.createRealIdentityMatrix(3)
    val initialStateEstimate: RealVector = ArrayRealVector(doubleArrayOf(1.0, 2.0, 3.0))
    val initialErrorCovariance: RealMatrix = MatrixUtils.createRealIdentityMatrix(3)

    // Process
    val processNoise: RealMatrix = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(5.0)

    // Measurement
    val measureMatrix: RealMatrix = MatrixUtils.createRealIdentityMatrix(3)
    val measureCovariance: RealMatrix = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(5.0)
    val measurementVectors = listOf(
        ArrayRealVector(doubleArrayOf(1.0, 0.5, 0.2)),
        ArrayRealVector(doubleArrayOf(1.1, 1.5, 1.2)),
        ArrayRealVector(doubleArrayOf(4.5, 6.0, 7.6)),
        ArrayRealVector(doubleArrayOf(4.5, 6.0, 7.6)),
    )

    // Control
    val control: RealMatrix = MatrixUtils.createRealIdentityMatrix(3)
    val controlVectors = listOf(
        ArrayRealVector(doubleArrayOf(1.0, 1.0, 2.0)),
        ArrayRealVector(doubleArrayOf(1.0, 0.0, 4.0)),
        ArrayRealVector(doubleArrayOf(1.0, 1.0, 0.0)),
        ArrayRealVector(doubleArrayOf(1.0, 2.0, 1.0)),
    )

    // Instantiate the dataset with the process and measurement parameters
    val dataset = GeneratedApacheDataSet(
        stateTransition = stateTransition,
        control = control,
        processNoise = processNoise,
        initialStateEstimate = initialStateEstimate,
        initialErrorCovariance = initialErrorCovariance,
        measurementVectors = measurementVectors,
        measurementMatrix = measureMatrix,
        controlVectors = controlVectors,
        measurementCovariance = measureCovariance
    )

    // Run the filter: for each measurement, predict and correct steps are performed.
    dataset.generate()

    // Print out the state estimations and error covariances after each update.
    println("State Estimations:")
    println(dataset.stateEstimations)


    println("\nError Covariances:")
    println(dataset.errorCovariances)

    val output = File("./test-data/output.txt")
    output.writeText(
        """
            {
                   state: ${dataset.stateEstimations},
                   covariance: ${dataset.errorCovariances}
            }
        """.trimIndent()
    )
}
