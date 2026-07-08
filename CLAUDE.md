# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
./gradlew test                 # run all tests (this is what CI runs)
./gradlew :core:test           # run tests for one module
./gradlew :utility:test --tests "org.kalmanfilter.utility.KalmanFilterCoreTest"   # run a single test class
./gradlew build                # compile + test everything
```

Requires JDK 17 (resolved automatically via the foojay toolchain plugin).

## Architecture

Multi-module Gradle project (Kotlin 2.0.20). Matrix/vector math comes from Apache Commons Math 3 (`RealMatrix`/`RealVector`) throughout — Kotlin has no built-in linear algebra.

- **`:core`** — the Kalman Filter implementation, intended for distribution as a package. Keep it minimal and dependency-light.
  - `KalmanFilterCore` is stateless: `predict()` and `update()` are pure functions taking all matrices (A, B, Q, H, R, …) per call, which is what enables dynamic covariance updates between iterations.
  - `core/wrapper/SimpleKalmanFilterProcess` is the stateful convenience wrapper. It carries state X and covariance P across calls and defaults to no control input (zero B/u) and identity measurement mapping (H = I). With `debugEnabled` it records x/P/K/y after every update for inspection via `debug()`.
- **`:utility`** — test, debugging, and calibration support for `:core`. Depends on `:core`; never the reverse.
  - `calibration/` — `MeasurementCalibration` implementations generate synthetic datasets (true hidden states + noisy measurements + per-step measurement covariances) for tuning filters.
  - `validation/GeneratedApacheDataSet` wraps Apache Commons Math's own `KalmanFilter` to produce reference outputs from the same inputs.
- **Root project (`src/`)** — not a real Gradle module; holds `src/notebook/` with Kotlin Notebook examples (`linear_2d_calibration.ipynb`) used for interactive calibration and visualization in IntelliJ.

### Validation strategy

Correctness of `KalmanFilterCore` is established by cross-validation against Apache Commons Math's reference `KalmanFilter`: `utility/src/test/.../KalmanFilterCoreTest` runs both implementations over identical inputs and asserts identical state estimates and covariances. When changing the core equations, this test is the ground truth. It lives in `:utility` (not `:core`) because it depends on `:utility`'s `GeneratedApacheDataSet`, and `:core` depending on `:utility` would be circular.

## Conventions

- Notebook files: one system model per notebook, named `{system_modeling}_calibration.ipynb`; custom classes used by a notebook get unit tests in the owning module.
- `KalmanFilterCore` KDoc documents each equation of the filter — preserve that style when modifying it.
