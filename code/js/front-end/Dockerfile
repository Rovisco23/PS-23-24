FROM node:20.15.1-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
RUN npx ngcc --properties es2023 browser module main --first-only --create-ivy-entry-points
COPY . .
RUN npm run build
FROM nginx:stable
COPY nginx.conf /etc/nginx/conf.d
COPY --from=build /app/dist/front-end/ /usr/share/nginx/html
EXPOSE 80
