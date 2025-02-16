package org.kalmanfilter.utility.validation

import org.junit.jupiter.api.Assertions.assertNotNull
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.linear.RealVector
import org.junit.jupiter.api.Test
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.spy
import org.apache.commons.math3.filter.KalmanFilter
import kotlin.test.assertEquals


class GeneratedApacheDataSetTest{

    @Test
    fun `given init, verify apache kalman filter sets correct parameters`() {
        // State
        val stateTransition: RealMatrix = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(10.0)
        val initialStateEstimate: RealVector = ArrayRealVector(doubleArrayOf(1.0, 2.0, 3.0))
        val initialErrorCovariance: RealMatrix = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(20.0)

        // Process
        val processNoise: RealMatrix = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(30.0)

        // Measurement
        val measureMatrix: RealMatrix = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(40.0)
        val measureCovariance: RealMatrix = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(50.0)
        val measurementVectors = listOf(
            ArrayRealVector(doubleArrayOf(1.0, 0.5, 0.2)),
            ArrayRealVector(doubleArrayOf(1.1, 1.5, 1.2)),
            ArrayRealVector(doubleArrayOf(4.5, 6.0, 7.6)),
            ArrayRealVector(doubleArrayOf(4.5, 6.0, 7.6)),
        )

        // Control
        val control: RealMatrix = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(60.0)
        val controlVectors = listOf(
            ArrayRealVector(doubleArrayOf(1.0, 1.0, 2.0)),
            ArrayRealVector(doubleArrayOf(1.0, 0.0, 4.0)),
            ArrayRealVector(doubleArrayOf(1.0, 1.0, 0.0)),
            ArrayRealVector(doubleArrayOf(1.0, 2.0, 1.0)),
        )

        val dataSet = GeneratedApacheDataSet(
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

        // Use reflection to access the private `kalmanFilter` field.
        val kalmanFilterField = GeneratedApacheDataSet::class.java.getDeclaredField("kalmanFilter")
        kalmanFilterField.isAccessible = true
        val originalKalmanFilter = kalmanFilterField.get(dataSet) as KalmanFilter
        assertNotNull(originalKalmanFilter, "KalmanFilter instance should not be null")

        // Create a spy for the KalmanFilter.
        val spyKalmanFilter = spy(originalKalmanFilter)
        // Replace the internal kalmanFilter with our spy.
        kalmanFilterField.set(dataSet, spyKalmanFilter)


        // assert
        assertEquals(spyKalmanFilter.stateDimension, initialStateEstimate.dimension)
        assertEquals(spyKalmanFilter.measurementDimension, measureMatrix.rowDimension)
        assertEquals(spyKalmanFilter.stateEstimationVector.toString(), initialStateEstimate.toString())
        assertEquals(spyKalmanFilter.errorCovarianceMatrix.toString(), initialErrorCovariance.toString())
    }

    @Test
    fun `given generate, verify predict is called before correct in each iteration with correct measurement and control params`() {
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

        val dataSet = GeneratedApacheDataSet(
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

        // Use reflection to access the private `kalmanFilter` field.
        val kalmanFilterField = GeneratedApacheDataSet::class.java.getDeclaredField("kalmanFilter")
        kalmanFilterField.isAccessible = true
        val originalKalmanFilter = kalmanFilterField.get(dataSet) as KalmanFilter
        assertNotNull(originalKalmanFilter, "KalmanFilter instance should not be null")

        // Create a spy for the KalmanFilter.
        val spyKalmanFilter = spy(originalKalmanFilter)

        // Replace the internal kalmanFilter with our spy.
        kalmanFilterField.set(dataSet, spyKalmanFilter)

        // Act: run the generate method.
        dataSet.generate()

        // Assert: Verify that in each iteration, predict is called before correct.
        val inOrderVerifier = inOrder(spyKalmanFilter)
        for (i in measurementVectors.indices) {
            // Verify predict was called with the corresponding control vector.
            inOrderVerifier.verify(spyKalmanFilter).predict(controlVectors[i])
            // Verify correct was called with the corresponding measurement vector.
            inOrderVerifier.verify(spyKalmanFilter).correct(measurementVectors[i])
        }
    }
}