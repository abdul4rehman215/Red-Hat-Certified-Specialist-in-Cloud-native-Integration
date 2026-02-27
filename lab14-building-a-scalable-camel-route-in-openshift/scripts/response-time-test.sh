#!/bin/bash
# scripts/response-time-test.sh
ROUTE_URL=$(oc get route camel-api-route -o jsonpath='{.spec.host}')
REQUESTS=${1:-100}

echo "Testing response times with $REQUESTS requests"
echo "URL: http://$ROUTE_URL"

# Test GET endpoint response times
echo "Testing GET endpoint..."
for i in $(seq 1 $REQUESTS); do
  response_time=$(curl -w "%{time_total}" -s -o /dev/null "http://$ROUTE_URL/api/v1/process/test-$i")
  echo "Request $i: ${response_time}s"
done | awk '{sum+=$3; count++} END {print "Average response time:", sum/count "s"}'

# Test POST endpoint response times
echo "Testing POST endpoint..."
for i in $(seq 1 $REQUESTS); do
  response_time=$(curl -w "%{time_total}" -s -o /dev/null \
    -X POST "http://$ROUTE_URL/api/v1/data" \
    -H "Content-Type: application/json" \
    -d "{\"test\":\"data-$i\"}")
  echo "Request $i: ${response_time}s"
done | awk '{sum+=$3; count++} END {print "Average response time:", sum/count "s"}'
