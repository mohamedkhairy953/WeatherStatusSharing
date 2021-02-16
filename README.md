# WeatherStatusSharing

 A “Weather” application lets users take a photo, add current weather information (e.g. place name, temperature, weather condition, …)
 as a banner overlay on top of the photo, and finally,
 share it on Facebook or Twitter (No need to share text content since all information is in photo)

Challenge:
 the challenge in this task was how to add overlay above images but the key for the solution was if you can transform a Layout to an Image and Likely you can do that by
 converting it to bitmap.

Technical specs:
 1- used modularization to decouple unrelated feature
 2- write in MVVM pattern to separate layers within the same feature
 3- used Hilt Jetpack for DI.
 4- Coroutines for Async Programming.
 5- Room for Persistent storage.
 6- Navigation Ui controller for navigating among Fragments.
 7- Retrofit for network calls.
