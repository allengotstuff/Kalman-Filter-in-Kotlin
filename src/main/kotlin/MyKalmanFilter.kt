package org.example

import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.linear.RealVector

class MyKalmanFilter {

    /**
     * Prediction Step, at step N, predict N + 1
     *
     * @param a is the state transition matrix, representing how each state variable is affecting the next cycle of the state.
     * @param x is the vector representation of system states
     * @param b is the input transition matrix, representing how controllable/measurable inputs of the system is affecting the next cycle of the state
     * @param u is a control variable or input variable - a measurable (deterministic) input to the system
     * @param p is covariance matrix of the current state, representing uncertainty of the state
     * @param q is the process noise matrix, representing uncertainties of the true/hidden state in the real world
     */
    fun predict(
        a: RealMatrix,
        x: RealVector,
        b: RealMatrix,
        u: RealVector,
        p: RealMatrix,
        q: RealMatrix,
    ): Pair<RealVector, RealMatrix> {
        /**
         * Formula 1: State Extrapolation Equation
         * Give Current state estimate ^X(n), predict the next state ^X(n + 1)
         * ^X(n+1, n)  = F^X(n,n) +GU(n,n)
         */
        val newX = a.operate(x).add(b.operate(u))

        /**
         * Formula 2: Covariance Extrapolation Equation
         * Given the current Covariance P(n), predict the next covariance P(n + 1)
         * P(n+1,n) =FP(n,n)F + Q
         */
        val newP = a.multiply(p).multiply(a.transpose()).add(q)
        return Pair(newX, newP)
    }

    /**
     * given prediction at step N -1 , Update State N.
     *
     * @param x is the vector representation of system states
     * @param y is the measure input vector, representing the measurement of the input
     * @param p is covariance matrix of the current state, representing uncertainty of the state
     * @param h is the observation matrix, mapping measurement input to states, and visa versa.
     * @param r is the measurement covariance, representing the uncertainties of the measurement variable
     */
    fun update(
        x: RealVector,
        y: RealVector,
        p: RealMatrix,
        h: RealMatrix,
        r: RealMatrix
    ): KalmanUpdateResult {
        /**
         * Part 1: The Mean of predictive distribution of Y within state: mapping X^(n-1) [previously predicted state] into shapes of measure input Y.
         *
         * H(n) * X^(n, n-1)
         *
         */
        val IM = h.operate(x)

        /**
         * Part 2:  The dominator of kalman gain: [previous predicted covariance of state] + [current measurement covariance]
         *
         * H(n) * P^(n,n-1) * H(n)(transpose) + R, or simplify version P + R
         *
         */
        val IS = h.multiply(p).multiply(h.transpose()).add(r)

        /**
         * Part 3: The kalman gain
         *
         * P(n) * H(transpose) / H(n) * P^(n,n-1) * H(n)(transpose)
         *
         */
        val K = p.multiply(h.transpose()).multiply(MatrixUtils.inverse(IS))

        /**
         * Part 4: The updated state vector.
         *
         * X^(n) = X^(n-1) + K(Y - H * X^(n-1))
         *
         */
        val updatedX = x.add(K.operate(y.subtract(IM)))

        /**
         * Part 5: Updated covariance matrix
         *
         * P - K*H*P
         */
        val updatedP = p.subtract(K.multiply(h).multiply(p))

        return KalmanUpdateResult(
            x = updatedX,
            p = updatedP,
            k = K
        )
    }

    // Data class for holding the result of kfUpdate
    data class KalmanUpdateResult(
        val x: RealVector,
        val p: RealMatrix,
        val k: RealMatrix
    )
}