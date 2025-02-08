# Kalman-Filter-in-Kotlin

A **Kalman Filter** implementation in **Kotlin**, designed with built-in support for **debugging, calibration, visualization**, and additional tools to enhance usability.

## 🚧 Under Active Development 🚧

This repository is actively under development, and its API, architecture, or functionality may change frequently. **Backward compatibility is not guaranteed**, and breaking changes may occur without prior notice.

If you're using this project, following closely with development updates. **Contributions and feedback are welcome!**

## 📌 What is Kalman Filter?

A **Kalman Filter** is an algorithm that provides an **optimal estimate of multidimensional variables in dynamic system** by iteratively refining predictions based on noisy sensor data. It is widely used in **robotics, tracking systems, navigation, and signal processing** due to its efficiency in filtering and predicting system states.

Specific use case of smartphone, including:
* GPS Positioning & Navigation
Problem: GPS signals are often noisy due to atmospheric interference, signal reflection, or loss in urban environments.
* Motion Tracking & Inertial Navigation (IMU Sensors)
  Problem: Smartphone accelerometers, gyroscopes, and magnetometers provide raw, noisy sensor readings.
* Camera Image Stabilization (OIS & EIS)
  Problem: Shaky hands cause unstable video recording and blurry photos.
* Touchscreen Smoothing & Gesture Recognition
  Problem: Raw touchscreen input contains noise, leading to shaky strokes in handwriting apps or inconsistent gestures.
* AR & VR of sensor reading to predict state of system. 

Mobile Application:
Apps like Ski Tracks, Strava, and Runkeeper use Kalman filtering to smooth motion tracking and estimate accurate speed and position.

### Why This Repository?

1. **Streamlined Calibration** – Tuning a Kalman Filter requires extensive trial and error. This repository aims to provide **interactive and extendable tools** for calibrating systems modeling.
2. **Native Kotlin Support** – Unlike other implementations, this repository is built natively in **Kotlin**, offering seamless integration with the Kotlin ecosystem.
3. **Enhanced Modeling Flexibility** – Traditional Kalman Filter implementations often assume fixed parameters per iteration. This implementation supports **dynamic covariance updates**, enabling more adaptable system modeling.

---

## 📂 Project Structure

### **Package Overview**
- **`org.kalman.core`** – The fundamental Kalman Filter implementation, designed with **detailed documentation** explaining its inner workings. This package offers **maximum flexibility** for system modeling and can be extended as needed.
- **`org.kalman.wrapper`** – Higher-level abstraction that wraps around `org.kalman.core` for common use cases on application layer.
- **`org.kalman.calibration`** - Extendable and reusable artificial datasets of system, in the purpose of streamline custom model calibration.


### Matrices and Vectors

The foundation of the Kalman Filter relies on **matrix and vector operations** for **multidimensional system modeling**. This implementation leverages **third-party libraries** for efficient computation.

Since **Kotlin lacks built-in matrix and vector operations**, this project uses:  
[Apache Commons Math](https://github.com/apache/commons-math) – A **stable** and well-supported mathematical library.


### Kotlin Notebook (`src/notebook/`)
This folder contains **calibrated examples of Kalman Filter for custom systems**. It serves as both:
- The **practical tutorial** to learn the Kalman Filter with visualization.
- The **debugging and interactive tool**, allowing developers to refine system parameters for different application use cases.

**Powered by Kotlin Notebook** – [Learn More](https://www.jetbrains.com/help/idea/kotlin-notebook.html).  
This is one of the core features of the repository, providing **interactive debugging, visualization, and rapid iteration**.

#### Best Practices: 
* Each Notebook file should focus on a single, unique system to serve as a clear example for developers, making it easier to find the relevant use case.
* Notebook files should follow clean code practices and include clear documentation.
* Custom classes and components used in a Notebook should be unit tested, or have unit tests added in `src/test`.
* Notebook filenames should follow the convention: `{system_modeling}_calibration.ipynb` (e.g., `linear_2d_calibration.ipynb`).
---

## 📢 Contributions & Feedback

This project is open to contributions! Whether you want to improve documentation, optimize performance, or expand features, feel free to open an issue or submit a pull request.

🚀 Happy coding!
