FROM node:20.20.2-alpine AS builder

ARG PORTAL_MODE=ygb

WORKDIR /app

COPY yuegongbao-ui/package*.json ./
RUN npm install --legacy-peer-deps

COPY yuegongbao-ui/ ./

RUN node ./node_modules/vite/bin/vite.js build --mode ${PORTAL_MODE}

FROM nginx:1.27-alpine

ARG PORTAL_MODE=ygb

COPY docker/admin-ui/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist/${PORTAL_MODE}/ /tmp/site/
RUN rm -rf /usr/share/nginx/html/* \
    && cp -r /tmp/site/* /usr/share/nginx/html/ \
    && rm -rf /tmp/site

EXPOSE 80
