FROM node:20.20.2-alpine

WORKDIR /work

COPY tmp/sql-runner/package.json ./
RUN npm install --omit=dev mysql2@2.3.3

COPY tmp/sql-runner/seed-worker-data.js ./

CMD ["node", "seed-worker-data.js"]

