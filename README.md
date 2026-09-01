# Backend Development Roadmap: FindIt Mobile App

This document outlines the step-by-step transition from the current **Mock Data** implementation to a production-ready **Cloud-based Backend**. 

---

## Phase 1: Architecture & Data Layer Refactoring
*Before connecting to a real server, the app needs a flexible architecture.*

- [ ] **Define Repository Interfaces**
    - [ ] Create `com.findit.app.data.repository.ItemRepository` interface to handle Lost/Found items.
    - [ ] Create `com.findit.app.data.repository.AuthRepository` interface for user login/registration.
    - [ ] Create `com.findit.app.data.repository.ClaimRepository` interface for handling item claims.
- [ ] **Implement Repository Pattern**
    - [ ] Move logic from `MockData.kt` into a `MockItemRepositoryImpl`.
    - [ ] Ensure all UI screens call Repository methods instead of accessing `MockData` directly.
- [ ] **Introduce ViewModels**
    - [ ] Create a `HomeViewModel` to manage the state of the Home Screen.
    - [ ] Create a `SearchViewModel` for filtering and searching logic.
    - [ ] Create an `ItemDetailViewModel` to handle fetching specific item data and claim logic.
- [ ] **Setup Dependency Injection (Hilt)**
    - [ ] Add Hilt dependencies to `build.gradle.kts`.
    - [ ] Annotate `MainActivity` and `Application` class.
    - [ ] Create a `DataModule` to provide repository implementations to ViewModels.

---

## Phase 2: Choosing a Backend Provider
*Decide on the infrastructure. Recommended: **Firebase** for rapid development or **Ktor/Node.js** for custom control.*

- [ ] **Option A: Firebase (Serverless - Recommended)**
    - [ ] Setup Firebase Project in the [Firebase Console](https://console.firebase.google.com/).
    - [ ] Add `google-services.json` to the `app/` directory.
    - [ ] Add Firebase SDKs (Auth, Firestore, Storage, Analytics).
- [ ] **Option B: Custom REST API (Ktor/Spring Boot)**
    - [ ] Design API endpoints (e.g., `GET /items`, `POST /claims`).
    - [ ] Setup a database (PostgreSQL or MongoDB).
    - [ ] Host the backend (AWS, Google Cloud, or Heroku).

---

## Phase 3: Identity & Authentication
*Moving from "Mock User" to "Registered User".*

- [ ] **Firebase Auth Integration**
    - [ ] Enable Email/Password and Google Sign-in in Firebase Console.
    - [ ] Implement `AuthRepositoryImpl` using `FirebaseAuth`.
    - [ ] Update `LoginScreen` and `RegisterScreen` to use the real Auth service.
- [ ] **User Profile Management**
    - [ ] Create a `users` collection in the database.
    - [ ] Store extended user info (Avatar URL, School ID, Department).
    - [ ] Update `ProfileScreen` to fetch and update live user data.

---

## Phase 4: Persistence (Real-time Database)
*Replacing `MockData.items` with a live database.*

- [ ] **Database Schema Design**
    - [ ] `items` collection: Store details, location, status, and reporter ID.
    - [ ] `claims` collection: Link `itemId` with `claimantId`.
    - [ ] `notifications` collection: Store user-specific alerts.
- [ ] **Firestore Implementation**
    - [ ] Implement `FirestoreRepositoryImpl`.
    - [ ] Implement "Real-time updates" using Firestore Snapshots so items appear instantly without refresh.
    - [ ] Implement pagination for the Home and Search screens (to avoid loading thousands of items at once).

---

## Phase 5: Multimedia & Files
*Handling real photos of lost items.*

- [ ] **Firebase Storage Setup**
    - [ ] Configure storage buckets for item images.
- [ ] **Image Upload Workflow**
    - [ ] Update `ReportLostScreen` and `ReportFoundScreen` to allow selecting photos from the gallery.
    - [ ] Implement image compression before uploading to save bandwidth.
    - [ ] Store the resulting URL in the Firestore item document.

---

## Phase 6: Business Logic & Server-side Code
*Handling complex logic that shouldn't live on the phone.*

- [ ] **Automated Matching Engine**
    - [ ] Use **Cloud Functions** to trigger a search whenever a new "Found" item is posted.
    - [ ] If a match is found, send a Push Notification to the "Lost" reporter.
- [ ] **Admin Controls**
    - [ ] Implement "Role-Based Access Control" (RBAC) to ensure only Admins can approve claims.
    - [ ] Use Security Rules to prevent users from editing items they didn't report.

---

## Phase 7: Push Notifications
*Keeping users engaged.*

- [ ] **Firebase Cloud Messaging (FCM)**
    - [ ] Setup FCM service in the Android app.
    - [ ] Implement `NotificationsScreen` to show a history of alerts retrieved from the backend.

---

## Phase 8: Hardening & Optimization
- [ ] **Offline Support (Room)**
    - [ ] Use Room to cache Firestore data so the app works in the school basement with no signal.
- [ ] **Security Audit**
    - [ ] Review all Database Rules.
    - [ ] Ensure API keys are not hardcoded in the source code.
