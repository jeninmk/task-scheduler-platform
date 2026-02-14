#!/bin/bash

echo "Task Scheduler Platform - Final Verification"
echo "==========================================="
echo ""

echo "1. All Docker Containers:"
echo "-------------------------"
docker-compose -f docker-compose-final.yml ps

echo ""
echo "2. Service Connectivity:"
echo "-----------------------"

# Test each service
services=(
    "8080:API Gateway"
    "8761:Discovery Service"
    "8081:Auth Service"
    "8082:Task Service"
    "8083:Notification Service"
    "8888:Config Server"
)

all_up=true
for service in "${services[@]}"; do
    port="${service%%:*}"
    name="${service#*:}"
    
    if curl -s "http://localhost:$port/actuator/health" > /dev/null 2>&1; then
        echo "  ✅ $name (port $port): ACCESSIBLE"
    else
        echo "  ❌ $name (port $port): INACCESSIBLE"
        all_up=false
    fi
done

echo ""
echo "3. External Services:"
echo "--------------------"
# PostgreSQL
if docker-compose -f docker-compose-final.yml exec -T postgres pg_isready -U postgres > /dev/null 2>&1; then
    echo "  ✅ PostgreSQL: CONNECTED"
else
    echo "  ❌ PostgreSQL: DISCONNECTED"
    all_up=false
fi

# RabbitMQ
if curl -s http://localhost:15672/api/overview -u guest:guest > /dev/null 2>&1; then
    echo "  ✅ RabbitMQ: CONNECTED"
else
    echo "  ❌ RabbitMQ: DISCONNECTED"
    all_up=false
fi

echo ""
echo "4. Eureka Service Registry:"
echo "--------------------------"
echo "Registered services:"
SERVICES=$(curl -s http://localhost:8761/eureka/apps 2>/dev/null | grep -o "<name>[^<]*</name>" | sed 's/<name>//g' | sed 's/<\/name>//g' | sort -u)

if [ -z "$SERVICES" ]; then
    echo "  (No services registered yet - they may still be starting)"
else
    echo "$SERVICES" | while read service; do
        echo "  ✅ $service"
    done
fi

echo ""
echo "5. Platform Summary:"
echo "-------------------"
if [ "$all_up" = true ]; then
    echo "🎉🎉🎉 PLATFORM IS FULLY OPERATIONAL! 🎉🎉🎉"
    echo ""
    echo "Your Task Scheduler Platform is now running with:"
    echo "  • 6 Microservices"
    echo "  • PostgreSQL Database"
    echo "  • RabbitMQ Message Broker"
    echo "  • Service Discovery (Eureka)"
    echo "  • API Gateway"
    echo ""
    echo "Access the platform at: http://localhost:8080"
    echo ""
    echo "To stop the platform: docker-compose -f docker-compose-final.yml down"
    echo "To view logs: docker-compose -f docker-compose-final.yml logs -f [service-name]"
else
    echo "⚠️  Platform is starting up (some services may still be initializing)"
    echo ""
    echo "Services may take 2-3 minutes to fully start. Check logs with:"
    echo "  docker-compose -f docker-compose-final.yml logs [service-name]"
fi
