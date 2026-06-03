# 🖼️ ImageLoader

Custom Android Image Loading Library built from scratch using **Kotlin
Coroutines**, featuring **memory & disk caching**, **4-hour TTL cache
policy**, and **manual cache invalidation** --- with **Compose-first
design** and optional View system support.

------------------------------------------------------------------------

## 📌 Overview

This project implements a lightweight Glide-like image loading system
for Android. It demonstrates internal image loading mechanics, caching
strategies, and modern UI integration patterns.

The project consists of: - A reusable **ImageLoader library** - A sample
**Android app (Views + MVVM architecture)**

------------------------------------------------------------------------

## ✨ Features

-   🚀 Asynchronous image loading using Kotlin Coroutines\
-   🧠 In-memory caching (LruCache)\
-   💾 Disk caching (file-based persistence)\
-   ⏳ Cache expiration policy (4 hours TTL)\
-   🔄 Manual cache invalidation\
-   🖼️ Placeholder support during loading\
-   🔁 Safe handling of view recycling\
-   📦 Clean architecture separation\
-   🧩 **Compose-first image loading API**\
-   🪟 Optional Android View support\
-   🔌 No third-party image libraries (Glide/Picasso excluded)

------------------------------------------------------------------------

## 🏗️ Architecture

``` text
ImageLoader (Public API)
   │
   ├── core
   │     ├── ImageLoader.kt
   │     ├── ImageRequest.kt
   │     ├── ImageResult.kt
   │     └── DispatcherProvider.kt
   │
   ├── cache
   │     ├── MemoryCache
   │     ├── DiskCache
   │     └── CachePolicy
   │
   ├── network
   │     ├── ImageDownloader
   │     └── HttpClient
   │
   ├── compose
   │     └── ImageLoaderImage.kt
   │
   ├── view
   │     └── ImageLoaderViewTarget.kt
   │
   ├── utils
   │     ├── BitmapUtils
   │     └── FileUtils
   │
   └── internal
         └── ImageLoaderImpl
```

------------------------------------------------------------------------

## 🔄 Image Loading Flow

``` text
1. Request image (URL)
        ↓
2. Check Memory Cache
        ↓ (hit → return bitmap)
3. Check Disk Cache (validate TTL)
        ↓ (valid → return bitmap)
4. Download image
        ↓
5. Decode bitmap
        ↓
6. Store in Memory + Disk cache
        ↓
7. Render in UI
```

------------------------------------------------------------------------

## ⏳ Cache Policy

-   Cache validity: **4 hours**
-   Expired images are re-fetched from network
-   Manual invalidation clears:
    -   Memory cache
    -   Disk cache

------------------------------------------------------------------------

## 🧩 Compose API (Preferred)

``` kotlin
ImageLoaderImage(
    url = "https://example.com/image.jpg",
    placeholder = R.drawable.placeholder
)
```

### Design principles:

-   Compose-first API
-   State-driven UI updates
-   Lifecycle-aware loading using coroutines

------------------------------------------------------------------------

## 🪟 View API (Legacy support)

``` kotlin
ImageLoader.load(
    url = "...",
    placeholder = R.drawable.placeholder,
    imageView = imageView
)
```

------------------------------------------------------------------------

## 🧠 Key Design Decisions

### 1. Separation of concerns

-   Networking
-   Caching
-   Bitmap decoding
-   UI rendering

### 2. Two-level caching

-   Memory (fast access)
-   Disk (persistent storage)

### 3. Coroutine-based async pipeline

All IO operations run on `Dispatchers.IO`.

### 4. Compose-first architecture

Modern Android UI integration is prioritized.

### 5. Single internal engine

Both Compose and View APIs share the same core implementation.

------------------------------------------------------------------------

## 📱 Sample App

The sample app demonstrates: - Fetching JSON image list (id + url) -
RecyclerView-based UI (Views) - Image loading with placeholder - MVVM
architecture - Cache invalidation button

------------------------------------------------------------------------

## 🧑‍💻 Usage

### Load image (View)

``` kotlin
ImageLoader.load(url, placeholder, imageView)
```

### Load image (Compose)

``` kotlin
ImageLoaderImage(url, placeholder)
```

### Clear cache

``` kotlin
ImageLoader.clearCache()
```

------------------------------------------------------------------------

## 🚧 Future Improvements

-   Request deduplication
-   Bitmap pooling
-   Request cancellation support
-   Image transformations (crop, blur)
-   Preloading strategy
-   Memory pressure handling

------------------------------------------------------------------------

## 🛠 Tech Stack

-   Kotlin
-   Coroutines
-   Jetpack Compose (primary UI integration)
-   Android Views (secondary support)
-   LruCache
-   File-based caching
-   MVVM architecture

------------------------------------------------------------------------

## 📂 Project Structure

    ImageLoader/
     ├── app/
     ├── imageloader/
     └── README.md

------------------------------------------------------------------------

## 📌 Notes

This project was built as a **senior Android engineering exercise** to
demonstrate: - System design thinking - Cache architecture design -
Modern Compose-first API design - Clean modular architecture
