package org.kalmanfilter.core.wrapper

import org.junit.jupiter.api.Test
import org.apache.commons.math3.linear.RealVector
import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.MatrixUtils
import org.junit.jupiter.api.BeforeEach
import org.kalmanfilter.core.KalmanFilterCore
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import kotlin.test.assertEquals


class SimpleKalmanFilterProcessTest {

    private lateinit var initialState: RealVector
    private lateinit var initialCovariance: RealMatrix
    private lateinit var stateTransitionMatrix: RealMatrix
    private lateinit var processMatrix: RealMatrix
    private lateinit var kfMock: KalmanFilterCore
    private lateinit var controlVector: RealVector
    private lateinit var controlMatrix: RealMatrix
    private lateinit var simpleKF: SimpleKalmanFilterProcess

    @BeforeEach
    fun setUp() {
        initialState = ArrayRealVector(doubleArrayOf(1.0, 2.0, 3.0))
        initialCovariance = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(11.0)
        stateTransitionMatrix = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(21.0)
        processMatrix = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(31.0)

        controlVector = ArrayRealVector(doubleArrayOf(4.0, 5.0, 6.0))
        controlMatrix = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(41.0)

        kfMock = mock(KalmanFilterCore::class.java)

        `when`(
            kfMock.update(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        ).thenAnswer {
            KalmanFilterCore.KalmanUpdateResult(
                x = ArrayRealVector(2),
                p = MatrixUtils.createRealIdentityMatrix(2),
                k = MatrixUtils.createRealIdentityMatrix(2)
            )
        }

        //Given
        simpleKF = SimpleKalmanFilterProcess(
            debugEnabled = true,
            states = initialState,
            covarianceMatrix = initialCovariance,
            stateTransitionMatrix = stateTransitionMatrix,
            processMatrix = processMatrix,
            kf = kfMock
        )
    }

    @Test
    fun `given predict once, validate params set correctly with expected value update`() {
        
        val newState = ArrayRealVector(doubleArrayOf(7.0, 8.0, 9.0))
        val newCovariance = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(51.0)

        `when`(
            kfMock.predict(
                a = stateTransitionMatrix,
                x = initialState,
                b = controlMatrix,
                u = controlVector,
                p = initialCovariance,
                q = processMatrix,
            )
        ).thenAnswer {
            Pair(newState, newCovariance)
        }

        simpleKF.predict(
            controlMatrix = controlMatrix,
            controlVector = controlVector,
        )

        // Then
        assertEquals(getState(simpleKF), newState)
        assertEquals(getCovariance(simpleKF), newCovariance)
    }

    @Test
    fun `given predict multiple times, validate params set correctly with expected value update`() {

        val newState = ArrayRealVector(doubleArrayOf(7.0, 8.0, 9.0))
        val newCovariance = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(51.0)

        `when`(
            kfMock.predict(
               any(),
               any(),
               any(),
               any(),
               any(),
               any()
            )
        ).thenAnswer {
            Pair(newState, newCovariance)
        }

        simpleKF.predict(
            controlMatrix = controlMatrix,
            controlVector = controlVector,
        )

        // Then
        assertEquals(getState(simpleKF), newState)
        assertEquals(getCovariance(simpleKF), newCovariance)

        // Given when predict steps

        val newState2 = ArrayRealVector(doubleArrayOf(10.0, 11.0, 12.0))
        val newCovariance2 = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(61.0)

        `when`(
            kfMock.predict(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        ).thenAnswer {
            Pair(newState2, newCovariance2)
        }

        // When
        simpleKF.predict(
            controlMatrix = controlMatrix,
            controlVector = controlVector,
        )

        // Then
        assertEquals(getState(simpleKF), newState2)
        assertEquals(getCovariance(simpleKF), newCovariance2)
    }

    @Test
    fun `given update, validate params set correctly with expected value update`() {

    }

    // Helper methods to get private fields via reflection.
    private fun getState(process: SimpleKalmanFilterProcess): RealVector {
        val field = SimpleKalmanFilterProcess::class.java.getDeclaredField("states")
        field.isAccessible = true
        return field.get(process) as RealVector
    }

    private fun getCovariance(process: SimpleKalmanFilterProcess): RealMatrix {
        val field = SimpleKalmanFilterProcess::class.java.getDeclaredField("covarianceMatrix")
        field.isAccessible = true
        return field.get(process) as RealMatrix
    }
}