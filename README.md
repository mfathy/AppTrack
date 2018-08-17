# AppTrack
A small android app that helps people not to get distracted on their phone.

# Instructions
1. Navigate to [AppTrack](https://github.com/mfathy/AppTrack)
2. Clone it locally using:
```git clone https://github.com/mfathy/AppTrack.git```
3. Open project using Android studio, then wait until project syncs and builds successfuly.
4. Run App using Android studio.

# Discussion
I have used MVP architecture to build the App structure with local data source to save blacklisted Apps.
The project has 3 main modules:
- app: is the main modules and has >> View and Presenter layers besides the background service.
- data: is the data layer which includes First: AsyncTaskLouder to load the Application list from Android, Second: LocalDatasource using Room database for saving black listed apps.
- multiutils: is a reusable utility modules.

I have not used any third party libraries for this project except the popular testing frameworks like 
* [Mockito](https://site.mockito.org/)
* [Hamcrest](http://hamcrest.org/JavaHamcrest/)
* [Robolectric](http://robolectric.org/)

# Project structure:
    .
    ├── data          # data layer.           
    │   ├── exception       # has data layer exceptions.
    │   ├── model           # Database entity java models.
    │   ├── helper          # has 2 helper classes for Loader.
    │   ├── local           # local data sources files.
    │   ├── Loader          # Loader data source.
    ├── App           # view and presentaion layer which includes all UI required files and preseters.
    │   ├── exception       # has exception wrapper for data layer exceptions.
    │   ├── presentation    # has views, presenters and contracts.
    │   ├── service         # has the distraction service with it's launch broadcast receiver.
    │   ├── .....           # the rest is some utilities and helpers.
    
# Task Requirements as user stories:
1. As a user I’d like to get a list of installed applications.
2. As a user I can blacklist any application.
3. As a user I can start/stop the no-distraction-mode.
4. As an App I can detect the application the user is currently using.
5. As an App I can notify and open myself again if the user is using a blacklisted application.

