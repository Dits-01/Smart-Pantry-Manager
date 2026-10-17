# Smart Pantry Manager

[![GitHub stars](https://img.shields.io/github/stars/Dits-01/Smart-Pantry-Manager?style=flat-square)](https://github.com/Dits-01/Smart-Pantry-Manager/stargazers)
[![Issues](https://img.shields.io/github/issues/Dits-01/Smart-Pantry-Manager?style=flat-square)](https://github.com/Dits-01/Smart-Pantry-Manager/issues)
[![Language](https://img.shields.io/badge/language-Java-orange?style=flat-square)](https://github.com/Dits-01/Smart-Pantry-Manager)
[![License](https://img.shields.io/badge/license-None-lightgrey?style=flat-square)](https://github.com/Dits-01/Smart-Pantry-Manager)

Smart Pantry Manager is a lightweight Android app that helps you track pantry inventory, maintain recipes, and suggest meals based on the ingredients you already have.

---

## Demo / Screenshots

> Replace the placeholders below with real screenshots. Put images in `docs/screenshots/` or the repo root and update the paths.

![App Icon](https://raw.githubusercontent.com/Dits-01/Smart-Pantry-Manager/master/app/src/main/ic_launcher-playstore.png)

<!-- Example screenshot placeholders -->
![Pantry List](docs/screenshots/pantry-list.png)
![Recipe Suggestions](docs/screenshots/recipe-suggestions.png)
![Add Ingredient](docs/screenshots/add-ingredient.png)

Tip: To add real screenshots:
1. Create `docs/screenshots/` in the repo.
2. Add PNG/JPG files named as above (or update the image paths in this README).
3. Commit and push — GitHub will render them automatically.

---

## Features

- Add, edit, delete pantry items (name, category, quantity, unit, shelf location, notes).
- Persistent local storage via Room database.
- Save recipes (title, prep time, ingredients, instructions).
- Suggest recipes that are 100% match with pantry contents.
- View missing ingredients and shopping suggestions for recipes.
- Settings: accent color, dark mode, preferred unit system (metric/imperial).
- Search pantry items.

---

## Quick Start

Requirements:

- Android Studio
- JDK 11+
- Android SDK (API 24+)
- Emulator or Android device

Clone and open in Android Studio:

```bash
git clone https://github.com/Dits-01/Smart-Pantry-Manager.git
cd Smart-Pantry-Manager
# open the project in Android Studio and let it sync
```

Build and run from command line:

```bash
./gradlew assembleDebug
./gradlew installDebug            # installs apk on connected device/emulator
./gradlew test                     # run JVM/unit tests
```

Run from Android Studio:
- Open the project
- Click "Sync Project with Gradle Files"
- Choose a device/emulator and Run.

---

## Project Layout

```text
app/
  build.gradle.kts         # app module Gradle config
  src/main/
    AndroidManifest.xml
    java/com/example/smartpantrymanager/
      MainActivity.java
      adapter/              # RecyclerView adapters
      data/                 # Room: AppDatabase, DAOs, repository
      fragments/            # UI screens (pantry, recipe, settings)
      model/                # Domain models and entities
      util/                 # Helpers
      viewmodel/            # ViewModels (ShelfViewModel)
    res/                    # layouts, drawables, values, xml
build.gradle.kts
gradle.properties
gradlew / gradlew.bat
settings.gradle.kts
```

---

## Architecture & Design

- Single-activity host (`MainActivity`) with BottomNavigationView.
- UI is composed of Fragments (pantry list, add/edit ingredient, recipe suggestions, settings).
- Room + DAO pattern for local persistence:
  - Entities: `ShelfItem`, `RecipeEntity`
  - DAOs: `ShelfDao`, `RecipeDao`
  - `AppDatabase` seeds example data on first run.
- `ShelfViewModel` exposes LiveData for UI reactive updates.
- Recipe matching logic lives in `SuggestedRecipesFragment` (matches recipe ingredient quantities against pantry quantities).

---

## Database & Seed Data

- Database: `shelf_control_database` (Room)
- On first creation, `AppDatabase` seeds a set of pantry items and example recipes to let users test the app immediately.

---

## Customization & Settings

- Accent color and dark mode persisted in `SharedPreferences` (`shelf_prefs`).
- Unit preferences (metric / imperial) stored and used in UI where quantities are shown.

---

## Development Notes

- Primary language: Java (Android SDK + AndroidX).
- Not using Jetpack Compose; uses classic XML layouts.
- Tests: basic unit tests are available (run via `./gradlew test`).

---

## Contributing

Contributions are welcome.

1. Fork the repo.
2. Create a feature branch: `git checkout -b feature/your-change`.
3. Commit changes: `git commit -m "Add feature"`.
4. Push: `git push origin feature/your-change`.
5. Open a Pull Request describing your changes.

Please open issues for bugs, feature requests, or questions.

---

## Roadmap / Ideas

- Add image support for pantry items (photo of product).
- Add expiration date tracking with notifications.
- Add more advanced recipe matching (partial matches + ranking).
- Export/import pantry data (CSV/JSON) and backup/restore.

---

## License

This repository currently does not include a license file. If you plan to publish or accept contributions, consider adding an open-source license (e.g., MIT, Apache-2.0).

---

## Contact

Repository: https://github.com/Dits-01/Smart-Pantry-Manager

For questions, open an issue on the repository or submit a PR.
