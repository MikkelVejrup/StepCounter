# Mysteps
------------------------------------
## Stepcounter app by Daniel and Mikkel
------------------------------------

### Introduction
This is the "MySteps" app, which as the word describes is an app for counting steps and display it in an orderly fashion.
The app will show the currently walked steps for the current day. When the day shifts the previous steps counted, will be saved in a Database and shown in the small history icons in the middel of the screen.

### Make it Work
For the application to work correctly, the app must be downloaded on a physical device and then allow the app to use physical activity sensors. This allows for it to use the step sensor correctly.

![image](https://user-images.githubusercontent.com/79592248/169834842-cc1e2d61-cdbd-470f-a210-0466bf09c50c.png)


---

The application can calculate how many steps one may take each day from writing age and activity level.
It saves how many steps the user have taken the past six days using MySQL light (Room).
The application uses android services to track how many steps are taken when the application is closed. 

The green dot on the pictures highlights the current day active for counting.
---
![image](https://user-images.githubusercontent.com/79631275/169879194-7837b85c-56c7-4631-a62b-ee6bec73dc32.png) ![image](https://user-images.githubusercontent.com/79631275/169879346-a6d16548-819a-4dfe-9009-260844e0036e.png)

