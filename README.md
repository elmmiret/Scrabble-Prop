# Scrabble Project 🎲 (`scrabble-prop`)

This project is a **Java-based** implementation of the classic **Scrabble** game, developed with a modular architecture and managed using **Gradle**. The system provides a comprehensive environment for playing matches, managing user data, and tracking competitive performance through both a Command Line Interface (CLI) and a Graphical User Interface (GUI) built with Swing.

---

## 🚀 Description

The project is structured to handle complex game logic, including tile management, scoring, and board constraints. It utilizes a robust domain model and data persistence to offer a complete gaming experience.

### Key Features:
* **Profile Management**: Allows users to create, modify, and delete player profiles.
* **Game Management**: Logic for starting new matches, pausing current games, and resuming saved sessions.
* **Rankings & Statistics**: Global leaderboards and detailed tracking of player performance and game history.
* **Hybrid Interface**: Supports interactive console menus and a Graphical User Interface (GUI) for an enhanced user experience.

---

## 🛠️ Prerequisites

* **Java 22**: The project is configured to use the Java 22 toolchain.
* **Gradle**: Build and dependency management are handled via the Gradle wrapper.

---

## 🏃 How to Run

### 1. Execute the Main Application
To launch the program (which starts both the CLI menu and the GUI), use the following command:

    ./gradlew run

### 2. Run Drivers (Development & Testing)
To execute specific drivers for testing domain logic:

**Build the project**:

    ./gradlew build

**Run the desired driver**:

    java -cp "build/classes/java/test:build/classes/java/main" ctrldomini.NOMBRE_DRIVER

*(Replace `NOMBRE_DRIVER` with the specific driver class name)*.

### 3. Other Useful Commands
* **Run Unit Tests**: `./gradlew test` (runs the test suite).
* **Clean Build Files**: `./gradlew clean` (removes compilation artifacts).
* **Create Executable Jar**: `./gradlew jar` (found in `build/libs`).
* **Full Distribution**: `./gradlew assembleDist` (creates `.tar` and `.zip` packages with all dependencies in `build/distributions`).
