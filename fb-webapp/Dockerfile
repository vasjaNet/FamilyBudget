FROM node:20-alpine AS build

WORKDIR /app

COPY . .

# Process all steps in a single RUN to avoid multiple layers and construct the final rootfs structure
RUN npm ci && \
    npm run build && \
    mkdir -p /stage/usr/share/nginx/html /stage/etc/nginx/templates && \
    cp -a dist/fb-webapp/browser/. /stage/usr/share/nginx/html/ && \
    cp nginx.conf.template /stage/etc/nginx/templates/default.conf.template

FROM nginx:alpine

# Use a single COPY command that copies the prepared structure into the root (/) to minimize layers
COPY --from=build /stage /

EXPOSE 80

ENV BACKEND_URL=http://backend:8080

CMD ["nginx", "-g", "daemon off;"]
