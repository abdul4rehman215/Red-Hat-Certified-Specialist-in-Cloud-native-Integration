#!/bin/bash
echo "=== Camel Error Handling Monitoring ==="
echo "Timestamp: $(date)"
echo
echo "=== Retry Strategy Results ==="
echo "Successful processing:"
ls -la output/retry/ 2>/dev/null || echo "No files processed yet"
echo
echo "=== Dead Letter Queue Results ==="
echo "Successful processing:"
ls -la output/dlq/success/ 2>/dev/null || echo "No successful files yet"
echo "Failed processing (DLQ):"
ls -la output/dlq/failed/ 2>/dev/null || echo "No failed files yet"
echo
echo "=== Fallback Strategy Results ==="
echo "Primary processing:"
ls -la output/fallback/primary/ 2>/dev/null || echo "No primary processed files yet"
echo "Fallback processing:"
ls -la output/fallback/fallback/ 2>/dev/null || echo "No fallback processed files yet"
echo
echo "=== Advanced Error Handling Results ==="
echo "Successful processing:"
ls -la output/advanced/success/ 2>/dev/null || echo "No successful files yet"
echo "Validation errors:"
ls -la output/advanced/validation-errors/ 2>/dev/null || echo "No validation errors yet"
echo "Runtime errors:"
ls -la output/advanced/runtime-errors/ 2>/dev/null || echo "No runtime errors yet"
echo "General errors:"
ls -la output/advanced/general-errors/ 2>/dev/null || echo "No general errors yet"
