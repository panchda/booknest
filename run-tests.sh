
#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

# Load environment variables
if [[ ! -f .env ]]; then
  echo "Missing .env file. Copy .env.example to .env and configure it."
  exit 1
fi

set -a
source .env
set +a

# Check Docker Compose
if ! docker compose version >/dev/null 2>&1; then
  echo "Docker Compose is not available."
  exit 1
fi

# Start PostgreSQL if necessary
echo "Starting PostgreSQL..."
docker compose up -d --wait postgres

# Run backend tests
services=("book-service" "review-service" "auth-service")

for service in "${services[@]}"; do
  echo ""
  echo "========================================"
  echo "Running tests: $service"
  echo "========================================"

  (
    cd "services/$service"
    ./mvnw test
  )
done

echo ""
echo "========================================"
echo "All backend tests passed!"
echo "========================================"
