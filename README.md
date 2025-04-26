# Gradebook Management System

## Overview
A Java-based desktop application for managing student grades, built with Swing and following MVC architecture with Strategy and Observer patterns.


### Key Patterns
1. **Strategy Pattern**:
   - `GradeCalculator` interface with:
     - `PointsBasedCalculator`: Simple points total
     - `CategoryBasedCalculator`: Weighted categories with drop-lowest

2. **Observer Pattern**:
   - Models notify views of changes via `PropertyChangeSupport`
   - Example: Grade changes automatically update GPA display

3. **Encapsulation**:
   - All model fields private with getters/setters
   - Collections return copies to prevent external modification

## How to Run

### Prerequisites
- Java JDK 17 or later installed
- (Optional) IDE like Eclipse or IntelliJ for easier execution

### Running the Application

1. **Download the project files**:
   - Get the complete project folder with all `.java` files
   - Ensure the package structure is preserved:
     ```
     /project-root
       /controller
       /model
       /test
       /util
       /view
     ```

2. **Command Line Instructions**:
   ```bash
   # Compile all Java files
   javac controller/MainController.java

   # Run the application
   java controller.MainController
