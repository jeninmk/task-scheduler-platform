#!/bin/bash

echo "=== Testing Gateway Routing ==="

echo "1. Testing health endpoint:"
curl -s http://localhost:8080/actuator/health | jq '.status' 2>/dev/null || curl -s http://localhost:8080/actuator/health

echo -e "\n2. Testing auth registration through gateway:"
curl -v -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"gatewayuser","email":"gateway@test.com","password":"password123"}' 2>&1 | \
  grep -E "(POST|HTTP/|Location:|{.*})" | tail -20

echo -e "\n3. Testing direct access (for comparison):"
curl -s -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"directuser","email":"direct@test.com","password":"password123"}' | \
  jq '.success' 2>/dev/null || echo "Direct call made"

echo -e "\n4. Checking gateway routes:"
curl -s http://localhost:8080/actuator/gateway/routes 2>/dev/null || echo "Routes endpoint not available"
