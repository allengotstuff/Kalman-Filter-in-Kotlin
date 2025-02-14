package org.kalman.core

import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.linear.RealVector
import org.junit.jupiter.api.Test
import org.kalmanfilter.utility.GeneratedApacheDataSet
import kotlin.test.assertEquals

class KalmanFilterCoreTest{

   @Test
    fun `given_3d_test_data_from_apache, kalmanFilter Core expect same result`(){
        val kalmanFilterCore = KalmanFilterCore()

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
        dataset.generate()

       // Given same params, calculate kalman filter core
       val stateEstimations: MutableList<RealVector> = mutableListOf()
       val errorCovariances: MutableList<RealMatrix> = mutableListOf()
       var state = initialStateEstimate.copy()
       var covariance = initialErrorCovariance.copy()

       measurementVectors.zip(controlVectors).map { (measureVec, controlVec) ->

           kalmanFilterCore.predict(
               a = stateTransition,
               x = state,
               b = control,
               u = controlVec,
               p = covariance,
               q = processNoise
           ).let { (newState, newCovariance) ->
               state = newState
               covariance = newCovariance
           }

           kalmanFilterCore.update(
               x = state,
               y = measureVec,
               p = covariance,
               h = measureMatrix,
               r = measureCovariance
           ).let {
               state = it.x
               covariance = it.p
           }

           stateEstimations.add(state.copy())
           errorCovariances.add(covariance.copy())
       }

       // assert
       assert(stateEstimations.size == dataset.stateEstimations.size)
       assertEquals(stateEstimations.toString(), dataset.stateEstimations.toString())

       assert(errorCovariances.size == dataset.errorCovariances.size)
       assertEquals(errorCovariances.toString() +"  ", dataset.errorCovariances.toString())
    }

}