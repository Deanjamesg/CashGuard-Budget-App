# CashGuard - Android Budgeting Application
An intuitive Android application designed to empower users to take control of their finances through detailed tracking of income, expenses, and progress towards their personal budgeting goals

<p align="center">
  <img src="https://github.com/user-attachments/assets/cfc396ea-3f9f-4905-9a0b-ea59e3f1fa2c">
</p>

</br>

## 1. Introduction

Welcome to the CashGuard mobile application. This Android app serves as a powerful and intuitive tool for personal finance management, demonstrating core functionalities for tracking income, expenses, and budgeting. The platform's aim is to provide users with a clear visual understanding of their spending habits and help them progress towards their financial goals.

This application is built using modern Android development practices:

* **Platform:** Android
* **Language:** Kotlin
* **Architecture:** MVVM (Model-View-ViewModel)
* **UI:** Android XML with Material Components
* **Database:** Firebase Realtime Database (for cloud storage & sync) and Room Persistence Library (for offline-first UI performance)
* **Authentication:** Firebase Authentication
* **Asynchronous Operations:** Kotlin Coroutines
* **Charting Library:** EazeGraph
* **Navigation:** Jetpack Navigation Component
* **Primary Development Environment:** Android Studio

</br>

## 2. Core Features Implemented

The application implements the following core functionalities:

* **User Management:** Secure user registration and login backed by Firebase Authentication.
* **Category Management:** A default set of income and expense categories are automatically created for each user upon registration.
* **Transaction Management:** Users can add detailed income or expense transactions, specifying an amount, category, date, and optional note.
* **Data Synchronization:** User data is synchronized with the Firebase Realtime Database for persistence and integrity.
* **Budgeting & Data Visualization:** Key financial data is presented in clear, graphical formats.

</br>

## 3. Custom Features for Final Submission

As per the POE requirements, two major custom features have been implemented to provide users with deeper financial insights.

### Feature 1: Categorical Spending Pie Chart

This feature provides an at-a-glance visual breakdown of the user's expenses by category for the current period.

* **Visual Breakdown:** A colorful pie chart instantly shows which categories make up the largest portions of the user's spending.
* **Purpose:** Helps users quickly identify their biggest areas of expenditure and make informed decisions about their spending habits.

![image](https://github.com/user-attachments/assets/5c372526-a12f-4d66-9a2b-0672e2e04478)
![image](https://github.com/user-attachments/assets/c03a92e5-d44f-41b7-8c5d-a282fb455a7f)

### Feature 2: Dynamic Budget Balance Tracking

This is a dedicated screen where users can set specific budget amounts for any expense category and track their spending progress in real-time.

* **Dedicated Budget Screen:** A central hub for managing all personal budgets. It displays a clear, scrollable list of every category for which a budget has been set.
* **Dynamic Progress List:** Each item in the list shows the category name, the amount spent versus the budget limit (e.g., R750 / R1000), and the percentage of the budget used.
* **Visual Progress Bars:** Every budget item includes a progress bar that visually fills as the user spends in that category, providing an immediate understanding of their remaining budget. The color of each progress bar is customized by the user.
* **Easy Budget Management:** When a user creates a category and then updates that category to get a budget the list is dynamically updated.

![image](https://github.com/user-attachments/assets/f051a0b6-0f7a-4713-8a0d-bf26f574f324)

</br>

## 4. User Roles & Test Credentials

The system is designed for a single user role. There are no pre-seeded user accounts.

* **Action Required:** To use the application, you must first **register a new account** through the registration screen.

</br>

## 5. Prerequisites (Development Environment Setup)

To set up and run this project on your local machine, you will need:

* **Android Studio:** Latest stable version recommended (e.g., Iguana, Jellyfish). Download from the [official Android Developer website](https://developer.android.com/studio).
* **Android SDK:** The project targets SDK version 35. Ensure you have this SDK platform installed via Android Studio's SDK Manager.
* **Git:** Required for cloning the repository.
* **Firebase Setup:** To run the project with full cloud sync capabilities, you will need to connect it to your own Firebase project in the Firebase Console and place your own `google-services.json` file in the `app/` directory.

</br>

## 6. Getting Started (Setup and Running the Project)

Follow these steps to get the project running on an Android emulator or a physical device:

1.  **Clone the Repository:** Open a terminal or command prompt and run the following command:
    ```bash
    git clone [https://github.com/Deanjamesg/CashGuard.git]
    ```

2.  **Firebase Setup:**
    * Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
    * Add an Android app to your Firebase project with the package name `com.example.cashguard`.
    * Follow the setup steps to download the `google-services.json` configuration file.
    * Place this downloaded `google-services.json` file into the `CashGuard/app/` directory of your project.

3.  **Open the Project in Android Studio:**
    * Launch Android Studio.
    * Select **Open** and navigate to the cloned `CashGuard` project folder.
    * Wait for the automatic Gradle sync process to complete.

4.  **Run the Application:**
    * Ensure an emulator is running or a physical device is connected.
    * Select the **`app`** run configuration from the top toolbar.
    * Click the **Run 'app'** button (the green play icon ►).

</br>

## 7. Building the APK

To generate a debug APK file without running the app:

* In Android Studio, select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
* A notification will appear with a link to locate the generated `.apk` file.

</br>

## 8. Database & Data Synchronization

This app uses a hybrid data management strategy to enhance user experience by combining the speed of a local database with the robustness of cloud storage.

### Firebase Realtime Database (Cloud Storage)

* **Role:** Serves as the single source of truth and provides cloud backup for all user data (transactions, categories, budgets).
* **Benefit:** This ensures data integrity and accessibility across different sessions or devices. All changes made locally are synchronized with Firebase.

### Room Database (Local Cache)

* **Role:** Acts as a local cache of the data stored in Firebase.
* **Benefit:** The UI reads data directly from the Room database, providing an extremely fast, responsive, and smooth user experience that works even when the device is temporarily offline.

</br>

## 9. Project Structure Overview

* **/Activities:** Contains Android Activities, the entry points and containers for UI screens (`MainActivity`, `UserActivity`).
* **/Fragments:** Contains individual UI screens (`BarGraphFragment`, `BudgetBalancesFragment`, etc.).
* **/ViewModel:** Contains ViewModels for each screen, holding UI state and business logic (MVVM).
* **/Repository:** Contains repository classes that abstract data sources from the ViewModels.
* **/Dao:** Contains Room Data Access Objects (DAOs) for all local database queries.
* **/data:** Contains the Room entities (`Category`, `Transaction`, `User`, `Budget`) and other data models.
* **/Database:** Contains the `AppDatabase` class that defines the Room database configuration.
* **/Helper:** Contains helper classes like `SessionManager` for managing user login state.
* **/res/layout:** Contains all XML layout files for Activities, Fragments, and custom components.
* **/res/navigation:** Contains the Jetpack Navigation graph XML file(s).

</br>

## 10. Troubleshooting

* **App crashes on startup:** Ensure you have placed your own `google-services.json` file in the `app/` directory. Also try performing a full **Build > Clean Project**, followed by **Build > Rebuild Project**.
* **Default categories not appearing:** Uninstall the app completely from the emulator/device and re-run it from Android Studio. This clears any old database data and ensures the seeding logic runs for the new user.

</br>

# References
- [Icons](https://ionic.io/ionicons)
  
- [Adapters and Spinners](https://www.geeksforgeeks.org/how-to-add-custom-spinner-in-android/)

- [Photo Uri](https://developer.android.com/reference/android/net/Uri)

- [Session Manager](https://developers.google.com/android/reference/com/google/android/gms/cast/framework/SessionManager)

- [Shared Preferences](https://developer.android.com/training/data-storage/shared-preferences)

- [Fragments Overview](https://developer.android.com/guide/fragments)

- [DialogFragment](https://developer.android.com/guide/fragments/dialogs)

- [Create dynamic lists with RecyclerView](https://developer.android.com/develop/ui/views/layout/recyclerview )

- [ListAdapter](https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter)

- [DiffUtil](https://developer.android.com/reference/androidx/recyclerview/widget/DiffUtil)

- [Custom view components](https://www.google.com/search?q=https://developer.android.com/develop/ui/views/layout/custom-views )

- [View binding](https://developer.android.com/topic/libraries/view-binding)

- [Styles and Themes](https://www.google.com/search?q=https://developer.android.com/develop/ui/views/theming/styles-themes)

- [Drawable resources](https://developer.android.com/guide/topics/resources/drawable-resource)

- [Color resources](https://developer.android.com/guide/topics/resources/color-list-resource)

- [Pie chart](https://github.com/PhilJay/MPAndroidChart)


* **Android Developers.** (2024) _Guide to app architecture_. Available at: https://developer.android.com/topic/architecture
* **Firebase Documentation.** (2025) _Get Started with Firebase Realtime Database_. Available at: https://firebase.google.com/docs/database
* **Android Developers.** (2024) _Room persistence library_. Available at: https://developer.android.com/training/data-storage/room
* **Roehr, P.** (n.d.) _EazeGraph Library_. GitHub. Available at: https://github.com/blackfizz/EazeGraph

* **Youtube Demonstration video.** Available at: https://www.youtube.com/watch?v=jS3HAYyBpT4
