#!/bin/bash
# scripts/setup.sh

set -e

echo "🚀 Setting up PDF Processing Service..."

# Check prerequisites
echo "Checking prerequisites..."
command -v java >/dev/null 2>&1 || { echo "Java 17+ required but not installed. Aborting." >&2; exit 1; }
command -v mvn >/dev/null 2>&1 || { echo "Maven required but not installed. Aborting." >&2; exit 1; }
command -v docker >/dev/null 2>&1 || { echo "Docker required but not installed. Aborting." >&2; exit 1; }

# Create project structure
echo "Creating project structure..."
mkdir -p src/main/java/com/company/pdfservice/{config,controller,service,engine,model/{entity,dto,enums},repository,exception,util}
mkdir -p src/main/resources/{templates,static,db/migration}
mkdir -p src/test/java/com/company/pdfservice/{controller,service,engine,integration}
mkdir -p src/test/resources/{test-data,testcontainers}
mkdir -p {docker,k8s,scripts,docs/{api,deployment,architecture}}

# Create essential files
echo "Creating essential files..."
touch README.md
touch .gitignore
touch .dockerignore

# Make scripts executable
chmod +x scripts/*.sh
chmod +x docker/scripts/*.sh

echo "✅ Project structure created successfully!"
echo "📝 Next steps:"
echo "1. Update pom.xml with dependencies"
echo "2. Configure application.yml"
echo "3. Create database schema"
echo "4. Start coding! 🎉"