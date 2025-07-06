@echo off
SETLOCAL ENABLEDELAYEDEXPANSION

echo 🚀 Setting up PDF Processing Service...

:: Check prerequisites
echo Checking prerequisites...

where java >nul 2>nul
IF ERRORLEVEL 1 (
    echo Java 17+ required but not installed. Aborting.
    exit /b 1
)

where mvn >nul 2>nul
IF ERRORLEVEL 1 (
    echo Maven required but not installed. Aborting.
    exit /b 1
)

where docker >nul 2>nul
IF ERRORLEVEL 1 (
    echo Docker required but not installed. Aborting.
    exit /b 1
)

:: Create project structure
echo Creating project structure...

mkdir src\main\java\com\company\pdfservice\config
mkdir src\main\java\com\company\pdfservice\controller
mkdir src\main\java\com\company\pdfservice\service
mkdir src\main\java\com\company\pdfservice\engine
mkdir src\main\java\com\company\pdfservice\model\entity
mkdir src\main\java\com\company\pdfservice\model\dto
mkdir src\main\java\com\company\pdfservice\model\enums
mkdir src\main\java\com\company\pdfservice\repository
mkdir src\main\java\com\company\pdfservice\exception
mkdir src\main\java\com\company\pdfservice\util

mkdir src\main\resources\templates
mkdir src\main\resources\static
mkdir src\main\resources\db\migration

mkdir src\test\java\com\company\pdfservice\controller
mkdir src\test\java\com\company\pdfservice\service
mkdir src\test\java\com\company\pdfservice\engine
mkdir src\test\java\com\company\pdfservice\integration

mkdir src\test\resources\test-data
mkdir src\test\resources\testcontainers

mkdir docker
mkdir k8s
mkdir scripts
mkdir docs\api
mkdir docs\deployment
mkdir docs\architecture

:: Create essential files
echo Creating essential files...

type nul > README.md
type nul > .gitignore
type nul > .dockerignore

:: NOTE: Windows does not require chmod for execution permissions

echo ✅ Project structure created successfully!
echo 📝 Next steps:
echo 1. Update pom.xml with dependencies
echo 2. Configure application.yml
echo 3. Create database schema
echo 4. Start coding! 🎉

ENDLOCAL
