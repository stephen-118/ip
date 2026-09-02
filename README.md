# Stephen task chatbot

Stephen is a task-management chatbot with both a command-line interface and a JavaFX graphical interface.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate `src/main/java/stephen/Launcher.java`, right-click it, and choose
   `Run Launcher.main()` to start the GUI. If the code editor shows compile errors, reload
   the Gradle project or restart the IDE.

## Running the application

Use JDK 25 for every command below. The Gradle wrapper downloads the required JavaFX modules
for Windows, macOS, and Linux.

Launch the JavaFX GUI:

```powershell
.\gradlew.bat run
```

The input box supports Enter to send and Shift+Enter to insert a new line.

Run the original command-line interface:

```powershell
.\gradlew.bat runCli
```

If the setup is correct, the CLI starts with:

```text
Hello! I'm Stephen.
What can I do for you?
```

The standalone JavaFX learning/setup check is retained separately and can be launched with:

```powershell
.\gradlew.bat runHelloWorld
```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Checking code style

Run Checkstyle on both the application and test code from the project folder:

```powershell
.\gradlew.bat checkstyleMain checkstyleTest
```

The rules in `config/checkstyle/checkstyle.xml` enforce the mechanically checkable
parts of the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
Fix reported violations before submitting code.

## Running tests

Run all automated tests and code-style checks:

```powershell
.\gradlew.bat check
```

Run the exact-output command-line UI tests after compiling:

```powershell
.\gradlew.bat classes
.\scripts\run_ui_tests.ps1 -JavaExecutable (Get-Command java).Source `
    -ClassPath build/classes/java/main -MainClass stephen.Stephen `
    -CasesFile test/ui-test-cases.json
```

## Finding tasks

Enter `find KEYWORD` to display tasks whose descriptions contain the keyword or phrase.
Matching is case-insensitive. For example, `find read book` finds a task described as
`Read Book`.
