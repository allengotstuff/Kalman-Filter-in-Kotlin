package org.kalmanfilter.core.wrapper

import org.junit.jupiter.api.Test
import org.apache.commons.math3.linear.RealVector
import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.MatrixUtils
import org.junit.jupiter.api.BeforeEach
import org.kalmanfilter.core.KalmanFilterCore
import org.kalmanfilter.core.wrapper.SimpleKalmanFilterProcess.DebugStats
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
    fun `given update once, validate params set correctly with expected value update`() {

        // Given
        val newMeasureVector = ArrayRealVector(doubleArrayOf(13.0, 14.0, 15.0))
        val newMeasureCovariance = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(71.0)
        val newMeasureMatrix = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(81.0)

        val newState = ArrayRealVector(doubleArrayOf(16.0, 17.0, 18.0))
        val newCovariance = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(91.0)
        val newK = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(101.0)

        `when`(
            kfMock.update(
                x = initialState,
                y = newMeasureVector,
                p = initialCovariance,
                h = newMeasureMatrix,
                r = newMeasureCovariance
            )
        ).thenAnswer {
            KalmanFilterCore.KalmanUpdateResult(
                x = newState,
                p = newCovariance,
                k = newK
            )
        }

        // When
        simpleKF.update(
            y = newMeasureVector,
            r = newMeasureCovariance,
            measureMatrix = newMeasureMatrix
        )

        // Then
        assertEquals(getState(simpleKF), newState)
        assertEquals(getCovariance(simpleKF), newCovariance)
    }

    @Test
    fun `given update multiple, debugResult should have expected dataset`() {

        // Given first invocation
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
                x = ArrayRealVector(doubleArrayOf(19.0, 20.0, 21.0)),
                p = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(111.0),
                k = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(121.0)
            )
        }

        simpleKF.update(
            y = ArrayRealVector(doubleArrayOf(22.0, 23.0, 24.0)),
            r = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(131.0),
            measureMatrix = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(141.0)
        )

        // Given second invocation
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
                x = ArrayRealVector(doubleArrayOf(25.0, 26.0, 27.0)),
                p = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(151.0),
                k = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(161.0)
            )
        }

        simpleKF.update(
            y = ArrayRealVector(doubleArrayOf(28.0, 29.0, 30.0)),
            r = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(171.0),
            measureMatrix = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(181.0)
        )

        // Given third invocation
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
                x = ArrayRealVector(doubleArrayOf(31.0, 32.0, 33.0)),
                p = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(191.0),
                k = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(201.0)
            )
        }

        simpleKF.update(
            y = ArrayRealVector(doubleArrayOf(34.0, 35.0, 36.0)),
            r = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(211.0),
            measureMatrix = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(221.0)
        )

        // Then
        assertEquals(
            simpleKF.debug(),
            listOf(
                DebugStats(
                    x = ArrayRealVector(doubleArrayOf(19.0, 20.0, 21.0)),
                    p = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(111.0),
                    k = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(121.0),
                    y = ArrayRealVector(doubleArrayOf(22.0, 23.0, 24.0))
                ),
                DebugStats(
                    x = ArrayRealVector(doubleArrayOf(25.0, 26.0, 27.0)),
                    p = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(151.0),
                    k = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(161.0),
                    y = ArrayRealVector(doubleArrayOf(28.0, 29.0, 30.0))
                ),
                DebugStats(
                    x = ArrayRealVector(doubleArrayOf(31.0, 32.0, 33.0)),
                    p = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(191.0),
                    k = MatrixUtils.createRealIdentityMatrix(3).scalarMultiply(201.0),
                    y = ArrayRealVector(doubleArrayOf(34.0, 35.0, 36.0))
                )
            )
        )

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