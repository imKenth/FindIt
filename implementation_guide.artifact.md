# Detailed Implementation Guide: Phase 1 & 2

This guide provides the exact steps to execute the first two phases of your [Backend Roadmap](file:///home/rai/Documents/FindItMobileApp/backend_roadmap.md).

---

## Phase 1: Architecture Refactoring

### Step 1: Create the Repository Interface
The goal is to stop the UI from knowing about "Mock Data" and instead ask a "Repository" for data.

1. Create a new package: `com.findit.app.data.repository`.
2. Create a file `ItemRepository.kt`:
```kotlin
package com.findit.app.data.repository

import com.findit.app.data.model.Item
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getAllItems(): Flow<List<Item>>
    fun getItemById(id: String): Flow<Item?>
    suspend fun reportItem(item: Item)
}
```

### Step 2: Implement the Mock Repository
Now, wrap your existing `MockData` inside this interface.

1. Create `MockItemRepositoryImpl.kt` in the same package:
```kotlin
class MockItemRepositoryImpl : ItemRepository {
    override fun getAllItems(): Flow<List<Item>> = flow {
        emit(MockData.items)
    }
    // ... implement other methods using MockData
}
```

### Step 3: Create the ViewModel
The ViewModel sits between the UI and the Repository. It handles the "logic" (like filtering).

1. Create package: `com.findit.app.ui.viewmodels`.
2. Create `HomeViewModel.kt`:
```kotlin
class HomeViewModel(private val repository: ItemRepository) : ViewModel() {
    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items = _items.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllItems().collect { _items.value = it }
        }
    }
}
```

### Step 4: Update the UI (HomeScreen.kt)
1. Find where `MockData.items` is used.
2. Replace it with `viewModel.items.collectAsState()`.
3. Stop importing `com.findit.app.data.mock.MockData`.

---

## Phase 2: Firebase Setup (The Real Backend)

### Step 1: Project Initialization
1. Go to [Firebase Console](https://console.firebase.google.com/).
2. Create a project named "FindIt".
3. Register your Android app using the package name: `com.findit.app`.
4. Download `google-services.json` and move it to your `app/` folder.

### Step 2: Add Dependencies
Open `app/build.gradle.kts` and add:
```kotlin
dependencies {
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))

    // Add the dependencies for the desired Firebase products
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
}
```

### Step 3: Swap the Repository
This is the "Magic" step. Once you are ready to go live, you create a `FirestoreRepositoryImpl.kt` that implements the same `ItemRepository` interface.

You then just swap one line in your Dependency Injection code, and the **entire app** switches from Mock Data to Real Cloud Data without you changing a single line of UI code.

---

## Next Steps
1. **Try Step 1 & 2 of Phase 1.**
2. Once you have the interfaces ready, I can help you write the specific **Firestore** queries to replace the mock lists.
