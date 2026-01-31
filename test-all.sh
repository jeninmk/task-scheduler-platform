#!/bin/bash

echo "=== MICROSERVICES TEST ==="
echo ""
echo "1. Testing Direct Auth Service:"
DIRECT_RESPONSE=$(curl -s -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"directuser","email":"direct@test.com","password":"pass123"}')
echo "   Response: $(echo $DIRECT_RESPONSE | grep -o '"success":[^,]*' || echo 'No response')"

echo ""
echo "2. Testing Gateway Health:"
GATEWAY_HEALTH=$(curl -s http://localhost:8080/actuator/health)
echo "   Status: $(echo $GATEWAY_HEALTH | grep -o '"status":"[^"]*"' | cut -d'"' -f4 || echo 'Unknown')"

echo ""
echo "3. Testing Auth Through Gateway:"
GATEWAY_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"gatewayuser","email":"gateway@test.com","password":"pass123"}')
echo "   Response: $(echo $GATEWAY_RESPONSE | grep -o '"success":[^,]*' || echo $GATEWAY_RESPONSE)"

echo ""
echo "4. If Gateway Auth Works, Test Full Flow:"
if echo "$GATEWAY_RESPONSE" | grep -q '"success":true'; then
    TOKEN=$(echo $GATEWAY_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    echo "   Token obtained: ${TOKEN:0:20}..."
    
    # Get user ID
    USER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/validate \
      -H "Authorization: Bearer $TOKEN" \
      -H "Content-Type: application/json")
    USER_ID=$(echo $USER_RESPONSE | grep -o '"userId":[0-9]*' | cut -d':' -f2)
    echo "   User ID: $USER_ID"
    
    # Create task
    TASK_RESPONSE=$(curl -s -X POST http://localhost:8080/api/tasks \
      -H "Content-Type: application/json" \
      -H "X-User-Id: $USER_ID" \
      -d '{"title":"Test Task","description":"From gateway","status":"PENDING","priority":1,"scheduledTime":"2024-12-31T23:59:59"}')
    echo "   Task Creation: $(echo $TASK_RESPONSE | grep -o '"success":[^,]*' || echo 'Failed')"
fi

echo ""
echo "=== TEST COMPLETE ==="
