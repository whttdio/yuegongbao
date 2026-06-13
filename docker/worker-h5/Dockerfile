FROM node:20.20.2-alpine AS builder

ARG VITE_WORKER_API_BASE_URL=/prod-api

WORKDIR /app

COPY yuegongbao-worker-uniapp/package*.json ./
RUN npm ci

COPY yuegongbao-worker-uniapp/ ./

RUN printf 'VITE_WORKER_API_BASE_URL=%s\n' "${VITE_WORKER_API_BASE_URL}" > .env.production
RUN UNI_INPUT_DIR=/app node ./node_modules/@dcloudio/vite-plugin-uni/bin/uni.js build -p h5 --mode production

FROM nginx:1.27-alpine

COPY docker/worker-h5/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist/build/h5 /usr/share/nginx/html

EXPOSE 80
