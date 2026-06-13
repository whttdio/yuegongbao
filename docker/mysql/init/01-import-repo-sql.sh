#!/bin/bash
set -euo pipefail

mysql_cmd=(mysql --default-character-set=utf8mb4 -uroot "-p${MYSQL_ROOT_PASSWORD}" "${MYSQL_DATABASE}")

run_sql() {
  local file="$1"
  echo "==> importing ${file}"
  "${mysql_cmd[@]}" < "${file}"
}

run_sql /docker-entrypoint-initdb.d/sql/yuegongbao_20260417.sql

while IFS= read -r file; do
  run_sql "${file}"
done < <(find /docker-entrypoint-initdb.d/sql -maxdepth 1 -type f -name 'ygb_phase*.sql' | sort -V)

if [ "${YGB_INIT_DEMO_DATA:-true}" = "true" ]; then
  while IFS= read -r file; do
    run_sql "${file}"
  done < <(find /docker-entrypoint-initdb.d/sql -maxdepth 1 -type f -name 'ygb_gd*_seed.sql' | sort -V)
fi

