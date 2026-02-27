#!/bin/bash
# scripts/load-test.sh
ROUTE_URL=$(oc get route camel-api-route -o jsonpath='{.spec.host}')
DURATION=${1:-300} # Default 5 minutes
CONCURRENT=${2:-10} # Default 10 concurrent requests

echo "Starting load test against: http://$ROUTE_URL"
echo "Duration: ${DURATION} seconds"
echo "Concurrent requests: ${CONCURRENT}"

# Function to make requests
make_requests() {
  local id=$1
  local end_time=$(($(date +%s) + DURATION))

  while [ $(date +%s) -lt $end_time ]; do
    # Mix of GET and POST requests
    if [ $((RANDOM % 2)) -eq 0 ]; then
      curl -s -X GET "http://$ROUTE_URL/api/v1/process/$id-$(date +%s)" > /dev/null
    else
      curl -s -X POST "http://$ROUTE_URL/api/v1/data" \
        -H "Content-Type: application/json" \
        -d "{\"worker\":\"$id\",\"timestamp\":$(date +%s)}" > /dev/null
    fi

    # Small delay to avoid overwhelming
    sleep 0.1
  done
}

# Start concurrent workers
for i in $(seq 1 $CONCURRENT); do
  make_requests $i &
done

echo "Load test started with $CONCURRENT workers"
echo "Monitor scaling with: watch 'oc get pods -l camel.apache.org/integration=scalable-camel-service'"
echo "Monitor HPA with: watch 'oc get hpa camel-service-hpa'"

# Wait for all background jobs to complete
wait
echo "Load test completed"
