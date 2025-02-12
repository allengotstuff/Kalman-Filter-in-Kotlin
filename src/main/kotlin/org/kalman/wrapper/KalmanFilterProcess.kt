package org.kalman.wrapper

import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.linear.RealVector
import org.kalman.core.KalmanFilterCore

class KalmanFilterProcess(
    private val debugEnabled: Boolean = false,
    private var states: RealVector, // X
    private var covarianceMatrix: RealMatrix, // P
    private val stateTransitionMatrix: RealMatrix, // A
    private val processMatrix: RealMatrix, // Q
) {
    private val kf: KalmanFilterCore = KalmanFilterCore()
    private val debugResult = mutableListOf<DebugStats>()

    /**
     * Assume that there are not controllable variable during input
     */
    fun predict() {
        kf.predict(
            a = stateTransitionMatrix,
            x = states,
            b = Array2DRowRealMatrix(
                arrayOf(
                    doubleArrayOf(0.0) // assuming the input has not effect on states
                )
            ),
            u = ArrayRealVector(
                doubleArrayOf(
                    0.0
                )
            ),
            p = covarianceMatrix,
            q = processMatrix
        ).let { result ->
            states = result.first
            covarianceMatrix = result.second
        }
    }

    fun update(
        y: RealVector, // measurement vector
        r: RealMatrix  // measurement covariance,
    ) {
        kf.update(
            x = states, // previous state
            y = y,
            p = covarianceMatrix, // previous convariance
            h = Array2DRowRealMatrix(
                arrayOf(
                    doubleArrayOf(1.0) // assuming the state to measure is 1: 1
                )
            ),
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