# Kalman-Filter-in-Kotlin
Implementation of a Kalman Filter in Kotlin, with added support for debugging, calibration, and various accessible tools along the way.

# What is Kalman Filter 
Add TLDR description 


# Why this Repo 
1. Calibration of kalman filter is difficult, it requires a lot of trail and error to get parameters right, so this repo aim to create simple yet extensive tool to help calibrate existing tool, but also new dimensional systems.   
2. Kalman filter implementation on kotlin, offer native Kotlin sytax support 
3. Offer more flexibility of system modeling than existing open source implementation, for example, each iteration of measument might have different covariance, so offer support for it   

# Usage 
KalManFilterCore.kt is the rudemental implementation, with added documentation explaining how things work. You can get the most flexiblity of system modeling by directly using this implementation, or build wrapping around it  
# Calibration Manual
Calibration would be one of the most valuable contributions of this repo, We are leveraging fully native kotlin support, Kotlin Notebook to increase the development feedback loop and visualization for debugging. 

