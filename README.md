# Bliss Android Challenge

Android app built with Kotlin and Jetpack Compose that consumes the GitHub API to show emojis, user avatars and Google's public repositories.

## Features

| # | Task | Where |
|---|------|-------|
| 1 | Fetch the GitHub emojis and map them into a list of `Emoji` | `data/remote`, `data/mapper/EmojiMapper` |
| 2 | Persist emojis locally, network only on first launch | `EmojiRepositoryImpl` (cache first, Room) |
| 3 | Random emoji on the home screen | `HomeViewModel` |
| 4 | Emoji list in a grid, tap removes from memory, pull to refresh restores | `emojilist/` |
| 5 | Avatar search by username | `HomeViewModel`, `AvatarRepositoryImpl` |
| 6 | Avatars saved in the database, repeated searches served from cache | `AvatarRepositoryImpl` (Room, case insensitive login) |
| 7 | Avatar list in a grid, tap deletes from the database | `avatarlist/` |
| 8 | Google repositories list | `googlerepos/` |
| 9 | Pagination on scroll | `UserReposPagingSource` (Paging 3) |

## Architecture

One Gradle module per layer, with dependencies pointing inwards:

```
app ──► presentation ──► domain ◄── data
 └──────────────────────────────────┘
```

- **domain**: pure Kotlin. Models, repository contracts and use cases. Use cases expose `Flow` and wrap failures in a single `DomainException` carrying the message produced by the data layer.
- **data**: Retrofit, OkHttp, kotlinx.serialization and Room. Repositories return `Result<T>` and own the caching strategy and the error messages shown to the user.
- **presentation**: Compose UI and ViewModels, following MVI without a base class. Each screen has a sealed `UiState`, an `Intent` and, when needed, a `SideEffect`. A `Root` composable binds the ViewModel and a stateless `Screen` renders the state, so every screen and component is fully previewable.
- **app**: Hilt application, Navigation 3 and the root scaffold that hosts the snackbar.
- **konsist**: architecture tests that enforce the layer dependency rules.

### Network

Retrofit uses a custom `CallAdapter` that turns every call into a `GithubApiResponse` (`Success` or `Error`), so failures are values and no exceptions leak from the network layer. GitHub specific behaviour is handled there:

- `403` is treated as rate limit only when `x-ratelimit-remaining` is `0` or `retry-after` is present, `429` is always rate limit.
- The rate limit reset header is formatted into a readable time, for example `API rate limit reached. Try again at 14:32`.
- Connection failures, `404` and unexpected errors are mapped into user friendly messages.

### Caching

- **Emojis** and **avatars** are cache first: Room is read first and the network is used only on a miss. Avatars are stored with the login as primary key and `COLLATE NOCASE`, so `BlissApps` and `blissapps` hit the same row.
- **Google repositories** are network only through a `PagingSource`, as the challenge states that caching is not required for this list. Pages are requested with `page` and `per_page=100`, and pagination ends when a page comes back partial or empty.

### Errors in the UI

- When there is content on screen, errors are transient and shown in a snackbar through a `SnackbarController` injected into the ViewModels and collected by the root scaffold.
- When there is nothing to show, the screen renders an error state with a retry action.

## Tech stack

Kotlin 2.3, AGP 9 with built-in Kotlin, Jetpack Compose with Material 3, Navigation 3, Hilt, Coroutines and Flow, Retrofit 3, OkHttp 5, kotlinx.serialization, Room, Paging 3, Coil 3.

Tests: JUnit 4, MockK, Turbine, MockWebServer, Paging testing, Robolectric, Compose UI test, Hilt testing and Konsist.

## Tests

| Module | Unit (JVM) | Instrumented |
|--------|-----------|--------------|
| domain | 10 | |
| data | 73 | 20 |
| presentation | 76 | 37 |
| app | 4 | 10 |
| konsist | 5 | |

- **Contract tests** with MockWebServer and real GitHub payloads check method, path, headers and every error status the API can return.
- **ViewModel tests** cover every intent and state transition.
- **Compose tests** run on the JVM with Robolectric and again on a device, for screens and components.
- **Room tests** run on a device against a real SQLite database.
- **App flow tests** launch `MainActivity` with Hilt, replacing only the repositories with in memory fakes, and go through navigation, ViewModels, use cases, snackbar and paging end to end.

Test names follow `given ..., when ..., then ...`.

```bash
./gradlew :domain:test :data:testDebugUnitTest :presentation:testDebugUnitTest :app:testDebugUnitTest :konsist:test
./gradlew connectedDebugAndroidTest
```

## Running

Requirements: Android Studio with AGP 9 support, JDK 21 and Android SDK 36. Minimum supported Android version is 7.0 (API 24).

```bash
./gradlew :app:installDebug
```

The app uses the public GitHub API without authentication, which is limited to 60 requests per hour per IP.
