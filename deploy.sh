#!/usr/bin/env sh

set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
LOCK_DIR=${DEPLOY_LOCK_DIR:-/tmp/love-app-deploy.lock}
WAIT_TIMEOUT=${DEPLOY_WAIT_TIMEOUT:-180}

log() {
    printf '[%s] %s\n' "$(date '+%Y-%m-%d %H:%M:%S')" "$*"
}

fail() {
    log "ERROR: $*" >&2
    exit 1
}

if ! mkdir "$LOCK_DIR" 2>/dev/null; then
    log "Another deployment is already running; exiting."
    exit 0
fi
trap 'rmdir "$LOCK_DIR" 2>/dev/null || true' EXIT HUP INT TERM

cd "$SCRIPT_DIR"

[ -f docker-compose.yml ] || fail "docker-compose.yml not found in $SCRIPT_DIR"
[ -f .env ] || fail ".env not found in $SCRIPT_DIR"
command -v docker >/dev/null 2>&1 || fail "docker command not found"

docker compose config --quiet

wait_for_healthy() {
    service=$1
    deadline=$(($(date +%s) + WAIT_TIMEOUT))

    while :; do
        container_id=$(docker compose ps --all --quiet "$service" 2>/dev/null || true)
        if [ -n "$container_id" ]; then
            health=$(docker inspect \
                --format '{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}' \
                "$container_id" 2>/dev/null || true)

            case "$health" in
                healthy)
                    log "$service is healthy"
                    return 0
                    ;;
                unhealthy|dead|exited)
                    fail "$service is $health"
                    ;;
            esac
        fi

        if [ "$(date +%s)" -ge "$deadline" ]; then
            fail "timed out waiting for $service to become healthy"
        fi
        sleep 3
    done
}

log "Pulling backend and frontend images"
docker compose pull backend frontend

log "Updating backend"
docker compose up -d --pull never --no-deps --remove-orphans backend
wait_for_healthy backend

log "Updating frontend"
docker compose up -d --pull never --no-deps --remove-orphans frontend
wait_for_healthy frontend

log "Deployment completed"
docker compose ps
