#!/bin/bash
# scripts/collect-metrics.sh
LOGFILE="scaling-metrics-$(date +%Y%m%d-%H%M%S).log"
echo "Collecting metrics to: $LOGFILE"

# Collect metrics every 30 seconds
while true; do
  echo "=== $(date) ===" >> $LOGFILE

  echo "Pod Count:" >> $LOGFILE
  oc get pods -l camel.apache.org/integration=scalable-camel-service --no-headers | wc -l >> $LOGFILE

  echo "HPA Status:" >> $LOGFILE
  oc get hpa camel-service-hpa --no-headers >> $LOGFILE

  echo "Resource Usage:" >> $LOGFILE
  oc top pods -l camel.apache.org/integration=scalable-camel-service --no-headers >> $LOGFILE

  echo "" >> $LOGFILE
  sleep 30
done
