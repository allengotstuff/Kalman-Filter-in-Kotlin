package org.kalman.wrapper

import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.linear.RealVector
import org.apache.commons.math3.linear.MatrixUtils
import org.kalman.core.KalmanFilterCore

/**
 * This is the simplest form of kalman filter process, where:
 * 1. current state has not effects on the next state
 * 2. No external control to change state between iteration cycle
 * 3. Process noise remain constant per iteration.
 * 4. Assuming measurement is fully mapped to states
 * 5. Measurement has different covariance between iteration cycle
 */
class SimpleKalmanFilterProcess(
    private val debugEnabled: Boolean = false,
    private var states: RealVector, // X
    private var covarianceMatrix: RealMatrix, // P
    private val stateTransitionMatrix: RealMatrix, // A
    private val processMatrix: RealMatrix, // Q
) {
    private val kf: KalmanFilterCore = KalmanFilterCore()
    private val debugResult = mutableListOf<DebugStats>()

    /**
     * Assume that there are not controllable variable during input, so make them 0 instead.
     */
    fun predict(
        controlMatrix: RealMatrix = MatrixUtils.createRealMatrix(states.dimension, states.dimension),
        controlVector: RealVector = ArrayRealVector(states.dimension)
    ) {
        kf.predict(
            a = stateTransitionMatrix,
            x = states,
            b = controlMatrix,
            u = controlVector,
            p = covarianceMatrix,
            q = processMatrix
        ).let { result ->
            states = result.first
            covarianceMatrix = result.second
        }
    }

    /**
     * Assume that measurement to state is identical map
     */
    fun update(
        y: RealVector, // measurement vector
        r: RealMatrix,  // measurement covariance
        measureMatrix: RealMatrix = MatrixUtils.createRealIdentityMatrix(y.dimension)
    ) {
        kf.update(
            x = states, // previous state
            y = y,
            p = covarianceMatrix, // previous covariance
            h = measureMatrix,
            r = r
        ).let { result ->
            states = result.x
            covarianceMatrix = result.p

            if (debugEnabled) {

                debugResult.add(
                    DebugStats(
                        x = states.copy(),
                        p = covarianceMatrix.copy(),
                        k = result.k.copy(),
                        y = y.copy()
                    )
                )
            }
        }
    }

    fun debug(): List<DebugStats> {
        return debugResult
    }

    data class DebugStats(
        val x: RealVector,
        val p: RealMatrix,
        val k: RealMatrix,
        val y: RealVector,
    )

}